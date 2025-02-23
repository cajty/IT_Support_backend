package org.ably.it_support.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;
    private List<Category> cachedCategories;


    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategory() {
        if (cachedCategories == null) {
            cachedCategories = categoryRepository.findAll();
        }
        return cachedCategories;
    }
}
