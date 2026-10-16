package com.shopease.service.impl;

import com.shopease.dao.CartDAO;
import com.shopease.dao.OrderDAO;
import com.shopease.dao.ProductDAO;
import com.shopease.dao.impl.CartDAOImpl;
import com.shopease.dao.impl.OrderDAOImpl;
import com.shopease.dao.impl.ProductDAOImpl;
import com.shopease.model.Cart;
import com.shopease.model.CartItem;
import com.shopease.model.DashboardStats;
import com.shopease.model.Order;
import com.shopease.model.OrderItem;
import com.shopease.model.PageResult;
import com.shopease.model.Product;
import com.shopease.model.User;
import com.shopease.service.OrderService;
import com.shopease.util.AppConstants;
import com.shopease.util.AppException;
import com.shopease.util.DBConnection;
import com.shopease.util.ValidationException;
import com.shopease.util.ValidationUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {
    private final OrderDAO orderDAO = new OrderDAOImpl();
    private final ProductDAO productDAO = new ProductDAOImpl();
    private final CartDAO cartDAO = new CartDAOImpl();

    @Override
    public Order placeOrder(User user, Cart cart, String paymentMethod) {
        if (cart == null || cart.isEmpty()) {
            throw new ValidationException("Your cart is empty.");
        }
        if (user.getAddress() == null) {
            throw new ValidationException("Please save an address in your profile before checkout.");
        }
        if (!"COD".equals(paymentMethod) && !"ONLINE".equals(paymentMethod)) {
            throw new ValidationException("Select a valid payment method.");
        }
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(user.getId());
        order.setFullName(user.getFullName());
        order.setEmail(user.getEmail());
        order.setPhone(user.getPhone());
        order.setAddressLine(user.getAddress().getAddressLine());
        order.setCity(user.getAddress().getCity());
        order.setState(user.getAddress().getState());
        order.setPincode(user.getAddress().getPincode());
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("ONLINE".equals(paymentMethod) ? "PAID" : "PENDING");
        order.setStatus("PLACED");
        order.setSubtotal(cart.getSubtotal());
        order.setDiscountAmount(cart.getDiscount());
        order.setShipping(cart.getShipping());
        order.setGrandTotal(cart.getGrandTotal());

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int orderId = orderDAO.insertOrder(conn, order);
                for (CartItem cartItem : cart.getItems()) {
                    Product product = productDAO.findById(conn, cartItem.getProductId());
                    if (product == null || !product.isActive()) {
                        throw new ValidationException("A product in your cart is no longer available.");
                    }
                    if (cartItem.getQuantity() > product.getStock()) {
                        throw new ValidationException(product.getName() + " does not have enough stock.");
                    }
                    productDAO.reduceStock(conn, product.getId(), cartItem.getQuantity());
                    OrderItem item = new OrderItem();
                    item.setProductId(product.getId());
                    item.setProductName(product.getName());
                    item.setUnitPrice(product.getPrice());
                    item.setQuantity(cartItem.getQuantity());
                    item.setDiscount(product.getDiscount());
                    item.setLineTotal(cartItem.getLineTotal());
                    orderDAO.insertItem(conn, orderId, item);
                }
                conn.commit();
                order.setId(orderId);
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                if (e instanceof ValidationException) {
                    throw (ValidationException) e;
                }
                throw new AppException("Could not place the order. Please try again.", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new AppException("Database connection error while placing the order.", e);
        }
        cart.clear();
        cartDAO.replaceCart(user.getId(), cart);
        return orderDAO.findById(order.getId());
    }

    @Override
    public PageResult<Order> myOrders(int userId, int page) {
        return orderDAO.findByUser(userId, Math.max(page, 1), AppConstants.PAGE_SIZE);
    }

    @Override
    public Order getForUser(String orderNumber, int userId) {
        Order order = orderDAO.findByNumberForUser(orderNumber, userId);
        if (order == null) {
            throw new AppException("Order not found.");
        }
        return order;
    }

    @Override
    public Order getById(int id) {
        Order order = orderDAO.findById(id);
        if (order == null) {
            throw new AppException("Order not found.");
        }
        return order;
    }

    @Override
    public void cancelForUser(String orderNumber, int userId) {
        Order order = getForUser(orderNumber, userId);
        if (!order.isCancellable()) {
            throw new ValidationException("This order can no longer be cancelled.");
        }
        cancelInternal(order);
    }

    @Override
    public PageResult<Order> adminSearch(String query, String status, int page) {
        return orderDAO.search(query, status, Math.max(page, 1), AppConstants.ADMIN_PAGE_SIZE);
    }

    @Override
    public void updateStatus(int orderId, String status) {
        Order order = getById(orderId);
        if (Arrays.stream(AppConstants.ORDER_STATUSES).noneMatch(s -> s.equals(status))) {
            throw new ValidationException("Invalid order status.");
        }
        if ("CANCELLED".equals(status) && !"CANCELLED".equals(order.getStatus())) {
            if (!order.isCancellable() && !"PACKED".equals(order.getStatus())) {
                throw new ValidationException("This order cannot be cancelled at the current stage.");
            }
            cancelInternal(order);
            return;
        }
        orderDAO.updateStatus(orderId, status);
    }

    @Override
    public DashboardStats dashboard() {
        DashboardStats stats = orderDAO.loadDashboardStats(AppConstants.LOW_STOCK_THRESHOLD);
        stats.setLowStockList(productDAO.findLowStock(AppConstants.LOW_STOCK_THRESHOLD));
        return stats;
    }

    private void cancelInternal(Order order) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (OrderItem item : order.getItems()) {
                    productDAO.restoreStock(conn, item.getProductId(), item.getQuantity());
                }
                try (var ps = conn.prepareStatement("UPDATE orders SET status = 'CANCELLED' WHERE id = ?")) {
                    ps.setInt(1, order.getId());
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new AppException("Unable to cancel the order.", e);
        }
    }

    private String generateOrderNumber() {
        return "SE" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
