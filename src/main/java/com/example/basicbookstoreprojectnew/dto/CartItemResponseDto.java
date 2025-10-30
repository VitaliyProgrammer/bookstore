package com.example.basicbookstoreprojectnew.dto;

public record CartItemResponseDto(
        Long id,
        Long bookId,
        String bookTitle,
        int quantity
) {}
