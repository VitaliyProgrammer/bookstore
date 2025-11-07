package com.example.basicbookstoreprojectnew.model.service;

import com.example.basicbookstoreprojectnew.dto.OrderRequestDto;
import com.example.basicbookstoreprojectnew.dto.OrderResponseDto;
import com.example.basicbookstoreprojectnew.dto.UpdateOrderStatusRequestDto;
import com.example.basicbookstoreprojectnew.model.Order;
import java.util.List;

public interface OrderService {

    OrderResponseDto makeOrder(Long userId, OrderRequestDto request);

    List<OrderResponseDto> getUserOrders(Long userId);

    public Order getOrderById(Long orderId);

    OrderResponseDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto request);
}

