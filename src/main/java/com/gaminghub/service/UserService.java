package com.gaminghub.service;

import com.gaminghub.dto.UserResponse;
import com.gaminghub.entity.User;

import java.util.UUID;

public interface UserService {
    User getCurrentUser();
    User getUserByEmail(String email);
    User getUserById(UUID userId);
    UserResponse getCurrentUserResponse();
    UserResponse updateCurrentUser(User currentUser, String fullName, String email, String city, String profileImageUrl);
    void deleteCurrentUser(User currentUser);
}
