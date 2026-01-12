package com.example.demo.service;

import com.example.demo.entity.Order;
import java.util.List;

public interface OrderService {
    List<Order> getAllOrders(); // Khớp với .getAllOrders() trong Controller
    Order getOrderById(Integer id);
    Order saveOrder(Order order); // Khớp với .saveOrder(order) trong Controller
    void deleteOrder(Integer id);
}