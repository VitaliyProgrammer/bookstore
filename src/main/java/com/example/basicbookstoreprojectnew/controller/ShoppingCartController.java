package com.example.basicbookstoreprojectnew.controller;

import com.example.basicbookstoreprojectnew.dto.AddToCartRequestDto;
import com.example.basicbookstoreprojectnew.dto.CartItemResponseDto;
import com.example.basicbookstoreprojectnew.dto.ShoppingCartResponseDto;
import com.example.basicbookstoreprojectnew.dto.UpdateCartItemRequestDto;
import com.example.basicbookstoreprojectnew.model.User;
import com.example.basicbookstoreprojectnew.model.service.ShoppingCartService;
import com.example.basicbookstoreprojectnew.security.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "Shopping cart management",
        description = "Endpoints for shopping cart information")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    private final AuthenticationUtil authenticationUtil;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get information for shopping cart",
            description = "See all information user about all orders")
    public ShoppingCartResponseDto getShoppingCart(Authentication authentication) {

        User user = authenticationUtil.getCurrentUser(authentication);
        return shoppingCartService.getShoppingCartByUser(user.getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Add a new book",
            description = "Insert a new book to the shopping cart")
    public ShoppingCartResponseDto addBookToCart(Authentication authentication,
                                                 @RequestBody AddToCartRequestDto request) {

        User user = authenticationUtil.getCurrentUser(authentication);

        return shoppingCartService.addBookToShoppingCart(
                user.getId(),
                request.bookId(),
                request.quantity());
    }

    @PutMapping("/cart-items/{carItemId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Change information in the shopping cart",
            description = "Update some information for an order in the shopping cart")
    public CartItemResponseDto updateCartItemQuantity(
            @PathVariable Long carItemId, @Valid @RequestBody UpdateCartItemRequestDto request) {

        return shoppingCartService.updateCartItemQuantity(carItemId, request.quantity());
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Cancel for buy book in the shopping cart",
            description = "Remove book for shopping cart")
    public void removeItem(@PathVariable Long cartItemId) {

        shoppingCartService.removeCartItem(cartItemId);
    }
}

