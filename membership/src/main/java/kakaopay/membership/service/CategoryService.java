package kakaopay.membership.service;

import kakaopay.membership.domain.Category;
import kakaopay.membership.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category getCategoryByName(String name) {
        return categoryRepository.findByCategoryName(name);
    }
}
