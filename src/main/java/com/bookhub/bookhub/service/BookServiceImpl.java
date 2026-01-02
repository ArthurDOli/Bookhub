package com.bookhub.bookhub.service;

import com.bookhub.bookhub.entity.Book;
import com.bookhub.bookhub.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book createBook(Book book) {
        if (book.getIsbn() != null &&
            bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new IllegalArgumentException("ISBN already registered: " + book.getIsbn());
        }

        if (book.getTotalCopies() == null) {
            book.setTotalCopies(1);
        }

        if (book.getAvailableCopies() == null) {
            book.setAvailableCopies(book.getTotalCopies());
        }

        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Long id, Book bookDetails) {
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
            book.setAvailableCopies(bookDetails.getAvailableCopies() + diff);
        }

        return bookRepository.save(book);
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
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword);
    }
}
