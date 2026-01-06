package com.bookhub.bookhub.factory;

import org.springframework.stereotype.Component;

@Component
public class UserFactory {
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
}
