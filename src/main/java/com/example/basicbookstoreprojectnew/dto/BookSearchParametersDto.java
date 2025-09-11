package com.example.basicbookstoreprojectnew.dto;

public record BookSearchParametersDto(
        String title,
        String author,
        String isbn
) {}
