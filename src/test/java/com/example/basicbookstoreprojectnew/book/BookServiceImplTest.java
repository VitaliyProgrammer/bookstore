package com.example.basicbookstoreprojectnew.book;

import com.example.basicbookstoreprojectnew.dto.BookDto;
import com.example.basicbookstoreprojectnew.dto.BookDtoCategoryResponse;
import com.example.basicbookstoreprojectnew.dto.BookSearchParametersDto;
import com.example.basicbookstoreprojectnew.dto.CreateBookRequestDto;
import com.example.basicbookstoreprojectnew.exception.EntityNotFoundException;
import com.example.basicbookstoreprojectnew.mapper.BookMapper;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.Category;
import com.example.basicbookstoreprojectnew.model.repository.BookRepository;
import com.example.basicbookstoreprojectnew.model.repository.CategoryRepository;
import com.example.basicbookstoreprojectnew.model.repository.impl.SpecificationBuilderImpl;
import com.example.basicbookstoreprojectnew.model.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl bookServiceImpl;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private SpecificationBuilderImpl specificationBuilderImpl;

    private Book book;

    private BookDto bookDto;

    private CreateBookRequestDto createBookRequestDto;

    private CreateBookRequestDto updateBookRequestDto;

    private Category category;

    @BeforeEach
    @DisplayName("Init data for testing")
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Science Fiction");
        category.setBooks(new HashSet<>());

        book = new Book();
        book.setId(1L);
        book.setTitle("The Proving Ground");
        book.setAuthor("Michael Connelly");
        book.setIsbn("5532190007443");
        book.setPrice(new BigDecimal("20.0"));
        book.setCategories(new HashSet<>());
        book.getCategories().add(category);

        category.getBooks().add(book);

        bookDto = new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getIsbn(),
                book.getPrice(),
                null,
                List.of(category.getId())
        );

        createBookRequestDto = new CreateBookRequestDto(
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getIsbn(),
                book.getPrice(),
                null,
                List.of(category.getId())
        );

        updateBookRequestDto = new CreateBookRequestDto(
                "Updated title",
                "Updated author",
                "Updated description",
                "Updated isbn",
                new BigDecimal("25.0"),
                null,
                List.of(category.getId())
        );

    }

    @Test
    void save_whenBookSaved() {

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        when(bookMapper.toEntity(createBookRequestDto)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        when(bookRepository.save(book)).thenReturn(book);

        BookDto result = bookServiceImpl.save(createBookRequestDto);

        assertNotNull(result);
        assertEquals(book.getId(), result.id());
        verify(bookRepository).save(book);
        verify(categoryRepository).findById(category.getId());
    }

    @Test
    void findAll_shouldReturnAllBooks() {

        Page<Book> page = new PageImpl<>(List.of(book));
        Pageable pageable = PageRequest.of(0, 10);

        when(bookRepository.findAll(pageable)).thenReturn(page);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> result = bookServiceImpl.findAll(pageable);

        assertEquals(1, result.getTotalPages());
        verify(bookRepository).findAll(pageable);
    }

    @Test
    void findById_ifBookExists() {

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookServiceImpl.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(bookRepository).findById(1L);
    }

    @Test
    void findById_shouldReturnThrowException_ifBookNotFound() {

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookServiceImpl.findById(1L));
    }

    @Test
    void updateBook_ifBookDataUpdated() {

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        doNothing().when(bookMapper).updateBookFromDto(createBookRequestDto, book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookServiceImpl.updateBook(1L, updateBookRequestDto);

        assertNotNull(result);
        assertEquals(book.getId(), result.id());
        verify(bookRepository).findById(1L);
    }

    @Test
    void deleteBook_ifIsDeletedTrue() {

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookServiceImpl.deleteBook(1L);

        assertTrue(book.isDeleted());
        verify(bookRepository).save(book);
    }

    @Test
    void search_ifBookFoundCertainAttribute() {

        Pageable pageable = PageRequest.of(0, 10);

        BookSearchParametersDto parameters = new BookSearchParametersDto(
                null, null, null
        );

        Specification<Book> specification = Specification.where(null);

        Page<Book> page = new PageImpl<>(List.of(book));

        when(specificationBuilderImpl.build(parameters)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(page);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> result = bookServiceImpl.search(parameters, pageable);

        assertEquals(1, result.getTotalPages());
        verify(bookRepository).findAll(specification, pageable);
    }

    @Test
    void findAllBooksByCategoryId_shouldReturnPageOfBookByCategory() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Book> page = new PageImpl<>(List.of(book));

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bookRepository.findAllByCategories_Id(1L, pageable)).thenReturn(page);
        when(bookMapper.toDtoCertainCategory(book)).thenReturn(new BookDtoCategoryResponse(
                book.getId(), book.getTitle(), book.getAuthor(), book.getDescription(),
                book.getIsbn(), book.getPrice(), book.getCoverImage()));

        Page<BookDtoCategoryResponse> result =
                bookServiceImpl.findAllBooksByCategoryId(1L, pageable);

        assertEquals(1, result.getTotalPages());
        verify(bookRepository).findAllByCategories_Id(1L, pageable);
    }
}
