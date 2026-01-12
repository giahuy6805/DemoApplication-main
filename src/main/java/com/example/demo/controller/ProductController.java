package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    // Hàm xử lý hiển thị trang chi tiết sản phẩm
    @GetMapping("/product/{id}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);

        // Lấy sản phẩm liên quan để hiển thị ở dưới trang chi tiết
        List<Product> relatedProducts = productService.findByCategory(product.getCategory().getName());
        model.addAttribute("relatedProducts", relatedProducts);

        return "product-detail"; // Mở file product-detail.html
    }
}