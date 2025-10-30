package com.example.basicbookstoreprojectnew.model.service;

import com.example.basicbookstoreprojectnew.dto.CategoryRequestDto;
import com.example.basicbookstoreprojectnew.dto.CategoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    Page<CategoryResponseDto> findAll(Pageable pageable);

    CategoryResponseDto getById(Long id);

    CategoryResponseDto create(CategoryRequestDto request);

    CategoryResponseDto update(Long id, CategoryRequestDto request);

    void deleteById(Long id);
}
