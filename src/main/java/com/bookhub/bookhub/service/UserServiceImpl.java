package com.bookhub.bookhub.service;

import com.bookhub.bookhub.entity.Loan;
import com.bookhub.bookhub.entity.User;
import com.bookhub.bookhub.exception.ResourceNotFoundException;
import com.bookhub.bookhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("E-mail already registered");
        }

        if (user.getRole() == null) {
            user.setRole(User.Role.READER);
        }

        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        if (userDetails.getEmail() != null &&
            !userDetails.getEmail().equals(user.getEmail())) {
            throw new IllegalArgumentException("Email can't be altered");
        }

        if (userDetails.getName() != null) {
            user.setName(userDetails.getName());
        }

        if (userDetails.getPassword() != null) {
            user.setPassword(userDetails.getPassword());
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        boolean hasActiveLoans = user.getLoans().stream()
                .anyMatch(loan -> loan.getStatus() == Loan.LoanStatus.ACTIVE);

        if (hasActiveLoans) {
            throw new IllegalStateException("It is not possible to delete a user with active loans");
        }

        userRepository.delete(user);
    }
}
