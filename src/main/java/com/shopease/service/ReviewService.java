package com.shopease.service;

import com.shopease.model.PageResult;
import com.shopease.model.Review;

import java.util.List;

public interface ReviewService {
    List<Review> forProduct(int productId);

    void add(int userId, int productId, int rating, String comment);

    boolean canReview(int userId, int productId);

    PageResult<Review> adminList(int page);

    void delete(int id);
}
