package ru.yandex.practicum.controller;

import org.apache.commons.collections4.ListUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.dto.Paging;
import ru.yandex.practicum.mapper.SortModes;
import ru.yandex.practicum.service.ItemService;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private static final String VIEWS_ITEMS_CHART_FORM = "items";
    private static final String VIEWS_ITEMS_ITEM_FORM = "item";
    private static final long 	USER_ID = 1;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    // Список пользователей
    @GetMapping
    public Mono<Rendering> list(@RequestParam(defaultValue = "") String search,
                                @RequestParam(name = "sort", defaultValue = "NO") SortModes sort,
                                @RequestParam(defaultValue = "1") int pageNumber,
                                @RequestParam(defaultValue = "5") int pageSize, Model model) {
        Sort sortmode = switch (sort) {
            case SortModes.PRICE 	-> Sort.by(Sort.Direction.ASC, "price") ;
            case SortModes.ALPHA 	-> Sort.by(Sort.Direction.ASC, "title") ;
            default					-> Sort.unsorted();
        };
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize, sortmode);

        model.addAttribute("items",  itemService.findAll(pageable).collectList()
                                                        .map(items -> {
                                                                while ((items.size() % 3) !=0 ) { items.add(new ItemDto());}
                                                                return ListUtils.partition(items, 3);
                                                        }));
        model.addAttribute("search", search);
        model.addAttribute("sort",   sort);
        model.addAttribute("paging", new Paging(pageSize, pageNumber, false, false));
        return Mono.just(Rendering.view(VIEWS_ITEMS_CHART_FORM)
                        .build()
        );
    }

    @GetMapping(value={"/{id}"})
    public Mono<Rendering> getItem(@PathVariable(name = "id") Long itemId) {
        return itemService.findItem(USER_ID, itemId)
                .map(u -> Rendering.view(VIEWS_ITEMS_ITEM_FORM)
                        .modelAttribute("item", u)
                        .build())
                .switchIfEmpty(Mono.just(Rendering.redirectTo(VIEWS_ITEMS_CHART_FORM).build()));
    }
    /*
    // Карточка пользователя
    @GetMapping("/{id}")
    public Mono<Rendering> view(@PathVariable Long id) {
        return service.findById(id)
                .map(u -> Rendering.view("users/view")
                        .modelAttribute("user", u)
                        .build())
                .switchIfEmpty(Mono.just(Rendering.redirectTo("/users").build()));
    }


    // Форма создания
    @GetMapping("/new")
    public Mono<Rendering> createForm() {
        return Mono.just(
                Rendering.view("users/form")
                        .modelAttribute("user", new User())
                        .build()
        );
    }

    // Создать
    @PostMapping
    public Mono<String> create(@ModelAttribute("user") User user,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Mono.just("users/form");
        }
        return service.create(user).map(saved -> "redirect:/users/" + saved.getId());
    }

    // Форма редактирования
    @GetMapping("/{id}/edit")
    public Mono<Rendering> editForm(@PathVariable Long id) {
        return service.findById(id)
                .map(u -> Rendering.view("users/form")
                        .modelAttribute("user", u)
                        .build())
                .switchIfEmpty(Mono.just(Rendering.redirectTo("/users").build()));
    }

    // Обновить
    @PostMapping("/{id}")
    public Mono<String> update(@PathVariable Long id,
                               @ModelAttribute("user") User user,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Mono.just("users/form");
        }
        return service.update(id, user).map(updated -> "redirect:/users/" + id);
    }

    // Удалить
    @PostMapping("/{id}/delete")
    public Mono<String> delete(@PathVariable Long id) {
        return service.delete(id).thenReturn("redirect:/users");
    }

    // Загрузка фото в БД
    @PostMapping(path = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> uploadPhoto(@PathVariable Long id,
                                    @RequestPart("photo") FilePart photo) {
        return photoService.savePhoto(id, photo).thenReturn("redirect:/users/" + id);
    }

    @GetMapping(value = "/{id}/photo", produces = MediaType.ALL_VALUE)
    public Mono<ResponseEntity<byte[]>> getPhoto(@PathVariable Long id) {
        return photoService.getPhoto(id)
                .map(photo -> {
                    MediaType mediaType;
                    try {
                        mediaType = MediaType.parseMediaType(photo.getContentType());
                        if (!"image".equalsIgnoreCase(mediaType.getType())) {
                            mediaType = MediaType.IMAGE_PNG;
                        }
                    } catch (Exception exception) {
                        mediaType = MediaType.IMAGE_PNG;
                    }
                    return ResponseEntity.ok()
                            .contentType(mediaType)
                            .body(photo.getData());
                })
                .defaultIfEmpty(ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(PNG_PLACEHOLDER));
    }*/
}