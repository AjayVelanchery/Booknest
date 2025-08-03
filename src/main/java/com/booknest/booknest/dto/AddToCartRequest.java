package com.booknest.booknest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartRequest {
    @NotNull
    private Long bookId;

    @Min(1)
    private int quantity;
}
