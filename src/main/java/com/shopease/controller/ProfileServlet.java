package com.shopease.controller;

import com.shopease.model.Address;
import com.shopease.model.User;
import com.shopease.service.UserService;
import com.shopease.service.impl.UserServiceImpl;
import com.shopease.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends BaseServlet {
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User fresh = userService.getById(SessionUtil.getUser(request).getId());
        SessionUtil.setUser(request, fresh);
        request.setAttribute("user", fresh);
        view(request, response, "customer/profile.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User sessionUser = SessionUtil.getUser(request);
        String action = param(request, "action");
        try {
            if ("password".equals(action)) {
                userService.changePassword(sessionUser.getId(), param(request, "currentPassword"),
                        param(request, "newPassword"), param(request, "confirmPassword"));
                SessionUtil.flashSuccess(request, "Password updated.");
            } else {
                User user = userService.getById(sessionUser.getId());
                user.setFullName(param(request, "fullName"));
                user.setPhone(param(request, "phone"));
                Address address = user.getAddress() == null ? new Address() : user.getAddress();
                address.setAddressLine(param(request, "address"));
                address.setCity(param(request, "city"));
                address.setState(param(request, "state"));
                address.setPincode(param(request, "pincode"));
                userService.updateProfile(user, address);
                SessionUtil.setUser(request, userService.getById(user.getId()));
                SessionUtil.flashSuccess(request, "Profile updated.");
            }
            redirect(request, response, "/profile");
        } catch (Exception e) {
            handleError(request, e);
            request.setAttribute("user", userService.getById(sessionUser.getId()));
            view(request, response, "customer/profile.jsp");
        }
    }
}
