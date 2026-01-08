package com.bookhub.bookhub.service.impl;

import com.bookhub.bookhub.dto.book.request.BookCreateRequest;
import com.bookhub.bookhub.dto.book.request.BookUpdateRequest;
import com.bookhub.bookhub.dto.book.response.BookResponse;
import com.bookhub.bookhub.entity.Book;
import com.bookhub.bookhub.factory.BookFactory;
import com.bookhub.bookhub.repository.BookRepository;
import com.bookhub.bookhub.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookFactory bookFactory;

    @Override
    public BookResponse createBook(BookCreateRequest bookRequest) {
        Book book = bookFactory.createBookWithCopies(
                bookRequest.getTitle(),
                bookRequest.getAuthor(),
                bookRequest.getIsbn(),
                bookRequest.getPublicationYear(),
                bookRequest.getTotalCopies() != null ? bookRequest.getTotalCopies() : 1
        );

        Book savedBook = bookRepository.save(book);

        return new BookResponse(savedBook);
    }

    @Override
    public BookResponse updateBook(Long id, BookUpdateRequest bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));

        if (bookDetails.getTitle() != null) {
            book.setTitle(bookDetails.getTitle());
        }

        if (bookDetails.getAuthor() != null) {
            book.setAuthor(bookDetails.getAuthor());
        }

        if (bookDetails.getPublicationYear() != null) {
            book.setPublicationYear(bookDetails.getPublicationYear());
        }

        if (bookDetails.getTotalCopies() != null) {
            int diff = bookDetails.getTotalCopies() - book.getTotalCopies();
            book.setTotalCopies(bookDetails.getTotalCopies());
            book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + diff));
        }

        Book updatedBook = bookRepository.save(book);

        return new BookResponse(updatedBook);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));

        if (!book.getLoans().isEmpty()) {
            throw new IllegalArgumentException("It is not possible to delete a book with active loans. ID: " + id);
        }

        bookRepository.delete(book);
    }

    @Override
    public Optional<BookResponse> getBookById(Long id) {
        return bookRepository.findById(id)
                .map(BookResponse::new);
    }

    @Override
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookResponse::new)
                .toList();
    }

    @Override
    public List<BookResponse> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword).stream()
                .map(BookResponse::new)
                .toList();
    }

    @Override
    public List<BookResponse> getBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author).stream()
                .map(BookResponse::new)
                .toList();
    }

    @Override
    public Optional<BookResponse> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(BookResponse::new);
    }

    @Override
    public void incrementCopies(Long bookId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        book.setAvailableCopies(book.getAvailableCopies() + quantity);
        book.setTotalCopies(book.getTotalCopies() + quantity);

        bookRepository.save(book);
    }

    @Override
    public void decrementCopies(Long bookId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        book.setAvailableCopies(book.getAvailableCopies() - quantity);
        book.setTotalCopies(book.getTotalCopies() - quantity);

        bookRepository.save(book);
    }

    @Override
    public boolean isBookAvailable(Long bookId) {
        return bookRepository.findById(bookId)
                .map(Book::isAvailable)
                .orElse(false);
    }
}
