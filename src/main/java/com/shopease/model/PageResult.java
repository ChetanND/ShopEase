package com.shopease.model;

import java.util.Collections;
import java.util.List;

public class PageResult<T> {
    private final List<T> items;
    private final int page;
    private final int pageSize;
    private final int totalItems;

    public PageResult(List<T> items, int page, int pageSize, int totalItems) {
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
    }

    public List<T> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getTotalPages() {
        if (pageSize <= 0) {
            return 1;
        }
        return Math.max(1, (int) Math.ceil(totalItems / (double) pageSize));
    }
}
