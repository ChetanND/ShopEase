package com.shopease.util;

public final class AppConstants {
    public static final String SESSION_USER = "authUser";
    public static final String SESSION_CART = "shoppingCart";
    public static final String FLASH_SUCCESS = "flashSuccess";
    public static final String FLASH_ERROR = "flashError";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final int PAGE_SIZE = 8;
    public static final int ADMIN_PAGE_SIZE = 10;
    public static final double FREE_SHIPPING_THRESHOLD = 999.00;
    public static final double SHIPPING_FEE = 49.00;
    public static final int LOW_STOCK_THRESHOLD = 8;

    public static final String[] ORDER_STATUSES = {
            "PLACED", "CONFIRMED", "PACKED", "SHIPPED", "DELIVERED", "CANCELLED"
    };

    private AppConstants() {
    }
}
