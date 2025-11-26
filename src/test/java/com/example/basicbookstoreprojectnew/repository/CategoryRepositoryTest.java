package com.example.basicbookstoreprojectnew.repository;

import com.example.basicbookstoreprojectnew.model.Category;
import com.example.basicbookstoreprojectnew.testcontainer.CustomMySqlContainer;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Container
    private static final CustomMySqlContainer myCustomSqlContainer =
            CustomMySqlContainer.getInstance();

    private Category category1, category2, savedCategory;

    @BeforeEach
    void setUp() {

        category1 = new Category();
        category1.setName("Programming");
        category1.setDescription("About programming");
        category1.setDeleted(false);

        category2 = new Category();
        category2.setName("Action");
        category2.setDescription("About action");

        savedCategory = categoryRepository.save(category1);
    }

    @Test
    @DisplayName("saveCategory: successfully")
    void saveCategory() {

        Category savedCategory = categoryRepository.save(category1);

        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo(category1.getName());
    }

    @Test
    @DisplayName("findById: successfully")
    void findById() {

        Optional<Category> foundCategoryById = categoryRepository.findById(savedCategory.getId());

        assertThat(foundCategoryById).isPresent();
        assertThat(foundCategoryById.get().getName()).isEqualTo(category1.getName());
    }

    @Test
    @DisplayName("findById: non-exists category id")
    void findById_notFound() {

        Optional<Category> result = categoryRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAll: success")
    void findAll() {

        categoryRepository.saveAll(List.of(category1, category2));

        List<Category> categoryList = categoryRepository.findAll();

        assertEquals(2, categoryList.size());
    }

    @Test
    @DisplayName("deleteCategory: success")
    void deleteCategory() {

        categoryRepository.delete(savedCategory);

        assertThat(categoryRepository.findById(savedCategory.getId())).isEmpty();
    }

    @Test
    @DisplayName("deleteCategoryById: success")
    void deleteCategoryById() {

        categoryRepository.deleteById(savedCategory.getId());

        assertThat(categoryRepository.existsById(savedCategory.getId())).isFalse();
    }

    @Test
    @DisplayName("existsById: success")
    void existsById() {

        boolean categoryIsExists = categoryRepository.existsById(savedCategory.getId());

        assertThat(categoryIsExists).isTrue();
    }

    @Test
    @DisplayName("existsById: non-exists")
    void existsById_notFound() {

        boolean idIsNotExists = categoryRepository.existsById(999L);

        assertThat(idIsNotExists).isFalse();
    }
}
