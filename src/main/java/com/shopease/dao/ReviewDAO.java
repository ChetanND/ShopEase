package com.shopease.dao;

import com.shopease.model.PageResult;
import com.shopease.model.Review;

import java.util.List;

public interface ReviewDAO {
    void save(Review review);

    List<Review> findByProduct(int productId);

    PageResult<Review> findAll(int page, int pageSize);

    boolean exists(int userId, int productId);

    void delete(int id);
}
