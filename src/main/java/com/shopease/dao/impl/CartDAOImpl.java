package com.shopease.dao.impl;

import com.shopease.dao.CartDAO;
import com.shopease.model.Cart;
import com.shopease.model.CartItem;
import com.shopease.util.AppException;
import com.shopease.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CartDAOImpl implements CartDAO {

    @Override
    public void replaceCart(int userId, Cart cart) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int cartId = ensureCart(conn, userId);
                try (PreparedStatement del = conn.prepareStatement("DELETE FROM cart_items WHERE cart_id = ?")) {
                    del.setInt(1, cartId);
                    del.executeUpdate();
                }
                String insert = "INSERT INTO cart_items (cart_id, product_id, quantity) VALUES (?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(insert)) {
                    for (CartItem item : cart.getItems()) {
                        ps.setInt(1, cartId);
                        ps.setInt(2, item.getProductId());
                        ps.setInt(3, item.getQuantity());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new AppException("Unable to save cart.", e);
        }
    }

    @Override
    public Cart loadCart(int userId) {
        String sql = "SELECT ci.product_id, ci.quantity, p.name, p.image_url, p.price, p.discount, p.stock "
                + "FROM cart c JOIN cart_items ci ON ci.cart_id = c.id "
                + "JOIN products p ON p.id = ci.product_id WHERE c.user_id = ?";
        Cart cart = new Cart();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setName(rs.getString("name"));
                    item.setImageUrl(rs.getString("image_url"));
                    item.setUnitPrice(rs.getBigDecimal("price"));
                    item.setDiscount(rs.getBigDecimal("discount"));
                    item.setStock(rs.getInt("stock"));
                    cart.getItems().add(item);
                }
            }
            return cart;
        } catch (SQLException e) {
            throw new AppException("Unable to load cart.", e);
        }
    }

    private int ensureCart(Connection conn, int userId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM cart WHERE user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO cart (user_id) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }
}
