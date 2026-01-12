package com.example.demo.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private Integer productId;
    private String name;
    private String image;
    private int quantity;
    private BigDecimal price; // Giữ lại duy nhất dòng này
}