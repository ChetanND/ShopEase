package com.shopease.controller.admin;

import com.shopease.controller.BaseServlet;
import com.shopease.service.OrderService;
import com.shopease.service.impl.OrderServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends BaseServlet {
    private final OrderService orderService = new OrderServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("stats", orderService.dashboard());
        view(request, response, "admin/dashboard.jsp");
    }
}
