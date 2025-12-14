package com.example.basicbookstoreprojectnew.service.impl;

import com.example.basicbookstoreprojectnew.dto.CategoryRequestDto;
import com.example.basicbookstoreprojectnew.dto.CategoryResponseDto;
import com.example.basicbookstoreprojectnew.exception.CategoryNotFoundException;
import com.example.basicbookstoreprojectnew.mapper.CategoryMapper;
import com.example.basicbookstoreprojectnew.model.Category;
import com.example.basicbookstoreprojectnew.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryServiceImpl;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    private Category category;
    private CategoryRequestDto categoryRequest;
    private CategoryResponseDto categoryResponse;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setName("Programming");
        category.setDescription("About programming");
        category.setDeleted(false);

        categoryRequest = new CategoryRequestDto("Java", "About Java");
        categoryResponse = new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }

    @Test
    @DisplayName("findAll: paginated list of categories")
    void findAllCategories() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> page = new PageImpl<>(List.of(category));

        when(categoryRepository.findAll(pageable)).thenReturn(page);
        when(categoryMapper.toDto(category)).thenReturn(categoryResponse);

        Page<CategoryResponseDto> pageResult = categoryServiceImpl.findAll(pageable);

        assertThat(pageResult.getTotalElements()).isEqualTo(1);
        assertThat(pageResult.getContent().get(0).name()).isEqualTo(category.getName());

        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("getById: existing specific category")
    void getById_existingCategory() {

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryResponse);

        CategoryResponseDto result = categoryServiceImpl.getById(1L);

        assertThat(result).isEqualTo(categoryResponse);

        verify(categoryRepository).findById(1L);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("getById: throw when category is not found")
    void getById_nonFound() {

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryServiceImpl.getById(999L));

        verify(categoryRepository).findById(999L);
        verifyNoInteractions(categoryMapper);
    }


    @Test
    @DisplayName("create category: successfully")
    void createCategory() {

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName(categoryRequest.name());
        savedCategory.setDescription(categoryRequest.description());

        when(categoryMapper.toEntity(categoryRequest)).thenReturn(savedCategory);
        when(categoryRepository.save(savedCategory)).thenReturn(savedCategory);
        when(categoryMapper.toDto(savedCategory)).thenReturn(categoryResponse);

        CategoryResponseDto result = categoryServiceImpl.create(categoryRequest);

        assertThat(result).isEqualTo(categoryResponse);

        verify(categoryMapper).toEntity(categoryRequest);
        verify(categoryRepository).save(savedCategory);
        verify(categoryMapper).toDto(savedCategory);
    }

    @Test
    @DisplayName("update category: existing update category successfully")
    void updateCategory() {

        CategoryRequestDto updatedCategoryRequest = new CategoryRequestDto(
                "Updated name",
                "Updated description"
        );

        Category updatedCategory = new Category();
        updatedCategory.setId(category.getId());
        updatedCategory.setName(category.getName());
        updatedCategory.setDescription(category.getDescription());


        CategoryResponseDto updatedCategoryResponse = new CategoryResponseDto(
                category.getId(),
                updatedCategoryRequest.name(),
                updatedCategoryRequest.description()
        );

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(updatedCategory);
        when(categoryMapper.toDto(updatedCategory)).thenReturn(updatedCategoryResponse);

        CategoryResponseDto result =
                categoryServiceImpl.update(category.getId(), updatedCategoryRequest);

        assertThat(result).isEqualTo(updatedCategoryResponse);

        verify(categoryRepository).findById(category.getId());
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDto(updatedCategory);
    }

    @Test
    @DisplayName("update category: update category non-existing")
    void updateCategory_notFound() {

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> categoryServiceImpl.update(999L, categoryRequest));

        verify(categoryRepository).findById(999L);
        verify(categoryMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("deleteById: soft delete category")
    void deleteCategory() {

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);

        categoryServiceImpl.deleteById(category.getId());

        assertThat(category.isDeleted()).isTrue();

        verify(categoryRepository).findById(category.getId());
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("deleteById: throw exception, category not found")
    void deleteCategory_notFound() {

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> categoryServiceImpl.deleteById(999L));

        verify(categoryRepository).findById(999L);
        verify(categoryRepository, never()).save(any());
    }
}
