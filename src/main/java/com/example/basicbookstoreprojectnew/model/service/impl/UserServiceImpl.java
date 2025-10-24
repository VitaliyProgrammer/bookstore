package com.example.basicbookstoreprojectnew.model.service.impl;

import com.example.basicbookstoreprojectnew.dto.UserLoginRequestDto;
import com.example.basicbookstoreprojectnew.dto.UserLoginResponseDto;
import com.example.basicbookstoreprojectnew.dto.UserRegistrationRequestDto;
import com.example.basicbookstoreprojectnew.dto.UserRegistrationResponseDto;
import com.example.basicbookstoreprojectnew.exception.RegistrationException;
import com.example.basicbookstoreprojectnew.mapper.UserMapper;
import com.example.basicbookstoreprojectnew.model.Role;
import com.example.basicbookstoreprojectnew.model.RoleName;
import com.example.basicbookstoreprojectnew.model.User;
import com.example.basicbookstoreprojectnew.model.repository.RoleRepository;
import com.example.basicbookstoreprojectnew.model.repository.UserRepository;
import com.example.basicbookstoreprojectnew.model.service.UserService;
import com.example.basicbookstoreprojectnew.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final JwtUtil jwtUtil;

    @Override
    public UserRegistrationResponseDto registration(
            UserRegistrationRequestDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RegistrationException("User with email: " + registrationDto.getEmail()
                    + " already exists!");
        }

        User user = userMapper.toModel(registrationDto);
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        user = userRepository.save(user);
        Role userRole = roleRepository.findByRoleName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("The USER role not found!: "));

        user.getRoles().add(userRole);

        user = userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with email!: " + request.email()));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password! ");
        }

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .toList();

        String token = jwtUtil.generateToken(user.getEmail(), roles);

        return new UserLoginResponseDto(token);
    }

    @Override
    public List<UserRegistrationResponseDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserRegistrationResponseDto findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("User with " + id + " not found!"));
    }

    @Override
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Can`t find user by id!: " + id);
        }
        userRepository.deleteById(id);
    }
}

