package com.example.basicbookstoreprojectnew.mapper;

import com.example.basicbookstoreprojectnew.dto.OrderResponseDto;
import com.example.basicbookstoreprojectnew.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    OrderResponseDto toDto(Order order);
}

