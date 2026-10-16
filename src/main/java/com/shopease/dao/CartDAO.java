package com.shopease.dao;

import com.shopease.model.Cart;
import com.shopease.model.CartItem;

public interface CartDAO {
    void replaceCart(int userId, Cart cart);

    Cart loadCart(int userId);
}
