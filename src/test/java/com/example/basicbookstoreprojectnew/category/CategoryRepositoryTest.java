package com.example.basicbookstoreprojectnew.category;

import com.example.basicbookstoreprojectnew.model.Category;
import com.example.basicbookstoreprojectnew.model.repository.CategoryRepository;
import com.example.basicbookstoreprojectnew.testcontainer.CustomMySqlContainer;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql(scripts = "sql/categories/setup.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "sql/categories/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_CLASS)
public class CategoryRepositoryTest {

    @Container
    public static CustomMySqlContainer customMySqlContainer = CustomMySqlContainer.getInstance();

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Checking 'Science Fiction' in DB")
    void testCategoryExistsByName_shouldReturnCategory() {

        Category category = categoryRepository.findAll().stream()
                .filter(existsCategory -> "Science Fiction".equals(existsCategory.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Category not found in DB!"));

        assertEquals("Science Fiction", category.getName());
    }

    @Test
    @DisplayName("Soft delete - mark category as deleted")
    void softDelete_ShouldNotedAsCategoryIsDeleted() {
        Category category = categoryRepository.findAll().stream()
                .filter(existsCategory -> "Science Fiction".equals(existsCategory.getName()))
                .findFirst()
                .orElseThrow();

        category.setDeleted(true);
        categoryRepository.save(category);

        Category categoryIsDeleted =
                categoryRepository.findById(category.getId()).orElseThrow();
        assertTrue(categoryIsDeleted.isDeleted(), "Category must be marked as deleted!");
    }

    @Test
    @DisplayName("Relation many-to-many: category should load books")
    void manyToMany_ShouldLoadBooks() {
        Category category = categoryRepository.findAll().stream()
                .filter(existsCategory -> "Science Fiction".equals(existsCategory.getName()))
                .findFirst()
                .orElseThrow();

        assertNotNull(category, "Books should not be null!");
        assertEquals("Science Fiction", category.getName());
    }

    @Test
    void testContainerIsRunning() {
        assertTrue(customMySqlContainer.isRunning());
    }
}
