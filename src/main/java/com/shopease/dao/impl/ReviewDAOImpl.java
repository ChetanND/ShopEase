package com.shopease.dao.impl;

import com.shopease.dao.ReviewDAO;
import com.shopease.model.PageResult;
import com.shopease.model.Review;
import com.shopease.util.AppException;
import com.shopease.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAOImpl implements ReviewDAO {

    @Override
    public void save(Review review) {
        String sql = "INSERT INTO reviews (user_id, product_id, rating, comment) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, review.getUserId());
            ps.setInt(2, review.getProductId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Unable to save review.", e);
        }
    }

    @Override
    public List<Review> findByProduct(int productId) {
        String sql = "SELECT r.*, u.full_name AS user_name FROM reviews r JOIN users u ON u.id = r.user_id "
                + "WHERE r.product_id = ? ORDER BY r.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Review> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(map(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new AppException("Unable to load reviews.", e);
        }
    }

    @Override
    public PageResult<Review> findAll(int page, int pageSize) {
        String countSql = "SELECT COUNT(*) FROM reviews";
        String dataSql = "SELECT r.*, u.full_name AS user_name FROM reviews r JOIN users u ON u.id = r.user_id "
                + "ORDER BY r.created_at DESC LIMIT ? OFFSET ?";
        try (Connection conn = DBConnection.getConnection()) {
            int total;
            try (PreparedStatement ps = conn.prepareStatement(countSql);
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                total = rs.getInt(1);
            }
            List<Review> list = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(dataSql)) {
                ps.setInt(1, pageSize);
                ps.setInt(2, Math.max(0, (page - 1) * pageSize));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(map(rs));
                    }
                }
            }
            return new PageResult<>(list, page, pageSize, total);
        } catch (SQLException e) {
            throw new AppException("Unable to list reviews.", e);
        }
    }

    @Override
    public boolean exists(int userId, int productId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new AppException("Unable to check review.", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Unable to delete review.", e);
        }
    }

    private Review map(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setId(rs.getInt("id"));
        review.setUserId(rs.getInt("user_id"));
        review.setProductId(rs.getInt("product_id"));
        review.setUserName(rs.getString("user_name"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        review.setCreatedAt(rs.getTimestamp("created_at"));
        return review;
    }
}
