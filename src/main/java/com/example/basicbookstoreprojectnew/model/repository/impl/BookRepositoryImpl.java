package com.example.basicbookstoreprojectnew.model.repository.impl;

import com.example.basicbookstoreprojectnew.model.repository.BookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import com.example.basicbookstoreprojectnew.model.Book;
import org.springframework.stereotype.Repository;

//BookRepository realization
@Repository
public class BookRepositoryImpl implements BookRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Book save(Book book) {
        if (book.getId() != null) {
            entityManager.persist(book);
            return book;
        } else {
            return entityManager.merge(book);
        }
    }

    @Override
    public List<Book> findAll() {
        return entityManager.createQuery("SELECT b FROM Book b", Book.class)
                .getResultList();
    }
}