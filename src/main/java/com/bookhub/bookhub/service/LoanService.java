package com.bookhub.bookhub.service;

import com.bookhub.bookhub.dto.loan.request.LoanCreateRequest;
import com.bookhub.bookhub.dto.loan.response.LoanResponse;
import com.bookhub.bookhub.entity.Loan;

import java.util.List;

public interface LoanService {
    LoanResponse createLoan(LoanCreateRequest loanRequest);
    LoanResponse returnLoan(Long loanId);
    List<LoanResponse> getActiveLoansByUser(Long userId);
    List<LoanResponse> getOverdueLoans();
    LoanResponse extendLoan(Long loanId, int additionalDays);
    boolean isLoanOverdue(Long loanId);
}
