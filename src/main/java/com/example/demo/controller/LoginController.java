package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.MailService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    /**
     * 1. Hiển thị trang đăng nhập
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Trả về file login.html trong templates
    }

    /**
     * 2. Xử lý logic đăng nhập
     */
    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        // Tìm kiếm người dùng trong Database thông qua Email
        User user = userService.findByEmail(email);

        // Kiểm tra xem user có tồn tại và mật khẩu có khớp không
        if (user != null && user.getPassword().equals(password)) {

            // LƯU Ý: Tên attribute "loggedInUser" phải trùng khớp với Interceptor của bạn
            session.setAttribute("loggedInUser", user);

            // PHÂN QUYỀN ĐIỀU HƯỚNG
            // Giả sử: 0 là ADMIN, 1 là CUSTOMER (Khách hàng)
            // Trong LoginController.java, sửa đoạn phân quyền:
            if (user.getRole() != null && user.getRole().equalsIgnoreCase("ADMIN")) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/"; // Hoặc /cart/view tùy ý Huy
            }
        }

        // Nếu thất bại: Gửi thông báo lỗi và ở lại trang login
        model.addAttribute("error", "Email hoặc mật khẩu không chính xác!");
        return "login";
    }

    /**
     * 3. Đăng xuất
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Xóa thông tin user khỏi session
        session.removeAttribute("loggedInUser");
        // Hoặc xóa sạch toàn bộ session
        session.invalidate();

        return "redirect:/login?logout=true";
    }

    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        User user = userService.findByEmail(email);

        if (user == null) {
            model.addAttribute("error", "Email này không tồn tại trong hệ thống!");
            return "forgot-password";
        }

        String newPassword = String.valueOf((int)(Math.random() * 900000 + 100000));
        user.setPassword(newPassword);
        userService.save(user);

        // SỬA DÒNG NÀY: Gọi đúng hàm sendForgotPasswordEmail
        mailService.sendForgotPasswordEmail(email, user.getFullName(), newPassword);

        model.addAttribute("message", "Mật khẩu mới đã được gửi vào Email của bạn!");
        return "login";
    }
}