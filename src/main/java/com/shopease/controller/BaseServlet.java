package com.shopease.controller;

import com.shopease.util.AppException;
import com.shopease.util.ValidationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {
    protected void view(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/" + path).forward(request, response);
    }

    protected void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }

    protected String param(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }

    protected void handleError(HttpServletRequest request, Exception e) {
        if (e instanceof ValidationException) {
            request.setAttribute("errorMessage", e.getMessage());
        } else if (e instanceof AppException) {
            request.setAttribute("errorMessage", e.getMessage());
        } else {
            request.setAttribute("errorMessage", "Something went wrong. Please try again.");
        }
    }
}
