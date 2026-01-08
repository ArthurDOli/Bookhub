package com.bookhub.bookhub.service.impl;

import com.bookhub.bookhub.dto.user.response.UserResponseDTO;
import com.bookhub.bookhub.dto.user.request.UserUpdateRequest;
import com.bookhub.bookhub.dto.user.request.UserCreateRequest;
import com.bookhub.bookhub.entity.Loan;
import com.bookhub.bookhub.entity.User;
import com.bookhub.bookhub.exception.ResourceNotFoundException;
import com.bookhub.bookhub.factory.UserFactory;
import com.bookhub.bookhub.repository.UserRepository;
import com.bookhub.bookhub.service.UserService;
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
    public UserResponseDTO updateUser(Long id, UserUpdateRequest userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        if (userDetails.getName() != null && !userDetails.getName().trim().isEmpty()) {
            user.setName(userDetails.getName().trim());
        }

        if (userDetails.getPassword() != null && !userDetails.getPassword().trim().isEmpty()) {
            String encryptedPassword = passwordEncoder.encode(userDetails.getPassword());
            user.setPassword(encryptedPassword);
        }

        User updatedUser = userRepository.save(user);

        return new UserResponseDTO(updatedUser);
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
    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserResponseDTO::new);
    }

    @Override
    public Optional<UserResponseDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponseDTO::new);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDTO::new)
                .toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserResponseDTO changeUserRole(Long userId, User.Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        user.setRole(newRole);

        User updatedUser = userRepository.save(user);

        return new UserResponseDTO(updatedUser);
    }
}
