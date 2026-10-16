package com.shopease.controller;

import com.shopease.service.ProductService;
import com.shopease.service.impl.ProductServiceImpl;
import com.shopease.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/products")
public class ProductServlet extends BaseServlet {
    private final ProductService productService = new ProductServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String q = param(request, "q");
        Integer categoryId = ValidationUtil.parsePositiveInt(param(request, "category"));
        Double minPrice = ValidationUtil.parseNonNegativeDouble(param(request, "minPrice"));
        Double maxPrice = ValidationUtil.parseNonNegativeDouble(param(request, "maxPrice"));
        String sort = param(request, "sort");
        Integer page = ValidationUtil.parsePositiveInt(param(request, "page"));
        request.setAttribute("pageResult", productService.search(q, categoryId, minPrice, maxPrice, sort, page == null ? 1 : page));
        request.setAttribute("categories", productService.activeCategories());
        request.setAttribute("q", q);
        request.setAttribute("selectedCategory", categoryId);
        request.setAttribute("minPrice", param(request, "minPrice"));
        request.setAttribute("maxPrice", param(request, "maxPrice"));
        request.setAttribute("sort", sort);
        view(request, response, "customer/products.jsp");
    }
}
