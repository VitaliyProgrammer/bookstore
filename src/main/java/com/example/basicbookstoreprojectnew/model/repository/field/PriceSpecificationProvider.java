package com.example.basicbookstoreprojectnew.model.repository.field;

import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.repository.specification.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "price";
    }

    public Specification<Book> getSpecification(String[] parameters) {
        return (root, query, criteriaBuilder) ->
                root.get("price").in(Arrays.stream(parameters).toArray());
    }
}
