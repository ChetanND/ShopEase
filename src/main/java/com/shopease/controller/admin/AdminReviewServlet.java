package com.shopease.controller.admin;

import com.shopease.controller.BaseServlet;
import com.shopease.service.ReviewService;
import com.shopease.service.impl.ReviewServiceImpl;
import com.shopease.util.SessionUtil;
import com.shopease.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/reviews")
public class AdminReviewServlet extends BaseServlet {
    private final ReviewService reviewService = new ReviewServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer page = ValidationUtil.parsePositiveInt(param(request, "page"));
        request.setAttribute("pageResult", reviewService.adminList(page == null ? 1 : page));
        view(request, response, "admin/reviews.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            reviewService.delete(ValidationUtil.parsePositiveInt(param(request, "id")));
            SessionUtil.flashSuccess(request, "Review removed.");
        } catch (Exception e) {
            SessionUtil.flashError(request, e.getMessage());
        }
        redirect(request, response, "/admin/reviews");
    }
}
