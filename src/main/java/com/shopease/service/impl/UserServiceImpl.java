package com.shopease.service.impl;

import com.shopease.dao.UserDAO;
import com.shopease.dao.impl.UserDAOImpl;
import com.shopease.model.Address;
import com.shopease.model.PageResult;
import com.shopease.model.User;
import com.shopease.service.UserService;
import com.shopease.util.AppConstants;
import com.shopease.util.AppException;
import com.shopease.util.PasswordUtil;
import com.shopease.util.ValidationException;
import com.shopease.util.ValidationUtil;

public class UserServiceImpl implements UserService {
    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    public User register(User user, Address address, String password, String confirmPassword) {
        validateRegistration(user, address, password, confirmPassword);
        if (userDAO.findByEmail(user.getEmail()) != null) {
            throw new ValidationException("An account with this email already exists.");
        }
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setRole(AppConstants.ROLE_CUSTOMER);
        user.setStatus(AppConstants.STATUS_ACTIVE);
        int id = userDAO.save(user, address);
        return userDAO.findById(id);
    }

    @Override
    public User login(String email, String password) {
        if (ValidationUtil.isBlank(email) || ValidationUtil.isBlank(password)) {
            throw new ValidationException("Email and password are required.");
        }
        User user = userDAO.findByEmail(email.trim().toLowerCase());
        if (user == null || !PasswordUtil.matches(password, user.getPasswordHash())) {
            throw new ValidationException("Invalid email or password.");
        }
        if (!user.isActive()) {
            throw new ValidationException("This account is inactive. Contact support.");
        }
        return user;
    }

    @Override
    public User getById(int id) {
        User user = userDAO.findById(id);
        if (user == null) {
            throw new AppException("User not found.");
        }
        return user;
    }

    @Override
    public void updateProfile(User user, Address address) {
        if (ValidationUtil.isBlank(user.getFullName()) || !ValidationUtil.isPhone(user.getPhone())) {
            throw new ValidationException("Please provide a valid name and 10-digit phone number.");
        }
        validateAddress(address);
        userDAO.updateProfile(user, address);
    }

    @Override
    public void changePassword(int userId, String currentPassword, String newPassword, String confirmPassword) {
        User user = getById(userId);
        if (!PasswordUtil.matches(currentPassword, user.getPasswordHash())) {
            throw new ValidationException("Current password is incorrect.");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new ValidationException("New password must be at least 6 characters.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("New password and confirmation do not match.");
        }
        userDAO.updatePassword(userId, PasswordUtil.hash(newPassword));
    }

    @Override
    public PageResult<User> listCustomers(String query, int page) {
        return userDAO.searchCustomers(query, Math.max(page, 1), AppConstants.ADMIN_PAGE_SIZE);
    }

    @Override
    public void setStatus(int userId, String status) {
        User user = getById(userId);
        if (user.isAdmin()) {
            throw new ValidationException("Admin accounts cannot be deactivated here.");
        }
        if (!AppConstants.STATUS_ACTIVE.equals(status) && !AppConstants.STATUS_INACTIVE.equals(status)) {
            throw new ValidationException("Invalid status.");
        }
        userDAO.updateStatus(userId, status);
    }

    private void validateRegistration(User user, Address address, String password, String confirmPassword) {
        if (ValidationUtil.isBlank(user.getFullName())) {
            throw new ValidationException("Full name is required.");
        }
        if (!ValidationUtil.isEmail(user.getEmail())) {
            throw new ValidationException("Enter a valid email address.");
        }
        user.setEmail(user.getEmail().trim().toLowerCase());
        if (!ValidationUtil.isPhone(user.getPhone())) {
            throw new ValidationException("Enter a valid 10-digit Indian mobile number.");
        }
        if (password == null || password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters.");
        }
        if (!password.equals(confirmPassword)) {
            throw new ValidationException("Password and confirmation do not match.");
        }
        validateAddress(address);
    }

    private void validateAddress(Address address) {
        if (ValidationUtil.isBlank(address.getAddressLine())
                || ValidationUtil.isBlank(address.getCity())
                || ValidationUtil.isBlank(address.getState())
                || !ValidationUtil.isPincode(address.getPincode())) {
            throw new ValidationException("Please provide a complete address with a 6-digit pincode.");
        }
    }
}
