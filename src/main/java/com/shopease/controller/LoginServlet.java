package com.shopease.controller;

import com.shopease.model.Address;
import com.shopease.model.Cart;
import com.shopease.model.User;
import com.shopease.service.CartService;
import com.shopease.service.UserService;
import com.shopease.service.impl.CartServiceImpl;
import com.shopease.service.impl.UserServiceImpl;
import com.shopease.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    private final UserService userService = new UserServiceImpl();
    private final CartService cartService = new CartServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (SessionUtil.isLoggedIn(request)) {
            redirect(request, response, SessionUtil.isAdmin(request) ? "/admin/dashboard" : "/home");
            return;
        }
        view(request, response, "customer/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            User user = userService.login(param(request, "email"), param(request, "password"));
            Cart sessionCart = SessionUtil.getCart(request);
            SessionUtil.setUser(request, user);
            if (!user.isAdmin()) {
                SessionUtil.setCart(request, cartService.mergeOnLogin(user, sessionCart));
            }
            SessionUtil.flashSuccess(request, "Welcome back, " + user.getFullName() + ".");
            String next = param(request, "next");
            if (user.isAdmin()) {
                redirect(request, response, "/admin/dashboard");
            } else if (!next.isEmpty() && next.startsWith("/")) {
                redirect(request, response, next);
            } else {
                redirect(request, response, "/home");
            }
        } catch (Exception e) {
            handleError(request, e);
            view(request, response, "customer/login.jsp");
        }
    }
}
