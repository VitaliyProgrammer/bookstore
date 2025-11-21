package com.example.basicbookstoreprojectnew.shoppingcart;

import com.example.basicbookstoreprojectnew.controller.ShoppingCartController;
import com.example.basicbookstoreprojectnew.dto.AddToCartRequestDto;
import com.example.basicbookstoreprojectnew.dto.CartItemResponseDto;
import com.example.basicbookstoreprojectnew.dto.ShoppingCartResponseDto;
import com.example.basicbookstoreprojectnew.dto.UpdateCartItemRequestDto;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.CartItem;
import com.example.basicbookstoreprojectnew.model.ShoppingCart;
import com.example.basicbookstoreprojectnew.model.User;
import com.example.basicbookstoreprojectnew.model.service.ShoppingCartService;
import com.example.basicbookstoreprojectnew.security.AuthenticationUtil;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;


import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShoppingCartController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ShoppingCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShoppingCartService shoppingCartService;

    @MockBean
    private AuthenticationUtil authenticationUtil;

    private User user;
    private Book book;
    private ShoppingCart shoppingCart;
    private CartItem cartItem;
    private CartItemResponseDto cartItemResponse;
    private CartItemResponseDto updatedCartItemResponse;
    private CartItemResponseDto newCartItemResponse;
    private ShoppingCartResponseDto shoppingCartResponse;
    private ShoppingCartResponseDto updatedShoppingCartResponse;
    private ShoppingCartResponseDto updatedNewCartItemResponse;
    private Authentication authentication;

    private AddToCartRequestDto addBookToShoppingCart;

    private UpdateCartItemRequestDto updateCartItem;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);

        book = new Book();
        book.setId(50L);
        book.setTitle("Testing book");
        book.setAuthor("Testing author");

        shoppingCart = new ShoppingCart();
        shoppingCart.setId(10L);
        shoppingCart.setUser(user);

        cartItem = new CartItem();
        cartItem.setId(70L);
        cartItem.setBook(book);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setQuantity(5);

        cartItemResponse = new CartItemResponseDto(
                cartItem.getId(),
                book.getId(),
                book.getTitle(),
                cartItem.getQuantity()
        );

        updatedCartItemResponse = new CartItemResponseDto(
                cartItem.getId(),
                book.getId(),
                book.getTitle(),
                10
        );

        newCartItemResponse = new CartItemResponseDto(
                123L,
                book.getId(),
                book.getTitle(),
                5
        );

        updatedNewCartItemResponse = new ShoppingCartResponseDto(
                shoppingCart.getId(),
                user.getId(),
                List.of(newCartItemResponse)
        );

        shoppingCartResponse = new ShoppingCartResponseDto(
                shoppingCart.getId(),
                user.getId(),
                List.of(cartItemResponse)
        );

        updatedShoppingCartResponse = new ShoppingCartResponseDto(
                shoppingCart.getId(),
                user.getId(),
                List.of(updatedCartItemResponse)
        );

        addBookToShoppingCart = new AddToCartRequestDto(book.getId(), 5);

        updateCartItem = new UpdateCartItemRequestDto(10);

        authentication = mock(Authentication.class);
        when(authenticationUtil.getCurrentUser(authentication)).thenReturn(user);
    }


    @Test
    @DisplayName("GET /cart: should return existing shopping cart")
    void getShoppingCart_IsExist() throws Exception {

        when(shoppingCartService.getShoppingCartByUser(user.getId()))
                .thenReturn(shoppingCartResponse);

        mockMvc.perform(get("/cart")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shoppingCartResponse.id()))
                .andExpect(jsonPath("$.userId").value(shoppingCartResponse.userId()))
                .andExpect(jsonPath("$.cartItems[0].id").value(cartItemResponse.id()))
                .andExpect(jsonPath("$.cartItems[0].bookId")
                        .value(cartItemResponse.bookId()))
                .andExpect(jsonPath("$.cartItems[0].bookTitle")
                        .value(cartItemResponse.bookTitle()))
                .andExpect(jsonPath("$.cartItems[0].quantity")
                        .value(cartItemResponse.quantity()));

        verify(shoppingCartService, times(1))
                .getShoppingCartByUser(user.getId());
    }

    @Test
    @DisplayName("POST /cart: add book to shopping cart")
    void addBookToShoppingCart() throws Exception {

        when(shoppingCartService.addBookToShoppingCart(user.getId(), book.getId(), 5))
                .thenReturn(updatedNewCartItemResponse);

        mockMvc.perform(post("/cart")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(addBookToShoppingCart)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedNewCartItemResponse.id()))
                .andExpect(jsonPath("$.userId")
                        .value(updatedNewCartItemResponse.userId()))
                .andExpect(jsonPath("$.cartItems[0].id")
                        .value(newCartItemResponse.id()))
                .andExpect(jsonPath("$.cartItems[0].bookId")
                        .value(newCartItemResponse.bookId()))
                .andExpect(jsonPath("$.cartItems[0].bookTitle")
                        .value(newCartItemResponse.bookTitle()))
                .andExpect(jsonPath("$.cartItems[0].quantity")
                        .value(newCartItemResponse.quantity()));

        verify(shoppingCartService, times(1))
                .addBookToShoppingCart(user.getId(), book.getId(), 5);
    }

    @Test
    @DisplayName("PUT /cart/cart-items/{id}: update cart item quantity")
    void updateCartItemQuantity() throws Exception {

        when(shoppingCartService.updateCartItemQuantity(cartItem.getId(), 10))
                .thenReturn(updatedCartItemResponse);

        mockMvc.perform(put("/cart/cart-items/{id}", cartItem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateCartItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedCartItemResponse.id()))
                .andExpect(jsonPath("$.bookId").value(updatedCartItemResponse.bookId()))
                .andExpect(jsonPath("$.bookTitle")
                        .value(updatedCartItemResponse.bookTitle()))
                .andExpect(jsonPath("$.quantity")
                        .value(updatedCartItemResponse.quantity()));

        verify(shoppingCartService, times(1))
                .updateCartItemQuantity(cartItem.getId(), 10);
    }


    @Test
    @DisplayName("DELETE /cart/cart-items/{id}: remove item from cart")
    void removeCartItem() throws Exception {

        doNothing().when(shoppingCartService).removeCartItem(cartItem.getId());

        mockMvc.perform(delete("/cart/cart-items/{id}", cartItem.getId()))
                .andExpect(status().isNoContent());

        verify(shoppingCartService, times(1))
                .removeCartItem(cartItem.getId());
    }

    private static String asJsonString(Object object) {
         try {
             return new ObjectMapper().writeValueAsString(object);
         } catch (JsonProcessingException exception) {
             throw new RuntimeException(exception);
         }
    }
}

