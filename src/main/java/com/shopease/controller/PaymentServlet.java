package com.shopease.controller;

import com.shopease.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/payment")
public class PaymentServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession(false) == null || request.getSession(false).getAttribute("pendingPayment") == null) {
            redirect(request, response, "/checkout");
            return;
        }
        request.setAttribute("cart", SessionUtil.getCart(request));
        view(request, response, "customer/payment.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession(false) != null) {
            request.getSession(false).removeAttribute("pendingPayment");
        }
        new CheckoutServlet().place(request, response, "ONLINE");
    }
}
