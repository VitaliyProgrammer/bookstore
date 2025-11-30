package com.example.basicbookstoreprojectnew.controller;

import com.example.basicbookstoreprojectnew.dto.CategoryRequestDto;
import com.example.basicbookstoreprojectnew.dto.CategoryResponseDto;
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

    private CategoryRequestDto categoryRequest;

    private CategoryResponseDto categoryResponse;

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
        category.setDescription("About programming");
        category.setDeleted(false);

        categoryRepository.save(category);

        book = new Book();
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setDescription("About effective programming");
        book.setIsbn("9780134685991");
        book.setPrice(BigDecimal.valueOf(30.0));
        book.setCategories(new HashSet<>(Set.of(category)));
        category.getBooks().add(book);

        bookRepository.save(book);
    }

    @Test
    @DisplayName("GET /categories - should return all categories")
    void getAllCategories() throws Exception {

        mockMvc.perform(get("/categories")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value(category.getName()));
    }

    @Test
    @DisplayName("GET /categories{id} - should return specific categories")
    void getAllCategoryById() throws Exception {

        mockMvc.perform(get("/categories/{id}", category.getId())
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value(category.getName()));
    }

    @Test
    @DisplayName("GET /categories{id} - non-existing category")
    void getAllCategoryById_notFound() throws Exception {

        mockMvc.perform(get("/categories/{id}", 999L)
                        .header("Authorization", userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /categories/{id}/books - should return books of category")
    void getBooksByCategory() throws Exception {

        mockMvc.perform(get("/categories/{id}/books", category.getId())
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value(book.getTitle()))
                .andExpect(jsonPath("$.content[0].author").value(book.getAuthor()));
    }

    @Test
    @DisplayName("POST /categories - should create category(ADMIN)")
    void createCategory() throws Exception {

        CategoryRequestDto categoryRequest = new CategoryRequestDto(
                "Action",
                "About action"
        );

        mockMvc.perform(post("/categories")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(categoryRequest.name()))
                .andExpect(jsonPath("$.description").value(categoryRequest.description()));
    }

    @Test
    @DisplayName("PUT /categories/{id} - should update existing category(ADMIN)")
    void updateCategory() throws Exception {

        CategoryRequestDto updateRequest = new CategoryRequestDto(
                "Thriller",
                "About thriller"
        );

        mockMvc.perform(put("/categories/{id}", category.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updateRequest.name()))
                .andExpect(jsonPath("$.description").value(updateRequest.description()));
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
