package com.example.basicbookstoreprojectnew.dto;

import com.example.basicbookstoreprojectnew.model.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(
        Long id,
        Long userId,
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime orderDate,
        BigDecimal total,
        Status status,
        String shippingAddress,
        List<OrderItemResponseDto> orderItems
) {
}

