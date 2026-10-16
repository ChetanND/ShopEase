package com.shopease.filter;

import com.shopease.model.Cart;
import com.shopease.util.AppConstants;
import com.shopease.util.SessionUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class CartFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        Cart cart = SessionUtil.getCart(httpRequest);
        httpRequest.setAttribute("cartCount", cart.getItemCount());
        httpRequest.setAttribute(AppConstants.SESSION_CART, cart);
        SessionUtil.moveFlashToRequest(httpRequest);
        chain.doFilter(request, response);
    }
}
