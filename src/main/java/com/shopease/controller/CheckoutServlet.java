package com.shopease.controller;

import com.shopease.model.Cart;
import com.shopease.model.Order;
import com.shopease.model.User;
import com.shopease.service.OrderService;
import com.shopease.service.UserService;
import com.shopease.service.impl.OrderServiceImpl;
import com.shopease.service.impl.UserServiceImpl;
import com.shopease.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/checkout")
public class CheckoutServlet extends BaseServlet {
    private final OrderService orderService = new OrderServiceImpl();
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = SessionUtil.getCart(request);
        if (cart.isEmpty()) {
            SessionUtil.flashError(request, "Your cart is empty.");
            redirect(request, response, "/cart");
            return;
        }
        User user = userService.getById(SessionUtil.getUser(request).getId());
        SessionUtil.setUser(request, user);
        request.setAttribute("cart", cart);
        request.setAttribute("user", user);
        view(request, response, "customer/checkout.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = param(request, "paymentMethod");
        if ("ONLINE".equals(method)) {
            request.getSession(true).setAttribute("pendingPayment", "ONLINE");
            redirect(request, response, "/payment");
            return;
        }
        place(request, response, "COD");
    }

    void place(HttpServletRequest request, HttpServletResponse response, String method) throws IOException, ServletException {
        try {
            User user = userService.getById(SessionUtil.getUser(request).getId());
            Order order = orderService.placeOrder(user, SessionUtil.getCart(request), method);
            SessionUtil.setUser(request, user);
            request.setAttribute("order", order);
            view(request, response, "customer/order-confirmation.jsp");
        } catch (Exception e) {
            handleError(request, e);
            request.setAttribute("cart", SessionUtil.getCart(request));
            request.setAttribute("user", SessionUtil.getUser(request));
            view(request, response, "customer/checkout.jsp");
        }
    }
}
