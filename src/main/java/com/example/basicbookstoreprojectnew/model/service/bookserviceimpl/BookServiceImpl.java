package com.example.basicbookstoreprojectnew.model.service.bookserviceimpl;

import com.example.basicbookstoreprojectnew.mapper.BookMapper;
import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.repository.BookRepository;
import com.example.basicbookstoreprojectnew.model.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(Long id) {
        return bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Book not found! "));
    }

    @Override
    public Book updateBook(Long id, Book updatedBook) {
        Book book = getBookById(id);
        bookMapper.updateBookFromDto(updatedBook, book);
        return bookRepository.save(book);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = getBookById(id);
        book.setDeleted(true);
        bookRepository.save(book);
    }
}
