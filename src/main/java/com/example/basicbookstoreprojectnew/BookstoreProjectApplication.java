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
        Book book = Book.builder()
                .title("The Lord of the Rings")
                .author("J.R.R. Tolkien")
                .description("Novel about struggle of good against evil")
                .isbn("568-345111000")
                .price(Integer.valueOf(100))
                .build();


        bookService.save(book);
        bookService.findAll().forEach(a -> System.out.println(a.getTitle()));
    }

    public static void main(String[] args) {

        SpringApplication.run(BookstoreProjectApplication.class, args);
    }
}
