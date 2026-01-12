package com.bookhub.bookhub.controller;

import com.bookhub.bookhub.dto.user.response.UserResponse;
import com.bookhub.bookhub.dto.user.request.UserUpdateRequest;
import com.bookhub.bookhub.dto.user.request.UserCreateRequest;
import com.bookhub.bookhub.entity.User;
import com.bookhub.bookhub.service.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
public class UserController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID")
            @PathVariable Long id
    ) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists")
    })
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserCreateRequest userRequest) {
        UserResponse newUser = userService.registerUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "User ID")
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest userDetails
    ) {
        UserResponse updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Cannot delete user with active loans")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID")
            @PathVariable Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieve a user by their email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> getUserByEmail(
            @Parameter(description = "User email")
            @PathVariable String email
    ) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/role")
    @Operation(summary = "Change user role", description = "Change a user's role (READER or LIBRARIAN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role changed successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> changeUserRole(
            @Parameter(description = "User ID")
            @PathVariable Long id,
            @Parameter(description = "New role (READER or LIBRARIAN)")
            @RequestParam User.Role role
    ) {
        UserResponse updatedUser = userService.changeUserRole(id, role);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/check-email")
    @Operation(summary = "Check if email exists", description = "Check if an email is already registered")
    @ApiResponse(responseCode = "200", description = "Check completed")
    public ResponseEntity<Boolean> checkEmailExists(
            @Parameter(description = "Email to check")
            @RequestParam String email
    ) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}
