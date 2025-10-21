package com.example.basicbookstoreprojectnew.model.service;

import com.example.basicbookstoreprojectnew.dto.UserLoginRequestDto;
import com.example.basicbookstoreprojectnew.dto.UserLoginResponseDto;
import com.example.basicbookstoreprojectnew.mapper.AuthenticationMapper;
import com.example.basicbookstoreprojectnew.model.User;
import com.example.basicbookstoreprojectnew.model.repository.UserRepository;
import com.example.basicbookstoreprojectnew.security.JwtUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationMapper authenticationMapper;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public UserLoginResponseDto login(UserLoginRequestDto request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .toList();

        String token = jwtUtil.generateToken(request.email(), roles);

        return authenticationMapper.loginResponse(user, token);
    }
}
