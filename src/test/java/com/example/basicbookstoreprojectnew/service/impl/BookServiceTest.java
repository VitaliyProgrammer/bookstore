package com.example.basicbookstoreprojectnew.service.impl;

import com.example.basicbookstoreprojectnew.dto.BookDto;
import com.example.basicbookstoreprojectnew.dto.BookDtoCategoryResponse;
import com.example.basicbookstoreprojectnew.dto.BookSearchParametersDto;
import com.example.basicbookstoreprojectnew.dto.CreateBookRequestDto;
import com.example.basicbookstoreprojectnew.exception.BookNotFoundException;
import com.example.basicbookstoreprojectnew.exception.CategoryNotFoundException;
import com.example.basicbookstoreprojectnew.mapper.BookMapper;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.Category;
import com.example.basicbookstoreprojectnew.repository.BookRepository;
import com.example.basicbookstoreprojectnew.repository.CategoryRepository;
import com.example.basicbookstoreprojectnew.repository.impl.SpecificationBuilderImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private SpecificationBuilderImpl specificationBuilderImpl;

    @InjectMocks
    private BookServiceImpl bookServiceImpl;

    private Category category;

    private Book book;

    private BookDto bookDto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Programming");
        category.setDescription("About programming");

        book = new Book();
        book.setId(1L);
        book.setTitle("Clean architecture");
        book.setAuthor("Robert C. Martin");
        book.setIsbn("9780134494166");
        book.setPrice(BigDecimal.valueOf(50.0));
        book.setCategories(Set.of(category));

        bookDto = new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getIsbn(),
                book.getPrice(),
                book.getCoverImage(),
                List.of(category.getId())
        );
    }

    @Test
    @DisplayName("save: save book with categories")
    void saveBook() {

        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto(
                "Clean Architecture",
                "Robert C. Martin",
                "About clean architecture",
                "9780134494166",
                BigDecimal.valueOf(50.0),
                "http://example.com/cover.jpg",
                List.of(category.getId())
        );

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(bookMapper.toEntity(bookRequestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto savedBook = bookServiceImpl.save(bookRequestDto);

        assertThat(savedBook).isEqualTo(bookDto);
        verify(bookRepository).save(book);
        verify(categoryRepository).findById(category.getId());
    }

    @Test
    @DisplayName("save: throw exception when category does not exist")
    void saveBook_categoryNotFound() {

        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto(
                "Unknown book",
                "Unknown book",
                "Unknown book",
                "0000000000000",
                BigDecimal.valueOf(0.00),
                "http://example.com/cover.jpg",
                List.of(999L)
        );

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> bookServiceImpl.save(bookRequestDto));
    }

    @Test
    @DisplayName("findAll: return paginated list of books")
    void findAllBooks() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Book> page = new PageImpl<>(List.of(book));

        when(bookRepository.findAll(pageable)).thenReturn(page);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> result = bookServiceImpl.findAll(pageable);
        assertThat(result.getContent()).containsExactly(bookDto);
    }

    @Test
    @DisplayName("findById: return existing book")
    void findById() {

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookServiceImpl.findById(book.getId());

        assertThat(result).isEqualTo(bookDto);
    }

    @Test
    @DisplayName("findById: throw exception when book not found")
    void findById_bookNotFound() {

        when(bookRepository.findById(10000L)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> bookServiceImpl.findById(10000L));
    }

    @Test
    @DisplayName("updateBook: update existing book")
    void updateBook() {

        CreateBookRequestDto updateRequest = new CreateBookRequestDto(
                "New Title",
                "New author",
                "About new description",
                "9999999999999",
                BigDecimal.valueOf(100.0),
                "http://new_example.com/cover.jpg",
                List.of(category.getId())
        );

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookServiceImpl.updateBook(book.getId(), updateRequest);

        verify(bookMapper).updateBookFromDto(eq(updateRequest), same(book));
        verify(bookRepository).save(book);

        assertThat(result).isEqualTo(bookDto);
    }

    @Test
    @DisplayName("updateBook: update existing book, not found")
    void updateBook_notFoundBook() {

        CreateBookRequestDto saveBook = new CreateBookRequestDto(
                "Non-existent book",
                "Non-existent author",
                "Non-existent description",
                "0000000000000",
                BigDecimal.valueOf(00.0),
                "http://unknown_example.com/cover.jpg",
                List.of(category.getId())
        );

        when(bookRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
                () -> bookServiceImpl.updateBook(9999L, saveBook));
    }

    @Test
    @DisplayName("updateBook: search existing book")
    void searchBook() {

        BookSearchParametersDto searchParameters = new BookSearchParametersDto(
                new String[]{"Clean Architecture"},
                new String[]{"Robert C. Martin"},
                new String[]{"50.0"}
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(List.of(book));

        Specification<Book> specification = mock(Specification.class);

        when(specificationBuilderImpl.build(searchParameters))
                .thenReturn(specification);
        when(bookRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> result = bookServiceImpl.search(searchParameters, pageable);

        assertThat(result.getContent()).containsExactly(bookDto);
    }

    @Test
    @DisplayName("updateBook: update existing book, not found parameters")
    void searchBook_noneExistentParameters() {

        BookSearchParametersDto searchParameters = new BookSearchParametersDto(
                new String[]{"None-existent"},
                new String[]{"None-existent"},
                new String[]{"99999"}
        );

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Book> specification = mock(Specification.class);

        when(specificationBuilderImpl.build(searchParameters)).thenReturn(specification);

        when(bookRepository.findAll(eq(specification), eq(pageable))).thenReturn(Page.empty());

        Page<BookDto> result = bookServiceImpl.search(searchParameters, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();

        verify(specificationBuilderImpl).build(searchParameters);
        verify(bookRepository).findAll(specification, pageable);
        verify(bookMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("findAllBooksByCategoryId: return books by category")
    void findAllBooksByCategoryId() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(List.of(book));

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(bookRepository.findAllByCategories_Id(category.getId(), pageable)).thenReturn(page);
        when(bookMapper.toDtoCertainCategory(book)).thenReturn(new BookDtoCategoryResponse(
                book.getId(), book.getTitle(), book.getAuthor(), book.getDescription(),
                book.getIsbn(), book.getPrice(), book.getCoverImage()));

        Page<BookDtoCategoryResponse> result =
                bookServiceImpl.findAllBooksByCategoryId(category.getId(), pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("findAllBooksByCategoryId: throw when category is not exists")
    void findAllBooksByCategoryId_notFound() {

        when(categoryRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> bookServiceImpl.findAllBooksByCategoryId(9999L,
                        PageRequest.of(0, 10)));
    }

    @Test
    @DisplayName("deleteBook: set deleted flag")
    void deleteBook() {

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        bookServiceImpl.deleteBook(book.getId());

        assertThat(book.isDeleted()).isTrue();
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("deleteBook: not found book")
    void deleteBook_notFound() {

        when(bookRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookServiceImpl.deleteBook(9999L));
    }
}
