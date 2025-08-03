package com.booknest.booknest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemoveFromCartRequest {
    @NotNull
    private Long bookId;
}
