package mybase.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mybase.domain.jpa.UserAccount;
import mybase.repo.AccountUserRepo;
import mybase.security.UserPrincipal;
import mybase.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider tokenProvider;
    private final AccountUserRepo userRepository;

    /**
     * Get current authenticated user
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not authenticated"));
        }

        Optional<UserAccount> userOptional = userRepository.findById(userPrincipal.getId());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        UserAccount user = userOptional.get();

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getUserAccountID());
        response.put("email", user.getEmail());
        response.put("name", user.getName());
        response.put("username", user.getUsername());
        response.put("avatarUrl", user.getAvatarUrl());
        response.put("primaryProvider", user.getPrimaryProvider());
        response.put("roles", user.getRoles());

        log.info("Returning user data - avatarUrl: {}", user.getAvatarUrl());

        return ResponseEntity.ok(response);
    }

    /**
     * Refresh access token using refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Refresh token is required"));
        }

        if (!tokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired refresh token"));
        }

        if (!tokenProvider.isRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token is not a refresh token"));
        }

        Long userId = tokenProvider.getUserIdFromToken(refreshToken);

        Optional<UserAccount> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        String newAccessToken = tokenProvider.generateToken(userId);
        String newRefreshToken = tokenProvider.generateRefreshToken(userId);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("token", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        log.info("Token refreshed for user: {}", userId);

        return ResponseEntity.ok(tokens);
    }

    /**
     * Logout - client should discard tokens
     * This endpoint is mainly for logging purposes
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null) {
            log.info("User logged out: {}", userPrincipal.getEmail());
        }

        // With JWT, logout is handled client-side by discarding the token
        // Server-side token invalidation would require a token blacklist
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    /**
     * Validate token
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("valid", false, "error", "Token is required"));
        }

        boolean isValid = tokenProvider.validateToken(token);

        if (isValid) {
            Long userId = tokenProvider.getUserIdFromToken(token);
            return ResponseEntity.ok(Map.of("valid", true, "userId", userId));
        } else {
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }
}
