package com.example.demo.repository;

import com.example.demo.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    // Tìm tất cả các món hàng thuộc về một mã đơn hàng nhất định
    List<OrderItem> findByOrderId(Integer orderId);
}