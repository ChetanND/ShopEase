package com.shopease.dao;

import com.shopease.model.DashboardStats;
import com.shopease.model.Order;
import com.shopease.model.PageResult;

import java.sql.Connection;
import java.sql.SQLException;

public interface OrderDAO {
    int insertOrder(Connection connection, Order order) throws SQLException;

    void insertItem(Connection connection, int orderId, com.shopease.model.OrderItem item) throws SQLException;

    Order findById(int id);

    Order findByNumberForUser(String orderNumber, int userId);

    PageResult<Order> findByUser(int userId, int page, int pageSize);

    PageResult<Order> search(String query, String status, int page, int pageSize);

    void updateStatus(int orderId, String status);

    boolean hasPurchased(int userId, int productId);

    DashboardStats loadDashboardStats(int lowStockThreshold);
}
