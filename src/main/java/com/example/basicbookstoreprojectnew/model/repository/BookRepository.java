package com.example.basicbookstoreprojectnew.model.repository;

import com.example.basicbookstoreprojectnew.model.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();

    Optional<Book> findById(Long id);

}
