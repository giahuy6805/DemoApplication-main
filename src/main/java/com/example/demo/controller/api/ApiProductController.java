package com.example.demo.controller.api;

import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ApiProductController {

    @Autowired
    private ProductService productService;

    // 1. Lấy tất cả
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        // Đã đổi từ findAll() thành getAllProducts() theo file Service của bạn
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    // 2. Lấy chi tiết
    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Integer id) {
        Product product = productService.getProductById(id);
        return product != null
                ? new ResponseEntity<>(product, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 3. Tạo mới (SỬA Ở ĐÂY: Thêm @Valid)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) { // Thêm @Valid
        Product savedProduct = productService.saveProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    // 4. Cập nhật (SỬA Ở ĐÂY: Thêm @Valid)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> update(@PathVariable Integer id, @Valid @RequestBody Product details) { // Thêm @Valid
        Product product = productService.getProductById(id);
        if (product != null) {
            product.setName(details.getName());
            product.setPrice(details.getPrice());
            product.setQuantity(details.getQuantity());
            product.setDescription(details.getDescription());
            product.setCategory(details.getCategory());

            productService.saveProduct(product);
            return new ResponseEntity<>(product, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 5. Xóa (ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productService.deleteProduct(id); // Đã đổi thành deleteProduct()
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}