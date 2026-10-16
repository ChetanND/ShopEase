package com.shopease.controller;

import com.shopease.dao.impl.ContactDAOImpl;
import com.shopease.service.ProductService;
import com.shopease.service.impl.ProductServiceImpl;
import com.shopease.util.SessionUtil;
import com.shopease.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/home", "/about", "/contact", "/categories"})
public class HomeServlet extends BaseServlet {
    private final ProductService productService = new ProductServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        request.setAttribute("categories", productService.activeCategories());
        if ("/about".equals(path)) {
            view(request, response, "customer/about.jsp");
            return;
        }
        if ("/contact".equals(path)) {
            view(request, response, "customer/contact.jsp");
            return;
        }
        if ("/categories".equals(path)) {
            view(request, response, "customer/categories.jsp");
            return;
        }
        request.setAttribute("featured", productService.featured());
        request.setAttribute("newest", productService.newest());
        view(request, response, "customer/home.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!"/contact".equals(request.getServletPath())) {
            doGet(request, response);
            return;
        }
        String name = param(request, "name");
        String email = param(request, "email");
        String subject = param(request, "subject");
        String message = param(request, "message");
        if (ValidationUtil.isBlank(name) || !ValidationUtil.isEmail(email)
                || ValidationUtil.isBlank(subject) || ValidationUtil.isBlank(message)) {
            request.setAttribute("errorMessage", "Please complete all contact fields with a valid email.");
            view(request, response, "customer/contact.jsp");
            return;
        }
        new ContactDAOImpl().save(name, email, subject, message);
        SessionUtil.flashSuccess(request, "Thanks for writing in. We will get back to you shortly.");
        redirect(request, response, "/contact");
    }
}
