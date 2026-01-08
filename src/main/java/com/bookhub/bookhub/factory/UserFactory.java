package com.bookhub.bookhub.factory;

import com.bookhub.bookhub.dto.user.request.UserCreateRequest;
import com.bookhub.bookhub.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {
    private static final User.Role DEFAULT_ROLE = User.Role.READER;

    private boolean isValidEmail(String email) {
        if (email == null) return false;

        email = email.trim();

        int atIndex = email.indexOf('@');
        if (atIndex < 1 || atIndex == email.length() - 1) {
            return false;
        }

        int dotIndex = email.lastIndexOf('.');
        if (dotIndex <= atIndex + 1 || dotIndex == email.length() - 1) {
            return false;
        }

        return true;
    }

    public User createUser(String name, String email, String password, User.Role role) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(password);
        user.setRole(role != null ? role : DEFAULT_ROLE);

        return user;
    }

    public User createLibrarian(String name, String email, String password) {
        return createUser(name, email, password, User.Role.LIBRARIAN);
    }

    public User createFromRequest(UserCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        return createUser(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        );
    }
}
