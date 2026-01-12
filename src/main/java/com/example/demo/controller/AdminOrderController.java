package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Thêm cái này
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List; // Thêm cái này

@Controller
@RequestMapping("/admin")
public class AdminOrderController {

    @Autowired
    private MailService mailService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository; // PHẢI THÊM DÒNG NÀY

    @GetMapping("/orders")
    public String listOrders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/orders";
    }

    @GetMapping("/orders/detail/{id}")
    public String showOrderDetail(@PathVariable("id") Integer id, Model model) {
        // 1. Lấy thông tin chung của đơn hàng
        Order order = orderRepository.findById(id).orElse(null);

        // 2. Lấy danh sách các món hàng trong đơn đó
        List<OrderItem> items = orderItemRepository.findByOrderId(id);

        model.addAttribute("order", order);
        model.addAttribute("items", items);

        return "admin/order-detail";
    }

    // Trong AdminOrderController.java

    @GetMapping("/orders/update-status/{id}")
    public String updateOrderStatus(@PathVariable("id") Integer id, @RequestParam("status") String status) {
        // 1. Tìm đơn hàng theo ID
        Order order = orderRepository.findById(id).orElse(null);

        if (order != null) {
            // 2. Cập nhật trạng thái mới (ví dụ: 'SHIPPED')
            order.setStatus(status);
            orderRepository.save(order);

            // 3. Nếu trạng thái là 'SHIPPED', thực hiện gửi email thông báo
            if ("SHIPPED".equalsIgnoreCase(status)) {
                mailService.sendDeliverySuccessEmail(
                        order.getUser().getEmail(),
                        "TH" + order.getId(),
                        order.getUser().getFullName()
                );
            }
        }
        // 4. Quay lại trang danh sách đơn hàng hoặc trang chi tiết
        return "redirect:/admin/orders";
    }
}