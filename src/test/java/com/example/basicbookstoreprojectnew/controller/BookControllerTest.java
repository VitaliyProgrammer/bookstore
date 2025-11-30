package com.example.basicbookstoreprojectnew.controller;

import com.example.basicbookstoreprojectnew.dto.BookSearchParametersDto;
import com.example.basicbookstoreprojectnew.dto.CreateBookRequestDto;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.Category;
import com.example.basicbookstoreprojectnew.model.repository.BookRepository;
import com.example.basicbookstoreprojectnew.model.repository.CategoryRepository;
import com.example.basicbookstoreprojectnew.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private String userToken;

    private String adminToken;

    private Book book;

    private Category category;

    @BeforeEach
    void setUp() {

        userToken = "Bearer " + jwtUtil.generateToken(
                "user@test.com",
                List.of("USER")
        );

        adminToken = "Bearer " + jwtUtil.generateToken(
                "admin@gmail.com",
                List.of("ADMIN")
        );

        bookRepository.deleteAll();
        categoryRepository.deleteAll();

        category = new Category();
        category.setName("Programming");
        category.setDescription("About programming books");
        categoryRepository.save(category);

        book = new Book();
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setDescription("About effective programming on Java");
        book.setPrice(BigDecimal.valueOf(30.00));
        book.setIsbn("9780134685991");
        book.setCategories(new HashSet<>(Set.of(category)));

        bookRepository.save(book);
    }

    @Test
    @DisplayName("GET /books - should return list of books")
    public void getAllBooks() throws Exception {

        mockMvc.perform(get("/books")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value(book.getTitle()))
                .andExpect(jsonPath("$.content[0].author").value(book.getAuthor()));
    }

    @Test
    @DisplayName("GET /books/{id}: should return specific book")
    void getBookById_IsExisting() throws Exception {

        mockMvc.perform(get("/books/{id}", book.getId())
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(book.getTitle()))
                .andExpect(jsonPath("$.author").value(book.getAuthor()));
    }

    @Test
    @DisplayName("GET /books/{id}: non-existing book should return 404")
    void getBookId_noneExisting() throws Exception {

        mockMvc.perform(get("/books/{id}", 555L)
                        .header("Authorization", userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /books: should create new book(ADMIN)")
    void createBook() throws Exception {

        CreateBookRequestDto postRequest = new CreateBookRequestDto(
                "Clean Architecture",
                "Robert C. Martin",
                "About clean architecture",
                "9780134494166",
                BigDecimal.valueOf(50.0),
                "http://example.com/cover.jpg",
                List.of(category.getId())
        );

        mockMvc.perform(post("/books")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(postRequest.title()))
                .andExpect(jsonPath("$.author").value(postRequest.author()))
                .andExpect(jsonPath("$.description").value(postRequest.description()))
                .andExpect(jsonPath("$.isbn").value(postRequest.isbn()))
                .andExpect(jsonPath("$.price").value(postRequest.price().doubleValue()))
                .andExpect(jsonPath("$.categoryIds[0]").value(category.getId()));
    }


    @Test
    @DisplayName("PUT /books/{id}: should update existing book(ADMIN)")
    void updateBook_isExisting() throws Exception {

        CreateBookRequestDto putRequest = new CreateBookRequestDto(
                "Effective Java 3rd edition",
                "Joshua Bloch",
                "About clean architecture, 3rd edition",
                book.getIsbn(),
                BigDecimal.valueOf(100.0),
                "http://example.com/cover.jpg",
                List.of(category.getId())
        );

        mockMvc.perform(put("/books/{id}", book.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(putRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(putRequest.title()))
                .andExpect(jsonPath("$.author").value(putRequest.author()))
                .andExpect(jsonPath("$.description").value(putRequest.description()))
                .andExpect(jsonPath("$.price").value(putRequest.price().doubleValue()));
    }

    @Test
    @DisplayName("PUT /books: non-existing book should return status: 404(ADMIN)")
    void updateBook_nonExisting() throws Exception {

        CreateBookRequestDto updateRequest = new CreateBookRequestDto(
                "Some Book",
                "Some Author",
                "About some description",
                "0000000000000",
                BigDecimal.valueOf(75.0),
                null,
                List.of(category.getId())
        );

        mockMvc.perform(put("/books/{id}", 999L)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /books/search: should return books matching search criteria")
    void searchBook() throws Exception {

        BookSearchParametersDto searchParamsRequest = new BookSearchParametersDto(
                new String[]{"Effective Java"},
                new String[]{"Joshua Bloch"},
                new String[]{"30.00"}
        );

        mockMvc.perform(get("/books/search")
                        .header("Authorization", userToken)
                        .param("title", searchParamsRequest.title())
                        .param("author", searchParamsRequest.author())
                        .param("price", searchParamsRequest.price())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value(book.getTitle()))
                .andExpect(jsonPath("$.content[0].author").value(book.getAuthor()))
                .andExpect(jsonPath("$.content[0].categoryIds[0]")
                        .value(category.getId()));
    }

    @Test
    @DisplayName("GET /books/search: there is no parameter")
    void searchBook_nonExistentParameter() throws Exception {

        BookSearchParametersDto unknownParameterRequest = new BookSearchParametersDto(
                new String[]{"Unknown title"},
                new String[]{"Unknown author"},
                new String[]{"10000"}
        );

        mockMvc.perform(get("/books/search")
                        .header("Authorization", userToken)
                        .param("title", unknownParameterRequest.title())
                        .param("author", unknownParameterRequest.author())
                        .param("price", unknownParameterRequest.price())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @DisplayName("DELETE /books/{id}: should delete existing book(ADMIN)")
    void deleteBook_Existing() throws Exception {

        mockMvc.perform(delete("/books/{id}", book.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /books/{id}: non-existing book, return status: 404(ADMIN)")
    void deleteBook_nonExisting() throws Exception {
        mockMvc.perform(delete("/books/{id}", 999L)
                        .header("Authorization", adminToken))
                .andExpect(status().isNotFound());
    }
}
