package com.trafficcontrol.controller;

import com.trafficcontrol.entity.Role;
import com.trafficcontrol.entity.User;
import com.trafficcontrol.service.AuthService;
import com.trafficcontrol.service.RoleService;
import com.trafficcontrol.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final RoleService roleService;
    
    @Autowired
    public AuthController(AuthService authService, UserService userService, RoleService roleService) {
        this.authService = authService;
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        User user = userService.getUserByUsername(username);
        if (user == null) {
            System.out.println("Kullanıcı bulunamadı: " + username);
            return ResponseEntity.status(401)
                                 .body(Map.of("error", "Kullanıcı adı veya şifre hatalı"));
        }

        System.out.println("Kullanıcı bulundu, şifre db: " + user.getPassword());

        String token = authService.login(username, password);
        if (token == null) {
            System.out.println("Şifre eşleşmedi");
            return ResponseEntity.status(401)
                                 .body(Map.of("error", "Kullanıcı adı veya şifre hatalı"));
        }

        return ResponseEntity.ok(Map.of("token", token));
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.getUserByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Kullanıcı zaten mevcut");
        }

        user.setPassword(authService.encodePassword(user.getPassword()));

        Role role;
        if (user.getRole() != null && user.getRole().getName() != null) {
            role = roleService.getRoleByName(user.getRole().getName());
            if (role == null) {
                return ResponseEntity.badRequest().body("Geçersiz rol");
            }
        } else {
            role = roleService.getRoleByName("USER"); // Default role
        }

        user.setRole(role);

        userService.createUser(user);
        return ResponseEntity.ok("Kullanıcı oluşturuldu");
    }
}
