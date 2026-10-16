package com.shopease.controller.admin;

import com.shopease.controller.BaseServlet;
import com.shopease.model.Product;
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
import java.math.BigDecimal;

@WebServlet("/admin/products")
public class AdminProductServlet extends BaseServlet {
    private final ProductService productService = new ProductServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = param(request, "action");
        request.setAttribute("categories", productService.allCategories());
        if ("new".equals(action) || "edit".equals(action)) {
            if ("edit".equals(action)) {
                Integer id = ValidationUtil.parsePositiveInt(param(request, "id"));
                request.setAttribute("product", productService.getById(id));
            }
            view(request, response, "admin/product-form.jsp");
            return;
        }
        Integer page = ValidationUtil.parsePositiveInt(param(request, "page"));
        Integer categoryId = ValidationUtil.parsePositiveInt(param(request, "category"));
        request.setAttribute("pageResult", productService.adminSearch(param(request, "q"), categoryId, page == null ? 1 : page));
        request.setAttribute("q", param(request, "q"));
        view(request, response, "admin/products.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String action = param(request, "action");
        try {
            if ("deactivate".equals(action)) {
                Integer id = ValidationUtil.parsePositiveInt(param(request, "id"));
                productService.deactivateProduct(id);
                SessionUtil.flashSuccess(request, "Product deactivated.");
            } else if ("activate".equals(action)) {
                Product product = productService.getById(ValidationUtil.parsePositiveInt(param(request, "id")));
                product.setStatus(AppConstants.STATUS_ACTIVE);
                productService.saveProduct(product);
                SessionUtil.flashSuccess(request, "Product activated.");
            } else {
                Product product = new Product();
                Integer id = ValidationUtil.parsePositiveInt(param(request, "id"));
                if (id != null) {
                    product.setId(id);
                }
                product.setName(param(request, "name"));
                product.setDescription(param(request, "description"));
                product.setCategoryId(ValidationUtil.parsePositiveInt(param(request, "categoryId")) == null ? 0
                        : ValidationUtil.parsePositiveInt(param(request, "categoryId")));
                product.setPrice(bd(param(request, "price")));
                product.setDiscount(bd(param(request, "discount")));
                Integer stock = ValidationUtil.parsePositiveInt(param(request, "stock"));
                product.setStock(stock == null ? 0 : stock);
                if ("0".equals(param(request, "stock"))) {
                    product.setStock(0);
                }
                product.setImageUrl(param(request, "imageUrl"));
                product.setStatus(param(request, "status"));
                productService.saveProduct(product);
                SessionUtil.flashSuccess(request, "Product saved.");
            }
            redirect(request, response, "/admin/products");
        } catch (Exception e) {
            handleError(request, e);
            request.setAttribute("categories", productService.allCategories());
            view(request, response, "admin/product-form.jsp");
        }
    }

    private BigDecimal bd(String value) {
        Double parsed = ValidationUtil.parseNonNegativeDouble(value);
        return parsed == null ? null : BigDecimal.valueOf(parsed);
    }
}
