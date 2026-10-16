package com.shopease.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private int productId;
    private String name;
    private String imageUrl;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private int quantity;
    private int stock;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public BigDecimal getSalePrice() {
        BigDecimal pct = discount == null ? BigDecimal.ZERO : discount;
        BigDecimal factor = BigDecimal.ONE.subtract(pct.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        return unitPrice.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getOriginalLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getLineTotal() {
        return getSalePrice().multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }
}
