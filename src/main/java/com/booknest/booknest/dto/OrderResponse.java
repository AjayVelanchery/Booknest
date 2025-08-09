package com.booknest.booknest.dto;

import com.booknest.booknest.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String currency;
    private LocalDateTime createdAt;
    private String message;

    private String razorpayOrderId;
}
