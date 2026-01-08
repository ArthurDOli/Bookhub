package com.bookhub.bookhub.service.impl;

import com.bookhub.bookhub.dto.UserResponseDTO;
import com.bookhub.bookhub.dto.request.UserCreateRequest;
import com.bookhub.bookhub.entity.Loan;
import com.bookhub.bookhub.entity.User;
import com.bookhub.bookhub.exception.ResourceNotFoundException;
import com.bookhub.bookhub.factory.UserFactory;
import com.bookhub.bookhub.repository.UserRepository;
import com.bookhub.bookhub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFactory userFactory;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserFactory userFactory) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userFactory = userFactory;
    }

    @Override
    public UserResponseDTO registerUser(UserCreateRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("E-mail already registered");
        }

        User user = userFactory.createFromRequest(userRequest);

        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        User savedUser = userRepository.save(user);

        return new UserResponseDTO(savedUser);
    }

    @Override
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
            String encryptedPassword = passwordEncoder.encode(userDetails.getPassword());
            user.setPassword(encryptedPassword);
        }

        return userRepository.save(user);
    }

    @Override
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

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User changeUserRole(Long userId, User.Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        user.setRole(newRole);

        return userRepository.save(user);
    }
}
