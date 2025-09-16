package com.example.basicbookstoreprojectnew.model.repository;


import java.util.List;
import com.example.basicbookstoreprojectnew.model.Book;
<<<<<<< HEAD
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
=======
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
>>>>>>> f9f9180 (Add Specification CriteriaQuery for search-function)
}
