package com.shopease.service;

import com.shopease.model.Cart;
import com.shopease.model.User;

public interface CartService {
    void add(Cart cart, int productId, int quantity);

    void update(Cart cart, int productId, int quantity);

    void remove(Cart cart, int productId);

    void persist(User user, Cart cart);

    Cart mergeOnLogin(User user, Cart sessionCart);
}
