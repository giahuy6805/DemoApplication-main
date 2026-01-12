package com.example.demo.service;

import com.example.demo.model.CartItem;
import java.util.Collection;

public interface ShoppingCartService {
    void add(Integer id); // Thêm sản phẩm vào giỏ dựa trên ID
    void remove(Integer id); // Xóa sản phẩm khỏi giỏ
    void update(Integer id, int qty); // Cập nhật số lượng
    void clear(); // Xóa sạch giỏ hàng
    Collection<CartItem> getItems(); // Lấy danh sách sản phẩm trong giỏ
    int getCount(); // Tính tổng số lượng sản phẩm
    double getAmount(); // Tính tổng tiền
}