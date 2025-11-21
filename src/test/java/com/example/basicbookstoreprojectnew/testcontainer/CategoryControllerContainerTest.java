package com.example.basicbookstoreprojectnew.testcontainer;

import com.example.basicbookstoreprojectnew.dto.CategoryRequestDto;
import com.example.basicbookstoreprojectnew.dto.CategoryResponseDto;
import com.example.basicbookstoreprojectnew.model.Category;
import com.example.basicbookstoreprojectnew.model.repository.CategoryRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CategoryControllerContainerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    CategoryRequestDto newCategoryRequest;

    CategoryRequestDto updatedCategoryRequest;

    @BeforeEach
    @DisplayName("Init data for testing")
    void setUp() {

        categoryRepository.deleteAll();

        category = new Category();
        category.setName("Science Fiction ");
        category.setDescription("About modern science");
        categoryRepository.save(category);

        newCategoryRequest = new CategoryRequestDto(
                "Romance",
                "Love stories, relationships, and emotional journeys."
        );

        updatedCategoryRequest = new CategoryRequestDto(
                "Updated category",
                "About of updated category"
        );
    }

    @Test
    @DisplayName("GET /categories – should return list of categories")
    void getAllCategories_shouldReturnListIfCategories() {

        ResponseEntity<CategoryResponseDto[]> response =
                testRestTemplate.getForEntity("/categories", CategoryResponseDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        CategoryResponseDto[] categories = response.getBody();

        assertNotNull(categories);
        assertTrue(categories.length > 0);

        CategoryResponseDto firstCategory = categories[0];

        assertEquals(category.getName(), firstCategory.name());
        assertEquals(category.getDescription(), firstCategory.description());
    }

    @Test
    @DisplayName("GET /categories/{id} – should return category by id")
    void getCategoryById_ShouldReturnSpecificCategory() {

        ResponseEntity<CategoryResponseDto> response = testRestTemplate.getForEntity(
                "/categories/" + category.getId(), CategoryResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        CategoryResponseDto responseDto = response.getBody();

        assertEquals(category.getName(), responseDto.name());
        assertEquals(category.getDescription(), responseDto.description());
    }

    @Test
    @DisplayName("POST /categories – should create new category")
    void createCategory_shouldCreateNewCategory() {

        ResponseEntity<CategoryResponseDto> response = testRestTemplate.postForEntity(
                "/categories", newCategoryRequest, CategoryResponseDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        CategoryResponseDto createdResponse = response.getBody();

        assertEquals(newCategoryRequest.name(), createdResponse.name());
        assertEquals(newCategoryRequest.description(), createdResponse.description());
    }

    @Test
    @DisplayName("PUT /categories/{id} – should update existing category")
    void updateCategory_shouldUpdateExistingCategory() {

        testRestTemplate.put("/categories/", category.getId(), updatedCategoryRequest);

        Category updatedCategory = categoryRepository.findById(category.getId()).orElseThrow();

        assertEquals(updatedCategoryRequest.name(), updatedCategory.getName());
        assertEquals(updatedCategoryRequest.description(), updatedCategory.getDescription());
    }

    @Test
    @DisplayName("DELETE /categories/{id} – should soft delete category")
    void deleteCategory_shouldSoftDeleteCategory() {

        testRestTemplate.delete("/categories/", category.getId());

        Category deletedCategory = categoryRepository.findById(category.getId()).orElseThrow();

        assertTrue(deletedCategory.isDeleted(), "Category should be soft deleted!");
    }
}
