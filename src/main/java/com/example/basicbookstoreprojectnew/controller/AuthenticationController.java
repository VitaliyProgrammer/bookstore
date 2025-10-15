package com.example.basicbookstoreprojectnew.controller;

import com.example.basicbookstoreprojectnew.dto.UserRegistrationRequestDto;
import com.example.basicbookstoreprojectnew.dto.UserRegistrationResponseDto;
import com.example.basicbookstoreprojectnew.model.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public UserRegistrationResponseDto registration(
            @RequestBody @Valid UserRegistrationRequestDto request) {

        return userService.registration(request);
    }
}
