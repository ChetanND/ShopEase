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

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        view(request, response, "customer/register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = new User();
        user.setFullName(param(request, "fullName"));
        user.setEmail(param(request, "email"));
        user.setPhone(param(request, "phone"));
        Address address = new Address();
        address.setAddressLine(param(request, "address"));
        address.setCity(param(request, "city"));
        address.setState(param(request, "state"));
        address.setPincode(param(request, "pincode"));
        try {
            User created = userService.register(user, address, param(request, "password"), param(request, "confirmPassword"));
            SessionUtil.setUser(request, created);
            SessionUtil.flashSuccess(request, "Your ShopEase account is ready.");
            redirect(request, response, "/home");
        } catch (Exception e) {
            handleError(request, e);
            request.setAttribute("formUser", user);
            request.setAttribute("formAddress", address);
            view(request, response, "customer/register.jsp");
        }
    }
}
