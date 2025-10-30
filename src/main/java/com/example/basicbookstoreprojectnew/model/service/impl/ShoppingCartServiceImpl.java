package com.example.basicbookstoreprojectnew.model.service.impl;

import com.example.basicbookstoreprojectnew.dto.CartItemResponseDto;
import com.example.basicbookstoreprojectnew.dto.ShoppingCartResponseDto;
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
import com.example.basicbookstoreprojectnew.model.service.ShoppingCartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;
    private final BookRepository bookRepository;

    private final UserRepository userRepository;

    @Override
    public ShoppingCartResponseDto getShoppingCartByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found with id: " + userId));

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    ShoppingCart newShoppingCart = new ShoppingCart();
                    newShoppingCart.setUser(user);
                    return shoppingCartRepository.save(newShoppingCart);
                });

        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartResponseDto addBookToShoppingCart(Long id, Long bookId, int quantity) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(id)
                .orElseThrow(
                        () -> new ShoppingCartNotFoundException("Shopping cart can`t to find!: "));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book can`t to find!: "));

        CartItem cartsInItem = shoppingCart.getCartItems().stream()
                .filter(cartItem -> cartItem.getBook().getId().equals(book.getId()))
                .findFirst()
                .map(cartItem -> {
                    cartItem.setQuantity(cartItem.getQuantity() + quantity);
                    return cartItem;
                })
                .orElseGet(() -> {
                    CartItem newCartItem = new CartItem();
                    newCartItem.setShoppingCart(shoppingCart);
                    newCartItem.setBook(book);
                    newCartItem.setQuantity(quantity);
                    return newCartItem;
                });

        cartItemRepository.save(cartsInItem);

        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public CartItemResponseDto updateCartItemQuantity(Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(
                        () -> new ShoppingCartNotFoundException("Cart item not fond in cart!: "));

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return cartItemMapper.toDto(cartItem);
    }

    @Override
    public void removeCartItem(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new ShoppingCartNotFoundException("Cart item not found in cart!: ");
        }

        cartItemRepository.deleteById(cartItemId);
    }
}
