package com.example.basicbookstoreprojectnew.model.repository;


import com.example.basicbookstoreprojectnew.model.Book;
import java.util.List;
import org.springframework.stereotype.Repository;

//BookRepository interface
@Repository
public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
