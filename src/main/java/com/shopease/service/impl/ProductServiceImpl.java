package com.shopease.service.impl;

import com.shopease.dao.CategoryDAO;
import com.shopease.dao.ProductDAO;
import com.shopease.dao.impl.CategoryDAOImpl;
import com.shopease.dao.impl.ProductDAOImpl;
import com.shopease.model.Category;
import com.shopease.model.PageResult;
import com.shopease.model.Product;
import com.shopease.service.ProductService;
import com.shopease.util.AppConstants;
import com.shopease.util.AppException;
import com.shopease.util.ValidationException;
import com.shopease.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.List;

public class ProductServiceImpl implements ProductService {
    private final ProductDAO productDAO = new ProductDAOImpl();
    private final CategoryDAO categoryDAO = new CategoryDAOImpl();

    @Override
    public PageResult<Product> search(String query, Integer categoryId, Double minPrice, Double maxPrice, String sort, int page) {
        return productDAO.search(query, categoryId, minPrice, maxPrice, sort, AppConstants.STATUS_ACTIVE,
                Math.max(page, 1), AppConstants.PAGE_SIZE);
    }

    @Override
    public PageResult<Product> adminSearch(String query, Integer categoryId, int page) {
        return productDAO.search(query, categoryId, null, null, "newest", null,
                Math.max(page, 1), AppConstants.ADMIN_PAGE_SIZE);
    }

    @Override
    public Product getActiveById(int id) {
        Product product = productDAO.findById(id);
        if (product == null || !product.isActive()) {
            throw new AppException("Product not found.");
        }
        return product;
    }

    @Override
    public Product getById(int id) {
        Product product = productDAO.findById(id);
        if (product == null) {
            throw new AppException("Product not found.");
        }
        return product;
    }

    @Override
    public List<Product> featured() {
        return productDAO.findFeatured(8);
    }

    @Override
    public List<Product> newest() {
        return productDAO.findNewest(8);
    }

    @Override
    public List<Category> activeCategories() {
        return categoryDAO.findAll(true);
    }

    @Override
    public List<Category> allCategories() {
        return categoryDAO.findAll(false);
    }

    @Override
    public Category getCategory(int id) {
        Category category = categoryDAO.findById(id);
        if (category == null) {
            throw new AppException("Category not found.");
        }
        return category;
    }

    @Override
    public void saveCategory(Category category) {
        if (ValidationUtil.isBlank(category.getName())) {
            throw new ValidationException("Category name is required.");
        }
        Category existing = categoryDAO.findByName(category.getName().trim());
        if (existing != null && existing.getId() != category.getId()) {
            throw new ValidationException("A category with this name already exists.");
        }
        if (ValidationUtil.isBlank(category.getStatus())) {
            category.setStatus(AppConstants.STATUS_ACTIVE);
        }
        if (category.getId() > 0) {
            categoryDAO.update(category);
        } else {
            categoryDAO.save(category);
        }
    }

    @Override
    public void deactivateCategory(int id) {
        categoryDAO.updateStatus(id, AppConstants.STATUS_INACTIVE);
    }

    @Override
    public void saveProduct(Product product) {
        if (ValidationUtil.isBlank(product.getName()) || ValidationUtil.isBlank(product.getDescription())
                || ValidationUtil.isBlank(product.getImageUrl())) {
            throw new ValidationException("Name, description, and image URL are required.");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Enter a valid price.");
        }
        if (product.getDiscount() == null) {
            product.setDiscount(BigDecimal.ZERO);
        }
        if (product.getDiscount().compareTo(BigDecimal.ZERO) < 0 || product.getDiscount().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new ValidationException("Discount must be between 0 and 100.");
        }
        if (product.getStock() < 0) {
            throw new ValidationException("Stock cannot be negative.");
        }
        if (categoryDAO.findById(product.getCategoryId()) == null) {
            throw new ValidationException("Select a valid category.");
        }
        if (ValidationUtil.isBlank(product.getStatus())) {
            product.setStatus(AppConstants.STATUS_ACTIVE);
        }
        if (product.getId() > 0) {
            productDAO.update(product);
        } else {
            productDAO.save(product);
        }
    }

    @Override
    public void deactivateProduct(int id) {
        productDAO.updateStatus(id, AppConstants.STATUS_INACTIVE);
    }
}
