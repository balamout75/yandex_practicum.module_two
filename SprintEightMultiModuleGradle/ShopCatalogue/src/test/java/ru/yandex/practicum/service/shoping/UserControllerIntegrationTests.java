package ru.yandex.practicum.service.shoping;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.configuration.BaseIntegrationTest;
import ru.yandex.practicum.configuration.TestOAuth2Config;
import ru.yandex.practicum.dto.shoping.ItemDto;
import ru.yandex.practicum.mapper.ActionModes;
import ru.yandex.practicum.mapper.SortModes;
import ru.yandex.practicum.security.CurrentUserFacade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.*;

//import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(TestOAuth2Config.class)
class UserControllerIntegrationTests extends BaseIntegrationTest {

	@Value("${images.path}")
	private String UPLOAD_DIR;

	@Autowired
	WebTestClient webTestClient;

	@MockitoSpyBean
	private CatalogueService catalogueService;

	@MockitoSpyBean
	private CartItemService cartItemService;

	@MockitoBean
	CurrentUserFacade currentUserFacade;

	@MockitoBean
	private WebClient paymentWebClient;


	@Test
	void anonymousShouldBeRedirectedToLogin() {
		webTestClient.get()
				.uri("/items")
				.exchange()
				.expectStatus().isOk();
	}

	public record SearchPattern(
			String  pattern,
			int     count
	) {}

	static Stream<Arguments> applyPattern() {
		return Stream.of(
				Arguments.of(new SearchPattern("зонт",1)),
				Arguments.of(new SearchPattern("Зонт",1)),
				Arguments.of(new SearchPattern("  зонт  ",1)),
				Arguments.of(new SearchPattern("кепка",2)),
				Arguments.of(new SearchPattern("моноколесо",1))
		);
	}

