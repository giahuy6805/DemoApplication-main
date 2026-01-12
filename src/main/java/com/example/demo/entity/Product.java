package com.example.demo.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 1. Tên sản phẩm (Đã gộp Validation và Column)
    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 5, max = 128, message = "Tên sản phẩm phải từ 5-128 ký tự")
    @Column(length = 128)
    private String name;

    // 2. Mô tả
    @NotBlank(message = "Mô tả không được để trống")
    @Column(columnDefinition = "TEXT")
    private String description;

    // 3. Giá bán
    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    // 4. Số lượng
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được âm")
    private Integer quantity;

    // 5. Lượt xem (Cái này không cần validation vì hệ thống tự tăng)
    private Integer view;

    // 6. Danh mục (Rất quan trọng: Phải có @NotNull để bắt buộc chọn)
    @NotNull(message = "Vui lòng chọn danh mục")
    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties("products")
    private Category category;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String image;

    // --- GETTERS AND SETTERS (Giữ nguyên phần cũ của Huy bên dưới) ---

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getView() { return view; }
    public void setView(Integer view) { this.view = view; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}