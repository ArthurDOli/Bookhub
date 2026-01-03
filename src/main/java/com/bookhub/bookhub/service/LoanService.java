package com.bookhub.bookhub.service;

import com.bookhub.bookhub.entity.Loan;

import java.util.List;

public interface LoanService {
    Loan createLoan(Long userId, Long bookId, int loanDays);
    Loan returnLoan(Long loanId);
    List<Loan> getActiveLoansByUser(Long userId);
    List<Loan> getOverdueLoans();
    Loan extendLoan(Long loanId, int additionalDays);
    Loan getLoanById(Long loanId);
    boolean isLoanOverdue(Long loanId);
}
