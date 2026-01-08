package com.bookhub.bookhub.service.impl;

import com.bookhub.bookhub.dto.loan.request.LoanCreateRequest;
import com.bookhub.bookhub.dto.loan.response.LoanResponse;
import com.bookhub.bookhub.entity.Book;
import com.bookhub.bookhub.entity.Loan;
import com.bookhub.bookhub.entity.User;
import com.bookhub.bookhub.exception.ResourceNotFoundException;
import com.bookhub.bookhub.factory.LoanFactory;
import com.bookhub.bookhub.repository.BookRepository;
import com.bookhub.bookhub.repository.LoanRepository;
import com.bookhub.bookhub.repository.UserRepository;
import com.bookhub.bookhub.service.LoanService;
import com.bookhub.bookhub.service.validation.LoanValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    public LoanResponse createLoan(LoanCreateRequest loanRequest) {
        validateLoanDays(loanRequest.getLoanDays());

        User user = findUser(loanRequest.getUserId());
        Book book = findBook(loanRequest.getBookId());

        validationService.validateUserCanBorrow(user);
        validateBookAvailable(book);

        Loan loan = loanFactory.createLoan(user, book, loanRequest.getLoanDays());
        book.borrow();

        Loan savedLoan = loanRepository.save(loan);

        return new LoanResponse(savedLoan);
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
    public LoanResponse returnLoan(Long loanId) {
        Loan loan = findLoan(loanId);
        validateLoanCanBeReturned(loan);
        processReturn(loan);

        Loan returnedLoan = loanRepository.save(loan);
        return new LoanResponse(returnedLoan);
    }

    private Loan findLoan(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", loanId));
    }

    private void validateLoanCanBeReturned(Loan loan) {
        if (loan.getStatus() == Loan.LoanStatus.RETURNED) {
            throw new IllegalStateException(
                    "Loan already returned on: " + loan.getReturnDate()
            );
        }
    }

    private void processReturn(Loan loan) {
        loan.setReturnDate(LocalDate.now());

        loan.setStatus(Loan.LoanStatus.RETURNED);

        loan.getBook().returnBook();
    }



    @Override
    public LoanResponse extendLoan(Long loanId, int additionalDays) {
        validateAdditionalDays(additionalDays);

        Loan loan = findLoan(loanId);
        validateLoanCanBeExtended(loan);

        processExtension(loan, additionalDays);

        Loan extendedLoan = loanRepository.save(loan);

        return new LoanResponse(extendedLoan);
    }

    private void validateAdditionalDays(int additionalDays) {
        if (additionalDays <= 0) {
            throw new IllegalStateException(
                    "Additional days must be positive"
            );
        }
    }

    private void validateLoanCanBeExtended(Loan loan) {
        if (loan.getRenewalCount() >= 3) {
            throw new IllegalStateException(
                    "Maximum renewals reached: " + loan.getRenewalCount()
            );
        }

        if (loan.getStatus() == Loan.LoanStatus.OVERDUE) {
            throw new IllegalStateException(
                    "Cannot extend overdue loan"
            );
        }

        if (loan.getStatus() == Loan.LoanStatus.RETURNED) {
            throw new IllegalStateException(
                    "Cannot extend returned loan"
            );
        }

        if (loan.getStatus() != Loan.LoanStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Can only extend active loans. Current status: " + loan.getStatus()
            );
        }
    }

    private void processExtension(Loan loan, int additionalDays) {
        LocalDate newDueDate = loan.getDueDate().plusDays(additionalDays);
        loan.setDueDate(newDueDate);

        loan.setRenewalCount(loan.getRenewalCount() + 1);
    }

    @Override
    public List<LoanResponse> getActiveLoansByUser(Long userId) {
        User user = findUser(userId);

        return loanRepository.findByUserIdAndStatus(userId, Loan.LoanStatus.ACTIVE)
                .stream()
                .map(LoanResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanResponse> getOverdueLoans() {
        return loanRepository.findByStatus(Loan.LoanStatus.OVERDUE)
                .stream()
                .map(LoanResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isLoanOverdue(Long loanId) {
        return validationService.isLoanOverdue(findLoan(loanId));
    }
}
