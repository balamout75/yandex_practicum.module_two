package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.service.OrderService;
import ru.yandex.practicum.service.UserService;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@Controller
@RequestMapping("/orders")
class OrderController {

    private final OrderService service;
    private final UserService userService;

    private static final String VIEW_ORDERS = "orders";
    private static final String VIEW_ORDER  = "order";
    private static final long USER_ID = 1;

    public OrderController(OrderService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping()
    public String getOrders( Model model ){
        List<OrderDto> convertedOrders = userService.findOrders(USER_ID);
        model.addAttribute("orders", convertedOrders);
        return VIEW_ORDERS;
    }

    @GetMapping("/{id}")
    public String getOrder(@PathVariable(name = "id") Long id, @RequestParam(defaultValue = "false") String action, Model model ){
        OrderDto orderDto = service.findOrder(USER_ID,id);
        model.addAttribute("order",     orderDto);
        model.addAttribute("newOrder",  action);
        return VIEW_ORDER;
    }


}
