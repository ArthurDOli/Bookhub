package com.bookhub.bookhub.service;

import com.bookhub.bookhub.entity.Book;
import com.bookhub.bookhub.entity.Loan;
import com.bookhub.bookhub.entity.User;
import com.bookhub.bookhub.exception.ResourceNotFoundException;
import com.bookhub.bookhub.repository.BookRepository;
import com.bookhub.bookhub.repository.LoanRepository;
import com.bookhub.bookhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public LoanServiceImpl(LoanRepository loanRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Loan createLoan(Long userId, Long bookId, int loanDays) {
        if (loanDays <= 0) {
            throw new IllegalArgumentException("Loan days must be positive");
        }

        if (loanDays > 60) {
            throw new IllegalArgumentException("Max loan: 60 days");
        }


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (user.getRole() != User.Role.READER && user.getRole() != User.Role.LIBRARIAN) {
            throw new IllegalStateException("The user is not authorized to make loans");
        }


        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", bookId));

        if (!book.isAvailable()) {
            throw new IllegalStateException("Book not available for loan");
        }

        boolean hasOverdueLoans = user.getLoans().stream()
                .anyMatch(
                        loan -> loan.getStatus() == Loan.LoanStatus.OVERDUE ||
                        (loan.getStatus() == Loan.LoanStatus.ACTIVE &&
                        loan.getDueDate().isBefore(LocalDate.now()))
                );

        if (hasOverdueLoans) {
            throw new IllegalStateException("User has overdue loans");
        }
        

        long activeLoansCount = user.getLoans().stream()
                .filter(loan -> loan.getStatus() == Loan.LoanStatus.ACTIVE)
                .count();

        if (activeLoansCount >= 5) {
            throw new IllegalStateException("Limit of 5 active loans reached");
        }
        
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(loanDays));
        loan.setStatus(Loan.LoanStatus.ACTIVE);
        
        book.borrow();
        
        user.getLoans().add(loan);
        book.getLoans().add(loan);

        Loan savedLoan = loanRepository.save(loan);

        bookRepository.save(book);
        userRepository.save(user);

        return savedLoan;
    }

//    Loan returnLoan(Long loanId);
//    List<Loan> getActiveLoansByUser(Long userId);
//    List<Loan> getOverdueLoans();
//    Loan extendLoan(Long loanId, int additionalDays);
//    Loan getLoanById(Long loanId);
//    boolean isLoanOverdue(Long loanId);
}
