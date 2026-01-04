package com.bookhub.bookhub.factory;

import com.bookhub.bookhub.entity.Book;
import com.bookhub.bookhub.entity.Loan;
import com.bookhub.bookhub.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class LoanFactory {
    private static final int DEFAULT_LOAN_DAYS = 14;

    public Loan createLoan(User user, Book book, int loanDays) {
        validateParameters(user, book, loanDays);

        Loan loan = new Loan();
        configureLoan(loan, user, book, loanDays);
        establishBidirectionalRelationships(loan, user, book);

        return loan;
    }

    public Loan createLoan(User user, Book book) {
        return createLoan(user, book, DEFAULT_LOAN_DAYS);
    }

    private void validateParameters(User user, Book book, int loanDays) {
        if (user == null) {
            throw new IllegalArgumentException("User can't be null");
        }

        if (book == null) {
            throw new IllegalArgumentException("Book can't be null");
        }

        if (loanDays <= 0) {
            throw new IllegalArgumentException("Loan days should be positive");
        }
    }

    private void configureLoan(Loan loan, User user, Book book, int loanDays) {
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(loanDays));
        loan.setStatus(Loan.LoanStatus.ACTIVE);
    }

    private void establishBidirectionalRelationships(Loan loan, User user, Book book) {
        user.getLoans().add(loan);
        book.getLoans().add(loan);
    }
}
