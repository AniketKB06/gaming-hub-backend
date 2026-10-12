package com.gaminghub.service;

import com.gaminghub.dto.UserResponse;
import com.gaminghub.entity.User;
import com.gaminghub.exception.ResourceNotFoundException;
import com.gaminghub.exception.UnauthorizedAccessException;
import com.gaminghub.repository.UserRepository;
import com.gaminghub.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userRepository.findById(userPrincipal.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", userPrincipal.getUserId()));
        }
        throw new UnauthorizedAccessException("No authenticated user found");
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    @Override
    public UserResponse getCurrentUserResponse() {
        return mapUser(getCurrentUser());
    }

    @Override
    public UserResponse updateCurrentUser(User currentUser, String fullName, String email, String city, String profileImageUrl) {
        if (fullName != null && !fullName.isBlank()) {
            currentUser.setFullName(fullName);
        }
        if (email != null && !email.isBlank()) {
            currentUser.setEmail(email.trim().toLowerCase());
        }
        if (city != null) {
            currentUser.setCity(city);
        }
        if (profileImageUrl != null) {
            currentUser.setProfileImageUrl(profileImageUrl);
        }
        return mapUser(userRepository.save(currentUser));
    }

    @Override
    public void deleteCurrentUser(User currentUser) {
        currentUser.setStatus(com.gaminghub.entity.UserStatus.DELETED);
        userRepository.save(currentUser);
    }

    private UserResponse mapUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getProfileImageUrl(),
                user.getCity(),
                user.getRole(),
                user.getStatus(),
                user.isEmailVerified(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
