
package com.example.postapi;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private final String adminPasswordHash = new BCryptPasswordEncoder().encode("admin123");

    private final String userPasswordHash = new BCryptPasswordEncoder().encode("user123");

    public AuthController(
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Map<String, String> login(
            @RequestBody Map<String, String> loginData) {

        String username = loginData.get("username");
        String password = loginData.get("password");

        if ("admin".equals(username)
                && passwordEncoder.matches(password, adminPasswordHash)) {

            String accessToken = jwtUtil.generateToken(username, "ADMIN");

            String refreshToken = jwtUtil.generateRefreshToken(username, "ADMIN");

            return Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken);
        }

        if ("user".equals(username)
                && passwordEncoder.matches(password, userPasswordHash)) {

            String accessToken = jwtUtil.generateToken(username, "USER");

            String refreshToken = jwtUtil.generateRefreshToken(username, "USER");

            return Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken);
        }

        throw new RuntimeException("Invalid username or password");
    }

    @PostMapping("/hash")
    public Map<String, String> hashPassword(
            @RequestBody Map<String, String> data) {

        String password = data.get("password");
        String hashedPassword = passwordEncoder.encode(password);

        return Map.of("hashedPassword", hashedPassword);
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(
            @RequestBody Map<String, String> request) {

        String refreshToken = request.get("refreshToken");

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        String role = jwtUtil.extractRole(refreshToken);

        String newAccessToken = jwtUtil.generateToken(username, role);

        return Map.of("accessToken", newAccessToken);
    }
}
