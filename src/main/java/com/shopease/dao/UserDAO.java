package com.shopease.dao;

import com.shopease.model.Address;
import com.shopease.model.PageResult;
import com.shopease.model.User;

public interface UserDAO {
    User findById(int id);

    User findByEmail(String email);

    int save(User user, Address address);

    void updateProfile(User user, Address address);

    void updatePassword(int userId, String passwordHash);

    void updateStatus(int userId, String status);

    PageResult<User> searchCustomers(String query, int page, int pageSize);

    int countByRole(String role);
}
