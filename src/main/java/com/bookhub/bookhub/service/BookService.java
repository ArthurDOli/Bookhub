package com.bookhub.bookhub.service;

import com.bookhub.bookhub.dto.book.request.BookCreateRequest;
import com.bookhub.bookhub.dto.book.request.BookUpdateRequest;
import com.bookhub.bookhub.dto.book.response.BookResponse;
import com.bookhub.bookhub.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    BookResponse createBook(BookCreateRequest bookRequest);
    BookResponse updateBook(Long id, BookUpdateRequest bookDetails);
    void deleteBook(Long id);
    Optional<BookResponse> getBookById(Long id);
    List<BookResponse> getAllBooks();

    List<BookResponse> searchBooks(String keyword);
    List<BookResponse> getBooksByAuthor(String author);
    Optional<BookResponse> getBookByIsbn(String isbn);

    void incrementCopies(Long bookId, int quantity);
    void decrementCopies(Long bookId, int quantity);
    boolean isBookAvailable(Long bookId);
}
