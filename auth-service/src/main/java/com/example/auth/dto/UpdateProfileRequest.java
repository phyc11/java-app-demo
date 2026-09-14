package com.example.auth.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UpdateProfileRequest {

    @NotBlank(message = "Họ và tên không được để trống!")
    @Size(max = 100, message = "Họ và tên tối đa 100 ký tự!")
    private String fullName;

    private String avatarColor;

    public UpdateProfileRequest() {}

    public UpdateProfileRequest(String fullName, String avatarColor) {
        this.fullName = fullName;
        this.avatarColor = avatarColor;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAvatarColor() { return avatarColor; }
    public void setAvatarColor(String avatarColor) { this.avatarColor = avatarColor; }
}
