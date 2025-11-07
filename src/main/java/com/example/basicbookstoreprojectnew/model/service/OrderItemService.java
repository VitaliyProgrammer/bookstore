package com.example.basicbookstoreprojectnew.model.service;

import com.example.basicbookstoreprojectnew.dto.OrderItemResponseDto;
import java.util.List;

public interface OrderItemService {

    List<OrderItemResponseDto> getOrderItemsByOrderId(Long orderId);

    OrderItemResponseDto getOrderItemById(Long orderId, Long orderItemId, Long userId);

    OrderItemResponseDto updateQuantity(Long orderId, Long orderItemId, int newQuantity);

    void deleteCartItem(Long orderId, Long orderItemId);
}

