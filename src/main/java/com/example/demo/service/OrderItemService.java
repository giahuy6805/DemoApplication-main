package com.example.demo.service;

import com.example.demo.entity.OrderItem;
import java.util.List;

public interface OrderItemService {
    List<OrderItem> getAllOrderItems(); // Tên hàm khớp với Controller
    OrderItem getOrderItemById(Integer id);
    OrderItem saveOrderItem(OrderItem item); // Tên hàm khớp với Controller
    void deleteOrderItem(Integer id);
}