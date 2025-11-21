package com.example.basicbookstoreprojectnew.shoppingcart;

import com.example.basicbookstoreprojectnew.model.ShoppingCart;
import com.example.basicbookstoreprojectnew.model.User;
import com.example.basicbookstoreprojectnew.model.repository.ShoppingCartRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartRepositoryTest {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    private User user;

    private ShoppingCart shoppingCart;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);

        shoppingCart = new ShoppingCart();
        shoppingCart.setId(10L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new ArrayList<>());
    }

    @Test
    @DisplayName("saveShoppingCart: should save and return a shopping cart")
    void saveShoppingCart() {

        when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);

        ShoppingCart result = shoppingCartRepository.save(shoppingCart);

        assertNotNull(result);
        assertEquals(shoppingCart.getId(), result.getId());
        assertEquals(user.getId(), result.getUser().getId());

        verify(shoppingCartRepository, times(1)).save(shoppingCart);
    }

    @Test
    @DisplayName("findByUserId: should return a shopping cart when it exists")
    void findByUserId_existingShoppingCart() {

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));

        Optional<ShoppingCart> result = shoppingCartRepository.findByUserId(user.getId());

        assertTrue(result.isPresent());
        assertEquals(shoppingCart.getId(), result.get().getId());
        verify(shoppingCartRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    @DisplayName("findByUserId: should return empty when a shopping cart don`t exists")
    void findByUserId_notExists() {

        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        Optional<ShoppingCart> result = shoppingCartRepository.findByUserId(user.getId());

        assertTrue(result.isEmpty());

        verify(shoppingCartRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    @DisplayName("existsShoppingCartById: should return true when the shopping cart exists")
    void existShoppingCartById_true() {

        when(shoppingCartRepository.existsById(shoppingCart.getId())).thenReturn(true);

        boolean result = shoppingCartRepository.existsById(shoppingCart.getId());

        assertTrue(result);

        verify(shoppingCartRepository, times(1))
                .existsById(shoppingCart.getId());
    }

    @Test
    @DisplayName("existsShoppingCartById: should return false "
            + "when the shopping cart does not exists")
    void existShoppingCartById_false() {

        when(shoppingCartRepository.existsById(999L)).thenReturn(false);

        boolean result = shoppingCartRepository.existsById(999L);

        assertFalse(result);

        verify(shoppingCartRepository, times(1)).existsById(999L);
    }

    @Test
    @DisplayName("deleteShoppingCartById: should delete an existing shopping cart")
    void deleteById_existingCart() {

        doNothing().when(shoppingCartRepository).deleteById(shoppingCart.getId());

        shoppingCartRepository.deleteById(shoppingCart.getId());

        verify(shoppingCartRepository, times(1))
                .deleteById(shoppingCart.getId());
    }

    @Test
    @DisplayName("findAll: should return all shopping carts")
    void findAllShoppingCarts() {

        List<ShoppingCart> shoppingCarts = List.of(shoppingCart);

        when(shoppingCartRepository.findAll()).thenReturn(shoppingCarts);

        List<ShoppingCart> result = shoppingCartRepository.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(shoppingCart.getId(), result.get(0).getId());

        verify(shoppingCartRepository, times(1)).findAll();
    }
}
