package com.example.basicbookstoreprojectnew.mapper;

import com.example.basicbookstoreprojectnew.dto.OrderItemResponseDto;
import com.example.basicbookstoreprojectnew.model.CartItem;
import com.example.basicbookstoreprojectnew.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "price", source = "book.price")
    OrderItem toOrderItem(CartItem cartItem);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    OrderItemResponseDto toDto(OrderItem orderItem);
}

