package com.example.basicbookstoreprojectnew.model.service;

import com.example.basicbookstoreprojectnew.dto.UserLoginRequestDto;
import com.example.basicbookstoreprojectnew.dto.UserLoginResponseDto;
import com.example.basicbookstoreprojectnew.dto.UserRegistrationRequestDto;
import com.example.basicbookstoreprojectnew.dto.UserRegistrationResponseDto;
import com.example.basicbookstoreprojectnew.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserRegistrationResponseDto registration(UserRegistrationRequestDto request);

    UserLoginResponseDto login(UserLoginRequestDto request);

    Optional<User> findByEmail(String email);

    List<UserRegistrationResponseDto> findAllUsers();

    UserRegistrationResponseDto findById(Long id);

    void deleteById(Long id);
}
