package com.shopease.dao.impl;

import com.shopease.dao.CategoryDAO;
import com.shopease.model.Category;
import com.shopease.util.AppException;
import com.shopease.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAOImpl implements CategoryDAO {

    @Override
    public List<Category> findAll(boolean activeOnly) {
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM products p WHERE p.category_id = c.id AND p.status = 'ACTIVE') AS product_count "
                + "FROM categories c " + (activeOnly ? "WHERE c.status = 'ACTIVE' " : "") + "ORDER BY c.name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Category> list = new ArrayList<>();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new AppException("Unable to load categories.", e);
        }
    }

    @Override
    public Category findById(int id) {
        String sql = "SELECT c.*, 0 AS product_count FROM categories c WHERE c.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new AppException("Unable to load category.", e);
        }
    }

    @Override
    public Category findByName(String name) {
        String sql = "SELECT c.*, 0 AS product_count FROM categories c WHERE c.name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new AppException("Unable to look up category.", e);
        }
    }

    @Override
    public void save(Category category) {
        String sql = "INSERT INTO categories (name, description, image_url, status) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setString(3, category.getImageUrl());
            ps.setString(4, category.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Unable to create category.", e);
        }
    }

    @Override
    public void update(Category category) {
        String sql = "UPDATE categories SET name = ?, description = ?, image_url = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setString(3, category.getImageUrl());
            ps.setString(4, category.getStatus());
            ps.setInt(5, category.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Unable to update category.", e);
        }
    }

    @Override
    public void updateStatus(int id, String status) {
        String sql = "UPDATE categories SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Unable to update category status.", e);
        }
    }

    @Override
    public boolean hasActiveProducts(int categoryId) {
        String sql = "SELECT COUNT(*) FROM products WHERE category_id = ? AND status = 'ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new AppException("Unable to check category products.", e);
        }
    }

    private Category map(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        category.setImageUrl(rs.getString("image_url"));
        category.setStatus(rs.getString("status"));
        category.setProductCount(rs.getInt("product_count"));
        return category;
    }
}
