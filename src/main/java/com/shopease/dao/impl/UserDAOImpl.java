package com.shopease.dao.impl;

import com.shopease.dao.UserDAO;
import com.shopease.model.Address;
import com.shopease.model.PageResult;
import com.shopease.model.User;
import com.shopease.util.AppException;
import com.shopease.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public User findById(int id) {
        String sql = "SELECT u.*, a.id AS address_id, a.address_line, a.city, a.state, a.pincode, a.is_default "
                + "FROM users u LEFT JOIN addresses a ON a.user_id = u.id AND a.is_default = 1 WHERE u.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new AppException("Unable to load user.", e);
        }
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT u.*, a.id AS address_id, a.address_line, a.city, a.state, a.pincode, a.is_default "
                + "FROM users u LEFT JOIN addresses a ON a.user_id = u.id AND a.is_default = 1 WHERE u.email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new AppException("Unable to look up user.", e);
        }
    }

    @Override
    public int save(User user, Address address) {
        String userSql = "INSERT INTO users (full_name, email, phone, password_hash, role, status) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int userId;
                try (PreparedStatement ps = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, user.getFullName());
                    ps.setString(2, user.getEmail());
                    ps.setString(3, user.getPhone());
                    ps.setString(4, user.getPasswordHash());
                    ps.setString(5, user.getRole());
                    ps.setString(6, user.getStatus());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new AppException("User was not created.");
                        }
                        userId = keys.getInt(1);
                    }
                }
                insertAddress(conn, userId, address);
                conn.commit();
                return userId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new AppException("Unable to register user.", e);
        }
    }

    @Override
    public void updateProfile(User user, Address address) {
        String userSql = "UPDATE users SET full_name = ?, phone = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(userSql)) {
                    ps.setString(1, user.getFullName());
                    ps.setString(2, user.getPhone());
                    ps.setInt(3, user.getId());
                    ps.executeUpdate();
                }
                try (PreparedStatement check = conn.prepareStatement("SELECT id FROM addresses WHERE user_id = ? AND is_default = 1")) {
                    check.setInt(1, user.getId());
                    try (ResultSet rs = check.executeQuery()) {
                        if (rs.next()) {
                            try (PreparedStatement upd = conn.prepareStatement(
                                    "UPDATE addresses SET address_line = ?, city = ?, state = ?, pincode = ? WHERE id = ?")) {
                                upd.setString(1, address.getAddressLine());
                                upd.setString(2, address.getCity());
                                upd.setString(3, address.getState());
                                upd.setString(4, address.getPincode());
                                upd.setInt(5, rs.getInt("id"));
                                upd.executeUpdate();
                            }
                        } else {
                            insertAddress(conn, user.getId(), address);
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new AppException("Unable to update profile.", e);
        }
    }

    @Override
    public void updatePassword(int userId, String passwordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Unable to change password.", e);
        }
    }

    @Override
    public void updateStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE id = ? AND role = 'CUSTOMER'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Unable to update user status.", e);
        }
    }

    @Override
    public PageResult<User> searchCustomers(String query, int page, int pageSize) {
        StringBuilder where = new StringBuilder(" WHERE role = 'CUSTOMER' ");
        List<Object> params = new ArrayList<>();
        if (query != null && !query.isBlank()) {
            where.append(" AND (full_name LIKE ? OR email LIKE ? OR phone LIKE ?) ");
            String like = "%" + query.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        String countSql = "SELECT COUNT(*) FROM users" + where;
        String dataSql = "SELECT u.*, a.id AS address_id, a.address_line, a.city, a.state, a.pincode, a.is_default "
                + "FROM users u LEFT JOIN addresses a ON a.user_id = u.id AND a.is_default = 1"
                + where + " ORDER BY u.created_at DESC LIMIT ? OFFSET ?";
        try (Connection conn = DBConnection.getConnection()) {
            int total;
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                bind(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    total = rs.getInt(1);
                }
            }
            List<User> users = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(dataSql)) {
                int index = bind(ps, params);
                ps.setInt(index++, pageSize);
                ps.setInt(index, (page - 1) * pageSize);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        users.add(map(rs));
                    }
                }
            }
            return new PageResult<>(users, page, pageSize, total);
        } catch (SQLException e) {
            throw new AppException("Unable to list customers.", e);
        }
    }

    @Override
    public int countByRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new AppException("Unable to count users.", e);
        }
    }

    private void insertAddress(Connection conn, int userId, Address address) throws SQLException {
        String sql = "INSERT INTO addresses (user_id, address_line, city, state, pincode, is_default) VALUES (?,?,?,?,?,1)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, address.getAddressLine());
            ps.setString(3, address.getCity());
            ps.setString(4, address.getState());
            ps.setString(5, address.getPincode());
            ps.executeUpdate();
        }
    }

    private int bind(PreparedStatement ps, List<Object> params) throws SQLException {
        int index = 1;
        for (Object param : params) {
            ps.setObject(index++, param);
        }
        return index;
    }

    private User map(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getString("status"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        int addressId = rs.getInt("address_id");
        if (!rs.wasNull()) {
            Address address = new Address();
            address.setId(addressId);
            address.setUserId(user.getId());
            address.setAddressLine(rs.getString("address_line"));
            address.setCity(rs.getString("city"));
            address.setState(rs.getString("state"));
            address.setPincode(rs.getString("pincode"));
            address.setDefaultAddress(rs.getInt("is_default") == 1);
            user.setAddress(address);
        }
        return user;
    }
}
