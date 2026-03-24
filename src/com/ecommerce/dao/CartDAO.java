package com.ecommerce.dao;

import com.ecommerce.models.CartItem;
import com.ecommerce.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    /**
     * Add item to cart (NOW SUPPORTS CUSTOM PRICE)
     */
    public boolean addItemToCart(CartItem cartItem) {

        String checkQuery = "SELECT * FROM cart WHERE user_id = ? AND product_id = ?";
        String updateQuery = "UPDATE cart SET quantity = quantity + ?, price = ? WHERE user_id = ? AND product_id = ?";
        String insertQuery = "INSERT INTO cart (user_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            PreparedStatement checkPs = conn.prepareStatement(checkQuery);
            checkPs.setInt(1, cartItem.getUserId());
            checkPs.setInt(2, cartItem.getProductId());
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                // ✅ Update quantity AND price
                PreparedStatement updatePs = conn.prepareStatement(updateQuery);
                updatePs.setInt(1, cartItem.getQuantity());
                updatePs.setDouble(2, cartItem.getProductPrice()); // 🔥 custom price
                updatePs.setInt(3, cartItem.getUserId());
                updatePs.setInt(4, cartItem.getProductId());
                updatePs.executeUpdate();
            } else {
                // ✅ Insert with price
                PreparedStatement insertPs = conn.prepareStatement(insertQuery);
                insertPs.setInt(1, cartItem.getUserId());
                insertPs.setInt(2, cartItem.getProductId());
                insertPs.setInt(3, cartItem.getQuantity());
                insertPs.setDouble(4, cartItem.getProductPrice()); // 🔥 custom price
                insertPs.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            System.out.println("[ERROR] Error adding item: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🔥 FIXED: Get cart items (USE CART PRICE NOT PRODUCT PRICE)
     */
    public List<CartItem> getCartItems(int userId) {
        List<CartItem> cartItems = new ArrayList<>();

        String query = "SELECT c.id, c.user_id, c.product_id, p.name, c.price, c.quantity " +
                       "FROM cart c " +
                       "JOIN products p ON c.product_id = p.id " +
                       "WHERE c.user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cartItems.add(new CartItem(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"), // ✅ FROM CART
                        rs.getInt("quantity")
                ));
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] Error fetching cart: " + e.getMessage());
        }

        return cartItems;
    }

    /**
     * Update quantity
     */
    public boolean updateCartItemQuantity(int cartId, int newQuantity) {
        if (newQuantity <= 0) return removeItemFromCart(cartId);

        String query = "UPDATE cart SET quantity = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, newQuantity);
            ps.setInt(2, cartId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("[ERROR] Update failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove item
     */
    public boolean removeItemFromCart(int cartId) {
        String query = "DELETE FROM cart WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, cartId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("[ERROR] Remove failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clear cart
     */
    public boolean clearCart(int userId) {
        String query = "DELETE FROM cart WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("[ERROR] Clear failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🔥 FIXED TOTAL (use cart price)
     */
    public double getCartTotal(int userId) {

        String query = "SELECT SUM(c.price * c.quantity) as total FROM cart c WHERE c.user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] Total error: " + e.getMessage());
        }

        return 0.0;
    }

    public boolean cartItemExists(int cartId) {
        String query = "SELECT * FROM cart WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, cartId);
            return ps.executeQuery().next();

        } catch (SQLException e) {
            return false;
        }
    }
}