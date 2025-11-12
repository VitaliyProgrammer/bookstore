package com.example.basicbookstoreprojectnew.model.repository;

import com.example.basicbookstoreprojectnew.model.OrderItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    Optional<OrderItem> findByIdAndOrderId(Long orderItemId, Long orderId);
}

