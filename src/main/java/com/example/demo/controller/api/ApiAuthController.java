package com.example.demo.controller.api;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        // Tìm user theo email
        User user = userService.findByEmail(email);

        // Kiểm tra mật khẩu (So sánh trực tiếp vì đồ án chưa yêu cầu BCrypt)
        if (user != null && user.getPassword().equals(password)) {
            // Trả về JSON thông tin User để Android App lưu vào SharedPreferences
            return ResponseEntity.ok(user);
        }

        // Trả về lỗi 401 nếu sai thông tin
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email hoặc mật khẩu không chính xác!");
    }
}