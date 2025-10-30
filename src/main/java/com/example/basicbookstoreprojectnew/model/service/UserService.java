package com.example.basicbookstoreprojectnew.model.service;

import com.example.basicbookstoreprojectnew.dto.UserLoginRequestDto;
import com.example.basicbookstoreprojectnew.dto.UserLoginResponseDto;
import com.example.basicbookstoreprojectnew.dto.UserRegistrationRequestDto;
import com.example.basicbookstoreprojectnew.dto.UserRegistrationResponseDto;
import java.util.List;

public interface UserService {
    UserRegistrationResponseDto registration(UserRegistrationRequestDto request);

    UserLoginResponseDto login(UserLoginRequestDto request);

    List<UserRegistrationResponseDto> findAllUsers();

    UserRegistrationResponseDto findById(Long id);

    void deleteById(Long id);
}
