package com.bookhub.bookhub.service;

import com.bookhub.bookhub.entity.Book;
import com.bookhub.bookhub.entity.Loan;
import com.bookhub.bookhub.entity.User;
import com.bookhub.bookhub.exception.ResourceNotFoundException;
import com.bookhub.bookhub.factory.LoanFactory;
import com.bookhub.bookhub.repository.BookRepository;
import com.bookhub.bookhub.repository.LoanRepository;
import com.bookhub.bookhub.repository.UserRepository;
import com.bookhub.bookhub.service.validation.LoanValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanValidationService validationService;
    private final LoanFactory loanFactory;

    @Override
    public Loan createLoan(Long userId, Long bookId, int loanDays) {
        validateLoanDays(loanDays);

        User user = findUser(userId);
        Book book = findBook(bookId);

        validationService.validateUserCanBorrow(user);
        validateBookAvailable(book);

        Loan loan = loanFactory.createLoan(user, book, loanDays);
        book.borrow();

        return loanRepository.save(loan);
    }

    private void validateLoanDays(int loanDays) {
        if (loanDays <= 0 || loanDays > 60) {
            throw new IllegalArgumentException(
                    "Loan days must be 1-60. Provided: " + loanDays
            );
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    private Book findBook(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", bookId));
    }

    private void validateBookAvailable(Book book) {
        if (!book.isAvailable()) {
            throw new IllegalStateException("Book not available: " + book.getTitle());
        }
    }



    @Override
    public Loan returnLoan(Long loanId) {
        Loan loan = findLoan(loanId);

        validateLoanCanBeReturned(loan);

        processReturn(loan);

        return loanRepository.save(loan);
    }

    private Loan findLoan(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", loanId));
    }

    private void validateLoanCanBeReturned(Loan loan) {
        if (loan.getStatus() == Loan.LoanStatus.RETURNED) {
            throw new IllegalArgumentException(
                    "Loan already returned on: " + loan.getReturnDate()
            );
        }
    }

    private void processReturn(Loan loan) {
        loan.setReturnDate(LocalDate.now());

        loan.setStatus(Loan.LoanStatus.RETURNED);

        loan.getBook().returnBook();
    }

//    Loan returnLoan(Long loanId);
//    List<Loan> getActiveLoansByUser(Long userId);
//    List<Loan> getOverdueLoans();
//    Loan extendLoan(Long loanId, int additionalDays);
//    Loan getLoanById(Long loanId);
//    boolean isLoanOverdue(Long loanId);
}
