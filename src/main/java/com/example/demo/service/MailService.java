package com.example.demo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderEmail(String to, String orderId, String receiverName, Double totalAmount) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

            String htmlContent = "<h1>Cảm ơn bạn đã đặt hàng tại TechHub!</h1>" +
                    "<p>Chào " + receiverName + ",</p>" +
                    "<p>Đơn hàng <b>" + orderId + "</b> của bạn đã được tiếp nhận thành công.</p>" +
                    "<p>Tổng thanh toán: <b>" + String.format("%,.0f", totalAmount) + " ₫</b></p>" +
                    "<p>Chúng tôi sẽ sớm liên hệ để giao hàng cho bạn.</p>" +
                    "<br><p>-- Đội ngũ TechHub Premium Store --</p>";

            helper.setTo(to);
            helper.setSubject("Xác nhận đơn hàng #" + orderId + " tại TechHub");
            helper.setText(htmlContent, true); // true để gửi định dạng HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendDeliverySuccessEmail(String to, String orderId, String receiverName) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

            // Nội dung email với định dạng HTML
            String htmlContent = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                    "<h2 style='color: #28a745;'>Đơn hàng #" + orderId + " đã giao thành công!</h2>" +
                    "<p>Chào <b>" + receiverName + "</b>,</p>" +
                    "<p>Lofi Tech xin thông báo đơn hàng của bạn đã được giao đến tận tay thành công.</p>" +
                    "<p>Hy vọng bạn sẽ hài lòng với sản phẩm mới này. Nếu có bất kỳ thắc mắc nào cần hỗ trợ hoặc bảo hành, đừng ngần ngại liên hệ với chúng mình qua hotline nhé!</p>" +
                    "<div style='margin-top: 20px; padding: 15px; background-color: #f8f9fa; border-radius: 8px;'>" +
                    "   <p style='margin: 0;'>Cảm ơn bạn đã tin tưởng và ủng hộ <b>Lofi Tech</b>!</p>" +
                    "</div>" +
                    "<p style='font-size: 12px; color: #888; margin-top: 20px;'>Đây là email tự động, vui lòng không phản hồi lại email này.</p>" +
                    "</div>";

            helper.setTo(to);
            helper.setSubject("Thông báo giao hàng thành công - Đơn hàng #" + orderId);
            helper.setText(htmlContent, true); // true để xác nhận đây là nội dung HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // Trong MailService.java
    public void sendForgotPasswordEmail(String to, String fullName, String newPassword) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

            String htmlContent = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 500px; margin: auto; border: 1px solid #eee; padding: 20px; border-radius: 10px;'>" +
                    "<h2 style='color: #000; text-align: center;'>TechHub.</h2>" +
                    "<hr style='border: 0; border-top: 1px solid #eee;'>" +
                    "<p>Chào <b>" + fullName + "</b>,</p>" +
                    "<p>Bạn vừa yêu cầu cấp lại mật khẩu tại hệ thống <b>TechHub</b>.</p>" +
                    "<div style='background-color: #f8f9fa; padding: 15px; border-radius: 8px; text-align: center; margin: 20px 0;'>" +
                    "   <p style='margin: 0; color: #888; font-size: 14px;'>Mật khẩu mới của bạn là:</p>" +
                    "   <h1 style='margin: 10px 0; color: #000; letter-spacing: 5px;'>" + newPassword + "</h1>" +
                    "</div>" +
                    "<p style='color: #ff4d4f; font-size: 13px;'><i>* Vui lòng đăng nhập và đổi lại mật khẩu ngay để đảm bảo an toàn cho tài khoản.</i></p>" +
                    "<p style='font-size: 12px; color: #888; margin-top: 30px; border-top: 1px solid #eee; pt-10px;'>Đây là email tự động từ hệ thống TechHub Premium Store.</p>" +
                    "</div>";

            helper.setTo(to);
            helper.setSubject("Khôi phục mật khẩu tài khoản TechHub");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}