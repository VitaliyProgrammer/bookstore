package com.example.basicbookstoreprojectnew.shoppingcart;

import com.example.basicbookstoreprojectnew.dto.CartItemResponseDto;
import com.example.basicbookstoreprojectnew.dto.ShoppingCartResponseDto;
import com.example.basicbookstoreprojectnew.exception.EntityNotFoundException;
import com.example.basicbookstoreprojectnew.exception.ShoppingCartNotFoundException;
import com.example.basicbookstoreprojectnew.mapper.CartItemMapper;
import com.example.basicbookstoreprojectnew.mapper.ShoppingCartMapper;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.CartItem;
import com.example.basicbookstoreprojectnew.model.ShoppingCart;
import com.example.basicbookstoreprojectnew.model.User;
import com.example.basicbookstoreprojectnew.model.repository.BookRepository;
import com.example.basicbookstoreprojectnew.model.repository.CartItemRepository;
import com.example.basicbookstoreprojectnew.model.repository.ShoppingCartRepository;
import com.example.basicbookstoreprojectnew.model.repository.UserRepository;
import com.example.basicbookstoreprojectnew.model.service.impl.ShoppingCartServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceImplTest {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartServiceImpl;

    private User user;

    private Book book;

    private ShoppingCart shoppingCart;

    private CartItem cartItem;

    private ShoppingCartResponseDto shoppingCartResponse;

    private CartItemResponseDto cartItemResponse;

    private CartItemResponseDto updatedCartItemResponse;

    private ShoppingCartResponseDto updatedShoppingCartResponse;

    private CartItemResponseDto newCartItemResponse;

    private ShoppingCartResponseDto updatedNewCartItemResponse;

    @BeforeEach
    @DisplayName("Init test data")
    void setUp() {

        user = new User();
        user.setId(1L);

        shoppingCart = new ShoppingCart();
        shoppingCart.setId(10L);
        shoppingCart.setUser(user);

        book = new Book();
        book.setId(50L);
        book.setTitle("New book");

        cartItem = new CartItem();
        cartItem.setId(70L);
        cartItem.setBook(book);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setQuantity(5);

        cartItemResponse = new CartItemResponseDto(
                cartItem.getId(),
                book.getId(),
                "Test book",
                5
        );

        shoppingCartResponse = new ShoppingCartResponseDto(
                shoppingCart.getId(),
                user.getId(),
                List.of(cartItemResponse)
        );

        updatedCartItemResponse = new CartItemResponseDto(
                cartItem.getId(),
                book.getId(),
                "Test book",
                10
        );

        updatedShoppingCartResponse = new ShoppingCartResponseDto(
                shoppingCart.getId(),
                user.getId(),
                List.of(updatedCartItemResponse)
        );

        newCartItemResponse = new CartItemResponseDto(
                123L,
                book.getId(),
                "Test book",
                5
        );

        updatedNewCartItemResponse = new ShoppingCartResponseDto(
                shoppingCart.getId(),
                user.getId(),
                List.of(newCartItemResponse)
        );
    }


    @Test
    @DisplayName("getShoppingCartByUser: should return existing shopping cart")
    void getShoppingCartByUser_existingCart() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));

        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartResponse);

        ShoppingCartResponseDto result =
                shoppingCartServiceImpl.getShoppingCartByUser(user.getId());

        assertEquals(10L, result.id());
        assertEquals(1L, result.userId());
        assertEquals(1, result.cartItems().size());
    }

    @Test
    @DisplayName("getShoppingCartByUser: shopping cart is none, create new")
    void getShoppingCartByUser_NoCart_createNew() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
        when(shoppingCartRepository.save(any())).thenReturn(shoppingCart);

        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartResponse);

        ShoppingCartResponseDto result =
                shoppingCartServiceImpl.getShoppingCartByUser(user.getId());

        assertEquals(10L, result.id());

        verify(shoppingCartRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("getShoppingCartByUser: user not found, throw exception")
    void getShoppingCartByUser_UserNotFound() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartServiceImpl.getShoppingCartByUser(user.getId()));
    }


    @Test
    @DisplayName("addBook: cartItem exists, increase quantity")
    void addBook_cartItemExists_IncreaseQuantity() {

        shoppingCart.setCartItems(new ArrayList<>(List.of(cartItem)));

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(updatedShoppingCartResponse);

        ShoppingCartResponseDto result =
                shoppingCartServiceImpl.
                        addBookToShoppingCart(user.getId(), book.getId(), 5);

        assertNotNull(result);
        assertEquals(10, cartItem.getQuantity());
        assertEquals(10, result.cartItems().get(0).quantity());

        verify(cartItemRepository).save(cartItem);
    }


    @Test
    @DisplayName("addBook: cartItem not exists, create new")
    void addBook_createNewCartItem() {

        shoppingCart.setCartItems(new ArrayList<>());

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(updatedNewCartItemResponse);

        ShoppingCartResponseDto result
                = shoppingCartServiceImpl.addBookToShoppingCart(
                user.getId(), book.getId(), 5);

        assertNotNull(result);

        verify(cartItemRepository).save(any(CartItem.class));
        assertEquals(1, shoppingCart.getCartItems().size());
        assertEquals(5, result.cartItems().get(0).quantity());
    }

    @Test
    @DisplayName("addBook: shopping cart not found, throw exception")
    void addBook_ShoppingCartNotFound_ThrowException() {

        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(ShoppingCartNotFoundException.class,
                () -> shoppingCartServiceImpl.addBookToShoppingCart(
                        user.getId(), book.getId(), 5));
    }

    @Test
    @DisplayName("addBook: book not found, throw exception")
    void addBook_BookNotFound_ThrowException() {

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartServiceImpl.addBookToShoppingCart(
                        user.getId(), book.getId(), 5));
    }


    @Test
    @DisplayName("updateCartItemQuantity: cartItem is exists, add and update quantity")
    void updateCartItemQuantity_AddAndUpdate() {

        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));
        when(cartItemMapper.toDto(cartItem)).thenReturn(cartItemResponse);

        CartItemResponseDto result =
                shoppingCartServiceImpl.updateCartItemQuantity(cartItem.getId(), 5);

        assertNotNull(result);
        assertEquals(10, cartItem.getQuantity());
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    @DisplayName("updateCartItemQuantity: cartItem not found, throw exception")
    void updateCartItemQuantity_NotFound_throwException() {

        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.empty());

        assertThrows(ShoppingCartNotFoundException.class,
                () -> shoppingCartServiceImpl.updateCartItemQuantity(cartItem.getId(), 5));
    }

    @Test
    @DisplayName("removeCartItem: specific cartItem")
    void removeCartItem_Delete() {

        when(cartItemRepository.existsById(cartItem.getId())).thenReturn(true);

        shoppingCartServiceImpl.removeCartItem(cartItem.getId());

        verify(cartItemRepository).deleteById(cartItem.getId());
    }

    @Test
    @DisplayName("removeCartItem: specific cartItem")
    void removeCartItem_CartItemNotFound_ThrowException() {

        when(cartItemRepository.existsById(cartItem.getId())).thenReturn(false);

        assertThrows(ShoppingCartNotFoundException.class,
                () -> shoppingCartServiceImpl.removeCartItem(cartItem.getId()));
    }
}
