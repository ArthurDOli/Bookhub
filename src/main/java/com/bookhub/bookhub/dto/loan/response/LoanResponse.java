package com.bookhub.bookhub.dto.loan.response;

import com.bookhub.bookhub.entity.Loan;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class LoanResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long bookId;
    private String bookTitle;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Loan.LoanStatus status;
    private int renewalCount;
    private boolean overdue;

    public LoanResponse(Loan loan) {
        this.id = loan.getId();
        this.userId = loan.getUser().getId();
        this.userName = loan.getUser().getName();
        this.bookId = loan.getBook().getId();
        this.bookTitle = loan.getBook().getTitle();
        this.loanDate = loan.getLoanDate();
        this.dueDate = loan.getDueDate();
        this.returnDate = loan.getReturnDate();
        this.status = loan.getStatus();
        this.renewalCount = loan.getRenewalCount();
        this.overdue = loan.getStatus() == Loan.LoanStatus.OVERDUE ||
                (loan.getStatus() == Loan.LoanStatus.ACTIVE &&
                loan.getDueDate().isBefore(LocalDate.now()));
    }
}
