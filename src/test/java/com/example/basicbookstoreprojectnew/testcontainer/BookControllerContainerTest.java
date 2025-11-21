package com.example.basicbookstoreprojectnew.testcontainer;

import com.example.basicbookstoreprojectnew.dto.BookDto;
import com.example.basicbookstoreprojectnew.dto.CreateBookRequestDto;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.Category;
import com.example.basicbookstoreprojectnew.model.repository.BookRepository;
import com.example.basicbookstoreprojectnew.model.repository.CategoryRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
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
public class BookControllerContainerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Book book;

    private Category category;

    CreateBookRequestDto createRequestDto;

    CreateBookRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setTitle("The Proving Ground");
        book.setAuthor("Michael Connelly");
        book.setDescription("Mickey Haller, the Lincoln Lawyer, takes on a civil lawsuit");
        book.setIsbn("5532190007443");
        book.setPrice(BigDecimal.valueOf(20.0));
        book.setCategories(Set.of(category));
        category.getBooks().add(book);
        bookRepository.save(book);

        createRequestDto = new CreateBookRequestDto(
                "New created book",
                "Java Developer",
                "About new new book",
                "3335551006843",
                BigDecimal.valueOf(30.0),
                null,
                List.of(category.getId())
        );

        updateRequestDto = new CreateBookRequestDto(
                "Updated existing book",
                book.getAuthor(),
                book.getDescription(),
                book.getIsbn(),
                BigDecimal.valueOf(25.0),
                null,
                List.of(category.getId())
        );
    }

    @Test
    @DisplayName("GET /books - should return list of books")
    void getAllBooks_shouldReturnsListOfBooks() {

        ResponseEntity<BookDto[]> response =
                testRestTemplate.getForEntity("/books", BookDto[].class);

        BookDto[] books = response.getBody();
        assertNotNull(books, "Response body should not be null!");

        assertTrue(books.length > 0);

        BookDto firstBook = books[0];
        assertEquals(book.getTitle(), firstBook.title());
        assertEquals(book.getAuthor(), firstBook.author());
        assertEquals(book.getDescription(), firstBook.description());
        assertEquals(book.getIsbn(), firstBook.isbn());
        assertEquals(BigDecimal.valueOf(20.0), firstBook.price());
    }

    @Test
    @DisplayName("GET /books/{id} - should return book by id")
    void getBookById_shouldReturnsSpecificBook() {

        ResponseEntity<BookDto> response =
                testRestTemplate.getForEntity("/books/" + book.getId(), BookDto.class);

        BookDto books = response.getBody();
        assertNotNull(books, "Response body should not be null!");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(book.getTitle(), books.title());
        assertEquals(book.getAuthor(), books.author());
        assertEquals(book.getDescription(), books.description());
        assertEquals(book.getIsbn(), books.isbn());
        assertEquals(BigDecimal.valueOf(20.0), books.price());
    }

    @Test
    @DisplayName("POST /books - create a new book")
    void createBook_shouldCreatedNewBook() {

        ResponseEntity<BookDto> response =
                testRestTemplate.postForEntity("/books", createRequestDto, BookDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createRequestDto.title(), response.getBody().title());
        assertEquals(createRequestDto.author(), response.getBody().author());
        assertEquals(createRequestDto.description(), response.getBody().description());
        assertEquals(createRequestDto.isbn(), response.getBody().isbn());
        assertEquals(BigDecimal.valueOf(30.0), response.getBody().price());
    }

    @Test
    @DisplayName("PUT /books/{id} - update existing book")
    void updateBook_shouldUpdateBook() {

        testRestTemplate.put("/books/" + book.getId(), updateRequestDto);

        Book updatedExistingBook = bookRepository.findById(book.getId()).orElseThrow();

        assertEquals("Updated existing book", updatedExistingBook.getTitle());
        assertEquals(BigDecimal.valueOf(25.0), updatedExistingBook.getPrice());
    }

    @Test
    @DisplayName("DELETE /books/{id} - delete existing book")
    void deleteBook_shouldDeleteBook() {

        testRestTemplate.delete("/books/" + book.getId());

        Book existsBook = bookRepository.findById(book.getId()).orElseThrow();

        assertTrue(existsBook.isDeleted());
    }
}
