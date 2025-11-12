package com.example.basicbookstoreprojectnew.controller;

import com.example.basicbookstoreprojectnew.dto.BookDto;
import com.example.basicbookstoreprojectnew.dto.BookSearchParametersDto;
import com.example.basicbookstoreprojectnew.dto.CreateBookRequestDto;
import com.example.basicbookstoreprojectnew.model.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Book management", description = "Endpoints for book information response")
public class BookController {
    private final BookService bookService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get information for all books",
            description = "Support pagination, sorting and filtering for books")
    public Page<BookDto> getAllBooks(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get information for a single books",
            description = "Get information for a one particular book for the id")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new book",
            description = "Insert a new book information into DB")
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto bookRequestDto) {
        return bookService.save(bookRequestDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update book",
            description = "Update already existing book for a new information")
    public BookDto updateBook(@PathVariable Long id,
                              @RequestBody CreateBookRequestDto updatedBook) {
        return bookService.updateBook(id, updatedBook);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Search books for a certain criteria",
            description = "Search books for a certain sighs using pagination and sorting")
    public Page<BookDto> searchBooks(BookSearchParametersDto searchParameters, Pageable pageable) {
        return bookService.search(searchParameters, pageable);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete book",
            description = "Delete certain book for an id from the DB")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}
