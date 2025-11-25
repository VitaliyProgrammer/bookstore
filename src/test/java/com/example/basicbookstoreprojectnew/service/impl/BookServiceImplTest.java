package com.example.basicbookstoreprojectnew.service.impl;

import com.example.basicbookstoreprojectnew.dto.BookDto;
import com.example.basicbookstoreprojectnew.dto.BookDtoCategoryResponse;
import com.example.basicbookstoreprojectnew.dto.BookSearchParametersDto;
import com.example.basicbookstoreprojectnew.dto.CreateBookRequestDto;
import com.example.basicbookstoreprojectnew.exception.EntityNotFoundException;
import com.example.basicbookstoreprojectnew.mapper.BookMapper;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.Category;
import com.example.basicbookstoreprojectnew.model.repository.BookRepository;
import com.example.basicbookstoreprojectnew.model.repository.CategoryRepository;
import com.example.basicbookstoreprojectnew.model.service.BookService;
import java.math.BigDecimal;
import java.util.List;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@Transactional
public class BookServiceImplTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;

    private Category category;

    @BeforeEach
    void setUp() {

        category = new Category();
        category.setName("Programming");
        category.setDescription("About programming");
        categoryRepository.save(category);
    }

    @Test
    @DisplayName("save: save book with categories")
    void saveBook() {

        CreateBookRequestDto saveBook = new CreateBookRequestDto(
                "Clean Architecture",
                "Robert C. Martin",
                "About clean architecture",
                "9780134494166",
                BigDecimal.valueOf(50.0),
                "http://example.com/cover.jpg",
                List.of(category.getId())
        );

        BookDto savedBookDto = bookService.save(saveBook);

        assertThat(savedBookDto.id()).isNotNull();
        assertThat(savedBookDto.title()).isEqualTo("Clean Architecture");
        assertThat(savedBookDto.categoryIds()).contains(category.getId());

        Book book = bookRepository.findById(savedBookDto.id())
                .orElseThrow(() -> new EntityNotFoundException("Book not found during test!"));
        assertThat(book.getCategories()).hasSize(1);
    }

    @Test
    @DisplayName("save: throw exception when category does not exist")
    void saveBook_categoryNotFound() {

        CreateBookRequestDto saveBook = new CreateBookRequestDto(
                "Unknown book",
                "Unknown book",
                "Unknown book",
                "0000000000000",
                BigDecimal.valueOf(0.00),
                "http://example.com/cover.jpg",
                List.of(999L)
        );

        assertThrows(EntityNotFoundException.class, () -> bookService.save(saveBook));
    }

    @Test
    @DisplayName("findAll: return paginated list of books")
    void findAllBooks() {

        for (int i = 1; i <= 5; i++) {

            String uniqueIsbn = String.format("11122233366%02d", i);

            CreateBookRequestDto savePaginationListOfBook = new CreateBookRequestDto(
                    "Book " + i,
                    "Author " + i,
                    "Description " + i,
                    uniqueIsbn,
                    BigDecimal.valueOf(10 + i),
                    "http://example.com/cover.jpg",
                    List.of(category.getId())
            );
            bookService.save(savePaginationListOfBook);
        }

        Pageable pageable = PageRequest.of(0, 2);

        Page<BookDto> page = bookService.findAll(pageable);

        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("findById: return existing book")
    void findById() {

        CreateBookRequestDto saveBook = new CreateBookRequestDto(
                "Clean Architecture",
                "Robert C. Martin",
                "About clean architecture",
                "9780134494166",
                BigDecimal.valueOf(50.0),
                "http://example.com/cover.jpg",
                List.of(category.getId())
        );

        BookDto savedBookDto = bookService.save(saveBook);

        BookDto foundBookById = bookService.findById(savedBookDto.id());

        assertThat(foundBookById.id()).isEqualTo(savedBookDto.id());
        assertThat(foundBookById.title()).isEqualTo(savedBookDto.title());
    }

    @Test
    @DisplayName("findById: throw exception when book not found")
    void findById_bookNotFound() {

        assertThrows(EntityNotFoundException.class, () -> bookService.findById(10000L));
    }

    @Test
    @DisplayName("updateBook: update existing book")
    void updateBook() {

        BookDto savedBook = bookService.save(
                new CreateBookRequestDto(
                        "Clean Architecture",
                        "Robert C. Martin",
                        "About clean architecture",
                        "9780134494166",
                        BigDecimal.valueOf(50.0),
                        "http://example.com/cover.jpg",
                        List.of(category.getId())
                )
        );

        CreateBookRequestDto updateRequest = new CreateBookRequestDto(
                "New Title",
                "New author",
                "About new description",
                "9999999999999",
                BigDecimal.valueOf(100.0),
                "http://new_example.com/cover.jpg",
                List.of(category.getId())
        );

        BookDto updateBook = bookService.updateBook(savedBook.id(), updateRequest);

        assertThat(updateBook.title()).isEqualTo("New Title");
        assertThat(updateBook.author()).isEqualTo("New author");

    }

    @Test
    @DisplayName("updateBook: update existing book")
    void updateBook_notFoundBook() {

        CreateBookRequestDto saveBook = new CreateBookRequestDto(
                "Non-existent book",
                "Non-existent author",
                "Non-existent description",
                "0000000000000",
                BigDecimal.valueOf(00.0),
                "http://unknown_example.com/cover.jpg",
                List.of(category.getId())
        );

        assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBook(9999L, saveBook));
    }

    @Test
    @DisplayName("updateBook: search existing book")
    void searchBook() {
        bookService.save(
                new CreateBookRequestDto(
                        "Clean Architecture",
                        "Robert C. Martin",
                        "About clean architecture",
                        "9780134494166",
                        BigDecimal.valueOf(50.0),
                        "http://example.com/cover.jpg",
                        List.of(category.getId())
                )
        );

        BookSearchParametersDto searchParameters = new BookSearchParametersDto(
                new String[]{"Clean Architecture"},
                new String[]{"Robert C. Martin"},
                new String[]{"50.0"}
        );

        Page<BookDto> result = bookService
                .search(searchParameters, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("updateBook: update existing book")
    void searchBook_noneExistentParameters() {

        BookSearchParametersDto searchParameters = new BookSearchParametersDto(
                new String[]{"None-existent"},
                new String[]{"None-existent"},
                new String[]{"99999"}
        );

        Page<BookDto> result = bookService.search(searchParameters,
                PageRequest.of(0, 10));

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("findAllBooksByCategoryId: return books by category")
    void findAllBooksByCategoryId() {

        bookService.save(
                new CreateBookRequestDto(
                        "Clean Architecture",
                        "Robert C. Martin",
                        "About clean architecture",
                        "9780134494166",
                        BigDecimal.valueOf(50.0),
                        "http://example.com/cover.jpg",
                        List.of(category.getId())
                )
        );

        Page<BookDtoCategoryResponse> pageResponse = bookService
                .findAllBooksByCategoryId(category.getId(),
                        PageRequest.of(0, 10));

        assertThat(pageResponse.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("findAllBooksByCategoryId: throw when category is not exists")
    void findAllBooksByCategoryId_notFound() {

        assertThrows(EntityNotFoundException.class,
                () -> bookService.findAllBooksByCategoryId(9999L,
                        PageRequest.of(0, 10)));
    }

    @Test
    @DisplayName("deleteBook: set deleted flag")
    void softDeleteBook() {
        BookDto savedBook = bookService.save(
                new CreateBookRequestDto(
                        "Clean Architecture",
                        "Robert C. Martin",
                        "About clean architecture",
                        "9780134494166",
                        BigDecimal.valueOf(50.0),
                        "http://example.com/cover.jpg",
                        List.of(category.getId())
                )
        );

        bookService.deleteBook(savedBook.id());

        Book deletedBook = bookRepository.findById(savedBook.id()).orElseThrow();

        assertThat(deletedBook.isDeleted()).isTrue();
    }
}
