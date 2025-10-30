package com.example.basicbookstoreprojectnew.dto;

import java.util.List;

public record ShoppingCartResponseDto(
        Long id,
        Long userId,
        List<CartItemResponseDto> cartItems
) {}

