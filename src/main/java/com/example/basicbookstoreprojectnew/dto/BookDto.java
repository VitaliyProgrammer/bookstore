package com.example.basicbookstoreprojectnew.dto;

import java.math.BigDecimal;

public record BookDto(
        Long id,
        String title,
        String author,
        String description,
        String isbn,
        BigDecimal price,
        String coverImage
) {
}
