package com.shopease.service.impl;

import com.shopease.dao.ProductDAO;
import com.shopease.dao.ReviewDAO;
import com.shopease.dao.impl.ProductDAOImpl;
import com.shopease.dao.impl.ReviewDAOImpl;
import com.shopease.model.PageResult;
import com.shopease.model.Review;
import com.shopease.service.OrderService;
import com.shopease.service.ReviewService;
import com.shopease.util.AppConstants;
import com.shopease.util.ValidationException;
import com.shopease.util.ValidationUtil;

import java.util.List;

public class ReviewServiceImpl implements ReviewService {
    private final ReviewDAO reviewDAO = new ReviewDAOImpl();
    private final ProductDAO productDAO = new ProductDAOImpl();
    private final OrderService orderService = new OrderServiceImpl();

    @Override
    public List<Review> forProduct(int productId) {
        return reviewDAO.findByProduct(productId);
    }

    @Override
    public void add(int userId, int productId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5.");
        }
        if (ValidationUtil.isBlank(comment) || comment.trim().length() < 10) {
            throw new ValidationException("Please write a short review of at least 10 characters.");
        }
        if (!canReview(userId, productId)) {
            throw new ValidationException("You can review a product only after purchasing it.");
        }
        if (reviewDAO.exists(userId, productId)) {
            throw new ValidationException("You have already reviewed this product.");
        }
        Review review = new Review();
        review.setUserId(userId);
        review.setProductId(productId);
        review.setRating(rating);
        review.setComment(comment.trim());
        reviewDAO.save(review);
        productDAO.updateAverageRating(productId);
    }

    @Override
    public boolean canReview(int userId, int productId) {
        return new com.shopease.dao.impl.OrderDAOImpl().hasPurchased(userId, productId);
    }

    @Override
    public PageResult<Review> adminList(int page) {
        return reviewDAO.findAll(Math.max(page, 1), AppConstants.ADMIN_PAGE_SIZE);
    }

    @Override
    public void delete(int id) {
        reviewDAO.delete(id);
    }
}
