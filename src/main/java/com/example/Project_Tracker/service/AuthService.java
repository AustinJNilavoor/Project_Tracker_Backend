package com.example.Project_Tracker.service;

import com.example.Project_Tracker.dto.AuthRequest;
import com.example.Project_Tracker.dto.AuthResponse;
import com.example.Project_Tracker.dto.ChangePasswordRequest;
import com.example.Project_Tracker.dto.RegisterRequest;
import com.example.Project_Tracker.dto.UserDto;
import com.example.Project_Tracker.entity.Ibu;
import com.example.Project_Tracker.entity.User;
import com.example.Project_Tracker.exceptions.ConflictException;
import com.example.Project_Tracker.exceptions.InvalidCredentialsException;
import com.example.Project_Tracker.exceptions.ResourceNotFoundException;
import com.example.Project_Tracker.repository.IbuRepository;
import com.example.Project_Tracker.repository.UserRepository;
import com.example.Project_Tracker.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final IbuRepository ibuRepository;

    public UserDto register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(
                    "User with email '" + request.getEmail() + "' already exists");
        }

        Ibu ibu = ibuRepository.findByName(request.getIbuName())
                .orElseThrow(() -> new ResourceNotFoundException(

                        "IBU not found with name: " + request.getIbuName()));
        if ("MANAGER".equalsIgnoreCase(request.getRole())) {
            // You'll need a new repository method like findByRoleAndIbu_Id
            List<User> existingManagers = userRepository.findByRoleAndIbu_Id("MANAGER", ibu.getId());

            if (!existingManagers.isEmpty()) {
                throw new ConflictException(
                        "IBU '" + ibu.getName() + "' already has a manager assigned.");
            }
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .ibu(ibu)
                .build();

        userRepository.save(user);

        // 🔹 Return DTO (flattened structure)
        return UserDto.builder()
                .id(user.getId().toString())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .ibuId(ibu.getId().toString())
                .ibuName(ibu.getName())
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token, user.getRole(), user.getName(),user.getId().toString());
    }

    // New Method for changing password
    public void changePassword(ChangePasswordRequest request) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 1. Fetch user
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found (Logged-in user issue)"));

        // 2. Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid current password");
        }

        // 3. Encode and set new password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        // 4. Save
        userRepository.save(user);
    }

}
