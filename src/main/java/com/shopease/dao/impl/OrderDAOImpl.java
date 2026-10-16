package com.shopease.dao.impl;

import com.shopease.dao.OrderDAO;
import com.shopease.model.DashboardStats;
import com.shopease.model.Order;
import com.shopease.model.OrderItem;
import com.shopease.model.PageResult;
import com.shopease.util.AppException;
import com.shopease.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {

    @Override
    public int insertOrder(Connection connection, Order order) throws SQLException {
        String sql = "INSERT INTO orders (order_number, user_id, full_name, email, phone, address_line, city, state, pincode, "
                + "payment_method, payment_status, status, subtotal, discount_amount, shipping, grand_total) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, order.getOrderNumber());
            ps.setInt(2, order.getUserId());
            ps.setString(3, order.getFullName());
            ps.setString(4, order.getEmail());
            ps.setString(5, order.getPhone());
            ps.setString(6, order.getAddressLine());
            ps.setString(7, order.getCity());
            ps.setString(8, order.getState());
            ps.setString(9, order.getPincode());
            ps.setString(10, order.getPaymentMethod());
            ps.setString(11, order.getPaymentStatus());
            ps.setString(12, order.getStatus());
            ps.setBigDecimal(13, order.getSubtotal());
            ps.setBigDecimal(14, order.getDiscountAmount());
            ps.setBigDecimal(15, order.getShipping());
            ps.setBigDecimal(16, order.getGrandTotal());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    @Override
    public void insertItem(Connection connection, int orderId, OrderItem item) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, product_id, product_name, unit_price, quantity, discount, line_total) "
                + "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, item.getProductId());
            ps.setString(3, item.getProductName());
            ps.setBigDecimal(4, item.getUnitPrice());
            ps.setInt(5, item.getQuantity());
            ps.setBigDecimal(6, item.getDiscount());
            ps.setBigDecimal(7, item.getLineTotal());
            ps.executeUpdate();
        }
    }

    @Override
    public Order findById(int id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Order order = map(rs);
                order.setItems(loadItems(conn, order.getId()));
                return order;
            }
        } catch (SQLException e) {
            throw new AppException("Unable to load order.", e);
        }
    }

    @Override
    public Order findByNumberForUser(String orderNumber, int userId) {
        String sql = "SELECT * FROM orders WHERE order_number = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNumber);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Order order = map(rs);
                order.setItems(loadItems(conn, order.getId()));
                return order;
            }
        } catch (SQLException e) {
            throw new AppException("Unable to load order.", e);
        }
    }

    @Override
    public PageResult<Order> findByUser(int userId, int page, int pageSize) {
        return searchInternal(" AND user_id = ? ", List.of(userId), page, pageSize);
    }

    @Override
    public PageResult<Order> search(String query, String status, int page, int pageSize) {
        StringBuilder extra = new StringBuilder();
        List<Object> params = new ArrayList<>();
        if (status != null && !status.isBlank()) {
            extra.append(" AND status = ? ");
            params.add(status);
        }
        if (query != null && !query.isBlank()) {
            extra.append(" AND (order_number LIKE ? OR full_name LIKE ? OR email LIKE ?) ");
            String like = "%" + query.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        return searchInternal(extra.toString(), params, page, pageSize);
    }

    @Override
    public void updateStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Unable to update order status.", e);
        }
    }

    @Override
    public boolean hasPurchased(int userId, int productId) {
        String sql = "SELECT COUNT(*) FROM orders o JOIN order_items oi ON oi.order_id = o.id "
                + "WHERE o.user_id = ? AND oi.product_id = ? AND o.status <> 'CANCELLED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new AppException("Unable to verify purchase.", e);
        }
    }

    @Override
    public DashboardStats loadDashboardStats(int lowStockThreshold) {
        DashboardStats stats = new DashboardStats();
        try (Connection conn = DBConnection.getConnection()) {
            stats.setTotalProducts(scalar(conn, "SELECT COUNT(*) FROM products"));
            stats.setTotalCustomers(scalar(conn, "SELECT COUNT(*) FROM users WHERE role='CUSTOMER'"));
            stats.setTotalOrders(scalar(conn, "SELECT COUNT(*) FROM orders"));
            stats.setPendingOrders(scalar(conn, "SELECT COUNT(*) FROM orders WHERE status IN ('PLACED','CONFIRMED','PACKED')"));
            stats.setTotalRevenue(decimal(conn, "SELECT COALESCE(SUM(grand_total),0) FROM orders WHERE status <> 'CANCELLED'"));
            stats.setLowStockProducts(scalar(conn, "SELECT COUNT(*) FROM products WHERE status='ACTIVE' AND stock <= " + lowStockThreshold));
            List<String> labels = new ArrayList<>();
            List<Integer> values = new ArrayList<>();
            String chartSql = "SELECT status, COUNT(*) AS total FROM orders GROUP BY status";
            try (PreparedStatement ps = conn.prepareStatement(chartSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    labels.add(rs.getString("status"));
                    values.add(rs.getInt("total"));
                }
            }
            if (labels.isEmpty()) {
                labels.addAll(Arrays.asList("PLACED", "DELIVERED"));
                values.addAll(Arrays.asList(0, 0));
            }
            stats.setChartLabels(labels);
            stats.setChartValues(values);
        } catch (SQLException e) {
            throw new AppException("Unable to load dashboard stats.", e);
        }
        return stats;
    }

    private PageResult<Order> searchInternal(String extraWhere, List<Object> params, int page, int pageSize) {
        String countSql = "SELECT COUNT(*) FROM orders WHERE 1=1 " + extraWhere;
        String dataSql = "SELECT * FROM orders WHERE 1=1 " + extraWhere + " ORDER BY created_at DESC LIMIT ? OFFSET ?";
        try (Connection conn = DBConnection.getConnection()) {
            int total;
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                bind(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    total = rs.getInt(1);
                }
            }
            List<Order> orders = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(dataSql)) {
                int index = bind(ps, params);
                ps.setInt(index++, pageSize);
                ps.setInt(index, Math.max(0, (page - 1) * pageSize));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        orders.add(map(rs));
                    }
                }
            }
            return new PageResult<>(orders, page, pageSize, total);
        } catch (SQLException e) {
            throw new AppException("Unable to list orders.", e);
        }
    }

    private List<OrderItem> loadItems(Connection conn, int orderId) throws SQLException {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getInt("id"));
                    item.setOrderId(orderId);
                    item.setProductId(rs.getInt("product_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setDiscount(rs.getBigDecimal("discount"));
                    item.setLineTotal(rs.getBigDecimal("line_total"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    private int scalar(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private BigDecimal decimal(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getBigDecimal(1);
        }
    }

    private int bind(PreparedStatement ps, List<Object> params) throws SQLException {
        int index = 1;
        for (Object param : params) {
            ps.setObject(index++, param);
        }
        return index;
    }

    private Order map(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setOrderNumber(rs.getString("order_number"));
        order.setUserId(rs.getInt("user_id"));
        order.setFullName(rs.getString("full_name"));
        order.setEmail(rs.getString("email"));
        order.setPhone(rs.getString("phone"));
        order.setAddressLine(rs.getString("address_line"));
        order.setCity(rs.getString("city"));
        order.setState(rs.getString("state"));
        order.setPincode(rs.getString("pincode"));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setPaymentStatus(rs.getString("payment_status"));
        order.setStatus(rs.getString("status"));
        order.setSubtotal(rs.getBigDecimal("subtotal"));
        order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        order.setShipping(rs.getBigDecimal("shipping"));
        order.setGrandTotal(rs.getBigDecimal("grand_total"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        return order;
    }
}
