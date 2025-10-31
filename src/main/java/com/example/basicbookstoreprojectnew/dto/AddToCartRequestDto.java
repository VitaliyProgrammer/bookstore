package com.example.basicbookstoreprojectnew.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddToCartRequestDto(
        @NotNull(message = "{bookId.notNull}")
        Long bookId,

        @Min(value = 1, message = "{quantity.minValue}")
        int quantity
) {
}

