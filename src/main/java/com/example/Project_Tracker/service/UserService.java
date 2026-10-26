package com.example.Project_Tracker.service;

import com.example.Project_Tracker.dto.UserDto;
import com.example.Project_Tracker.entity.Ibu;
import com.example.Project_Tracker.entity.User;
import com.example.Project_Tracker.exceptions.ConflictException;
import com.example.Project_Tracker.exceptions.ResourceNotFoundException;
import com.example.Project_Tracker.repository.IbuRepository;
import com.example.Project_Tracker.repository.UserRepository;
import com.example.Project_Tracker.utils.MapperUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final IbuRepository ibuRepository;
    private final MapperUtils mapperUtils;

    public List<UserDto> getAllUsers() {
        // Get logged-in user details

        String loggedInEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // Find user in DB
        User currentUser = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found"));

        List<User> users;

        // ✅ If Admin → get all users
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            users = userRepository.findAll();
        }
        // ✅ Else → get only users in same IBU
        else if (currentUser.getIbu() != null) {
            users = userRepository.findByIbu_Id(currentUser.getIbu().getId());
        }
        // ✅ If user has no IBU (edge case)
        else {
            users = List.of(currentUser);
        }

        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ✅ Get single user by ID
    public UserDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return convertToDto(user);
    }

    // ✅ Update existing user
    public UserDto updateUser(String id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Only update the name if the DTO provided a non-null value
        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        // Only update the email if the DTO provided a non-null value
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }



    Ibu ibu = null;
    if (userDto.getIbuName() != null && !userDto.getIbuName().isEmpty()) {
        ibu = ibuRepository.findByName(userDto.getIbuName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "IBU not found with name: " + userDto.getIbuName()));
    }

    // Check manager duplication - only when manager & IBU changed
    if (userDto.getRole() != null && "MANAGER".equalsIgnoreCase(userDto.getRole()) && ibu != null) {
        
        // If user is changing to new IBU or becoming manager newly
        if (!"MANAGER".equalsIgnoreCase(existingUser.getRole()) ||
            existingUser.getIbu() == null ||
            !existingUser.getIbu().getId().equals(ibu.getId())) {

            List<User> existingManagers = userRepository.findByRoleAndIbu_Id("MANAGER", ibu.getId());
            
            // Allow if the only manager is this user itself
            if (!existingManagers.isEmpty() &&
                (existingManagers.size() > 1 || !existingManagers.get(0).getId().equals(existingUser.getId()))) {
                throw new ConflictException(
                        "IBU '" + ibu.getName() + "' already has a manager assigned.");
            }
        }
    }
            // Only update the role if the DTO provided a non-null value
        if (userDto.getRole() != null) {
            existingUser.setRole(userDto.getRole());
        }
        existingUser.setIbu(ibu);
        existingUser.setUpdatedAt(java.time.LocalDateTime.now());
        User updatedUser = userRepository.save(existingUser);
        return convertToDto(updatedUser);
    }

    // ✅ Delete user
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    // ✅ Convert Entity → DTO
    private UserDto convertToDto(User user) {
        UserDto dto = mapperUtils.map(user, UserDto.class);

        if (user.getIbu() != null) {
            dto.setIbuId(user.getIbu().getId());
            dto.setIbuName(user.getIbu().getName());
        } else {
            dto.setIbuId(null);
            dto.setIbuName(null);
        }

        return dto;
    }
}
