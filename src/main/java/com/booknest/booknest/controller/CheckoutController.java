package com.booknest.booknest.controller;

import com.booknest.booknest.dto.OrderRequest;
import com.booknest.booknest.dto.OrderResponse;
import com.booknest.booknest.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(HttpSession session,
                                        @RequestBody @Valid OrderRequest orderRequest,
                                        @AuthenticationPrincipal(expression = "username") String username) {
        try {
            OrderResponse response = orderService.placeOrder(session, username);
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
