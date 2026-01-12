package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller // Cực kỳ quan trọng để Spring nhận diện
public class UserController {

    @Autowired
    private UserService userService; // Tiêm Service để gọi hàm save()

    // 1. Hiển thị trang thông tin cá nhân
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        // 1. Lấy user tạm từ session
        User sessionUser = (User) session.getAttribute("loggedInUser");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        // 2. TRUY VẤN LẠI TỪ DB (Đây là bước quan trọng nhất)
        // Dùng id để lấy bản ghi mới nhất từ database thông qua userService
        User user = userService.findById(sessionUser.getId());

        if (user == null) {
            return "redirect:/login";
        }

        // 3. Truyền đúng biến 'user' vào model
        model.addAttribute("user", user);
        return "profile";
    }

    // 2. Xử lý cập nhật thông tin
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User userForm, HttpSession session) {
        User existingUser = userService.findById(userForm.getId());

        if (existingUser != null) {
            // Chỉ cập nhật fullName vì CSDL chỉ có cột này
            existingUser.setFullName(userForm.getFullName());

            // Cột phone không có trong DB nên mình xóa dòng setPhone cũ đi

            userService.save(existingUser);
            session.setAttribute("loggedInUser", existingUser);
        }

        return "redirect:/profile?success=true";
    }
}