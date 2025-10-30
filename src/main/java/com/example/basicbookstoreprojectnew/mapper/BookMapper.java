package com.example.basicbookstoreprojectnew.mapper;

import com.example.basicbookstoreprojectnew.dto.BookDto;
import com.example.basicbookstoreprojectnew.dto.BookDtoCategoryResponse;
import com.example.basicbookstoreprojectnew.dto.CreateBookRequestDto;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.Category;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Book toEntity(CreateBookRequestDto dto);

    @Mapping(target = "categoryIds", source = "categories")
    BookDto toDto(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "categories", ignore = true)
    void updateBookFromDto(CreateBookRequestDto dto, @MappingTarget Book target);

    BookDtoCategoryResponse toDtoCertainCategory(Book book);

    default List<Long> mapCategoriesToIds(Set<Category> categories) {
        return categories == null ? List.of() : categories.stream().map(Category::getId).toList();
    }
}
