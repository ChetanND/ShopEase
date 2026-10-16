package com.shopease.dao;

import com.shopease.model.Category;

import java.util.List;

public interface CategoryDAO {
    List<Category> findAll(boolean activeOnly);

    Category findById(int id);

    Category findByName(String name);

    void save(Category category);

    void update(Category category);

    void updateStatus(int id, String status);

    boolean hasActiveProducts(int categoryId);
}
