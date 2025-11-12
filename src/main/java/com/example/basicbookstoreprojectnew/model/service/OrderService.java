package com.example.basicbookstoreprojectnew.model.service;

import com.example.basicbookstoreprojectnew.dto.OrderRequestDto;
import com.example.basicbookstoreprojectnew.dto.OrderResponseDto;
import com.example.basicbookstoreprojectnew.dto.UpdateOrderStatusRequestDto;
import com.example.basicbookstoreprojectnew.model.Order;
import com.example.basicbookstoreprojectnew.model.User;
import java.util.List;

public interface OrderService {

    OrderResponseDto makeOrder(Long userId, OrderRequestDto request);

    List<OrderResponseDto> getUserOrders(Long userId);

    Order getOrderById(User user, Long orderId);

    OrderResponseDto updateOrderStatus(
            User user, Long orderId, UpdateOrderStatusRequestDto request);
}

