package com.example.basicbookstoreprojectnew.model.service.impl;

import com.example.basicbookstoreprojectnew.dto.CategoryRequestDto;
import com.example.basicbookstoreprojectnew.dto.CategoryResponseDto;
import com.example.basicbookstoreprojectnew.mapper.CategoryMapper;
import com.example.basicbookstoreprojectnew.model.Category;
import com.example.basicbookstoreprojectnew.model.repository.CategoryRepository;
import com.example.basicbookstoreprojectnew.model.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Page<CategoryResponseDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toDto);
    }

    @Override
    public CategoryResponseDto getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id!: " + id));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryResponseDto create(CategoryRequestDto request) {
        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public CategoryResponseDto update(Long id, CategoryRequestDto request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id!: " + id));

        category.setName(request.name());
        category.setDescription(request.description());

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    public void deleteById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Can't find category by id: " + id));

        category.setDeleted(true);
        categoryRepository.save(category);
    }
}
