package com.booknest.booknest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartRequest {
    @NotNull
    private Long bookId;

    @Min(0)
    private int quantity;
}
