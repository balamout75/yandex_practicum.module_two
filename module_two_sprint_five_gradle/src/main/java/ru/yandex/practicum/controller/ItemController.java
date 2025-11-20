package ru.yandex.practicum.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.dto.Paging;
import ru.yandex.practicum.service.ItemService;

@CrossOrigin(maxAge = 3600)
@Controller
@RequestMapping()
class ItemController {

	private final ItemService service;

	private static final String VIEWS_ITEMS_CHART_FORM = "items";
	private static final String VIEWS_ITEMS_ITEM_FORM = "item";

	public ItemController(ItemService service) {
		this.service = service;
	}

	@GetMapping(value={"/","/items"})
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

	@PostMapping(value={"/items"})
	public String postItems(HttpServletRequest request,
							@RequestParam(required = true) long id,
							@RequestParam(defaultValue = "") String search,
							@RequestParam(defaultValue = "NO") String sort,
							@RequestParam(defaultValue = "1") int pageNumber,
							@RequestParam(defaultValue = "5") int pageSize,
							@RequestParam(required = true) String action,
							Model model ){

		switch (action.toLowerCase()) {
			case "minus": System.out.println("minus"); service.changeInCardCount(id, false); break;
			case "plus" : System.out.println("plus"); service.changeInCardCount(id, true); break;
			default		: System.out.println("default");

		};


		return "redirect:/items?search="+search+"&sort="+sort+"&pageNumber="+pageNumber+"&pageSize="+pageSize;
	}

	@GetMapping(value={"/items/{id}"})
	public String getItem(@PathVariable(name = "id") Long id, Model model){
		ItemDto itemDto = service.findById(id);
		model.addAttribute("item", itemDto);
		return VIEWS_ITEMS_ITEM_FORM;
	}

	@PostMapping(value={"/items/{id}"})
	public String controlItem(@PathVariable(name = "id") Long id, @RequestParam(required = true) String action, Model model){
		ItemDto itemDto = service.findById(id);
		model.addAttribute("item", itemDto);
		return VIEWS_ITEMS_ITEM_FORM;
	}

	@PostMapping(value={"/buy"})
	public String buyCart() {

		return "sss";
	}

}
