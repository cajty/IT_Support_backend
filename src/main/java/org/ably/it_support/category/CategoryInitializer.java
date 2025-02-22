package org.ably.it_support.category;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public CategoryInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        List<String> defaultCategories = List.of(
            "Network", "Hardware", "Software",
            "Security", "Access & Permissions", "Email Issues", "Printing Issues",
            "System Crash", "Performance Issues", "Data Backup & Recovery",
            "Software Installation", "Technical Assistance"
        );

        for (String categoryName : defaultCategories) {
            if(!categoryRepository.existsByName(categoryName)) {
                 Category newCategory = CategoryFactory.createCategory(categoryName);
                 categoryRepository.save(newCategory);
            }
        }

        System.out.println("Default categories inserted successfully!");
    }
}
