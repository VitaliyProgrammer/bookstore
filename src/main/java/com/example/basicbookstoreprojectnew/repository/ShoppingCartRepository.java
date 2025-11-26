package com.example.basicbookstoreprojectnew.repository;

import com.example.basicbookstoreprojectnew.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUserId(Long id);
}

