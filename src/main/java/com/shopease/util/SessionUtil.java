package com.shopease.util;

import com.shopease.model.Cart;
import com.shopease.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class SessionUtil {
    private SessionUtil() {
    }

    public static HttpSession session(HttpServletRequest request, boolean create) {
        return request.getSession(create);
    }

    public static User getUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(AppConstants.SESSION_USER);
        return value instanceof User ? (User) value : null;
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return getUser(request) != null;
    }

    public static boolean isAdmin(HttpServletRequest request) {
        User user = getUser(request);
        return user != null && AppConstants.ROLE_ADMIN.equals(user.getRole());
    }

    public static Cart getCart(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        Object value = session.getAttribute(AppConstants.SESSION_CART);
        if (value instanceof Cart) {
            return (Cart) value;
        }
        Cart cart = new Cart();
        session.setAttribute(AppConstants.SESSION_CART, cart);
        return cart;
    }

    public static void setCart(HttpServletRequest request, Cart cart) {
        request.getSession(true).setAttribute(AppConstants.SESSION_CART, cart);
    }

    public static void setUser(HttpServletRequest request, User user) {
        request.getSession(true).setAttribute(AppConstants.SESSION_USER, user);
    }

    public static void flashSuccess(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute(AppConstants.FLASH_SUCCESS, message);
    }

    public static void flashError(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute(AppConstants.FLASH_ERROR, message);
    }

    public static void moveFlashToRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        Object success = session.getAttribute(AppConstants.FLASH_SUCCESS);
        Object error = session.getAttribute(AppConstants.FLASH_ERROR);
        if (success != null) {
            request.setAttribute("successMessage", success);
            session.removeAttribute(AppConstants.FLASH_SUCCESS);
        }
        if (error != null) {
            request.setAttribute("errorMessage", error);
            session.removeAttribute(AppConstants.FLASH_ERROR);
        }
    }

    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
