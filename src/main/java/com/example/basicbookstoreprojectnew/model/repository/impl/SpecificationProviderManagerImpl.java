package com.example.basicbookstoreprojectnew.model.repository.impl;

import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.repository.specification.SpecificationProvider;
import com.example.basicbookstoreprojectnew.model.repository.specification.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpecificationProviderManagerImpl implements SpecificationProviderManager<Book> {

    private final List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(a -> a.getKey().equals(key))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Can`t find specification provider for key " + key));
    }
}
