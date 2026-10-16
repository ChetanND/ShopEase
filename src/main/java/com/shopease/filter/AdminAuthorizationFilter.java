package com.shopease.filter;

import com.shopease.util.SessionUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminAuthorizationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (!SessionUtil.isLoggedIn(httpRequest)) {
            SessionUtil.flashError(httpRequest, "Please log in as an administrator.");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }
        if (!SessionUtil.isAdmin(httpRequest)) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/home");
            return;
        }
        chain.doFilter(request, response);
    }
}
