package com.example.bookstoreproject.model.service;

import com.example.bookstoreproject.model.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
