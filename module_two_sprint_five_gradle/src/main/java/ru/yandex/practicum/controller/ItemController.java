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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
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



	/*@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable(name = "ownerId", required = false) @Nullable Integer ownerId) {
		return ownerId == null ? new Owner()
				: this.owners.findById(ownerId)
					.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId
							+ ". Please ensure the ID is correct " + "and the owner exists in the database."));
	}
	*/


	@GetMapping(value={"/","/items"})
	public String getItems(	@RequestParam(defaultValue = "") String search,
						   	@RequestParam(defaultValue = "NO") String sort,
							@RequestParam(defaultValue = "1") int pageNumber,
							@RequestParam(defaultValue = "5") int pageSize, Model model ){

		Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
		Page<ItemDto> paged = service.findAll(search,pageable);
		List<ItemDto> itemsList = new ArrayList<>(paged.getContent ());
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
							@RequestParam(defaultValue = "") String search,
							@RequestParam(defaultValue = "NO") String sort,
							@RequestParam(defaultValue = "1") int pageNumber,
							@RequestParam(defaultValue = "5") int pageSize,
							@RequestParam(required = true) String action,
							Model model ){
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

	/*
	@PostMapping("/owners/new")
	public String processCreationForm(@Valid Owner owner, BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in creating the owner.");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "New Owner Created");
		return "redirect:/owners/" + owner.getId();
	}

	@GetMapping("/owners/find")
	public String initFindForm() {
		return "owners/findOwners";
	}

	@GetMapping("/owners")
	public String processFindForm(@RequestParam(defaultValue = "1") int page, Owner owner, BindingResult result,
			Model model) {
		// allow parameterless GET request for /owners to return all records
		String lastName = owner.getLastName();
		if (lastName == null) {
			lastName = ""; // empty string signifies broadest possible search
		}

		// find owners by last name
		Page<Owner> ownersResults = findPaginatedForOwnersLastName(page, lastName);
		if (ownersResults.isEmpty()) {
			// no owners found
			result.rejectValue("lastName", "notFound", "not found");
			return "owners/findOwners";
		}

		if (ownersResults.getTotalElements() == 1) {
			// 1 owner found
			owner = ownersResults.iterator().next();
			return "redirect:/owners/" + owner.getId();
		}

		// multiple owners found
		return addPaginationModel(page, model, ownersResults);
	}

	private String addPaginationModel(int page, Model model, Page<Owner> paginated) {
		List<Owner> listOwners = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listOwners", listOwners);
		return "owners/ownersList";
	}

	private Page<Owner> findPaginatedForOwnersLastName(int page, String lastname) {
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return owners.findByLastNameStartingWith(lastname, pageable);
	}

	@GetMapping("/owners/{ownerId}/edit")
	public String initUpdateOwnerForm() {
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/{ownerId}/edit")
	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result, @PathVariable("ownerId") int ownerId,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in updating the owner.");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		if (!Objects.equals(owner.getId(), ownerId)) {
			result.rejectValue("id", "mismatch", "The owner ID in the form does not match the URL.");
			redirectAttributes.addFlashAttribute("error", "Owner ID mismatch. Please try again.");
			return "redirect:/owners/{ownerId}/edit";
		}

		owner.setId(ownerId);
		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "Owner Values Updated");
		return "redirect:/owners/{ownerId}";
	}


	@GetMapping("/owners/{ownerId}")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		ModelAndView mav = new ModelAndView("owners/ownerDetails");
		Optional<Owner> optionalOwner = this.owners.findById(ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
		mav.addObject(owner);
		return mav;
	}


	 */

}
