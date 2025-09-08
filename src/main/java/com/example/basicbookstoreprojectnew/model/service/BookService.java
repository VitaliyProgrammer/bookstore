package com.example.basicbookstoreprojectnew.model.service;

import com.example.basicbookstoreprojectnew.model.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();

    Book getBookById(Long id);

    Book updateBook(Long id, Book updatedBook);

    void deleteBook(Long id);

}
