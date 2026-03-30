package com.example.demo.controller.api;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.MailService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class ApiUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService; // THÊM DÒNG NÀY VÀO ĐÂY

    // 1. Lấy danh sách tất cả User -> Link: {{basic_url}}/api/users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin mới được xem danh sách User
    public ResponseEntity<List<User>> getAll() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    // 2. Lấy chi tiết 1 User -> Link: {{basic_url}}/api/users/1
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> findById(@PathVariable Integer id) {
        User user = userService.findById(id);
        return user != null
                ? new ResponseEntity<>(user, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 3. Tạo mới User (Đăng ký) -> Link: {{basic_url}}/api/users

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // 1. Ép ID về null để Spring Boot hiểu là tạo mới hoàn toàn (Insert)
            user.setId(null);

            // 2. Kiểm tra email trùng (như mình đã hướng dẫn trước đó)
            if (userService.findByEmail(user.getEmail()) != null) {
                return ResponseEntity.badRequest().body("Email đã tồn tại!");
            }

            // 3. Thiết lập role mặc định
            user.setRole("USER");
            user.setId(null);

            // 4. Lưu User
            User savedUser = userService.save(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }
    // Trong file ApiUserController.java
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        // Tìm user theo email trong Database
        User user = userService.findByEmail(loginRequest.getEmail());

        // Kiểm tra: Nếu tìm thấy user và mật khẩu khớp (đang so sánh chuỗi thuần)
        if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.ok(user); // Trả về đối tượng User (kèm ID, FullName...)
        }

        // Nếu sai thì trả về lỗi 401
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email hoặc mật khẩu không chính xác!");
    }

    // 4. Cập nhật User -> Link: {{basic_url}}/api/users/1
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Hoặc bạn có thể phân quyền "người dùng tự sửa chính mình"
    public ResponseEntity<User> update(@PathVariable Integer id, @RequestBody User userDetails) {
        User user = userService.findById(id);
        if (user != null) {
            user.setEmail(userDetails.getEmail());
            user.setRole(userDetails.getRole());
            if (userDetails.getPassword() != null) {
                user.setPassword(userDetails.getPassword());
            }
            userService.save(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 5. Xóa User -> Link: {{basic_url}}/api/users/1
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Trong file ApiUserController.java

    @PostMapping("/forgot-password")
    public ResponseEntity<?> handleForgotPasswordApi(@RequestParam("email") String email) {
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email không tồn tại!");
        }

        // 1. Tạo mật khẩu mới
        String newPassword = String.valueOf((int)(Math.random() * 900000 + 100000));

        // 2. Lưu vào DB
        user.setPassword(newPassword);
        userService.save(user);

        // 3. Gửi Mail qua MailService
        mailService.sendForgotPasswordEmail(email, user.getFullName(), newPassword);

        return ResponseEntity.ok("Mật khẩu mới đã được gửi vào Email của bạn!");
    }
}