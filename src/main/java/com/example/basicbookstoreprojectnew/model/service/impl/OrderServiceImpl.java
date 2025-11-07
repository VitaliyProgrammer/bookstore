package com.example.basicbookstoreprojectnew.model.service.impl;

import com.example.basicbookstoreprojectnew.dto.OrderRequestDto;
import com.example.basicbookstoreprojectnew.dto.OrderResponseDto;
import com.example.basicbookstoreprojectnew.dto.UpdateOrderStatusRequestDto;
import com.example.basicbookstoreprojectnew.exception.OrderNotFoundException;
import com.example.basicbookstoreprojectnew.exception.ShoppingCartNotFoundException;
import com.example.basicbookstoreprojectnew.mapper.OrderItemMapper;
import com.example.basicbookstoreprojectnew.mapper.OrderMapper;
import com.example.basicbookstoreprojectnew.model.Order;
import com.example.basicbookstoreprojectnew.model.OrderItem;
import com.example.basicbookstoreprojectnew.model.ShoppingCart;
import com.example.basicbookstoreprojectnew.model.Status;
import com.example.basicbookstoreprojectnew.model.repository.OrderRepository;
import com.example.basicbookstoreprojectnew.model.repository.ShoppingCartRepository;
import com.example.basicbookstoreprojectnew.model.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponseDto makeOrder(Long userId, OrderRequestDto request) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new ShoppingCartNotFoundException("Shopping cart not found!"));

        Order order = new Order();
        order.setUser(shoppingCart.getUser());
        order.setShippingAddress(request.shippingAddress());
        order.setStatus(Status.PENDING);
        order.setOrderDate(LocalDateTime.now());

        Set<OrderItem> orderItems = shoppingCart.getCartItems().stream()
                .map(orderItemMapper::toOrderItem)
                .peek(orderItem -> orderItem.setOrder(order))
                .collect(Collectors.toSet());

        BigDecimal total = orderItems.stream()
                .map(orderItem -> orderItem.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotal(total);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        shoppingCart.getCartItems().clear();
        shoppingCartRepository.save(shoppingCart);

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getUserOrders(Long userId) {
        return orderRepository.findAllByUserId(userId).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found with id: " + orderId));
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(
            Long orderId, UpdateOrderStatusRequestDto request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found with id: " + orderId));

        if (request.status() != null) {
            order.setStatus(Status.valueOf(request.status().toUpperCase()));
        }

        if (request.shippingAddress() != null && !request.shippingAddress().isBlank()) {
            order.setShippingAddress(request.shippingAddress());
        }

        orderRepository.save(order);

        return orderMapper.toDto(order);
    }
}

