package com.shopease.controller;

import com.shopease.model.User;
import com.shopease.service.ProductService;
import com.shopease.service.ReviewService;
import com.shopease.service.impl.ProductServiceImpl;
import com.shopease.service.impl.ReviewServiceImpl;
import com.shopease.util.SessionUtil;
import com.shopease.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/product")
public class ProductDetailsServlet extends BaseServlet {
    private final ProductService productService = new ProductServiceImpl();
    private final ReviewService reviewService = new ReviewServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = ValidationUtil.parsePositiveInt(param(request, "id"));
        if (id == null) {
            request.setAttribute("errorMessage", "Invalid product ID.");
            view(request, response, "error/error.jsp");
            return;
        }
        try {
            request.setAttribute("product", productService.getActiveById(id));
            request.setAttribute("reviews", reviewService.forProduct(id));
            User user = SessionUtil.getUser(request);
            request.setAttribute("canReview", user != null && !user.isAdmin() && reviewService.canReview(user.getId(), id));
            view(request, response, "customer/product-details.jsp");
        } catch (Exception e) {
            handleError(request, e);
            view(request, response, "error/error.jsp");
        }
    }
}
