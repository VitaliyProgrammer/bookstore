package com.example.basicbookstoreprojectnew.dto;

public record BookDto(
        Long id,
        String title,
        String author,
        String description,
        String isbn,
        Integer price,
        String coverImage
) {
}
