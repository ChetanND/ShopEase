package com.shopease.controller;

import com.shopease.service.ReviewService;
import com.shopease.service.impl.ReviewServiceImpl;
import com.shopease.util.SessionUtil;
import com.shopease.util.ValidationUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/review")
public class ReviewServlet extends BaseServlet {
    private final ReviewService reviewService = new ReviewServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer productId = ValidationUtil.parsePositiveInt(param(request, "productId"));
        Integer rating = ValidationUtil.parsePositiveInt(param(request, "rating"));
        try {
            if (productId == null || rating == null) {
                throw new com.shopease.util.ValidationException("Invalid review details.");
            }
            reviewService.add(SessionUtil.getUser(request).getId(), productId, rating, param(request, "comment"));
            SessionUtil.flashSuccess(request, "Thanks for your review.");
        } catch (Exception e) {
            SessionUtil.flashError(request, e.getMessage());
        }
        redirect(request, response, "/product?id=" + (productId == null ? "" : productId));
    }
}
