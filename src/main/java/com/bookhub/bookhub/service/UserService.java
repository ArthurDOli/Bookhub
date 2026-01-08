package com.bookhub.bookhub.service;

import com.bookhub.bookhub.dto.user.response.UserResponse;
import com.bookhub.bookhub.dto.user.request.UserUpdateRequest;
import com.bookhub.bookhub.dto.user.request.UserCreateRequest;
import com.bookhub.bookhub.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponse registerUser(UserCreateRequest userRequest);
    UserResponse updateUser(Long id, UserUpdateRequest userDetails);
    void deleteUser(Long id);
    Optional<UserResponse> getUserById(Long id);
    Optional<UserResponse> getUserByEmail(String email);
    List<UserResponse> getAllUsers();
    boolean existsByEmail(String email);
    UserResponse changeUserRole(Long userId, User.Role newRole);
}
