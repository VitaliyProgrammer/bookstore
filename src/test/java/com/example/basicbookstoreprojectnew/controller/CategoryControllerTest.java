package com.example.basicbookstoreprojectnew.controller;

import com.example.basicbookstoreprojectnew.dto.BookDtoCategoryResponse;
import com.example.basicbookstoreprojectnew.dto.CategoryRequestDto;
import com.example.basicbookstoreprojectnew.dto.CategoryResponseDto;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.Category;
import com.example.basicbookstoreprojectnew.repository.BookRepository;
import com.example.basicbookstoreprojectnew.repository.CategoryRepository;
import com.example.basicbookstoreprojectnew.security.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EntityManager entityManager;

    private CategoryRequestDto categoryRequest;

    private CategoryResponseDto categoryResponse;

    private String userToken;

    private String adminToken;

    private Book book;

    private Category category;

    private CategoryResponseDto categoryDto;

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
        category.setDescription("About programming");
        category.setDeleted(false);

        categoryRepository.save(category);

        book = new Book();
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setDescription("About effective programming");
        book.setIsbn("9780134685991");
        book.setPrice(BigDecimal.valueOf(30.0));
        book.setCategories(Set.of(category));

        bookRepository.save(book);

        category.getBooks().add(book);

        categoryDto = new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /categories - should return all categories")
    void getAllCategories() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/categories")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        JsonNode content = root.get("content");

        CategoryResponseDto[] arrayDto =
                objectMapper.treeToValue(content, CategoryResponseDto[].class);

        List<CategoryResponseDto> listDto = List.of(arrayDto);

        assertThat(listDto).hasSize(1);
        assertThat(listDto).containsExactly(categoryDto);
    }

    @Test
    @DisplayName("GET /categories/{id} - should return specific categories")
    void getCategoryById_Existing() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/categories/{id}", category.getId())
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andReturn();

        CategoryResponseDto arrayDto = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(),
                        CategoryResponseDto.class);

        assertThat(arrayDto).isEqualTo(categoryDto);
    }

    @Test
    @DisplayName("GET /categories/{id} - non-existing category")
    void getCategoryById_notFound() throws Exception {

        mockMvc.perform(get("/categories/{id}", 999L)
                        .header("Authorization", userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /categories/{id}/books - should return books of category")
    void getBooksByCategory() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/categories/{id}/books", category.getId())
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        JsonNode content = root.has("content") ? root.get("content") : root;

        BookDtoCategoryResponse[] arrayDto =
                objectMapper.treeToValue(content, BookDtoCategoryResponse[].class);

        assertThat(arrayDto).hasSize(1);

        BookDtoCategoryResponse categoryResponse = arrayDto[0];

        assertThat(categoryResponse.title()).isEqualTo(book.getTitle());
        assertThat(categoryResponse.author()).isEqualTo(book.getAuthor());
        assertThat(categoryResponse.description()).isEqualTo(book.getDescription());
    }

    @Test
    @DisplayName("POST /categories - should create category(ADMIN)")
    void createCategory() throws Exception {

        CategoryRequestDto categoryRequest = new CategoryRequestDto(
                "Action",
                "About action"
        );

        MvcResult mvcResult = mockMvc.perform(post("/categories")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryResponseDto createdDto = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(),
                        CategoryResponseDto.class);

        assertThat(createdDto.name()).isEqualTo(categoryRequest.name());
        assertThat(createdDto.description()).isEqualTo(categoryRequest.description());
    }

    @Test
    @DisplayName("PUT /categories/{id} - should update existing category(ADMIN)")
    void updateCategory() throws Exception {

        CategoryRequestDto updateRequest = new CategoryRequestDto(
                "Thriller",
                "About thriller"
        );

        MvcResult mvcResult = mockMvc.perform(put("/categories/{id}", category.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        CategoryResponseDto updatedDto = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(),
                        CategoryResponseDto.class);

        assertThat(updatedDto.name()).isEqualTo(updateRequest.name());
        assertThat(updatedDto.description()).isEqualTo(updateRequest.description());
    }

    @Test
    @DisplayName("PUT /categories/{id} - non-existing category")
    void updateCategory_notFound() throws Exception {

        CategoryRequestDto updateRequest = new CategoryRequestDto(
                "Unknown title",
                "Unknown description"
        );

        mockMvc.perform(put("/categories/{id}", 999L)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /categories/{id} - should delete category(ADMIN)")
    void deleteCategory() throws Exception {

        mockMvc.perform(delete("/categories/{id}", category.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /categories/{id} - non-existing category")
    void deleteCategory_notFound() throws Exception {
        mockMvc.perform(delete("/categories/{id}", 999L)
                        .header("Authorization", adminToken))
                .andExpect(status().isNotFound());
    }
}
