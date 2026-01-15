package com.bookhub.bookhub.service;

import com.bookhub.bookhub.dto.auth.request.LoginRequest;
import com.bookhub.bookhub.dto.auth.response.AuthResponse;
import com.bookhub.bookhub.entity.User;
import com.bookhub.bookhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthResponse authenticate(LoginRequest loginRequest) {
        UserDetails userDetails = authenticateAndGetUserDetails(loginRequest);
        String jwtToken = generateTokenForUser(userDetails);
        User user = fetchUserFromDatabase(loginRequest.getEmail());

        return buildAuthResponse(jwtToken, user);
    }

    private UserDetails authenticateAndGetUserDetails(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return (UserDetails) authentication.getPrincipal();
    }

    private String generateTokenForUser(UserDetails userDetails) {
        return jwtService.generateToken(userDetails);
    }

    private User fetchUserFromDatabase(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        "User not found after authentication:" + email
                ));
    }

    private AuthResponse buildAuthResponse(String token, User user) {
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponse(token, "Bearer", userInfo);
    }
}
