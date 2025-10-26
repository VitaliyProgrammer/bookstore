package com.example.basicbookstoreprojectnew.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record CreateBookRequestDto(
        @NotBlank(message = "{title.notBlank}")
        String title,
        @NotBlank(message = "{author.notBlank}")
        String author,
        @NotBlank(message = "{description.notBlank}")
        @Size(min = 2, max = 500, message = "{description.pattern}")
        String description,
        @NotBlank(message = "{isbn.notBlank}")
        @Pattern(regexp = "^[0-9]{13}$", message = "{isbn.size}")
        String isbn,

        @NotNull(message = "{price.notNull}")
        @DecimalMin(value = "0.00", inclusive = false, message = "{price.decimalMin}")
        @Digits(integer = 8, fraction = 2, message = "{price.digits}")
        BigDecimal price,
        @Pattern(regexp = "^(http|https)://.*$", message = "{coverImage.pattern}")
        String coverImage,

        @NotNull(message = "{category.notBlank}")
        @Size(min = 1, message = "{category.pattern}")
        List<Long> categoryIds
) {
}
