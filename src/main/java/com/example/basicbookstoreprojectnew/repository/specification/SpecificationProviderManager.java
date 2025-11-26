package com.example.basicbookstoreprojectnew.repository.specification;

public interface SpecificationProviderManager<T> {
    SpecificationProvider<T> getSpecificationProvider(String key);
}
