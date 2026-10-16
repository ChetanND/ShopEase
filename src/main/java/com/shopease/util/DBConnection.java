package com.shopease.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                PROPERTIES.load(in);
            }
            Class.forName(getDriver());
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Unable to initialize database configuration: " + e.getMessage());
        }
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl(), getUser(), getPassword());
    }

    public static String getDriver() {
        return PROPERTIES.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }

    public static String getUrl() {
        String env = System.getenv("DB_URL");
        return env != null && !env.isBlank() ? env : PROPERTIES.getProperty("db.url");
    }

    public static String getUser() {
        String env = System.getenv("DB_USER");
        return env != null && !env.isBlank() ? env : PROPERTIES.getProperty("db.user");
    }

    public static String getPassword() {
        String env = System.getenv("DB_PASSWORD");
        return env != null ? env : PROPERTIES.getProperty("db.password", "");
    }
}
