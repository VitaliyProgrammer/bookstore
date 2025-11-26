package com.example.basicbookstoreprojectnew.model.service;

import com.example.basicbookstoreprojectnew.dto.OrderItemResponseDto;
import com.example.basicbookstoreprojectnew.model.User;
import java.util.List;

public interface OrderItemService {

    List<OrderItemResponseDto> getOrderItemsByOrderId(User user, Long orderId);

    OrderItemResponseDto getOrderItemById(User user, Long orderId, Long orderItem);

    OrderItemResponseDto updateQuantity(Long orderId, Long orderItemId, int newQuantity);

    void deleteCartItem(Long orderId, Long orderItemId);
}

