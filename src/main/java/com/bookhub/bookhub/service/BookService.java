package com.bookhub.bookhub.service;

import com.bookhub.bookhub.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Book createBook(Book book);
    Book updateBook(Long id, Book bookDetails);
    void deleteBook(Long id);
    Optional<Book> getBookById(Long id);
    List<Book> getAllBooks();

    List<Book> searchBooks(String keyword);
    List<Book> getBooksByAuthor(String author);
    Optional<Book> getBookByIsbn(String isbn);

    void incrementCopies(Long bookId, int quantity);
    void decrementCopies(Long bookId, int quantity);
    boolean isBookAvailable(Long bookId);
}
