package com.shopease.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() {
        return items;
    }

    public void addOrUpdate(CartItem item) {
        for (CartItem existing : items) {
            if (existing.getProductId() == item.getProductId()) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                return;
            }
        }
        items.add(item);
    }

    public void setQuantity(int productId, int quantity) {
        Iterator<CartItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getProductId() == productId) {
                if (quantity <= 0) {
                    iterator.remove();
                } else {
                    item.setQuantity(quantity);
                }
                return;
            }
        }
    }

    public void remove(int productId) {
        items.removeIf(item -> item.getProductId() == productId);
    }

    public void clear() {
        items.clear();
    }

    public int getItemCount() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public BigDecimal getSubtotal() {
        return sum(items.stream().map(CartItem::getOriginalLineTotal).toArray(BigDecimal[]::new));
    }

    public BigDecimal getDiscount() {
        return getSubtotal().subtract(getPayableBeforeShipping()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getPayableBeforeShipping() {
        return sum(items.stream().map(CartItem::getLineTotal).toArray(BigDecimal[]::new));
    }

    public BigDecimal getShipping() {
        if (isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal payable = getPayableBeforeShipping();
        if (payable.compareTo(BigDecimal.valueOf(com.shopease.util.AppConstants.FREE_SHIPPING_THRESHOLD)) >= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(com.shopease.util.AppConstants.SHIPPING_FEE).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getGrandTotal() {
        return getPayableBeforeShipping().add(getShipping()).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal sum(BigDecimal[] values) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            total = total.add(value);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
