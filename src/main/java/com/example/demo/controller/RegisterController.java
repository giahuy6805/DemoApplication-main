package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        // Mặc định gán role = 1 (Khách hàng) cho tài khoản mới
        user.setRole("USER"); // Sửa thành chuỗi "USER" để khớp với kiểu String

        // Lưu xuống Database
        userService.save(user);

        // Đăng ký xong, đẩy sang trang login kèm thông báo thành công
        return "redirect:/login?registered=true";
    }
}