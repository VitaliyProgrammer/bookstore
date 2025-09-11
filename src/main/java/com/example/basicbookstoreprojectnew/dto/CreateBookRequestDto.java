package com.example.basicbookstoreprojectnew.dto;

import java.math.BigDecimal;

public record CreateBookRequestDto(
        String title,
        String author,
        String description,
        String isbn,
        BigDecimal price,
        String coverImage
) {}
