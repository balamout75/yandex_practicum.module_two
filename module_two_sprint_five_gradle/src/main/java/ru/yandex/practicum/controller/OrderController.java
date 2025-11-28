package ru.yandex.practicum.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
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
        List<OrderDto> orders = userService.findOrders(USER_ID);
        model.addAttribute("orders", orders);
        return VIEW_ORDERS;
    }

    @GetMapping("/{id}")
    public String getOrder(@PathVariable(name = "id") Long orderId, @RequestParam(defaultValue = "false") String newOrder,
                           Model model, HttpServletResponse response){
        if (!userService.existsOrder(USER_ID, orderId)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return "not-found"; // Страница not-found.html
        }
        OrderDto order = userService.findOrder(USER_ID, orderId);
        model.addAttribute("order",     order);
        model.addAttribute("newOrder",  newOrder);
        return VIEW_ORDER;
    }


}
