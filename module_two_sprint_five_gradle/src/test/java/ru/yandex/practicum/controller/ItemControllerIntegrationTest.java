package ru.yandex.practicum.controller;


import org.junit.ClassRule;
import org.junit.jupiter.api.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.data.domain.*;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.yandex.practicum.configuration.TestPostgresContainer;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapping.ActionModes;
import ru.yandex.practicum.mapping.SortModes;
import ru.yandex.practicum.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
@AutoConfigureMockMvc
@ImportTestcontainers(TestPostgresContainer.class)
class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @ClassRule
    public static PostgreSQLContainer<TestPostgresContainer> postgreSQLContainer = TestPostgresContainer.getInstance();

    @MockitoSpyBean
    private UserService userService;

    @Value("${images.path}")
    private String UPLOAD_DIR;

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
    };

    @ParameterizedTest
    @MethodSource("applyPattern")
    void findItemsByTitleOrDescription(SearchPattern searchPattern) throws Exception {
        mockMvc.perform(get("/items")
                .queryParam("id", "1")
                .queryParam("search", searchPattern.pattern())
                .queryParam("sort", SortModes.ALPHA.toString())
                .queryParam("pageNumber", "1")
                .queryParam("pageSize", "5")
        )       .andExpect(status().isOk());
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "title"));
        Page<ItemDto> paged = userService.findAll(1L,searchPattern.pattern(), pageable);
        assertEquals(searchPattern.count, paged.getTotalElements());
    }

    @Test
    void getItemByIdSuccesfull() throws Exception {
        ItemDto item = new ItemDto (1,"Кепка","бейсболка большого размера",UPLOAD_DIR+"cap.jpg", 1200,0);
        assertTrue(userService.existsItem(1L,1L));
        assertEquals(userService.findItem(1L,1L),item);
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    void getItemByIdNotFound() throws Exception {
        assertFalse(userService.existsItem(1L,21L));
        mockMvc.perform(get("/items/21"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("text/html;charset=UTF-8"));
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
                Arguments.of(new SortRequest("NO",SortModes.NO)),
                Arguments.of(new SortRequest("No",SortModes.NO)),
                Arguments.of(new SortRequest("no",SortModes.NO)),
                Arguments.of(new SortRequest("Other",SortModes.NO))
        );
    }

    // Тестируем StringToSortModesConverter - конвертор значения параметра сортировки товаров,
    // вызываемый в методе ItemController.getItems(). Для разгрузки сервисов замокал userService.findAll
    // Проверяю, что при вызове подается корректно сгенерированный pageable=f(sortmode)
    @ParameterizedTest
    @MethodSource("applySortModeString")
    void testRequestSortParameterConverter(SortRequest sortRequest) throws Exception {
        Sort sortmode = switch (sortRequest.sortModes) {
            case SortModes.PRICE 	-> Sort.by(Sort.Direction.ASC, "price") ;
            case SortModes.ALPHA 	-> Sort.by(Sort.Direction.ASC, "title") ;
            default					-> Sort.unsorted();
        };

        Pageable pageable = PageRequest.of(0, 5, sortmode);
        List<ItemDto> items = new ArrayList<>();
        Page<ItemDto> pagedResponse = new PageImpl<>(items);
        doReturn(pagedResponse).when(userService).findAll(anyLong(), anyString(),any());
        mockMvc.perform(get("/items?search=&sort="+sortRequest.inputString+"&pageNumber=1&pageSize=5"))
                .andExpect(status().isOk());
        verify(userService).findAll(anyLong(), anyString(), eq(pageable));
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
    void testRequestActionParameterConverterAndRedirection(ActionRequest actionRequest) throws Exception {
        doNothing().when(userService).changeInCardCount(anyLong(), anyLong(), eq(actionRequest.actonModes()));
        mockMvc.perform(post("/items")
                    .queryParam("id", "1")
                    .queryParam("search", "")
                    .queryParam("sort", SortModes.ALPHA.toString())
                    .queryParam("pageNumber", "1")
                    .queryParam("pageSize", "5")
                    .queryParam("action", actionRequest.inputString)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attribute("search", ""))
                .andExpect(model().attribute("sort", SortModes.ALPHA.toString()))
                .andExpect(model().attribute("pageNumber", "1"))
                .andExpect(model().attribute("pageSize", "5"));

        verify(userService).changeInCardCount(anyLong(), anyLong(), eq(actionRequest.actonModes()));
    }
}