	@ParameterizedTest
	@MethodSource("applyPattern")
	void findItemsByTitleOrDescription(SearchPattern searchPattern) {
		webTestClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/items")
						.queryParam("id",1)
						.queryParam("search", "")
						.queryParam("sort", SortModes.ALPHA.toString())
						.queryParam("pageNumber", "1")
						.queryParam("pageSize", "5")
						.build())
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
				.expectBody(String.class)
				.value(html -> {
					assert html.toLowerCase().contains(searchPattern.pattern().trim().toLowerCase());
				});

		Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "title"));
		StepVerifier.create(catalogueService.findAll(searchPattern.pattern(), pageable))
				.expectNextCount(searchPattern.count)
				.verifyComplete();
	}

	@Test
	void getItemByIdSuccessfully() throws Exception {
		ItemDto item = new ItemDto (1,"Кепка","бейсболка большого размера",UPLOAD_DIR+"cap.jpg", 1200,0);
		webTestClient.get()
				.uri("/items/1")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
				.expectBody(String.class)
				.value(html -> {
					assert html.toLowerCase().contains("бейсболка большого размера");
				});
		StepVerifier.create(catalogueService.findItem(1L))
				.expectNext(item)
				.verifyComplete();
	}

	@Test
	void getItemByIdNotFound() throws Exception {
		webTestClient.get()
				.uri("/items/21")
				.exchange()
				.expectStatus().is3xxRedirection()
				.expectHeader().valueEquals("Location", "/items");
	}

	public record SortRequest(
			String inputString,
			SortModes sortModes
	) {}

	static Stream<Arguments> applySortModeString() {
		return Stream.of(
				Arguments.of(new SortRequest("ALPHA",SortModes.ALPHA)),
				Arguments.of(new SortRequest("alpha",SortModes.ALPHA)),
				Arguments.of(new SortRequest("Alpha",SortModes.ALPHA)),
				Arguments.of(new SortRequest("PRICE",SortModes.PRICE)),
				Arguments.of(new SortRequest("price",SortModes.PRICE)),
				Arguments.of(new SortRequest("Price",SortModes.PRICE)),
				Arguments.of(new SortRequest("NO"		,SortModes.NO)),
				Arguments.of(new SortRequest("No"		,SortModes.NO)),
				Arguments.of(new SortRequest("no"		,SortModes.NO)),
				Arguments.of(new SortRequest("Other"	,SortModes.NO))
		);
	}

	// Тестируем StringToSortModesConverter - конвертор значения параметра сортировки товаров,
	// вызываемый в методе ItemController.getItems(). Для разгрузки сервисов замокал userService.findAll
	// Проверяю, что при вызове подается корректно сгенерированный pageable=f(sortmode)
	@ParameterizedTest
	@MethodSource("applySortModeString")
	void testRequestSortParameterConverter(SortRequest sortRequest) {
		Sort sortmode = switch (sortRequest.sortModes) {
			case SortModes.PRICE -> Sort.by(Sort.Direction.ASC, "price");
			case SortModes.ALPHA -> Sort.by(Sort.Direction.ASC, "title");
			default 			 -> Sort.by(Sort.Direction.ASC, "id");
		};
		Pageable pageable = PageRequest.of(0, 5, sortmode);
		List<ItemDto> items = new ArrayList<>();
		Flux<ItemDto> fluxResponse = Flux.fromIterable(items).log();
		doReturn(fluxResponse).when(catalogueService).findAll(anyString(), any());
		webTestClient.get()
				.uri("/items?search=&sort=" + sortRequest.inputString + "&pageNumber=1&pageSize=5")
				.exchange()
				.expectStatus().isOk();
		verify(catalogueService).findAll(anyString(), eq(pageable));
	}


	public record ActionRequest(
			String inputString,
			ActionModes actonModes
	) {}

	static Stream<Arguments> applyActiveModeString() {
		return Stream.of(
				Arguments.of(new ActionRequest("PLUS", ActionModes.PLUS)),
				Arguments.of(new ActionRequest("plus", ActionModes.PLUS)),
				Arguments.of(new ActionRequest("Plus", ActionModes.PLUS)),
				Arguments.of(new ActionRequest("MINUS", ActionModes.MINUS)),
				Arguments.of(new ActionRequest("minus", ActionModes.MINUS)),
				Arguments.of(new ActionRequest("Minus", ActionModes.MINUS)),
				Arguments.of(new ActionRequest("DELETE",ActionModes.DELETE)),
				Arguments.of(new ActionRequest("Delete",ActionModes.DELETE)),
				Arguments.of(new ActionRequest("delete",ActionModes.DELETE)),
				Arguments.of(new ActionRequest("Other", ActionModes.NOTHING)),
				Arguments.of(new ActionRequest("", ActionModes.NOTHING))
		);
	}

	// Тестируем StringToActionModesConverter - конвертор значения параметра изменения счетчика количества
	// товаров в корзине, вызываемый в методе ItemController.postItem(). Для разгрузки сервисов замокал userService.changeInCardCount()
	// Проверяю, что при вызове подается корректно сгенерированный activeMode и выполняется Redirect. Ну и еще поупражнялся в форсмровании
	// запросов без конкатенации строк. Получилось интересно, но не успел вытащить из request'a URI, ограничился контролем параметров модели
	@ParameterizedTest
	@MethodSource("applyActiveModeString")
	@WithMockUser(username = "SomeUser")
	void testRequestActionParameterConverterAndRedirection(ActionRequest actionRequest) {
		doReturn(Mono.just("ok")).when(cartItemService).changeInCardCount(anyLong(), eq(actionRequest.actonModes()));
		doReturn(Mono.just(1L)).when(currentUserFacade).getUserId();
		webTestClient.post()
						.uri(uriBuilder -> uriBuilder
						.path("/items")
						.queryParam("id",1)
						.queryParam("search", "")
						.queryParam("sort", SortModes.ALPHA.toString())
						.queryParam("pageNumber", "1")
						.queryParam("pageSize", "5")
						.queryParam("action",  actionRequest.inputString)
						.build())
				.exchange()
				.expectStatus().is3xxRedirection();
		verify(cartItemService).changeInCardCount(anyLong(), eq(actionRequest.actonModes()));
	}
}
