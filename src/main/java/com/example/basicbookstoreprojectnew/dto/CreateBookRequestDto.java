package com.example.basicbookstoreprojectnew.dto;

public record CreateBookRequestDto(
        String title,
        String author,
        String description,
        String isbn,
        Integer price,
        String coverImage
) {}
