package com.example.demo.seeder;

import com.example.demo.entity.Category;
import com.example.demo.service.CategoryService;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class CategoryDataSeed implements CommandLineRunner {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private Faker faker;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>>Seeding");
        for (int i = 0; i < 100; i++) {
            Category category = new Category();
            category.setName(faker.name().fullName());
            category.setThumbnail("thumbnail category");
            categoryService.saveCategory(category);
        }
        System.out.println("Seed finished");
    }
}
