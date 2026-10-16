package com.shopease.controller.admin;

import com.shopease.controller.BaseServlet;
import com.shopease.service.OrderService;
import com.shopease.service.impl.OrderServiceImpl;
import com.shopease.util.AppConstants;
import com.shopease.util.SessionUtil;
import com.shopease.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/orders")
public class AdminOrderServlet extends BaseServlet {
    private final OrderService orderService = new OrderServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = ValidationUtil.parsePositiveInt(param(request, "id"));
        if (id != null) {
            request.setAttribute("order", orderService.getById(id));
            request.setAttribute("statuses", AppConstants.ORDER_STATUSES);
            view(request, response, "admin/order-details.jsp");
            return;
        }
        Integer page = ValidationUtil.parsePositiveInt(param(request, "page"));
        request.setAttribute("q", param(request, "q"));
        request.setAttribute("status", param(request, "status"));
        request.setAttribute("statuses", AppConstants.ORDER_STATUSES);
        request.setAttribute("pageResult", orderService.adminSearch(param(request, "q"), param(request, "status"), page == null ? 1 : page));
        view(request, response, "admin/orders.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = ValidationUtil.parsePositiveInt(param(request, "id"));
        try {
            orderService.updateStatus(id, param(request, "status"));
            SessionUtil.flashSuccess(request, "Order status updated.");
        } catch (Exception e) {
            SessionUtil.flashError(request, e.getMessage());
        }
        redirect(request, response, "/admin/orders?id=" + id);
    }
}
