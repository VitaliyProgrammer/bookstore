package com.example.basicbookstoreprojectnew.controller;

import com.example.basicbookstoreprojectnew.dto.BookDto;
import com.example.basicbookstoreprojectnew.dto.BookSearchParametersDto;
import com.example.basicbookstoreprojectnew.dto.CreateBookRequestDto;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.Category;
import com.example.basicbookstoreprojectnew.model.repository.BookRepository;
import com.example.basicbookstoreprojectnew.model.repository.CategoryRepository;
import com.example.basicbookstoreprojectnew.security.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private EntityManager entityManager;

    @Autowired
    private JwtUtil jwtUtil;

    private String userToken;

    private String adminToken;

    private Book book;

    private Category category;

    private BookDto bookDto;

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

        bookDto = new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getIsbn(),
                book.getPrice(),
                book.getCoverImage(),
                book.getCategories().stream()
                        .map(Category::getId)
                        .toList()
        );
    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /books - should return list of books")
    public void getAllBooks() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/books")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        JsonNode content = root.get("content");


        BookDto[] arrayDto = objectMapper.treeToValue(content, BookDto[].class);

        List<BookDto> actualList = List.of(arrayDto);

        assertThat(actualList).hasSize(1);
        assertThat(actualList.get(0)).isEqualTo(bookDto);
    }

    @Test
    @DisplayName("GET /books/{id}: should return specific book")
    void getBookById_IsExisting() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/books/{id}", book.getId())
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andReturn();

        BookDto dto = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), BookDto.class);

        assertThat(dto).isEqualTo(bookDto);
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

        MvcResult mvcResult = mockMvc.perform(post("/books")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto arrayDto = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), BookDto.class);

        assertThat(arrayDto.title()).isEqualTo(postRequest.title());
        assertThat(arrayDto.author()).isEqualTo(postRequest.author());
        assertThat(arrayDto.description()).isEqualTo(postRequest.description());
        assertThat(arrayDto.isbn()).isEqualTo(postRequest.isbn());
        assertThat(arrayDto.price()).isEqualTo(postRequest.price());
        assertThat(arrayDto.categoryIds()).containsExactlyElementsOf(postRequest.categoryIds());
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

        MvcResult mvcResult = mockMvc.perform(put("/books/{id}", book.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(putRequest)))
                .andExpect(status().isOk())
                .andReturn();

        BookDto bookDto = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), BookDto.class);

        assertThat(bookDto.title()).isEqualTo(putRequest.title());
        assertThat(bookDto.author()).isEqualTo(putRequest.author());
        assertThat(bookDto.description()).isEqualTo(putRequest.description());
        assertThat(bookDto.price()).isEqualTo(putRequest.price());
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

        MvcResult mvcResult = mockMvc.perform(get("/books/search")
                        .header("Authorization", userToken)
                        .param("title", searchParamsRequest.title())
                        .param("author", searchParamsRequest.author())
                        .param("price", searchParamsRequest.price())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        JsonNode content = root.get("content");

        BookDto[] arrayDto = objectMapper.treeToValue(content, BookDto[].class);

        List<BookDto> listDto = List.of(arrayDto);

        assertThat(listDto).hasSize(1);
        assertThat(listDto.get(0)).isEqualTo(bookDto);
    }

    @Test
    @DisplayName("GET /books/search: there is no parameter")
    void searchBook_nonExistentParameter() throws Exception {

        BookSearchParametersDto unknownParameterRequest = new BookSearchParametersDto(
                new String[]{"Unknown title"},
                new String[]{"Unknown author"},
                new String[]{"10000"}
        );

        MvcResult mvcResult = mockMvc.perform(get("/books/search")
                        .header("Authorization", userToken)
                        .param("title", unknownParameterRequest.title())
                        .param("author", unknownParameterRequest.author())
                        .param("price", unknownParameterRequest.price())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        JsonNode content = root.get("content");

        BookDto[] arrayDto = objectMapper.treeToValue(content, BookDto[].class);

        List<BookDto> listDto = List.of(arrayDto);

        assertThat(listDto).isEmpty();
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
