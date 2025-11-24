package com.example.pagination.dto;

import com.example.pagination.entity.User;

import java.util.List;

public class PaginationResponse {

    private int page;
    private int size;
    private int totalItems;
    private int totalPages;
    private List<User> data;

    public PaginationResponse(int page, int size, int totalItems, int totalPages, List<User> data) {
        this.page = page;
        this.size = size;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.data = data;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<User> getData() {
        return data;
    }
}
