package com.example.basicbookstoreprojectnew.repository;

import com.example.basicbookstoreprojectnew.exception.CategoryNotFoundException;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.Category;
import com.example.basicbookstoreprojectnew.testcontainer.MyCustomSqlContainer;
import com.example.basicbookstoreprojectnew.testcontainer.MyCustomSqlContainer.CustomMySqlContainer;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
public class BookRepositoryTest {

    @Container
    private static final MyCustomSqlContainer.CustomMySqlContainer myCustomSqlContainer =
            CustomMySqlContainer.getInstance();

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    private Book newBook, savedBook;

    @BeforeEach
    void setUp() {

        bookRepository.deleteAll();
        categoryRepository.deleteAll();
        bookRepository.flush();
        categoryRepository.flush();

        category = new Category();
        category.setName("Programming");
        category.setDescription("About programming");
        categoryRepository.save(category);

        newBook = new Book();
        newBook.setTitle("Effective Java");
        newBook.setAuthor("Joshua Bloch");
        newBook.setDescription("About effective programming");
        newBook.setIsbn("9780134685991");
        newBook.setPrice(BigDecimal.valueOf(30.0));
        newBook.setCategories(new HashSet<>(Set.of(category)));
        category.getBooks().add(newBook);

        savedBook = bookRepository.save(newBook);
    }

    @Test
    @DisplayName("Find all books with pagination")
    void findAllWithPagination() {

        Page<Book> page = bookRepository.findAll(PageRequest.of(0, 10));

        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("Effective Java");
    }

    @Test
    @DisplayName("Find specific book by id")
    void findById() {

        Book foundBook = bookRepository.findById(newBook.getId()).orElse(null);
        assertThat(foundBook).isNotNull();
        assertThat(foundBook.getTitle()).isEqualTo("Effective Java");
    }

    @Test
    @DisplayName("Find all books by category id")
    void findAllByCategoryId() {

        Page<Book> page = bookRepository
                .findAllByCategories_Id(category.getId(), PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("Effective Java");
    }

    @Test
    @DisplayName("Create and save book")
    void saveBook() {

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Effective Java");
    }

    @Test
    @DisplayName("Soft delete book sets deleted flag")
    void softDeleteBook() {

        newBook.setDeleted(true);
        Book deletedBook = bookRepository.save(newBook);

        assertThat(deletedBook.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("Throws exception when category not found")
    void categoryNotFoundForBook() {

        Long nonExistingCategoryById = 9999L;

        assertThrows(Exception.class, () -> categoryRepository.findById(nonExistingCategoryById)
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category with id " + nonExistingCategoryById + " not found!"))
        );
    }
}
