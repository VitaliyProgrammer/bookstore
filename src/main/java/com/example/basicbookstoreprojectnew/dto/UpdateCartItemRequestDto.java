package com.example.basicbookstoreprojectnew.dto;

import jakarta.validation.constraints.Min;

public record UpdateCartItemRequestDto(
        @Min(value = 1, message = "{quantity.minValue}")
        int quantity
) {}
