package com.trafficcontrol.service;

import com.trafficcontrol.entity.User;
import com.trafficcontrol.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String login(String username, String password) {
        User user = userService.getUserByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtil.generateToken(user.getUsername());
        }
        return null;
    }

    public boolean validateToken(String token, String username) {
        return jwtUtil.isTokenValid(token, username);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
