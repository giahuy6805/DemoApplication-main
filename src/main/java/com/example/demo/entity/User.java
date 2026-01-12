package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100)
    private String fullName;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    /**
     * Quan trọng: Để khớp với code (user.getRole() == 0) ở Controller,
     * mình để kiểu Integer. 0: Admin, 1: Khách hàng.
     */
    private String role;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    // KHÔNG CẦN viết Getters/Setters thủ công vì đã có @Data của Lombok rồi Huy nhé!
}