package com.bookhub.bookhub.repository;

import com.bookhub.bookhub.entity.Loan;
import com.bookhub.bookhub.entity.Loan.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);
    List<Loan> findByBookId(Long bookId);
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findByUserIdAndStatus(Long userId, LoanStatus status);
}
