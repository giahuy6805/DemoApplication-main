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

    // 1. Gửi email xác nhận đặt hàng (Dùng khi Android đặt hàng thành công)
    public void sendOrderEmail(String to, String orderId, String receiverName, Double totalAmount) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            String htmlContent = "<div style='font-family: Arial, sans-serif; color: #333;'>" +
                    "<h2 style='color: #007bff;'>Cảm ơn bạn đã đặt hàng tại Lofi Tech!</h2>" +
                    "<p>Chào <b>" + receiverName + "</b>,</p>" +
                    "<p>Đơn hàng <b>#" + orderId + "</b> của bạn đã được tiếp nhận thành công.</p>" +
                    "<p>Tổng thanh toán: <span style='color: #d9534f; font-weight: bold;'>" + String.format("%,.0f", totalAmount) + " ₫</span></p>" +
                    "<p>Chúng tôi sẽ sớm liên hệ để giao hàng cho bạn.</p>" +
                    "<br><p>-- Đội ngũ Lofi Tech Premium Store --</p></div>";

            helper.setTo(to);
            helper.setSubject("Xác nhận đơn hàng #" + orderId + " tại Lofi Tech");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // 2. Gửi email giao hàng thành công (Dùng khi Admin bấm xác nhận trên Web)
    public void sendDeliverySuccessEmail(String to, String orderId, String receiverName) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            String htmlContent = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                    "<h2 style='color: #28a745;'>Đơn hàng #" + orderId + " đã giao thành công!</h2>" +
                    "<p>Chào <b>" + receiverName + "</b>,</p>" +
                    "<p>Lofi Tech xin thông báo đơn hàng của bạn đã được giao đến tận tay thành công.</p>" +
                    "<div style='margin-top: 20px; padding: 15px; background-color: #f8f9fa; border-radius: 8px;'>" +
                    "   <p style='margin: 0;'>Cảm ơn bạn đã tin tưởng và ủng hộ <b>Lofi Tech</b>!</p>" +
                    "</div>" +
                    "</div>";

            helper.setTo(to);
            helper.setSubject("Thông báo giao hàng thành công - Đơn hàng #" + orderId);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // 3. Gửi email khôi phục mật khẩu
    public void sendForgotPasswordEmail(String to, String fullName, String newPassword) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #eee; border-radius: 10px;'>" +
                    "<h2 style='text-align: center; color: #007bff;'>Lofi Tech.</h2>" +
                    "<p>Chào <b>" + fullName + "</b>,</p>" +
                    "<p>Mật khẩu mới của bạn là: <b style='font-size: 20px; color: #d9534f;'>" + newPassword + "</b></p>" +
                    "<p>Vui lòng đổi lại mật khẩu sau khi đăng nhập.</p></div>";

            helper.setTo(to);
            helper.setSubject("Khôi phục mật khẩu tài khoản Lofi Tech");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // 4. Gửi email hủy đơn hàng (ĐÃ CHUYỂN SANG HTML CHO ĐẸP)
    public void sendCancelOrderEmail(String toEmail, String orderCode, String customerName) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            String htmlContent = "<div style='font-family: Arial, sans-serif; color: #333;'>" +
                    "<h2 style='color: #dc3545;'>Thông báo hủy đơn hàng</h2>" +
                    "<p>Chào <b>" + customerName + "</b>,</p>" +
                    "<p>Lofi Tech xin thông báo đơn hàng mã <b>" + orderCode + "</b> của bạn đã được hủy thành công.</p>" +
                    "<p>Nếu đây là nhầm lẫn, vui lòng liên hệ hotline để được hỗ trợ kịp thời.</p>" +
                    "<br><p>Trân trọng,<br>Đội ngũ Lofi Tech.</p></div>";

            helper.setTo(toEmail);
            helper.setSubject("Thông báo: Đơn hàng " + orderCode + " đã bị hủy");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}