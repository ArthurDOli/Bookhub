package com.bookhub.bookhub.service;

import com.bookhub.bookhub.dto.UserResponseDTO;
import com.bookhub.bookhub.dto.request.UserCreateRequest;
import com.bookhub.bookhub.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponseDTO registerUser(UserCreateRequest user);
    User updateUser(Long id, User userDetails);
    void deleteUser(Long id);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    List<User> getAllUsers();
    boolean existsByEmail(String email);
    User changeUserRole(Long userId, User.Role newRole);
}
