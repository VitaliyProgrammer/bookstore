package com.example.basicbookstoreprojectnew.model.repository;


import java.util.List;
import com.example.basicbookstoreprojectnew.model.Book;
import org.springframework.stereotype.Repository;


@Repository
public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
