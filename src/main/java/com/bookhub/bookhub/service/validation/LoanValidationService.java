package com.bookhub.bookhub.service.validation;

import com.bookhub.bookhub.entity.Loan;
import com.bookhub.bookhub.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class LoanValidationService {
    public void validateUserCanBorrow(User user) {
        validateUserRole(user);
        validateNoOverdueLoans(user);
        validateLoanLimit(user);
    }

    private void validateUserRole(User user) {
        if (user.getRole() != User.Role.READER &&
            user.getRole() != User.Role.LIBRARIAN) {
            throw new IllegalStateException("Unauthorized user for loans");
        }
    }

    private void validateNoOverdueLoans(User user) {
        boolean hasOverdue = user.getLoans().stream()
                .anyMatch(this::isLoanOverdue);

        if (hasOverdue) {
            throw new IllegalStateException("User has overdue loans");
        }
    }

    private void validateLoanLimit(User user) {
        long activeCount = user.getLoans().stream()
                .filter(loan -> loan.getStatus() == Loan.LoanStatus.ACTIVE)
                .count();

        if (activeCount >= 5) {
            throw new IllegalStateException("Limit of 5 active loans reached. Current: " + activeCount);
        }
    }

    public boolean isLoanOverdue(Loan loan) {
        return loan.getStatus() == Loan.LoanStatus.OVERDUE ||
                (loan.getStatus() == Loan.LoanStatus.ACTIVE &&
                 loan.getDueDate().isBefore(LocalDate.now()));
    }
}
