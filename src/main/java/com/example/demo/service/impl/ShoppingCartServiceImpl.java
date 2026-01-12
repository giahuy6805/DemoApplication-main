package com.example.demo.service.impl;

import com.example.demo.entity.Product;
import com.example.demo.model.CartItem;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@SessionScope
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ProductRepository repo;

    Map<Integer, CartItem> maps = new HashMap<>();

    @Override
    public void add(Integer id) {
        CartItem item = maps.get(id);
        if (item == null) {
            Product p = repo.findById(id).orElse(null);
            if (p != null) {
                // Đảm bảo thứ tự: ID, Name, Image, Quantity, Price
                item = new CartItem(p.getId(), p.getName(), p.getImage(), 1, p.getPrice());
                maps.put(id, item);
            }
        } else {
            item.setQuantity(item.getQuantity() + 1);
        }
    }

    @Override
    public void remove(Integer id) {
        maps.remove(id);
    }

    @Override
    public void update(Integer id, int qty) {
        CartItem item = maps.get(id);
        if (item != null) {
            item.setQuantity(qty);
        }
    }

    @Override
    public void clear() {
        maps.clear();
    }

    @Override
    public Collection<CartItem> getItems() {
        return maps.values();
    }

    @Override
    public int getCount() {
        return maps.values().stream()
                .mapToInt(item -> item.getQuantity())
                .sum();
    }

    @Override
    public double getAmount() {
        // Chuyển BigDecimal sang double để tính toán tổng tiền
        return maps.values().stream()
                .mapToDouble(item -> item.getPrice().doubleValue() * item.getQuantity())
                .sum();
    }
}