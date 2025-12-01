package ru.yandex.practicum.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
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

    private final OrderService orderService;

    private static final String VIEW_ORDERS = "orders";
    private static final String VIEW_ORDER  = "order";
    private static final long USER_ID = 1;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping()
    public String getOrders( Model model ){
        List<OrderDto> orders = orderService.findOrders(USER_ID);
        model.addAttribute("orders", orders);
        return VIEW_ORDERS;
    }

    @GetMapping("/{id}")
    public String getOrder(@PathVariable(name = "id") Long orderId, @RequestParam(defaultValue = "false") String newOrder,
                           Model model, HttpServletResponse response){
        if (!orderService.exists(USER_ID, orderId)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return "not-found"; // Страница not-found.html
        }
        OrderDto order = orderService.findOrder(USER_ID, orderId);
        model.addAttribute("order",     order);
        model.addAttribute("newOrder",  newOrder);
        return VIEW_ORDER;
    }


}
