package com.shopease.dao.impl;

import com.shopease.dao.ProductDAO;
import com.shopease.model.PageResult;
import com.shopease.model.Product;
import com.shopease.util.AppException;
import com.shopease.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {
    private static final String BASE_SELECT =
            "SELECT p.*, c.name AS category_name, (SELECT COUNT(*) FROM reviews r WHERE r.product_id = p.id) AS review_count "
                    + "FROM products p JOIN categories c ON c.id = p.category_id ";

    @Override
    public Product findById(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            return findById(conn, id);
        } catch (SQLException e) {
            throw new AppException("Unable to load product.", e);
        }
    }

    @Override
    public Product findById(Connection connection, int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE p.id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public PageResult<Product> search(String query, Integer categoryId, Double minPrice, Double maxPrice,
                                      String sort, String status, int page, int pageSize) {
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (status != null) {
            where.append(" AND p.status = ? ");
            params.add(status);
        }
        if (query != null && !query.isBlank()) {
            where.append(" AND (p.name LIKE ? OR p.description LIKE ? OR c.name LIKE ?) ");
            String like = "%" + query.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (categoryId != null) {
            where.append(" AND p.category_id = ? ");
            params.add(categoryId);
        }
        if (minPrice != null) {
            where.append(" AND (p.price * (1 - p.discount/100)) >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            where.append(" AND (p.price * (1 - p.discount/100)) <= ? ");
            params.add(maxPrice);
        }
        String orderBy = resolveSort(sort);
        String countSql = "SELECT COUNT(*) FROM products p JOIN categories c ON c.id = p.category_id " + where;
        String dataSql = BASE_SELECT + where + orderBy + " LIMIT ? OFFSET ?";
        try (Connection conn = DBConnection.getConnection()) {
            int total;
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                bind(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    total = rs.getInt(1);
                }
            }
            List<Product> products = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(dataSql)) {
                int index = bind(ps, params);
                ps.setInt(index++, pageSize);
                ps.setInt(index, Math.max(0, (page - 1) * pageSize));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        products.add(map(rs));
                    }
                }
            }
            return new PageResult<>(products, page, pageSize, total);
        } catch (SQLException e) {
            throw new AppException("Unable to search products.", e);
        }
    }

    @Override
    public List<Product> findFeatured(int limit) {
        return listLimited(BASE_SELECT + " WHERE p.status = 'ACTIVE' ORDER BY p.rating DESC, p.id DESC LIMIT ?", limit);
    }

    @Override
    public List<Product> findNewest(int limit) {
        return listLimited(BASE_SELECT + " WHERE p.status = 'ACTIVE' ORDER BY p.created_at DESC LIMIT ?", limit);
    }

    @Override
    public List<Product> findLowStock(int threshold) {
        return listLimited(BASE_SELECT + " WHERE p.status = 'ACTIVE' AND p.stock <= ? ORDER BY p.stock ASC LIMIT 20", threshold);
    }

    @Override
    public int save(Product product) {
        String sql = "INSERT INTO products (name, description, category_id, price, discount, stock, image_url, rating, status) "
                + "VALUES (?,?,?,?,?,?,?,0,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindProduct(ps, product);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new AppException("Unable to create product.", e);
        }
    }

    @Override
    public void update(Product product) {
        String sql = "UPDATE products SET name=?, description=?, category_id=?, price=?, discount=?, stock=?, image_url=?, status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindProduct(ps, product);
            ps.setInt(9, product.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Unable to update product.", e);
        }
    }

    @Override
    public void updateStatus(int id, String status) {
        String sql = "UPDATE products SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Unable to update product status.", e);
        }
    }

    @Override
    public void reduceStock(Connection connection, int productId, int quantity) throws SQLException {
        String sql = "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Insufficient stock for product " + productId);
            }
        }
    }

    @Override
    public void restoreStock(Connection connection, int productId, int quantity) throws SQLException {
        String sql = "UPDATE products SET stock = stock + ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    @Override
    public void updateAverageRating(int productId) {
        String sql = "UPDATE products SET rating = (SELECT COALESCE(AVG(rating),0) FROM reviews WHERE product_id = ?) WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Unable to refresh product rating.", e);
        }
    }

    @Override
    public int countAll() {
        return scalar("SELECT COUNT(*) FROM products");
    }

    @Override
    public int countLowStock(int threshold) {
        String sql = "SELECT COUNT(*) FROM products WHERE status = 'ACTIVE' AND stock <= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new AppException("Unable to count low-stock products.", e);
        }
    }

    private List<Product> listLimited(String sql, int value) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                List<Product> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(map(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new AppException("Unable to load products.", e);
        }
    }

    private int scalar(String sql) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new AppException("Unable to count products.", e);
        }
    }

    private String resolveSort(String sort) {
        if ("price_asc".equals(sort)) {
            return " ORDER BY (p.price * (1 - p.discount/100)) ASC ";
        }
        if ("price_desc".equals(sort)) {
            return " ORDER BY (p.price * (1 - p.discount/100)) DESC ";
        }
        if ("newest".equals(sort)) {
            return " ORDER BY p.created_at DESC ";
        }
        return " ORDER BY p.rating DESC, p.id DESC ";
    }

    private void bindProduct(PreparedStatement ps, Product product) throws SQLException {
        ps.setString(1, product.getName());
        ps.setString(2, product.getDescription());
        ps.setInt(3, product.getCategoryId());
        ps.setBigDecimal(4, product.getPrice());
        ps.setBigDecimal(5, product.getDiscount());
        ps.setInt(6, product.getStock());
        ps.setString(7, product.getImageUrl());
        ps.setString(8, product.getStatus());
    }

    private int bind(PreparedStatement ps, List<Object> params) throws SQLException {
        int index = 1;
        for (Object param : params) {
            ps.setObject(index++, param);
        }
        return index;
    }

    private Product map(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setCategoryName(rs.getString("category_name"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setDiscount(rs.getBigDecimal("discount"));
        product.setStock(rs.getInt("stock"));
        product.setImageUrl(rs.getString("image_url"));
        product.setRating(rs.getBigDecimal("rating"));
        product.setStatus(rs.getString("status"));
        product.setCreatedAt(rs.getTimestamp("created_at"));
        product.setReviewCount(rs.getInt("review_count"));
        return product;
    }
}
