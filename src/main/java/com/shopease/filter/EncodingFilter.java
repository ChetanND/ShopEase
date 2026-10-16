package com.shopease.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (response instanceof HttpServletResponse && request instanceof HttpServletRequest) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        }
        chain.doFilter(request, response);
    }
}
