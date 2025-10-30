package com.example.basicbookstoreprojectnew.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDto(

        @NotBlank(message = "{category.notBlank}")
        String name,
        @Size(max = 255, message = "{category.pattern}")
        String description
) {
}
