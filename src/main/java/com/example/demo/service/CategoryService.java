package com.example.demo.service;

import com.example.demo.entity.Category;

import java.util.List;

public interface CategoryService {
    // Thêm 2 dòng này để khớp với Controller và Seeder
    List<Category> getAllCategories();
    void saveCategory(Category category);

    // Các hàm bạn đã có
    List<Category> findAll();
    Category findById(Integer id);
    Category save(Category category);
    void delete(Integer id);
}