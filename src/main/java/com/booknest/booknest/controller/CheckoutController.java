package com.booknest.booknest.controller;

import com.booknest.booknest.dto.OrderResponse;
import com.booknest.booknest.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final OrderService orderService;

    public CheckoutController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(HttpSession session, Authentication auth) {
        return ResponseEntity.ok(orderService.placeOrder(session, auth.getName()));
    }
}
