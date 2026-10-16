package com.shopease.controller;

import com.shopease.model.Cart;
import com.shopease.service.CartService;
import com.shopease.service.impl.CartServiceImpl;
import com.shopease.util.SessionUtil;
import com.shopease.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/cart")
public class CartServlet extends BaseServlet {
    private final CartService cartService = new CartServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("cart", SessionUtil.getCart(request));
        view(request, response, "customer/cart.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = param(request, "action");
        Integer productId = ValidationUtil.parsePositiveInt(param(request, "productId"));
        Integer quantity = ValidationUtil.parsePositiveInt(param(request, "quantity"));
        Cart cart = SessionUtil.getCart(request);
        try {
            if (productId == null) {
                throw new com.shopease.util.ValidationException("Invalid product.");
            }
            if ("add".equals(action)) {
                cartService.add(cart, productId, quantity == null ? 1 : quantity);
                SessionUtil.flashSuccess(request, "Added to cart.");
            } else if ("update".equals(action)) {
                cartService.update(cart, productId, quantity == null ? 0 : quantity);
                SessionUtil.flashSuccess(request, "Cart updated.");
            } else if ("increase".equals(action)) {
                int current = cart.getItems().stream().filter(i -> i.getProductId() == productId).mapToInt(i -> i.getQuantity()).findFirst().orElse(0);
                cartService.update(cart, productId, current + 1);
            } else if ("decrease".equals(action)) {
                int current = cart.getItems().stream().filter(i -> i.getProductId() == productId).mapToInt(i -> i.getQuantity()).findFirst().orElse(0);
                cartService.update(cart, productId, current - 1);
            } else if ("remove".equals(action)) {
                cartService.remove(cart, productId);
                SessionUtil.flashSuccess(request, "Item removed.");
            }
            cartService.persist(SessionUtil.getUser(request), cart);
        } catch (Exception e) {
            SessionUtil.flashError(request, e.getMessage());
        }
        String buyNow = param(request, "buyNow");
        if ("1".equals(buyNow)) {
            redirect(request, response, "/checkout");
        } else {
            redirect(request, response, "/cart");
        }
    }
}
