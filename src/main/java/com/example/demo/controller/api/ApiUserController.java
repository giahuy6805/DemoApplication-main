package com.example.demo.controller.api;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class ApiUserController {

    @Autowired
    private UserService userService;

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
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        // Trong thực tế bạn nên mã hóa mật khẩu ở đây trước khi lưu
        User savedUser = userService.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
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
}