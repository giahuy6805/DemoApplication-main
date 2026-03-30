package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminOrderController {

    @Autowired
    private MailService mailService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    // 1. Danh sách đơn hàng
    @GetMapping("/orders")
    public String listOrders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/orders";
    }

    // 2. Chi tiết đơn hàng (Chỉ giữ lại 1 hàm duy nhất)
    @GetMapping("/orders/detail/{id}")
    public String showOrderDetail(@PathVariable("id") Integer id, Model model) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return "redirect:/admin/orders";

        // Lấy danh sách sản phẩm để hiện lên bảng "Sản phẩm trong đơn"
        List<OrderItem> items = orderItemRepository.findByOrderId(id);

        model.addAttribute("order", order);
        model.addAttribute("items", items);

        return "admin/order-detail";
    }

    // 3. Cập nhật trạng thái và gửi Mail
    @GetMapping("/orders/update-status/{id}")
    public String updateOrderStatus(@PathVariable("id") Integer id, @RequestParam("status") String status) {
        Order order = orderRepository.findById(id).orElse(null);

        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);

            // Gửi mail khi đơn hàng thành công
            if ("SHIPPED".equalsIgnoreCase(status)) {
                try {
                    mailService.sendDeliverySuccessEmail(
                            order.getUser().getEmail(),
                            "TH" + order.getId(),
                            order.getUser().getFullName()
                    );
                } catch (Exception e) {
                    System.out.println("Lỗi gửi mail: " + e.getMessage());
                }
            }
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Integer id) {
        try {
            // 1. Tìm đơn hàng để lấy thông tin Email khách hàng trước khi xóa
            Order order = orderRepository.findById(id).orElse(null);

            if (order != null) {
                // 2. Gửi email thông báo hủy đơn
                try {
                    // Giả sử Huy đã có hàm sendCancelOrderEmail trong MailService
                    // Nếu chưa có, mình sẽ hướng dẫn Huy viết hàm này bên dưới
                    mailService.sendCancelOrderEmail(
                            order.getUser().getEmail(),
                            order.getCode(),
                            order.getUser().getFullName()
                    );
                } catch (Exception mailEx) {
                    // Nếu gửi mail lỗi thì chỉ log ra, vẫn cho phép xóa đơn hàng
                    System.out.println("Lỗi gửi mail hủy đơn: " + mailEx.getMessage());
                }

                // 3. Thực hiện xóa đơn hàng trong Database
                orderRepository.delete(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 4. Quay lại danh sách đơn hàng
        return "redirect:/admin/orders";
    }
}