package com.example.basicbookstoreprojectnew.category;

import com.example.basicbookstoreprojectnew.config.DisableSecurityConfiguration;
import com.example.basicbookstoreprojectnew.controller.CategoryController;
import com.example.basicbookstoreprojectnew.dto.CategoryRequestDto;
import com.example.basicbookstoreprojectnew.dto.CategoryResponseDto;
import com.example.basicbookstoreprojectnew.model.service.BookService;
import com.example.basicbookstoreprojectnew.model.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(DisableSecurityConfiguration.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryRequestDto createRequest;

    private CategoryResponseDto createResponse;

    private CategoryRequestDto updateRequest;

    private CategoryResponseDto updateResponse;

    @BeforeEach
    @DisplayName("Init data for testing")
    void setUp() {
        createRequest = new CategoryRequestDto(
                "Science Fiction",
                "About modern science"
        );

        createResponse = new CategoryResponseDto(
                1L,
                "Science Fiction",
                "About modern science"
        );

        updateRequest = new CategoryRequestDto(
                "Updated category",
                "Updated category for testing"
        );

        updateResponse = new CategoryResponseDto(
                1L,
                "Updated category",
                "Updated category for testing"
        );
    }

    @Test
    @DisplayName("GET /categories - should return list of categories")
    void getAllCategories_shouldReturnListOfCategories() throws Exception {

        when(categoryService.findAll(any()))
                .thenReturn(new PageImpl<>(List.of(createResponse)));

        mockMvc.perform(get("/categories")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(createResponse.id()))
                .andExpect(jsonPath("$.content[0].name").value(createResponse.name()));
    }

    @Test
    @DisplayName("GET /categories/{id} - should return category by id")
    void getCategoryById_shouldReturnSpecificCategory() throws Exception {

        when(categoryService.getById(1L)).thenReturn(createResponse);

        mockMvc.perform(get("/categories/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createResponse.id()))
                .andExpect(jsonPath("$.name").value(createResponse.name()));
    }

    @Test
    @DisplayName("POST /categories - should return create category")
    void createCategory_shouldCreateSpecificCategory() throws Exception {

        when(categoryService.create(any())).thenReturn(createResponse);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(createResponse.id()))
                .andExpect(jsonPath("$.name").value(createResponse.name()));
    }


    @Test
    @DisplayName("PUT /categories/{id} should update category")
    void updateCategory_shouldUpdatedCategory() throws Exception {

        when(categoryService.update(eq(1L), any())).thenReturn(updateResponse);

        mockMvc.perform(put("/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(createResponse.name()))
                .andExpect(jsonPath("$.description").value(createResponse.description()));
    }

    @Test
    @DisplayName("DELETE /categories/{id} should delete category")
    void deleteCategory_shouldDeleteCategory() throws Exception {

        doNothing().when(categoryService).deleteById(1L);

        mockMvc.perform(delete("/categories/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteById(1L);
    }
}
