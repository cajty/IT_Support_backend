package org.ably.it_support.category;

public class CategoryFactory {
    public static Category createCategory(String name) {
        return Category.builder()
            .name(name)
            .build();
    }
}
