package com.example.basicbookstoreprojectnew.model.repository.impl;

import com.example.basicbookstoreprojectnew.dto.BookSearchParametersDto;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.repository.specification.SpecificationBuilder;
import com.example.basicbookstoreprojectnew.model.repository.specification.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpecificationBuilderImpl implements SpecificationBuilder<Book> {

    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParametersDto) {
        Specification<Book> specification = Specification.where(null);
        if (searchParametersDto.title() != null && searchParametersDto.title().length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider("title")
                    .getSpecification(searchParametersDto.title()));
        }

        if (searchParametersDto.author() != null && searchParametersDto.author().length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider("author")
                    .getSpecification(searchParametersDto.author()));
        }

        if (searchParametersDto.price() != null && searchParametersDto.price().length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider("price")
                    .getSpecification((searchParametersDto.price())));
        }
        return specification;
    }
}
