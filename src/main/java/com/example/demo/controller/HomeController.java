package com.example.demo.controller;

import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @GetMapping("/") // Khi vào địa chỉ trang chủ
    public String index(@RequestParam(name = "category", required = false) String category, Model model) {

        // Logic lọc sản phẩm: Nếu có chọn danh mục thì lọc, không thì hiện tất cả
        if (category != null && !category.isEmpty()) {
            model.addAttribute("products", productService.findByCategory(category));
        } else {
            model.addAttribute("products", productService.findAll());
        }

        return "index"; // Trỏ đúng vào file index.html trong thư mục templates
    }
}