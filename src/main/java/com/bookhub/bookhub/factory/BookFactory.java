package com.bookhub.bookhub.factory;

import com.bookhub.bookhub.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookFactory {
    public Book createBook(String title, String author, String isbn, Integer publicationYear) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPublicationYear(publicationYear);
        book.setAvailableCopies(1);
        book.setTotalCopies(1);

        return book;
    }

    public Book createFromGoogleBooks(String title, String author, String isbn, Integer publicationYear, String description) {
        Book book = createBook(title, author, isbn, publicationYear);

        return book;
    }
}
