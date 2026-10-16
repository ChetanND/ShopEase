package com.shopease.dao;

import com.shopease.model.PageResult;
import com.shopease.model.Product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ProductDAO {
    Product findById(int id);

    Product findById(Connection connection, int id) throws SQLException;

    PageResult<Product> search(String query, Integer categoryId, Double minPrice, Double maxPrice,
                               String sort, String status, int page, int pageSize);

    List<Product> findFeatured(int limit);

    List<Product> findNewest(int limit);

    List<Product> findLowStock(int threshold);

    int save(Product product);

    void update(Product product);

    void updateStatus(int id, String status);

    void reduceStock(Connection connection, int productId, int quantity) throws SQLException;

    void restoreStock(Connection connection, int productId, int quantity) throws SQLException;

    void updateAverageRating(int productId);

    int countAll();

    int countLowStock(int threshold);
}
