package com.gaminghub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UserProfileUpdateRequest {

    @Size(max = 150, message = "Full name must not exceed 150 characters")
    private String fullName;

    @Email(message = "Email must be valid")
    private String email;

    private String city;

    @Size(max = 1024, message = "Profile image URL too long")
    private String profileImageUrl;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}
