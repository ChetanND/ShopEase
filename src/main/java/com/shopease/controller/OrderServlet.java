package com.shopease.controller;

import com.shopease.model.User;
import com.shopease.service.OrderService;
import com.shopease.service.impl.OrderServiceImpl;
import com.shopease.util.SessionUtil;
import com.shopease.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/orders", "/order"})
public class OrderServlet extends BaseServlet {
    private final OrderService orderService = new OrderServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = SessionUtil.getUser(request);
        if ("/order".equals(request.getServletPath())) {
            try {
                request.setAttribute("order", orderService.getForUser(param(request, "id"), user.getId()));
                view(request, response, "customer/order-details.jsp");
            } catch (Exception e) {
                handleError(request, e);
                view(request, response, "error/error.jsp");
            }
            return;
        }
        Integer page = ValidationUtil.parsePositiveInt(param(request, "page"));
        request.setAttribute("pageResult", orderService.myOrders(user.getId(), page == null ? 1 : page));
        view(request, response, "customer/orders.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            orderService.cancelForUser(param(request, "id"), SessionUtil.getUser(request).getId());
            SessionUtil.flashSuccess(request, "Order cancelled. Stock has been restored.");
        } catch (Exception e) {
            SessionUtil.flashError(request, e.getMessage());
        }
        redirect(request, response, "/order?id=" + param(request, "id"));
    }
}
