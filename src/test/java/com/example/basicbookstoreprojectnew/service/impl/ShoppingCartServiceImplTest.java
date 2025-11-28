package com.example.basicbookstoreprojectnew.service.impl;

import com.example.basicbookstoreprojectnew.dto.CartItemResponseDto;
import com.example.basicbookstoreprojectnew.dto.ShoppingCartResponseDto;
import com.example.basicbookstoreprojectnew.exception.EntityNotFoundException;
import com.example.basicbookstoreprojectnew.exception.ShoppingCartNotFoundException;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.CartItem;
import com.example.basicbookstoreprojectnew.model.ShoppingCart;
import com.example.basicbookstoreprojectnew.model.User;
import com.example.basicbookstoreprojectnew.model.repository.BookRepository;
import com.example.basicbookstoreprojectnew.model.repository.CartItemRepository;
import com.example.basicbookstoreprojectnew.model.repository.ShoppingCartRepository;
import com.example.basicbookstoreprojectnew.model.repository.UserRepository;
import com.example.basicbookstoreprojectnew.model.service.ShoppingCartService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@Transactional
public class ShoppingCartServiceImplTest {

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    ShoppingCartRepository shoppingCartRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookRepository bookRepository;

    private User savedUser;

    private Book savedBook;

    @BeforeEach
    void setUp() {

        cartItemRepository.deleteAll();
        shoppingCartRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("1234567890");
        user.setFirstName("User");
        user.setLastName("Test");

        savedUser = userRepository.save(user);

        Book book = new Book();
        book.setTitle("Clean Architecture");
        book.setAuthor("Robert C. Martin");
        book.setPrice(BigDecimal.valueOf(50.0));
        book.setIsbn("9780134494166");

        savedBook = bookRepository.save(book);
    }

    @Test
    @DisplayName("createShoppingCart - creates new shopping cart")
    void createShoppingCart() {

        ShoppingCartResponseDto shoppingCartResponseDto =
                shoppingCartService.getShoppingCartByUser(savedUser.getId());

        assertThat(shoppingCartResponseDto.id()).isNotNull();
        assertThat(shoppingCartResponseDto.userId()).isEqualTo(savedUser.getId());
        assertThat(shoppingCartResponseDto.cartItems()).asList().isEmpty();

        assertThat(shoppingCartRepository.findByUserId(savedUser.getId())).isPresent();
    }

    @Test
    @DisplayName("createShoppingCart - return existing shopping cart")
    void getShoppingCartByUserId() {

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(savedUser);
        shoppingCartRepository.save(shoppingCart);

        ShoppingCartResponseDto shoppingCartResponseDto =
                shoppingCartService.getShoppingCartByUser(savedUser.getId());

        assertThat(shoppingCartResponseDto.id()).isEqualTo(shoppingCart.getId());
    }

    @Test
    @DisplayName("getShoppingCartByUserNotFound - throws exception, user not found")
    void getShoppingCartByUserNotFound() {

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.getShoppingCartByUser(9999L));
    }

    @Test
    @DisplayName("addBook_newCartItem - adds new book into cart item")
    void addBook_newCartItem() {

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(savedUser);
        shoppingCartRepository.save(shoppingCart);

        ShoppingCartResponseDto shoppingCartResponseDto =
                shoppingCartService.addBookToShoppingCart(
                        savedUser.getId(), savedBook.getId(), 5);

        assertThat(shoppingCartResponseDto.id()).isEqualTo(shoppingCart.getId());

        assertEquals(1, shoppingCartResponseDto.cartItems().size());

        assertThat(shoppingCartResponseDto.cartItems().get(0).quantity()).isEqualTo(5);

        assertThat(shoppingCartResponseDto.cartItems().get(0).bookId())
                .isEqualTo(savedBook.getId());
    }

    @Test
    @DisplayName("addBookIncreaseQuantity - increases quantity of existing cart item")
    void addBookIncreaseQuantity() {

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(savedUser);
        shoppingCartRepository.save(shoppingCart);

        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(savedBook);
        cartItem.setQuantity(2);

        shoppingCart.getCartItems().add(cartItem);

        ShoppingCartResponseDto shoppingCartResponseDto =
                shoppingCartService.addBookToShoppingCart(
                        savedUser.getId(), savedBook.getId(), 3);

        assertThat(shoppingCartResponseDto.cartItems().get(0).quantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("addBook_shoppingCartNotFound - shopping cart not found, throws exception")
    void addBook_shoppingCartNotFound() {

        assertThrows(ShoppingCartNotFoundException.class,
                () -> shoppingCartService.addBookToShoppingCart(
                        9999L, savedBook.getId(), 1));

    }

    @Test
    @DisplayName("addBook_shoppingCartNotFound - books not found, throws exception")
    void addBook_notFound() {

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(savedUser);
        shoppingCartRepository.save(shoppingCart);

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addBookToShoppingCart(
                        savedUser.getId(), 9999L, 1));
    }

    @Test
    @DisplayName("updateCartItemQuantity - updates properly")
    void updateCartItem() {

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(savedUser);

        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(savedBook);
        cartItem.setQuantity(3);

        shoppingCart.getCartItems().add(cartItem);
        shoppingCartRepository.save(shoppingCart);

        CartItemResponseDto cartItemResponseDto =
                shoppingCartService.updateCartItemQuantity(cartItem.getId(), 5);

        assertThat(cartItemResponseDto.quantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("updateCartItemQuantity - cart item not found")
    void updateCartItem_notFound() {

        assertThrows(ShoppingCartNotFoundException.class,
                () -> shoppingCartService.updateCartItemQuantity(9999L, 5));
    }

    @Test
    @DisplayName("removeCartItem - delete, success")
    void removeCartItem() {

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(savedUser);

        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(savedBook);
        cartItem.setQuantity(5);

        shoppingCart.getCartItems().add(cartItem);
        shoppingCartRepository.save(shoppingCart);

        shoppingCartService.removeCartItem(cartItem.getId());

        assertThat(cartItemRepository.existsById(cartItem.getId())).isTrue();
    }

    @Test
    @DisplayName("removeCartItem - cart item not found, throws exception")
    void removeCartItem_notFound() {

        assertThrows(ShoppingCartNotFoundException.class,
                () -> shoppingCartService.removeCartItem(9999L));
    }
}
