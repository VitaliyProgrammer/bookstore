package com.example.basicbookstoreprojectnew.controller;

import com.example.basicbookstoreprojectnew.dto.AddToCartRequestDto;
import com.example.basicbookstoreprojectnew.dto.CartItemResponseDto;
import com.example.basicbookstoreprojectnew.dto.ShoppingCartResponseDto;
import com.example.basicbookstoreprojectnew.dto.UpdateCartItemRequestDto;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.CartItem;
import com.example.basicbookstoreprojectnew.model.ShoppingCart;
import com.example.basicbookstoreprojectnew.model.User;
import com.example.basicbookstoreprojectnew.repository.BookRepository;
import com.example.basicbookstoreprojectnew.repository.CartItemRepository;
import com.example.basicbookstoreprojectnew.repository.ShoppingCartRepository;
import com.example.basicbookstoreprojectnew.repository.UserRepository;
import com.example.basicbookstoreprojectnew.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    private ShoppingCart savedShoppingCart;

    private String userToken;

    @BeforeEach
    void setUp() {

        User user = new User();
        user.setEmail("user@test.com");
        user.setPassword("1234567890");
        user.setFirstName("User");
        user.setLastName("Test");
        savedUser = userRepository.save(user);

        userToken = "Bearer " + jwtUtil.generateToken(
                savedUser.getEmail(),
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
        shoppingCart.setCartItems(new ArrayList<>());
        savedShoppingCart = shoppingCartRepository.save(shoppingCart);
    }

    @AfterEach
    void tearDown() {
        cartItemRepository.deleteAll();
        shoppingCartRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /cart - should return shopping cart")
    void getShoppingCart() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/cart")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartResponseDto shoppingCartResponse =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                        ShoppingCartResponseDto.class);

        assertThat(shoppingCartResponse.userId()).isEqualTo(savedUser.getId());
        assertThat(shoppingCartResponse.cartItems()).isNotNull();
        assertThat(shoppingCartResponse.cartItems()).isEmpty();
    }

    @Test
    @DisplayName("POST /cart - should add book to cart")
    void addBookShoppingCart() throws Exception {

        AddToCartRequestDto addBookRequest = new AddToCartRequestDto(
                savedBook.getId(), 5
        );

        MvcResult mvcResult = mockMvc.perform(post("/cart")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addBookRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        ShoppingCartResponseDto shoppingCartResponse =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                        ShoppingCartResponseDto.class);

        assertThat(shoppingCartResponse.cartItems()).hasSize(1);

        CartItemResponseDto cartItemResponse = shoppingCartResponse.cartItems().get(0);

        assertThat(cartItemResponse.bookId()).isEqualTo(savedBook.getId());
        assertThat(cartItemResponse.quantity()).isEqualTo(5);
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

        MvcResult mvcResult = mockMvc.perform(put("/cart/cart-items/" + cartItem.getId())
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateQuantityRequest)))
                .andExpect(status().isOk())
                .andReturn();

        CartItemResponseDto updatedCartItem =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                        CartItemResponseDto.class);

        assertThat(updatedCartItem.quantity()).isEqualTo(10);
        assertThat(updatedCartItem.bookId()).isEqualTo(savedBook.getId());
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
