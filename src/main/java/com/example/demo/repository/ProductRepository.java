package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // 1. Bản tìm kiếm TRẢ VỀ LIST (Dùng Ycho dòng 31 trong Service đang bị đỏ)
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // 2. Bản tìm kiếm CÓ PHÂN TRANG (Trả về Page)
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    // 3. Tìm theo tên danh mục (Laptop, Điện thoại...)
    List<Product> findByCategoryName(String categoryName);
}