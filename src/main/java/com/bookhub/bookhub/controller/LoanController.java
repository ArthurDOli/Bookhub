package com.bookhub.bookhub.controller;

import com.bookhub.bookhub.dto.loan.request.LoanCreateRequest;
import com.bookhub.bookhub.dto.loan.request.LoanReturnRequest;
import com.bookhub.bookhub.dto.loan.response.LoanResponse;
import com.bookhub.bookhub.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Book loan management endpoints")
public class LoanController {
    private final LoanService loanService;

    @PostMapping
    @Operation(summary = "Create a new loan", description = "Create a new book loan for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Loan created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or book not available"),
            @ApiResponse(responseCode = "409", description = "User has reached loan limit or has overdue loans")
    })
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody LoanCreateRequest loanRequest) {
        LoanResponse createdLoan = loanService.createLoan(loanRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLoan);
    }

    @PatchMapping("/{id}/return")
    @Operation(summary = "Return a loan", description = "Mark a loan as returned")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan returned successfully"),
            @ApiResponse(responseCode = "404", description = "Loan not found"),
            @ApiResponse(responseCode = "409", description = "Loan already returned")
    })
    public ResponseEntity<LoanResponse> returnLoan(
            @Parameter(description = "Loan ID")
            @PathVariable Long id,
            @RequestBody(required = false) LoanReturnRequest request
    ) {
        LoanResponse returnedLoan = loanService.returnLoan(id);
        return ResponseEntity.ok(returnedLoan);
    }

    @PatchMapping("/{id}/extend")
    @Operation(summary = "Extend a loan", description = "Extend the due date of an active loan (max 3 renewals)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan extended successfully"),
            @ApiResponse(responseCode = "404", description = "Loan not found"),
            @ApiResponse(responseCode = "409", description = "Cannot extend: loan is overdue, returned, or max renewals reached")
    })
    public ResponseEntity<LoanResponse> extendLoan(
            @Parameter(description = "Loan ID")
            @PathVariable Long id,
            @Parameter(description = "number of days to extend")
            @RequestParam int additionalDays
    ) {
        LoanResponse extendedLoan = loanService.extendLoan(id, additionalDays);
        return ResponseEntity.ok(extendedLoan);
    }

    @GetMapping("/user/{userId}/active")
    @Operation(summary = "Get active loans by user", description = "Retrieve all active loans for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loans retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<LoanResponse>> getActiveLoansByUser(
            @Parameter(description = "User ID")
            @PathVariable Long userId
    ) {
        List<LoanResponse> loans = loanService.getActiveLoansByUser(userId);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue loans", description = "Retrieve all overdue loans in the system")
    @ApiResponse(responseCode = "200", description = "Overdue loans retrieved successfully")
    public ResponseEntity<List<LoanResponse>> getOverdueLoans() {
        List<LoanResponse> loans = loanService.getOverdueLoans();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{id}/overdue")
    @Operation(summary = "Check if loan is overdue", description = "Check if a specific loan is overdue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check completed"),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    public ResponseEntity<Boolean> isLoanOverdue(
            @Parameter(description = "Loan ID")
            @PathVariable Long id
    ) {
        boolean isOverdue = loanService.isLoanOverdue(id);
        return ResponseEntity.ok(isOverdue);
    }
}
