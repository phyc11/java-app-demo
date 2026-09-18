package com.example.auth.controller;

import com.example.common.dto.ApiResponse;
import com.example.auth.dto.*;
import com.example.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

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
    @PostMapping("/refresh") public ApiResponse<AuthResponse> refresh(@RequestBody RefreshTokenRequest r){return ApiResponse.ok("Token refreshed",authService.refresh(r.getRefreshToken()));}
    @PostMapping("/logout") public ApiResponse<Void> logout(@RequestBody RefreshTokenRequest r){authService.logout(r.getRefreshToken());return ApiResponse.ok("Logged out",null);}
    @GetMapping("/sessions") public ApiResponse<List<AuthSessionDTO>> sessions(Principal principal){return ApiResponse.ok("Sessions retrieved",authService.getSessions(requirePrincipal(principal)));}
    @DeleteMapping("/sessions/{sessionId}") public ApiResponse<Void> revokeSession(@PathVariable Long sessionId,Principal principal){authService.revokeSession(requirePrincipal(principal),sessionId);return ApiResponse.ok("Session revoked",null);}
    @PostMapping("/logout-all") public ApiResponse<Void> logoutAll(Principal principal){authService.logoutAll(requirePrincipal(principal));return ApiResponse.ok("All sessions revoked",null);}
    @PostMapping("/verify-email") public ApiResponse<Void> verify(@RequestBody TokenRequest r){authService.verifyEmail(r.getToken());return ApiResponse.ok("Email verified",null);}
    @PostMapping("/forgot-password") public ApiResponse<Void> forgot(@RequestBody ForgotPasswordRequest r){authService.forgotPassword(r.getUsername());return ApiResponse.ok("If the account exists, reset instructions were sent",null);}
    @PostMapping("/reset-password") public ApiResponse<Void> reset(@RequestBody TokenPasswordRequest r){authService.resetPassword(r);return ApiResponse.ok("Password reset",null);}

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

    private String requirePrincipal(Principal principal){if(principal==null||principal.getName()==null)throw new SecurityException("Authentication required");return principal.getName();}
}
