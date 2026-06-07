package com.compassuol.sp.challenge.ecommerce.auth.service;

import com.compassuol.sp.challenge.ecommerce.auth.dto.LoginRequestDTO;
import com.compassuol.sp.challenge.ecommerce.auth.dto.LoginResponseDTO;
import com.compassuol.sp.challenge.ecommerce.auth.dto.RegisterRequestDTO;
import com.compassuol.sp.challenge.ecommerce.auth.entity.User;
import com.compassuol.sp.challenge.ecommerce.auth.enums.Role;
import com.compassuol.sp.challenge.ecommerce.auth.exception.EmailAlreadyExistsException;
import com.compassuol.sp.challenge.ecommerce.auth.jwt.JwtUtil;
import com.compassuol.sp.challenge.ecommerce.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public void register(RegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new LoginResponseDTO(jwtUtil.generateToken(user));
    }
}
