package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.service.OrderService;
import java.util.List;

@CrossOrigin(maxAge = 3600)
@Controller
@RequestMapping("/orders")
class OrderController {

    private final OrderService service;

    private static final String VIEW_ORDERS = "orders";
    private static final String VIEW_ORDER  = "order";
    private static final long USER_ID = 1;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping()
    public String getOrders( Model model ){
        List<OrderDto> convertedOrders = service.findOrders(USER_ID);
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
