package com.example.basicbookstoreprojectnew.model.service.BookServiceImpl;

import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.repository.BookRepository;
import com.example.basicbookstoreprojectnew.model.service.BookService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//BookService realization
@Service
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }


    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
}
