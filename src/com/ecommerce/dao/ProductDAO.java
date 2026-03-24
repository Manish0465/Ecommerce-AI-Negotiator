package com.ecommerce.dao;

import com.ecommerce.models.Product;
import com.ecommerce.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductDAO class handles all product-related database operations
 * Manages CRUD operations for products in the e-commerce system
 */
public class ProductDAO {

    /**
     * Create a new product (admin functionality)
     * 
     * @param product Product object to create
     * @return true if product created successfully, false otherwise
     */
    public boolean createProduct(Product product) {
        String query = "INSERT INTO products (name, price, stock, description) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getStock());
            ps.setString(4, product.getDescription());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("[ERROR] Error creating product: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get product by ID
     * 
     * @param productId Product ID
     * @return Product object if found, null otherwise
     */
    public Product getProductById(int productId) {
        String query = "SELECT * FROM products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getString("description"));
            }
            return null;

        } catch (SQLException e) {
            System.out.println("[ERROR] Error fetching product: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get all products
     * 
     * @return List of all products in the system
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getString("description")));
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] Error fetching products: " + e.getMessage());
        }

        return products;
    }

    /**
     * Get available products (stock > 0)
     * 
     * @return List of available products
     */
    public List<Product> getAvailableProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products WHERE stock > 0";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getString("description")));
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] Error fetching available products: " + e.getMessage());
        }

        return products;
    }

    /**
     * Update product information (admin functionality)
     * 
     * @param product Product object with updated data
     * @return true if update successful, false otherwise
     */
    public boolean updateProduct(Product product) {
        String query = "UPDATE products SET name = ?, price = ?, stock = ?, description = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getStock());
            ps.setString(4, product.getDescription());
            ps.setInt(5, product.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("[ERROR] Error updating product: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete product (admin functionality)
     * 
     * @param productId Product ID to delete
     * @return true if delete successful, false otherwise
     */
    public boolean deleteProduct(int productId) {
        String query = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, productId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("[ERROR] Error deleting product: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if product exists
     * 
     * @param productId Product ID
     * @return true if product exists, false otherwise
     */
    public boolean productExists(int productId) {
        return getProductById(productId) != null;
    }

    /**
     * Update product stock
     * 
     * @param productId Product ID
     * @param newStock  New stock value
     * @return true if update successful, false otherwise
     */
    public boolean updateStock(int productId, int newStock) {
        String query = "UPDATE products SET stock = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, newStock);
            ps.setInt(2, productId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("[ERROR] Error updating stock: " + e.getMessage());
            return false;
        }
    }
}
