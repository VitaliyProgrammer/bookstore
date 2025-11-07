package com.example.basicbookstoreprojectnew.controller;

import com.example.basicbookstoreprojectnew.dto.OrderItemResponseDto;
import com.example.basicbookstoreprojectnew.dto.OrderRequestDto;
import com.example.basicbookstoreprojectnew.dto.OrderResponseDto;
import com.example.basicbookstoreprojectnew.dto.UpdateOrderStatusRequestDto;
import com.example.basicbookstoreprojectnew.model.Order;
import com.example.basicbookstoreprojectnew.model.User;
import com.example.basicbookstoreprojectnew.model.service.OrderItemService;
import com.example.basicbookstoreprojectnew.model.service.OrderService;
import com.example.basicbookstoreprojectnew.security.AuthenticationUtil;
import com.example.basicbookstoreprojectnew.validation.AccessValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "For create and manage orders")
public class OrderController {
    private final OrderService orderService;

    private final OrderItemService orderItemService;

    private final AuthenticationUtil authenticationUtil;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create a new order",
            description = "Create a new order from user's shopping cart")
    public OrderResponseDto createOrder(Authentication authentication,
                                        @RequestBody @Valid OrderRequestDto request) {

        User user = authenticationUtil.getCurrentUser(authentication);
        return orderService.makeOrder(user.getId(), request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get user's orders", description = "View all orders placed by the user")
    public List<OrderResponseDto> getUserOrders(Authentication authentication) {

        User user = authenticationUtil.getCurrentUser(authentication);
        return orderService.getUserOrders(user.getId());
    }

    @GetMapping("/{orderId}/items")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get order items",
            description = "Get all items for a specific order(for a current user)")
    public List<OrderItemResponseDto> getOrderItems(
            @PathVariable Long orderId, Authentication authentication) {

        User user = authenticationUtil.getCurrentUser(authentication);

        Order order = orderService.getOrderById(orderId);

        AccessValidator.validateOwnership(order.getUser().getId(), user.getId(), "order");
        return orderItemService.getOrderItemsByOrderId(orderId);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get specific order item",
            description = "Get one item from a specific order(for a current user)")
    public OrderItemResponseDto getOrderItem(
            @PathVariable Long itemId, @PathVariable Long orderId, Authentication authentication) {

        User user = authenticationUtil.getCurrentUser(authentication);
        return orderItemService.getOrderItemById(orderId, itemId, user.getId());
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user's orders",
            description = "Allows admin to update order status or shipping address")
    public OrderResponseDto updateOrder(
            @PathVariable Long id, @RequestBody @Valid UpdateOrderStatusRequestDto request) {

        return orderService.updateOrderStatus(id, request);
    }
}

