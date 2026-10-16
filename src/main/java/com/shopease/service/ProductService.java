package com.shopease.service;

import com.shopease.model.Category;
import com.shopease.model.PageResult;
import com.shopease.model.Product;

import java.util.List;

public interface ProductService {
    PageResult<Product> search(String query, Integer categoryId, Double minPrice, Double maxPrice, String sort, int page);

    PageResult<Product> adminSearch(String query, Integer categoryId, int page);

    Product getActiveById(int id);

    Product getById(int id);

    List<Product> featured();

    List<Product> newest();

    List<Category> activeCategories();

    List<Category> allCategories();

    Category getCategory(int id);

    void saveCategory(Category category);

    void deactivateCategory(int id);

    void saveProduct(Product product);

    void deactivateProduct(int id);
}
