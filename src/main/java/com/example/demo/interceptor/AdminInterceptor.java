package com.example.demo.interceptor;

import com.example.demo.entity.User; // Nhớ import entity User vào nhé Huy
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loggedInUser");

        // 1. Kiểm tra xem đã đăng nhập chưa
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=access-denied");
            return false;
        }

        // 2. Kiểm tra quyền hạn (Phải là ADMIN mới được vào)
        // Lưu ý: So sánh chuỗi "ADMIN" vì chúng ta đã đổi role sang String rồi
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            // Nếu là User thường mà đòi vào Admin thì đá về trang chủ hoặc báo lỗi
            response.sendRedirect(request.getContextPath() + "/?error=no-permission");
            return false;
        }

        return true; // Nếu là Admin thì cho qua "cửa hải quan"
    }
}