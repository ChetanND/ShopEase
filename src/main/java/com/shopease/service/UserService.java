package com.shopease.service;

import com.shopease.model.Address;
import com.shopease.model.PageResult;
import com.shopease.model.User;

public interface UserService {
    User register(User user, Address address, String password, String confirmPassword);

    User login(String email, String password);

    User getById(int id);

    void updateProfile(User user, Address address);

    void changePassword(int userId, String currentPassword, String newPassword, String confirmPassword);

    PageResult<User> listCustomers(String query, int page);

    void setStatus(int userId, String status);
}
