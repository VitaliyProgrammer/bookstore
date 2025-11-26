package com.example.basicbookstoreprojectnew.model.repository.field;

import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.repository.specification.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {

    @Override
    public String getKey() {
        return BookSpecificationKeys.TITLE;
    }

    public Specification<Book> getSpecification(String[] parameters) {
        return (root, query, criteriaBuilder) ->
                root.get(BookSpecificationKeys.TITLE).in(Arrays.stream(parameters).toArray());
    }
}
