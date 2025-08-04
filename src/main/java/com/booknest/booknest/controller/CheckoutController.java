package com.booknest.booknest.controller;

import com.booknest.booknest.dto.OrderRequest;
import com.booknest.booknest.dto.OrderResponse;
import com.booknest.booknest.entity.Order;
import com.booknest.booknest.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(HttpSession session, @RequestBody @Valid OrderRequest orderRequest) {
        try {
            Order order = orderService.placeOrder(session);

            OrderResponse response = new OrderResponse(
                    order.getId(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    order.getCurrency(),
                    order.getCreatedAt(),
                    "Order placed successfully!"
            );

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    private record ErrorResponse(String error) {}
}
