package com.example.basicbookstoreprojectnew.service.impl;

import com.example.basicbookstoreprojectnew.dto.CategoryRequestDto;
import com.example.basicbookstoreprojectnew.dto.CategoryResponseDto;
import com.example.basicbookstoreprojectnew.exception.CategoryNotFoundException;
import com.example.basicbookstoreprojectnew.mapper.CategoryMapper;
import com.example.basicbookstoreprojectnew.model.Category;
import com.example.basicbookstoreprojectnew.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@Testcontainers
@Transactional
public class CategoryServiceImplTest {

    @Autowired
    private CategoryServiceImpl categoryServiceImpl;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    private Category category;

    @BeforeEach
    void setUp() {

        category = new Category();
        category.setName("Programming");
        category.setDescription("About programming");
        categoryRepository.save(category);
    }

    @Test
    @DisplayName("findAll: paginated list of categories")
    void findAllCategories() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<CategoryResponseDto> pageResponse = categoryServiceImpl.findAll(pageable);

        assertThat(pageResponse.getTotalElements()).isEqualTo(1);
        assertThat(pageResponse.getContent().get(0).name()).isEqualTo(category.getName());
    }

    @Test
    @DisplayName("getById: existing specific category")
    void getById_existingCategory() {

        CategoryResponseDto categoryResponse = categoryServiceImpl.getById(category.getId());

        assertThat(categoryResponse).isNotNull();
        assertThat(categoryResponse.id()).isEqualTo(category.getId());
        assertThat(categoryResponse.name()).isEqualTo(category.getName());
    }

    @Test
    @DisplayName("getById: throw when category is not found")
    void getById_nonFound() {

        assertThrows(CategoryNotFoundException.class, () -> categoryServiceImpl.getById(999L));
    }

    @Test
    @DisplayName("create category: successfully")
    void createCategory() {

        CategoryRequestDto categoryRequest = new CategoryRequestDto(
                "Java",
                "Java programming books"
        );

        CategoryResponseDto categoryResponse = categoryServiceImpl.create(categoryRequest);

        assertThat(categoryResponse).isNotNull();
        assertThat(categoryResponse.id()).isNotNull();
        assertThat(categoryResponse.name()).isEqualTo(categoryRequest.name());
    }

    @Test
    @DisplayName("update category: existing update category successfully")
    void updateCategory() {

        CategoryRequestDto updatedCategoryRequest = new CategoryRequestDto(
                "Updated category",
                "About updated category"
        );

        CategoryResponseDto updatedCategoryResponse =
                categoryServiceImpl.update(category.getId(), updatedCategoryRequest);

        assertThat(updatedCategoryRequest.name()).isEqualTo(updatedCategoryResponse.name());
        assertThat(updatedCategoryRequest.description())
                .isEqualTo(updatedCategoryResponse.description());
    }

    @Test
    @DisplayName("update category: update category non-existing")
    void updateCategory_notFound() {

        CategoryRequestDto updatedCategoryRequest = new CategoryRequestDto(
                "Updated category",
                "About updated category"
        );

        assertThrows(CategoryNotFoundException.class,
                () -> categoryServiceImpl.update(999L, updatedCategoryRequest));
    }

    @Test
    @DisplayName("deleteById: soft delete category")
    void deleteCategory() {

        categoryServiceImpl.deleteById(category.getId());

        Category deletedCategory = categoryRepository.findById(category.getId()).orElseThrow();

        assertThat(deletedCategory.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("deleteById: throw exception, category not found")
    void deleteCategory_notFound() {
        assertThrows(CategoryNotFoundException.class,
                () -> categoryServiceImpl.deleteById(999L));
    }
}
