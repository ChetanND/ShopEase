package com.shopease.service.impl;

import com.shopease.dao.CartDAO;
import com.shopease.dao.ProductDAO;
import com.shopease.dao.impl.CartDAOImpl;
import com.shopease.dao.impl.ProductDAOImpl;
import com.shopease.model.Cart;
import com.shopease.model.CartItem;
import com.shopease.model.Product;
import com.shopease.model.User;
import com.shopease.service.CartService;
import com.shopease.util.ValidationException;

public class CartServiceImpl implements CartService {
    private final ProductDAO productDAO = new ProductDAOImpl();
    private final CartDAO cartDAO = new CartDAOImpl();

    @Override
    public void add(Cart cart, int productId, int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be at least 1.");
        }
        Product product = requireProduct(productId);
        int existing = cart.getItems().stream()
                .filter(item -> item.getProductId() == productId)
                .mapToInt(CartItem::getQuantity)
                .findFirst()
                .orElse(0);
        int next = existing + quantity;
        if (next > product.getStock()) {
            throw new ValidationException("Only " + product.getStock() + " items are available in stock.");
        }
        CartItem item = toItem(product, quantity);
        cart.addOrUpdate(item);
        refreshStocks(cart);
    }

    @Override
    public void update(Cart cart, int productId, int quantity) {
        Product product = requireProduct(productId);
        if (quantity > product.getStock()) {
            throw new ValidationException("Only " + product.getStock() + " items are available in stock.");
        }
        cart.setQuantity(productId, quantity);
        refreshStocks(cart);
    }

    @Override
    public void remove(Cart cart, int productId) {
        cart.remove(productId);
    }

    @Override
    public void persist(User user, Cart cart) {
        if (user != null) {
            cartDAO.replaceCart(user.getId(), cart);
        }
    }

    @Override
    public Cart mergeOnLogin(User user, Cart sessionCart) {
        Cart stored = cartDAO.loadCart(user.getId());
        if (sessionCart != null) {
            for (CartItem item : sessionCart.getItems()) {
                stored.addOrUpdate(item);
            }
        }
        refreshStocks(stored);
        for (CartItem item : stored.getItems()) {
            if (item.getQuantity() > item.getStock()) {
                item.setQuantity(item.getStock());
            }
        }
        stored.getItems().removeIf(item -> item.getQuantity() <= 0 || item.getStock() <= 0);
        cartDAO.replaceCart(user.getId(), stored);
        return stored;
    }

    private Product requireProduct(int productId) {
        Product product = productDAO.findById(productId);
        if (product == null || !product.isActive()) {
            throw new ValidationException("Product not found.");
        }
        if (!product.isInStock()) {
            throw new ValidationException("This product is currently out of stock.");
        }
        return product;
    }

    private CartItem toItem(Product product, int quantity) {
        CartItem item = new CartItem();
        item.setProductId(product.getId());
        item.setName(product.getName());
        item.setImageUrl(product.getImageUrl());
        item.setUnitPrice(product.getPrice());
        item.setDiscount(product.getDiscount());
        item.setQuantity(quantity);
        item.setStock(product.getStock());
        return item;
    }

    private void refreshStocks(Cart cart) {
        for (CartItem item : cart.getItems()) {
            Product product = productDAO.findById(item.getProductId());
            if (product != null) {
                item.setStock(product.getStock());
                item.setUnitPrice(product.getPrice());
                item.setDiscount(product.getDiscount());
                item.setName(product.getName());
                item.setImageUrl(product.getImageUrl());
            }
        }
    }
}
