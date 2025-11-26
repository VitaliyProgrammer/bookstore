package com.example.basicbookstoreprojectnew.repository;

import com.example.basicbookstoreprojectnew.model.ShoppingCart;
import com.example.basicbookstoreprojectnew.model.User;
import com.example.basicbookstoreprojectnew.model.repository.ShoppingCartRepository;
import com.example.basicbookstoreprojectnew.model.repository.UserRepository;
import com.example.basicbookstoreprojectnew.testcontainer.CustomMySqlContainer;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
public class ShoppingCartRepositoryTest {

    @Container
    private static final CustomMySqlContainer mySqlContainer =
            CustomMySqlContainer.getInstance();

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("1234567890");
        user.setFirstName("User");
        user.setLastName("Test");

        savedUser = userRepository.save(user);
    }

    @Test
    @DisplayName("save - should created shopping cart")
    void saveShoppingCart() {

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(savedUser);

        ShoppingCart savedShoppingCart = shoppingCartRepository.save(shoppingCart);

        assertThat(savedShoppingCart.getId()).isNotNull();
        assertThat(savedShoppingCart.getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("save - should return existing shopping car")
    void shoppingCartFindById() {

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(savedUser);
        ShoppingCart savedShoppingCart = shoppingCartRepository.save(shoppingCart);

        Optional<ShoppingCart> foundShoppingCartById =
                shoppingCartRepository.findById(savedShoppingCart.getId());

        assertThat(foundShoppingCartById).isPresent();
        assertThat(foundShoppingCartById.get().getUser().getEmail())
                .isEqualTo(savedUser.getEmail());
    }

    @Test
    @DisplayName("save - should return shopping car by specific user id")
    void ShoppingCartFindByUserId() {

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(savedUser);
        shoppingCartRepository.save(shoppingCart);

        Optional<ShoppingCart> foundShoppingCart =
                shoppingCartRepository.findByUserId(savedUser.getId());

        assertThat(foundShoppingCart).isPresent();
        assertThat(foundShoppingCart.get().getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("ShoppingCartFindByUserId_notFound - " +
            "should return empty for non-existing user")
    void ShoppingCartFindByUserId_notFound() {

        Optional<ShoppingCart> notFoundShoppingCartById =
                shoppingCartRepository.findByUserId(9999L);

        assertThat(notFoundShoppingCartById).isEmpty();
    }

    @Test
    @DisplayName("ShoppingCartFindAll - " +
            "should return list of all existing shopping carts")
    void ShoppingCartFindAll() {

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(savedUser);
        shoppingCartRepository.save(shoppingCart);

        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findAll();

        assertEquals(1, shoppingCarts.size());
    }

    @Test
    @DisplayName("delete - should remove shopping cart")
    void deleteShoppingCart() {

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(savedUser);
        ShoppingCart savedShoppingCart = shoppingCartRepository.save(shoppingCart);

        shoppingCartRepository.delete(savedShoppingCart);

        Optional<ShoppingCart> foundShoppingCart =
                shoppingCartRepository.findById(savedShoppingCart.getId());

        assertThat(foundShoppingCart).isEmpty();
    }
}
