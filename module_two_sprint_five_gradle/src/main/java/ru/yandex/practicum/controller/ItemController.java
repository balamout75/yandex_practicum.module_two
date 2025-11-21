package ru.yandex.practicum.controller;

import java.util.ArrayList;
import java.util.List;


import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.dto.Paging;
import ru.yandex.practicum.service.ItemService;

@CrossOrigin(maxAge = 3600)
@Controller
@RequestMapping("/items")
class ItemController {

	private final ItemService service;

	private static final String VIEWS_ITEMS_CHART_FORM = "items";
	private static final String VIEWS_ITEMS_ITEM_FORM = "item";
	private static final long USER_ID = 1;

	public ItemController(ItemService service) {
		this.service = service;
	}

	@GetMapping()
	public String getItems(	@RequestParam(defaultValue = "") String search,
						   	@RequestParam(defaultValue = "NO") String sort,
							@RequestParam(defaultValue = "1") int pageNumber,
							@RequestParam(defaultValue = "5") int pageSize, Model model ){
		Sort sortmode = switch (sort.toLowerCase()) {
			case "price" 	-> Sort.by(Sort.Direction.ASC, "price") ;
			case "alpha" 	-> Sort.by(Sort.Direction.ASC, "title") ;
			default			-> Sort.unsorted();
		};

		Pageable pageable = PageRequest.of(pageNumber-1, pageSize, sortmode);
		Page<ItemDto> paged = service.findAll(search,pageable);
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
	public String postItems(HttpServletRequest request,
							@RequestParam() long id,
							@RequestParam(defaultValue = "") String search,
							@RequestParam(defaultValue = "NO") String sort,
							@RequestParam(defaultValue = "1") int pageNumber,
							@RequestParam(defaultValue = "5") int pageSize,
							@RequestParam() String action,
							RedirectAttributes redirectAttributes  ){

		switch (action.toLowerCase()) {
			case "minus": System.out.println("minus"); service.changeInCardCount(USER_ID, id, false); break;
			case "plus" : System.out.println("plus");  service.changeInCardCount(USER_ID, id, true); break;
			default		: System.out.println("default");

		};
		redirectAttributes.addAttribute("search", search);
		redirectAttributes.addAttribute("sort", sort);
		redirectAttributes.addAttribute("pageNumber", pageNumber);
		redirectAttributes.addAttribute("pageSize", pageSize);
		return "redirect:/items?search={search}&sort={sort}&pageNumber={pageNumber}&pageSize={pageSize}";
	}

	@GetMapping(value={"/{id}"})
	public String getItem(@PathVariable(name = "id") Long id, Model model){
		ItemDto itemDto = service.findById(id);
		model.addAttribute("item", itemDto);
		return VIEWS_ITEMS_ITEM_FORM;
	}
}
