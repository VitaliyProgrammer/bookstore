package com.example.basicbookstoreprojectnew.security;

import com.example.basicbookstoreprojectnew.exception.UserNotFoundException;
import com.example.basicbookstoreprojectnew.model.User;
import com.example.basicbookstoreprojectnew.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationUtil {

    private final UserService userService;

    public User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        return userService.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with email: " + email));
    }
}

