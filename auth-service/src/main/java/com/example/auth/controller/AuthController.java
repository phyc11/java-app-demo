package com.example.auth.controller;

import com.example.common.dto.ApiResponse;
import com.example.auth.dto.*;
import com.example.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.ok("Login successful", response);
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ApiResponse.ok("User registered successfully", response);
    }

    @GetMapping("/me")
    public ApiResponse<UserDTO> getMe(Principal principal) {
        if (principal == null) {
            return ApiResponse.error("Not authenticated");
        }
        UserDTO user = authService.getCurrentUser(principal.getName());
        return ApiResponse.ok("Current user retrieved", user);
    }

    @PutMapping("/profile")
    public ApiResponse<UserDTO> updateProfile(@Valid @RequestBody UpdateProfileRequest request, Principal principal) {
        if (principal == null) {
            return ApiResponse.error("Not authenticated");
        }
        UserDTO updatedUser = authService.updateProfile(principal.getName(), request);
        return ApiResponse.ok("Cập nhật thông tin cá nhân thành công!", updatedUser);
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request, Principal principal) {
        if (principal == null) {
            return ApiResponse.error("Not authenticated");
        }
        authService.changePassword(principal.getName(), request);
        return ApiResponse.ok("Đổi mật khẩu thành công!", null);
    }
}
