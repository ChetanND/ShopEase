package com.shopease.controller.admin;

import com.shopease.controller.BaseServlet;
import com.shopease.service.UserService;
import com.shopease.service.impl.UserServiceImpl;
import com.shopease.util.SessionUtil;
import com.shopease.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/users")
public class AdminUserServlet extends BaseServlet {
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = ValidationUtil.parsePositiveInt(param(request, "id"));
        if (id != null) {
            request.setAttribute("customer", userService.getById(id));
            view(request, response, "admin/user-details.jsp");
            return;
        }
        Integer page = ValidationUtil.parsePositiveInt(param(request, "page"));
        request.setAttribute("q", param(request, "q"));
        request.setAttribute("pageResult", userService.listCustomers(param(request, "q"), page == null ? 1 : page));
        view(request, response, "admin/users.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            userService.setStatus(ValidationUtil.parsePositiveInt(param(request, "id")), param(request, "status"));
            SessionUtil.flashSuccess(request, "Customer status updated.");
        } catch (Exception e) {
            SessionUtil.flashError(request, e.getMessage());
        }
        redirect(request, response, "/admin/users");
    }
}
