package com.shopease.service;

import com.shopease.model.Cart;
import com.shopease.model.DashboardStats;
import com.shopease.model.Order;
import com.shopease.model.PageResult;
import com.shopease.model.User;

public interface OrderService {
    Order placeOrder(User user, Cart cart, String paymentMethod);

    PageResult<Order> myOrders(int userId, int page);

    Order getForUser(String orderNumber, int userId);

    Order getById(int id);

    void cancelForUser(String orderNumber, int userId);

    PageResult<Order> adminSearch(String query, String status, int page);

    void updateStatus(int orderId, String status);

    DashboardStats dashboard();
}
