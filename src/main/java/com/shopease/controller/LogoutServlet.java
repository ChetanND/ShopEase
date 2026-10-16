package com.shopease.controller;

import com.shopease.util.SessionUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUtil.logout(request);
        response.sendRedirect(request.getContextPath() + "/home");
    }
}
