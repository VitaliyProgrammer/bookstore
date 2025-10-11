package com.example.basicbookstoreprojectnew.model.service;

import com.example.basicbookstoreprojectnew.dto.BookDto;
import com.example.basicbookstoreprojectnew.dto.BookSearchParametersDto;
import com.example.basicbookstoreprojectnew.dto.CreateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto bookRequestDto);

    Page<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    BookDto updateBook(Long id, CreateBookRequestDto createBookRequestDto);

    Page<BookDto> search(BookSearchParametersDto parametersDto, Pageable pageable);

    void deleteBook(Long id);
}
