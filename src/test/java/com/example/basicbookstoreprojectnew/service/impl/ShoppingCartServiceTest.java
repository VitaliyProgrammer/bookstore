package com.example.basicbookstoreprojectnew.service.impl;

import com.example.basicbookstoreprojectnew.dto.CartItemResponseDto;
import com.example.basicbookstoreprojectnew.dto.ShoppingCartResponseDto;
import com.example.basicbookstoreprojectnew.exception.ShoppingCartNotFoundException;
import com.example.basicbookstoreprojectnew.exception.UserNotFoundException;
import com.example.basicbookstoreprojectnew.mapper.CartItemMapper;
import com.example.basicbookstoreprojectnew.mapper.ShoppingCartMapper;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.CartItem;
import com.example.basicbookstoreprojectnew.model.ShoppingCart;
import com.example.basicbookstoreprojectnew.model.User;
import com.example.basicbookstoreprojectnew.repository.BookRepository;
import com.example.basicbookstoreprojectnew.repository.CartItemRepository;
import com.example.basicbookstoreprojectnew.repository.ShoppingCartRepository;
import com.example.basicbookstoreprojectnew.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartServiceImpl;

    private User user;

    private Book book;

    private ShoppingCart shoppingCart;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");

        book = new Book();
        book.setId(10L);
        book.setTitle("Book title");

        shoppingCart = new ShoppingCart();
        shoppingCart.setId(100L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new ArrayList<>());
    }

    @Test
    @DisplayName("createShoppingCart - creates new shopping cart")
    void createShoppingCart() {

        when(shoppingCartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.save(any())).thenReturn(shoppingCart);

        ShoppingCartResponseDto shoppingCartResponse = new ShoppingCartResponseDto(
                100L, 1L, List.of()
        );

        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartResponse);

        ShoppingCartResponseDto result = shoppingCartServiceImpl.getShoppingCartByUser(1L);

        assertThat(result.id()).isEqualTo(shoppingCartResponse.id());
        assertThat(result.userId()).isEqualTo(shoppingCartResponse.userId());

        verify(shoppingCartRepository).save(any());
    }

    @Test
    @DisplayName("createShoppingCart - return existing shopping cart")
    void getShoppingCartByUserId() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));

        ShoppingCartResponseDto shoppingCartResponse =
                new ShoppingCartResponseDto(shoppingCart.getId(), user.getId(), List.of());

        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartResponse);

        ShoppingCartResponseDto result =
                shoppingCartServiceImpl.getShoppingCartByUser(user.getId());

        assertThat(result.id()).isEqualTo(shoppingCart.getId());
    }

    @Test
    @DisplayName("getShoppingCartByUserNotFound - throws exception, user not found")
    void getShoppingCartByUserNotFound() {

        when(userRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> shoppingCartServiceImpl.getShoppingCartByUser(9999L));
    }

    @Test
    @DisplayName("addBook_newCartItem - adds new book into cart item")
    void addBook_newCartItem() {

        shoppingCart.setCartItems(new ArrayList<>());

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        CartItem savedCartItem = new CartItem();
        savedCartItem.setBook(book);
        savedCartItem.setQuantity(5);
        savedCartItem.setId(60L);

        when(cartItemRepository.save(any())).thenReturn(savedCartItem);

        CartItemResponseDto cartItemResponse = new CartItemResponseDto(
                60L, 10L, "Clean code", 5
        );

        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(new ShoppingCartResponseDto(
                shoppingCart.getId(), user.getId(), List.of(cartItemResponse))
        );

        ShoppingCartResponseDto shoppingCartResponse =
                shoppingCartServiceImpl.addBookToShoppingCart(
                        user.getId(), book.getId(), 5);

        assertThat(shoppingCartResponse.cartItems()).hasSize(1);
        assertThat(shoppingCartResponse.cartItems().get(0).id()).isEqualTo(cartItemResponse.id());

        verify(cartItemRepository).save(any());
    }

    @Test
    @DisplayName("addBookIncreaseQuantity - increases quantity of existing cart item")
    void addBookIncreaseQuantity() {

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        CartItem cartItem = new CartItem();
        cartItem.setId(200L);
        cartItem.setBook(book);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setQuantity(2);

        shoppingCart.getCartItems().add(cartItem);

        CartItemResponseDto cartItemResponse = new CartItemResponseDto(
                cartItem.getId(), book.getId(), book.getTitle(), cartItem.getQuantity() + 5);

        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(
                new ShoppingCartResponseDto(shoppingCart.getId(), user.getId(),
                        List.of(cartItemResponse)));

        ShoppingCartResponseDto result =
                shoppingCartServiceImpl.addBookToShoppingCart(user.getId(), book.getId(), 5);

        assertThat(result.cartItems().get(0).quantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("addBook_shoppingCartNotFound - shopping cart not found, throws exception")
    void addBook_shoppingCartNotFound() {

        assertThrows(ShoppingCartNotFoundException.class,
                () -> shoppingCartServiceImpl.addBookToShoppingCart(
                        user.getId(), book.getId(), 1));
    }


    @Test
    @DisplayName("addBook_notFound - books not found, throws exception")
    void addBook_notFound() {

        assertThrows(ShoppingCartNotFoundException.class,
                () -> shoppingCartServiceImpl.addBookToShoppingCart(
                        user.getId(), 9999L, 1));
    }

    @Test
    @DisplayName("updateCartItemQuantity - updates properly")
    void updateCartItem() {

        CartItem cartItem = new CartItem();
        cartItem.setId(200L);
        cartItem.setQuantity(3);

        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));

        CartItemResponseDto cartItemResponse = new CartItemResponseDto(
                cartItem.getId(), book.getId(), "Clean code", 5
        );

        when(cartItemMapper.toDto(cartItem)).thenReturn(cartItemResponse);

        CartItemResponseDto updatedCartItemQuantity =
                shoppingCartServiceImpl.updateCartItemQuantity(cartItem.getId(), 5);

        assertThat(updatedCartItemQuantity.quantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("updateCartItemQuantity - cart item not found")
    void updateCartItem_notFound() {

        when(cartItemRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(ShoppingCartNotFoundException.class,
                () -> shoppingCartServiceImpl.updateCartItemQuantity(9999L, 5));
    }

    @Test
    @DisplayName("removeCartItem - delete, success")
    void removeCartItem() {

        CartItem cartItem = new CartItem();
        cartItem.setId(200L);

        when(cartItemRepository.existsById(cartItem.getId())).thenReturn(true);

        shoppingCartServiceImpl.removeCartItem(cartItem.getId());

        verify(cartItemRepository).deleteById(cartItem.getId());
    }

    @Test
    @DisplayName("removeCartItem - cart item not found, throws exception")
    void removeCartItem_notFound() {

        assertThrows(ShoppingCartNotFoundException.class,
                () -> shoppingCartServiceImpl.removeCartItem(9999L));
    }
}
