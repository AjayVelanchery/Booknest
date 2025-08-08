package com.booknest.booknest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NotBlank(message = "ISBN is required")
    @Pattern(
            regexp = "^(97(8|9))?\\d{9}(\\d|X)$",
            message = "Invalid ISBN format"
    )
    private String isbn;

    private String genre;
    private String publisher;
    private LocalDate publicationDate;

    @NotNull
    private BigDecimal price;

    private String imageUrl;
    private String imagePublicId;
    private Integer stock;
}
