package com.shopease.dao.impl;

import com.shopease.dao.ContactDAO;
import com.shopease.util.AppException;
import com.shopease.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ContactDAOImpl implements ContactDAO {
    @Override
    public void save(String name, String email, String subject, String message) {
        String sql = "INSERT INTO contact_messages (name, email, subject, message) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, subject);
            ps.setString(4, message);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Unable to send message right now. Please try again later.", e);
        }
    }
}
