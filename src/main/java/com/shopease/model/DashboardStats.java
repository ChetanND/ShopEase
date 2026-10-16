package com.shopease.model;

import java.math.BigDecimal;
import java.util.List;

public class DashboardStats {
    private int totalProducts;
    private int totalCustomers;
    private int totalOrders;
    private BigDecimal totalRevenue;
    private int pendingOrders;
    private int lowStockProducts;
    private List<Product> lowStockList;
    private List<String> chartLabels;
    private List<Integer> chartValues;

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(int totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(int pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public int getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(int lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public List<Product> getLowStockList() {
        return lowStockList;
    }

    public void setLowStockList(List<Product> lowStockList) {
        this.lowStockList = lowStockList;
    }

    public List<String> getChartLabels() {
        return chartLabels;
    }

    public void setChartLabels(List<String> chartLabels) {
        this.chartLabels = chartLabels;
    }

    public List<Integer> getChartValues() {
        return chartValues;
    }

    public void setChartValues(List<Integer> chartValues) {
        this.chartValues = chartValues;
    }
}
