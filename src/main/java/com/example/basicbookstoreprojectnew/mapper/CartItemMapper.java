package com.example.basicbookstoreprojectnew.mapper;

import com.example.basicbookstoreprojectnew.dto.CartItemResponseDto;
import com.example.basicbookstoreprojectnew.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    CartItemResponseDto toDto(CartItem cartItem);
}
