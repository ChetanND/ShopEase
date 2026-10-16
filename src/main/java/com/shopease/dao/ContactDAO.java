package com.shopease.dao;

public interface ContactDAO {
    void save(String name, String email, String subject, String message);
}
