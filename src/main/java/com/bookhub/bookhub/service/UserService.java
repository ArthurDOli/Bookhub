package com.bookhub.bookhub.service;

import com.bookhub.bookhub.dto.UserResponseDTO;
import com.bookhub.bookhub.dto.UserUpdateDTO;
import com.bookhub.bookhub.dto.request.UserCreateRequest;
import com.bookhub.bookhub.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponseDTO registerUser(UserCreateRequest user);
    UserResponseDTO updateUser(Long id, UserUpdateDTO userDetails);
    void deleteUser(Long id);
    Optional<UserResponseDTO> getUserById(Long id);
    Optional<UserResponseDTO> getUserByEmail(String email);
    List<UserResponseDTO> getAllUsers();
    boolean existsByEmail(String email);
    UserResponseDTO changeUserRole(Long userId, User.Role newRole);
}
