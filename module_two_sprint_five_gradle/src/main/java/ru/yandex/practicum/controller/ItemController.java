package ru.yandex.practicum.controller;

import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.dto.Paging;
import ru.yandex.practicum.mapping.ActionModes;
import ru.yandex.practicum.mapping.SortModes;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.ItemService;


@CrossOrigin(maxAge = 3600)
@Controller
@RequestMapping("/items")
class ItemController {

	private final ItemService 	itemService;
	private static final String VIEWS_ITEMS_CHART_FORM = "items";
	private static final String VIEWS_ITEMS_ITEM_FORM = "item";
	private static final long 	USER_ID = 1;
	private final CartService cartService;

	ItemController(ItemService itemService, CartService cartService) {
        this.itemService = itemService;
		this.cartService = cartService;
	}

	@GetMapping()
	public String getItems(	@RequestParam(defaultValue = "") String search,
							@RequestParam(name = "sort", defaultValue = "NO") SortModes sort,
							@RequestParam(defaultValue = "1") int pageNumber,
							@RequestParam(defaultValue = "5") int pageSize, Model model ){
		Sort sortmode = switch (sort) {
			case SortModes.PRICE 	-> Sort.by(Sort.Direction.ASC, "price") ;
			case SortModes.ALPHA 	-> Sort.by(Sort.Direction.ASC, "title") ;
			default					-> Sort.unsorted();
		};
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize, sortmode);
		Page<ItemDto> paged = itemService.findAll(USER_ID,search,pageable);
		List<ItemDto> itemsList = new ArrayList<>(paged.getContent());
		while ((itemsList.size() % 3) !=0 ) { itemsList.add(new ItemDto());}
		List<List<ItemDto>> itemsTupleList = ListUtils.partition(itemsList, 3);

        model.addAttribute("items", itemsTupleList);
		model.addAttribute("search", search);
		model.addAttribute("sort", sort);
		model.addAttribute("paging", new Paging(pageSize, pageNumber, false, false));
		return VIEWS_ITEMS_CHART_FORM;
	}

	@PostMapping()
	public String postItems(@RequestParam(name = "id") long itemId,
							@RequestParam(defaultValue = "") String search,
							@RequestParam(defaultValue = "NO") String sort,
							@RequestParam(defaultValue = "1") int pageNumber,
							@RequestParam(defaultValue = "5") int pageSize,
							@RequestParam() ActionModes action,
							RedirectAttributes redirectAttributes  ){

		cartService.changeInCardCount(USER_ID, itemId, action);
		redirectAttributes.addAttribute("search", search);
		redirectAttributes.addAttribute("sort", sort);
		redirectAttributes.addAttribute("pageNumber", pageNumber);
		redirectAttributes.addAttribute("pageSize", pageSize);
		return "redirect:/items?search={search}&sort={sort}&pageNumber={pageNumber}&pageSize={pageSize}";
	}

	@GetMapping(value={"/{id}"})
	public String getItem(@PathVariable(name = "id") Long itemId, Model model, HttpServletResponse response){
		if (!itemService.exists(USER_ID, itemId)) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return "not-found"; // Страница not-found.html
		}
		ItemDto itemDto = itemService.findItem(USER_ID, itemId);
		model.addAttribute("item", itemDto);
		return VIEWS_ITEMS_ITEM_FORM;
	}
}
