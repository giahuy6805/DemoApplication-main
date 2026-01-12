package com.example.demo.service;

import com.example.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.demo.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> findAll();
    List<Product> getAllProducts();
    Product getProductById(Integer id);
    Product saveProduct(Product product);
    void deleteProduct(Integer id);
    List<Product> searchProducts(String keyword);
    // Thêm hàm lấy sản phẩm có phân trang và tìm kiếm
    Page<Product> searchProducts(String keyword, Pageable pageable);
    Page<Product> getAllProducts(Pageable pageable);
    List<Product> findByCategory(String categoryName);
}
