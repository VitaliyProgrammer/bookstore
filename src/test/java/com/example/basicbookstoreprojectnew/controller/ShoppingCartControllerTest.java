package com.example.basicbookstoreprojectnew.controller;

import com.example.basicbookstoreprojectnew.dto.AddToCartRequestDto;
import com.example.basicbookstoreprojectnew.dto.UpdateCartItemRequestDto;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.CartItem;
import com.example.basicbookstoreprojectnew.model.ShoppingCart;
import com.example.basicbookstoreprojectnew.model.User;
import com.example.basicbookstoreprojectnew.model.repository.BookRepository;
import com.example.basicbookstoreprojectnew.model.repository.CartItemRepository;
import com.example.basicbookstoreprojectnew.model.repository.ShoppingCartRepository;
import com.example.basicbookstoreprojectnew.model.repository.UserRepository;
import com.example.basicbookstoreprojectnew.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ShoppingCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private User savedUser;

    private Book savedBook;

    private String userToken;

    @BeforeEach
    void setUp() {

        cartItemRepository.deleteAll();
        shoppingCartRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("user@test.com");
        user.setPassword("1234567890");
        user.setFirstName("User");
        user.setLastName("Test");
        savedUser = userRepository.save(user);

        userToken = "Bearer " + jwtUtil.generateToken(
                "user@test.com",
                List.of("USER")
        );

        Book book = new Book();
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setDescription("About effective programming on Java");
        book.setPrice(BigDecimal.valueOf(30.00));
        book.setIsbn("9780134685991");
        savedBook = bookRepository.save(book);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(savedUser);
        shoppingCartRepository.save(shoppingCart);
    }

    @Test
    @DisplayName("GET /cart - should return shopping cart")
    void getShoppingCart() throws Exception {

        mockMvc.perform(get("/cart")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(savedUser.getId()))
                .andExpect(jsonPath("$.cartItems").isArray());
    }

    @Test
    @DisplayName("POST /cart - should add book to cart")
    void addBookShoppingCart() throws Exception {

        AddToCartRequestDto addBookRequest = new AddToCartRequestDto(
                savedBook.getId(), 5
        );

        mockMvc.perform(post("/cart")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addBookRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cartItems[0].bookId").value(savedBook.getId()))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(5));
    }

    @Test
    @DisplayName("PUT /cart/cart-items/ - should update cart item quantity")
    void updateCartItemQuantity() throws Exception {

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(savedUser.getId()).get();

        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(savedBook);
        cartItem.setQuantity(5);
        shoppingCart.getCartItems().add(cartItem);
        cartItemRepository.save(cartItem);

        UpdateCartItemRequestDto updateQuantityRequest =
                new UpdateCartItemRequestDto(10);

        mockMvc.perform(put("/cart/cart-items/" + cartItem.getId())
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateQuantityRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    @DisplayName("DELETE /cart/cart-items/{id} - should remove cart item")
    void deleteCartItem() throws Exception {

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(savedUser.getId()).get();

        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(savedBook);
        cartItem.setQuantity(5);
        shoppingCart.getCartItems().add(cartItem);
        cartItemRepository.save(cartItem);

        mockMvc.perform(delete("/cart/cart-items/" + cartItem.getId())
                        .header("Authorization", userToken))
                .andExpect(status().isNoContent());
    }
}
