package com.example.demo.controller.api;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.repository.OrderItemRepository; // Thêm import này
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;   // Thêm import này
import com.example.demo.repository.UserRepository;
import com.example.demo.service.MailService;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional; // Import cực kỳ quan trọng
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class ApiOrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository; // PHẢI THÊM DÒNG NÀY

    @Autowired
    private OrderItemRepository orderItemRepository; // PHẢI THÊM DÒNG NÀY

    @Autowired
    private MailService mailService; // THÊM DÒNG NÀY VÀO ĐÂY

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAll() {
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    @PostMapping("/add")
    @Transactional // Giúp đảm bảo: Hoặc lưu hết, hoặc không lưu gì nếu gặp lỗi
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> request) {
        try {
            // 1. Lưu thông tin Order chung
            Order order = new Order();
            order.setAddress((String) request.get("address"));
            order.setTotalAmount(Double.parseDouble(request.get("totalAmount").toString()));
            order.setCreatedAt(LocalDateTime.now());
            order.setStatus("PENDING");
            order.setCode("ORD" + System.currentTimeMillis());

            // Gắn User vào đơn hàng
            Map<String, Object> userMap = (Map<String, Object>) request.get("user");
            if (userMap != null && userMap.containsKey("id")) {
                Integer userId = (Integer) userMap.get("id");
                userRepository.findById(userId).ifPresent(order::setUser);
            }

            Order savedOrder = orderRepository.save(order);

            // 2. LƯU CHI TIẾT SẢN PHẨM (OrderItem)
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");
            if (items != null) {
                for (Map<String, Object> itemMap : items) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(savedOrder);

                    Integer pId = (Integer) itemMap.get("productId");
                    productRepository.findById(pId).ifPresent(product -> {
                        orderItem.setProduct(product);
                        orderItem.setQuantity((Integer) itemMap.get("quantity"));
                        orderItem.setPrice(new java.math.BigDecimal(itemMap.get("price").toString()));

                        orderItemRepository.save(orderItem);

                        // TRỪ TỒN KHO (Phần ghi điểm A+)
                        int newStock = product.getQuantity() - orderItem.getQuantity();
                        product.setQuantity(Math.max(0, newStock)); // Không cho âm kho
                        productRepository.save(product);
                    });
                }
            }
            if (savedOrder.getUser() != null) {
                try {
                    mailService.sendOrderEmail(
                            savedOrder.getUser().getEmail(),
                            savedOrder.getCode(),
                            savedOrder.getUser().getFullName(),
                            savedOrder.getTotalAmount()
                    );
                    System.out.println("Đã gửi mail xác nhận đơn hàng cho Android!");
                } catch (Exception mailEx) {
                    System.out.println("Lỗi gửi mail: " + mailEx.getMessage());
                }
            }

            return ResponseEntity.ok(Map.of("message", "Đặt hàng thành công!", "orderCode", order.getCode()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi Server: " + e.getMessage());
        }
    }

    // Trong ApiOrderController.java
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Integer userId) {
        // Tìm tất cả đơn hàng thuộc về User ID này và sắp xếp mới nhất lên đầu
        List<Order> orders = orderRepository.findByUserIdOrderByIdDesc(userId);
        return ResponseEntity.ok(orders);
    }
}