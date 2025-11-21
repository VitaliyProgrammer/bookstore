package com.example.basicbookstoreprojectnew.book;

import com.example.basicbookstoreprojectnew.config.DisableSecurityConfiguration;
import com.example.basicbookstoreprojectnew.controller.BookController;
import com.example.basicbookstoreprojectnew.dto.BookDto;
import com.example.basicbookstoreprojectnew.dto.CreateBookRequestDto;
import com.example.basicbookstoreprojectnew.model.service.BookService;
import com.example.basicbookstoreprojectnew.security.JwtUtil;
import com.example.basicbookstoreprojectnew.security.jwt.JwtAuthenticationFilter;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.BeforeTest;

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

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(DisableSecurityConfiguration.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private BookDto bookDto;

    private CreateBookRequestDto requestDto;

    @BeforeTest
    @DisplayName("Init data for testing")
    void setUp() {
        bookDto = new BookDto(
                1L,
                "The Proving Ground",
                "Michael Connelly",
                "Mickey Haller, the Lincoln Lawyer, takes on a civil lawsuit",
                "5532190007443",
                BigDecimal.valueOf(20.0),
                null,
                List.of(1L)
        );

        requestDto = new CreateBookRequestDto(
                "The Proving Ground",
                "Michael Connelly",
                "Mickey Haller, the Lincoln Lawyer, takes on a civil lawsuit",
                "5532190007443",
                BigDecimal.valueOf(20.0),
                null,
                List.of(1L)
        );
    }

    @Test
    @DisplayName("GET /books - should return list of books")
    void getAllBooks_shouldReturnsListOfBooks() throws Exception {

        when(bookService.findAll(any())).thenReturn(new PageImpl<>(List.of(bookDto)));

        mockMvc.perform(get("/books")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(bookDto.id()))
                .andExpect(jsonPath("$[0].title").value(bookDto.title()))
                .andExpect(jsonPath("$[0].author").value(bookDto.author()));
    }

    @Test
    @DisplayName("GET /books/{id} - should return book by id")
    void getBookById_shouldReturnSpecificBook() throws Exception {

        when(bookService.findById(1L)).thenReturn(bookDto);

        mockMvc.perform(get("/books/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookDto.id()))
                .andExpect(jsonPath("$.title").value(bookDto.title()))
                .andExpect(jsonPath("$.author").value(bookDto.author()));
    }

    @Test
    @DisplayName("POST /books - should return created new book")
    void createBook_shouldCreatedBook() throws Exception {

        when(bookService.save(eq(requestDto))).thenReturn(bookDto);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookDto.id()))
                .andExpect(jsonPath("$.title").value(bookDto.title()))
                .andExpect(jsonPath("$.author").value(bookDto.author()));
    }

    @Test
    @DisplayName("PUT /books/{id} - update existing book")
    void updateBook_shouldUpdateBook() throws Exception {

        BookDto updatedBookDto = new BookDto(
                1L,
                "Updated new title",
                "Michael Connelly",
                bookDto.description(),
                bookDto.isbn(),
                bookDto.price(),
                bookDto.coverImage(),
                bookDto.categoryIds()
        );

        CreateBookRequestDto updatedRequest = new CreateBookRequestDto(
                "Updated new title",
                "Michael Connelly",
                bookDto.description(),
                bookDto.isbn(),
                BigDecimal.valueOf(20.0),
                bookDto.coverImage(),
                bookDto.categoryIds()
        );

        when(bookService.updateBook(eq(1L), eq(updatedRequest))).thenReturn(updatedBookDto);

        mockMvc.perform(put("/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRequest)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated new title"));
    }

    @Test
    @DisplayName("DELETE /books/{id} - delete existing book")
    void deleteBook_shouldDeleteBook() throws Exception {

        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/books/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(1L);
    }
}
