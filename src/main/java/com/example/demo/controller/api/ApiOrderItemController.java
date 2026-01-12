package com.example.demo.controller.api;

import com.example.demo.entity.OrderItem;
import com.example.demo.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class ApiOrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    // 1. Lấy tất cả chi tiết món hàng
    @GetMapping
    public ResponseEntity<List<OrderItem>> getAll() {
        return new ResponseEntity<>(orderItemService.getAllOrderItems(), HttpStatus.OK);
    }

    // 2. Thêm một món vào đơn hàng (POST)
    @PostMapping
    public ResponseEntity<OrderItem> create(@RequestBody OrderItem item) {
        return new ResponseEntity<>(orderItemService.saveOrderItem(item), HttpStatus.CREATED);
    }
}