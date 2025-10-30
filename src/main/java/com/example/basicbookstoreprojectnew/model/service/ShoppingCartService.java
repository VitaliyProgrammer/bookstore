package com.example.basicbookstoreprojectnew.model.service;

import com.example.basicbookstoreprojectnew.dto.CartItemResponseDto;
import com.example.basicbookstoreprojectnew.dto.ShoppingCartResponseDto;

public interface ShoppingCartService {

    ShoppingCartResponseDto getShoppingCartByUser(Long userId);

    ShoppingCartResponseDto addBookToShoppingCart(Long id, Long bookId, int quantity);

    CartItemResponseDto updateCartItemQuantity(Long cartItemId, int quantity);

    void removeCartItem(Long cartItem);
}

