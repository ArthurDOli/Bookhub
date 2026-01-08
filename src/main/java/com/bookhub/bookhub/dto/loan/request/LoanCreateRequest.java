package com.bookhub.bookhub.dto.loan.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanCreateRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @Min(value = 1, message = "Loan days must be at least 1")
    private int loanDays = 14;
}
