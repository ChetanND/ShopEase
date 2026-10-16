package com.shopease.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

public class Product {
    private int id;
    private String name;
    private String description;
    private int categoryId;
    private String categoryName;
    private BigDecimal price;
    private BigDecimal discount;
    private int stock;
    private String imageUrl;
    private BigDecimal rating;
    private String status;
    private Timestamp createdAt;
    private int reviewCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public boolean isInStock() {
        return stock > 0;
    }

    public boolean hasDiscount() {
        return discount != null && discount.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getSalePrice() {
        if (price == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal pct = discount == null ? BigDecimal.ZERO : discount;
        BigDecimal factor = BigDecimal.ONE.subtract(pct.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        return price.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
    public boolean isHasDiscount() {
        return discount != null && discount.compareTo(BigDecimal.ZERO) > 0;
    }
}
