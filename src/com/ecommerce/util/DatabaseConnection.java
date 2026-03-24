package com.ecommerce.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection utility class
 * Manages JDBC connections to MySQL database
 * Use singleton pattern for efficient connection management
 */
public class DatabaseConnection {
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ecommerce_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    // Static instance for singleton pattern
    private static Connection connection = null;

    /**
     * Private constructor to prevent instantiation
     */
    private DatabaseConnection() {
    }

    /**
     * Get connection to database (creates new connection if needed)
     * 
     * @return Connection object or null if connection fails
     */
    public static Connection getConnection() {
        try {
            // Load the MySQL JDBC driver
            Class.forName(DRIVER);

            // Create new connection if needed
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                System.out.println("[SUCCESS] Database connected successfully!");
            }
            return connection;
        } catch (ClassNotFoundException e) {
            System.out.println("[ERROR] MySQL JDBC Driver not found!");
            System.out.println("Please add mysql-connector-j-x.x.x.jar to the lib folder");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.out.println("[ERROR] Database connection failed!");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[SUCCESS] Database connection closed");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test the database connection
     */
    public static void testConnection() {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("[SUCCESS] Database connection test: SUCCESS");
        } else {
            System.out.println("[ERROR] Database connection test: FAILED");
        }
    }
}
