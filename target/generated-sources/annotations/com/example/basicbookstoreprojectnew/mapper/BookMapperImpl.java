package com.example.basicbookstoreprojectnew.mapper;

import com.example.basicbookstoreprojectnew.dto.BookDto;
import com.example.basicbookstoreprojectnew.dto.CreateBookRequestDto;
import com.example.basicbookstoreprojectnew.model.Book;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-15T20:24:03+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 19.0.1 (Oracle Corporation)"
)
@Component
public class BookMapperImpl implements BookMapper {

    @Override
    public Book toEntity(CreateBookRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Book book = new Book();

        book.setTitle( dto.title() );
        book.setAuthor( dto.author() );
        book.setIsbn( dto.isbn() );
        if ( dto.price() != null ) {
            book.setPrice( dto.price().intValue() );
        }
        book.setDescription( dto.description() );
        book.setCoverImage( dto.coverImage() );

        return book;
    }

    @Override
    public BookDto toDto(Book book) {
        if ( book == null ) {
            return null;
        }

        Long id = null;
        String title = null;
        String author = null;
        String description = null;
        String isbn = null;
        BigDecimal price = null;
        String coverImage = null;

        id = book.getId();
        title = book.getTitle();
        author = book.getAuthor();
        description = book.getDescription();
        isbn = book.getIsbn();
        if ( book.getPrice() != null ) {
            price = BigDecimal.valueOf( book.getPrice() );
        }
        coverImage = book.getCoverImage();

        BookDto bookDto = new BookDto( id, title, author, description, isbn, price, coverImage );

        return bookDto;
    }

    @Override
    public void updateBookFromDto(CreateBookRequestDto dto, Book target) {
        if ( dto == null ) {
            return;
        }

        target.setTitle( dto.title() );
        target.setAuthor( dto.author() );
        target.setIsbn( dto.isbn() );
        if ( dto.price() != null ) {
            target.setPrice( dto.price().intValue() );
        }
        else {
            target.setPrice( null );
        }
        target.setDescription( dto.description() );
        target.setCoverImage( dto.coverImage() );
    }
}
