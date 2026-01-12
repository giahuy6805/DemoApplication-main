package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity // Đây là chìa khóa để dùng Policy (PreAuthorize)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Mở toang cửa cho tất cả các đường dẫn để Interceptor tự quản lý
                        .requestMatchers("/**").permitAll()
                        .requestMatchers("/", "/login", "/logout").permitAll()
                );

        // Xóa bỏ hoặc comment dòng .httpBasic(...) đi Huy nhé
        // .httpBasic(basic -> {});

        return http.build();
    }
}