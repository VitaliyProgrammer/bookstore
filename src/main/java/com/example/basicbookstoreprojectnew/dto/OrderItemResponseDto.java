package com.example.basicbookstoreprojectnew.dto;

import java.math.BigDecimal;

public record OrderItemResponseDto(
        Long id,
        Long bookId,
        String bookTitle,
        int quantity,
        BigDecimal price
) { }

