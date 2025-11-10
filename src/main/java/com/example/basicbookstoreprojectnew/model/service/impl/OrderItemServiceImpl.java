package com.example.basicbookstoreprojectnew.model.service.impl;

import com.example.basicbookstoreprojectnew.dto.OrderItemResponseDto;
import com.example.basicbookstoreprojectnew.exception.OrderItemNotFoundException;
import com.example.basicbookstoreprojectnew.exception.OrderNotFoundException;
import com.example.basicbookstoreprojectnew.mapper.OrderItemMapper;
import com.example.basicbookstoreprojectnew.model.Order;
import com.example.basicbookstoreprojectnew.model.OrderItem;
import com.example.basicbookstoreprojectnew.model.User;
import com.example.basicbookstoreprojectnew.model.repository.OrderItemRepository;
import com.example.basicbookstoreprojectnew.model.repository.OrderRepository;
import com.example.basicbookstoreprojectnew.model.service.OrderItemService;
import com.example.basicbookstoreprojectnew.validation.AccessValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderItemResponseDto updateQuantity(Long orderId, Long orderItemId, int newQuantity) {

        OrderItem orderItem = orderItemRepository.findByIdAndOrderId(orderItemId, orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order item not found!"));

        orderItem.setQuantity(newQuantity);
        orderItemRepository.save(orderItem);
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public List<OrderItemResponseDto> getOrderItemsByOrderId(User user, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found with id: " + orderId));

        AccessValidator.validateOwnership(user, orderId);

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        if (orderItems.isEmpty()) {
            throw new OrderItemNotFoundException("Items for order not found with id: " + orderId);
        }

        return orderItems.stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemResponseDto getOrderItemById(User user, Long orderId, Long orderItemId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Item for order not found with id: " + orderId));

        AccessValidator.validateOwnership(user, order.getUser().getId());

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .filter(item -> item.getOrder().getId().equals(orderId))
                .orElseThrow(() -> new OrderItemNotFoundException(
                        "Item with id: " + orderItemId + " not found with order id: " + orderId));

        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public void deleteCartItem(Long orderId, Long orderItemId) {

        OrderItem orderItem = orderItemRepository.findByIdAndOrderId(orderItemId, orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order item not found!"));

        orderItemRepository.delete(orderItem);
    }
}
