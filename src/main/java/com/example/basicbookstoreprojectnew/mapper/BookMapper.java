package com.example.basicbookstoreprojectnew.mapper;

import com.example.basicbookstoreprojectnew.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {
    void updateBookFromDto(Book source, @MappingTarget Book target);
}
