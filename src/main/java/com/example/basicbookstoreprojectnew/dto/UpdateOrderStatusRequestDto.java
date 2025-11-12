package com.example.basicbookstoreprojectnew.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequestDto(

        @NotNull(message = "{status.notNull}")
        String status,
        String shippingAddress
) { }

