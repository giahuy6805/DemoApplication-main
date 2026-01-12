package com.example.demo.repository;

import com.example.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.User;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // 1. Đếm tổng số đơn hàng
    long count();

    // 2. Tính tổng doanh thu (chỉ tính những đơn đã giao thành công SHIPPED)
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'SHIPPED'")
    Double getTotalRevenue();

    // 3. Đếm số đơn hàng mới (đang chờ xử lý PENDING)
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'PENDING'")
    long countPendingOrders();

    @Query("SELECT SUM(o.totalAmount) FROM Order o " +
            "WHERE o.status = 'SHIPPED' AND FUNCTION('MONTH', o.createdAt) = :month " +
            "AND FUNCTION('YEAR', o.createdAt) = FUNCTION('YEAR', CURRENT_DATE)")
    Double getRevenueByMonth(@Param("month") int month);

    @Query("SELECT SUM(oi.price * oi.quantity) FROM OrderItem oi " +
            "WHERE oi.order.status = 'SHIPPED' AND oi.product.category.name = :catName")
    Double getRevenueByCategory(@Param("catName") String catName);

    List<Order> findByUserOrderByCreatedAtDesc(User user);
}