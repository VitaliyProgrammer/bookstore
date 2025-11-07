package com.example.basicbookstoreprojectnew.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderRequestDto(

        @NotBlank(message = "{shippingAddress.notBlank}")
        String shippingAddress
) { }

