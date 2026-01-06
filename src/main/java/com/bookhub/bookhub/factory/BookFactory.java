package com.bookhub.bookhub.factory;

import com.bookhub.bookhub.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookFactory {
    private static final int DEFAULT_COPIES = 1;

    public Book createBook(String title, String author, String isbn, Integer publicationYear) {
        validateParameters(title, author);

        Book book = new Book();
        book.setTitle(title.trim());
        book.setAuthor(author.trim());
        book.setIsbn(isbn != null ? isbn.replaceAll("[^\\dX]", "") : null);
        book.setPublicationYear(publicationYear);
        book.setAvailableCopies(DEFAULT_COPIES);
        book.setTotalCopies(DEFAULT_COPIES);

        return book;
    }

    public Book createBookWithCopies(String title, String author, String isbn, Integer year, int totalCopies) {
        Book book = createBook(title, author, isbn, year);
        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(totalCopies);

        return book;
    }

    private void validateParameters(String title, String author) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }
    }
}
