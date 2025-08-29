package com.example.basicbookstoreprojectnew;


import com.example.basicbookstoreprojectnew.model.Book;
import com.example.basicbookstoreprojectnew.model.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//Output to the screen of Book
@SpringBootApplication
public class BookstoreProjectApplication implements CommandLineRunner {
    private final BookService bookService;

    public BookstoreProjectApplication(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public void run(String... args) throws Exception {
        Book book = new Book();
        book.setTitle("The Lord of the Rings");
        book.setAuthor("J.R.R. Tolkien");
        book.setDescription("Novel about struggle of good against evil");
        book.setIsbn("568-345111000");
        book.setPrice(100);


        bookService.save(book);
        bookService.findAll().forEach(System.out::println);

    }

    public static void main(String[] args) {

        SpringApplication.run(BookstoreProjectApplication.class, args);
    }
}
