package com.example.demo.config;

import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
public class FakerConfig {

    @Bean
    public Faker faker() {
        // Bạn có thể thiết lập ngôn ngữ (Locale) tại đây, ví dụ: Tiếng Việt
        return new Faker(new Locale("vi"));
    }
}