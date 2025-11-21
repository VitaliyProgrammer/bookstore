package com.example.basicbookstoreprojectnew.book;

import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.Category;
import com.example.basicbookstoreprojectnew.model.repository.BookRepository;
import com.example.basicbookstoreprojectnew.model.repository.CategoryRepository;
import com.example.basicbookstoreprojectnew.testcontainer.CustomMySqlContainer;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "sql/books/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "sql/books/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_CLASS)
public class BookRepositoryTest {

    @Container
    public static CustomMySqlContainer customMySqlContainer = CustomMySqlContainer.getInstance();

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findAllByCategories_Id_ShouldReturnBooks() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Book> page = bookRepository.findAllByCategories_Id(1L, pageable);

        assertEquals(1, page.getContent().size());

        assertEquals("The Hobbit", page.getContent().get(0).getTitle());
    }

    @Test
    void softDelete_ShouldNotReturnDeletedBooks() {

        Book book = bookRepository.findById(10L).get();

        book.setDeleted(true);
        bookRepository.save(book);

        List<Book> allBooks = bookRepository.findAll();
        assertTrue(allBooks.isEmpty());
    }

    @Test
    void manyToMany_ShouldLoadCategories() {

        Book book = bookRepository.findById(10L).get();

        assertEquals(1, book.getCategories().size());

        Category category = book.getCategories().iterator().next();

        assertEquals("Science Fiction", category.getName());
    }

    @Test
    void testRun() {
        assert (customMySqlContainer.isRunning());
    }
}
