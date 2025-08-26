package com.example.basicbookstoreprojectnew.model.service;

import com.example.basicbookstoreprojectnew.model.Book;
import java.util.List;

//BookService interface
public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
