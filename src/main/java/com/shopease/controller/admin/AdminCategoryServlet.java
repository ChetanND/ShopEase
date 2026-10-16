package com.shopease.controller.admin;

import com.shopease.controller.BaseServlet;
import com.shopease.model.Category;
import com.shopease.service.ProductService;
import com.shopease.service.impl.ProductServiceImpl;
import com.shopease.util.AppConstants;
import com.shopease.util.SessionUtil;
import com.shopease.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/categories")
public class AdminCategoryServlet extends BaseServlet {
    private final ProductService productService = new ProductServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("categories", productService.allCategories());
        Integer id = ValidationUtil.parsePositiveInt(param(request, "id"));
        if (id != null) {
            request.setAttribute("category", productService.getCategory(id));
        }
        view(request, response, "admin/categories.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = param(request, "action");
        try {
            if ("deactivate".equals(action)) {
                productService.deactivateCategory(ValidationUtil.parsePositiveInt(param(request, "id")));
                SessionUtil.flashSuccess(request, "Category deactivated.");
            } else {
                Category category = new Category();
                Integer id = ValidationUtil.parsePositiveInt(param(request, "id"));
                if (id != null) {
                    category.setId(id);
                }
                category.setName(param(request, "name"));
                category.setDescription(param(request, "description"));
                category.setImageUrl(param(request, "imageUrl"));
                category.setStatus(param(request, "status").isEmpty() ? AppConstants.STATUS_ACTIVE : param(request, "status"));
                productService.saveCategory(category);
                SessionUtil.flashSuccess(request, "Category saved.");
            }
        } catch (Exception e) {
            SessionUtil.flashError(request, e.getMessage());
        }
        redirect(request, response, "/admin/categories");
    }
}
