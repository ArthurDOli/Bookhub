package com.bookhub.bookhub.service.validation;

import com.bookhub.bookhub.entity.Book;
import com.bookhub.bookhub.entity.Loan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookValidationService {
    public void validateBookCanBeDeleted(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        boolean hasActiveLoans = book.getLoans().stream()
                .anyMatch(loan -> loan.getStatus() == Loan.LoanStatus.ACTIVE);

        if (hasActiveLoans) {
            throw new IllegalStateException(
                    "Cannot delete book with active loans. Book: " + book.getTitle()
            );
        }
    }
    
    public void validateBookData(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title is required");
        }

        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Book author is required");
        }

        if (book.getTotalCopies() != null && book.getTotalCopies() < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }

        if (book.getAvailableCopies() != null && book.getAvailableCopies() < 0) {
            throw new IllegalArgumentException("Available copies cannot be negative");
        }

        if (book.getTotalCopies() != null && book.getAvailableCopies() != null) {
            if (book.getAvailableCopies() > book.getTotalCopies()) {
                throw new IllegalArgumentException(
                        "Available copies cannot exceed total copies. " +
                        "Available: " + book.getAvailableCopies() +
                        ", Total: " + book.getTotalCopies()
                );
            }
        }
    }
}
