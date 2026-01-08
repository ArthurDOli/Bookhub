package com.bookhub.bookhub.controller;

import com.bookhub.bookhub.dto.loan.request.LoanCreateRequest;
import com.bookhub.bookhub.dto.loan.request.LoanReturnRequest;
import com.bookhub.bookhub.dto.loan.response.LoanResponse;
import com.bookhub.bookhub.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody LoanCreateRequest loanRequest) {
        LoanResponse createdLoan = loanService.createLoan(loanRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLoan);
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<LoanResponse> returnLoan(@PathVariable Long id, @RequestBody(required = false) LoanReturnRequest request) {
        LoanResponse returnedLoan = loanService.returnLoan(id);
        return ResponseEntity.ok(returnedLoan);
    }

    @PatchMapping("/{id}/extend")
    public ResponseEntity<LoanResponse> extendLoan(@PathVariable Long id, @RequestParam int additionalDays) {
        LoanResponse extendedLoan = loanService.extendLoan(id, additionalDays);
        return ResponseEntity.ok(extendedLoan);
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<LoanResponse>> getActiveLoansByUser(@PathVariable Long userId) {
        List<LoanResponse> loans = loanService.getActiveLoansByUser(userId);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<LoanResponse>> getOverdueLoans() {
        List<LoanResponse> loans = loanService.getOverdueLoans();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{id}/overdue")
    public ResponseEntity<Boolean> isLoanOverdue(@PathVariable Long id) {
        boolean isOverdue = loanService.isLoanOverdue(id);
        return ResponseEntity.ok(isOverdue);
    }
}
