package com.example.basicbookstoreprojectnew.model.service.impl;

import com.example.basicbookstoreprojectnew.dto.BookDto;
import com.example.basicbookstoreprojectnew.dto.BookDtoCategoryResponse;
import com.example.basicbookstoreprojectnew.dto.BookSearchParametersDto;
import com.example.basicbookstoreprojectnew.dto.CreateBookRequestDto;
import com.example.basicbookstoreprojectnew.mapper.BookMapper;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.Category;
import com.example.basicbookstoreprojectnew.model.repository.BookRepository;
import com.example.basicbookstoreprojectnew.model.repository.CategoryRepository;
import com.example.basicbookstoreprojectnew.model.repository.impl.SpecificationBuilderImpl;
import com.example.basicbookstoreprojectnew.model.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;

    private final SpecificationBuilderImpl specificationBuilderImpl;

    @Override
    public BookDto save(CreateBookRequestDto createBookRequestDto) {
        Book book = bookMapper.toEntity(createBookRequestDto);

        Set<Category> categories = createBookRequestDto.categoryIds().stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Category with id " + id + " not found!")))
                .collect(Collectors.toSet());

        book.setCategories(categories);
        categories.forEach(category -> category.getBooks().add(book));

        Book savedBook = bookRepository.save(book);

        return bookMapper.toDto(savedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toDto);
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto updateBook(Long id, CreateBookRequestDto createBookRequestDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id " + id));
        bookMapper.updateBookFromDto(createBookRequestDto, book);
        bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    public Page<BookDto> search(BookSearchParametersDto parametersDto, Pageable pageable) {
        Specification<Book> bookSpecification = specificationBuilderImpl.build(parametersDto);
        return bookRepository.findAll(bookSpecification, pageable)
                .map(bookMapper::toDto);
    }

    @Override
    public Page<BookDtoCategoryResponse> findAllBooksByCategoryId(
            Long categoryId, Pageable pageable) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Can`t to find this category!: "
                        + categoryId));

        Page<Book> page = bookRepository.findAllByCategories_Id(categoryId, pageable);

        return page.map(bookMapper::toDtoCertainCategory);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id: " + id));

        book.setDeleted(true);
        bookRepository.save(book);
    }
}
