package com.example.demo.controller;

import com.example.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("") // Thêm dòng này để gọi được URL: http://localhost:8080/categories
    public String listCategories(Model model){
        // Lưu ý: Tên thuộc tính là "products" thì trong file JSP/HTML bạn phải dùng ${products}
        model.addAttribute("products", categoryService.getAllCategories());
        return "product-list"; // Spring sẽ tìm file product-list.jsp hoặc product-list.html
    }
}
