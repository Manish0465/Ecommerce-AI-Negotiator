package com.ecommerce.dao;

import com.ecommerce.models.Order;
import com.ecommerce.models.OrderItem;
import com.ecommerce.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // ================= CREATE ORDER =================
    public int createOrder(Order order) {
        String query = "INSERT INTO orders (user_id, total_amount, order_date, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, order.getUserId());
            ps.setDouble(2, order.getTotalAmount());
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, "Pending");

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] Error creating order: " + e.getMessage());
        }

        return -1;
    }

    // ================= ADD ORDER ITEM =================
    public boolean addOrderItem(OrderItem item) {
        String query = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, item.getOrderId());
            ps.setInt(2, item.getProductId());
            ps.setInt(3, item.getQuantity());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("[ERROR] Error adding order item: " + e.getMessage());
            return false;
        }
    }

    // ================= GET USER ORDERS (FIXED) =================
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();

        String query = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            // ✅ FIX: set BEFORE execute
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Order order = new Order(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getDouble("total_amount"),
                            rs.getTimestamp("order_date").toLocalDateTime(),
                            rs.getString("status")
                    );

                    orders.add(order);
                }
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] Error fetching orders: " + e.getMessage());
        }

        return orders;
    }

    // ================= GET ORDER ITEMS =================
    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();

        String query = "SELECT oi.id, oi.order_id, oi.product_id, p.name, p.price, oi.quantity " +
                       "FROM order_items oi " +
                       "JOIN products p ON oi.product_id = p.id " +
                       "WHERE oi.order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new OrderItem(
                            rs.getInt("id"),
                            rs.getInt("order_id"),
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("quantity")
                    ));
                }
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] Error fetching order items: " + e.getMessage());
        }

        return items;
    }

    // ================= UPDATE STATUS =================
    public boolean updateOrderStatus(int orderId, String status) {
        String query = "UPDATE orders SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, status);
            ps.setInt(2, orderId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("[ERROR] Error updating order status: " + e.getMessage());
            return false;
        }
    }

    // ================= ADMIN ALL ORDERS =================
    public List<Order> getAllOrders() {
    List<Order> orders = new ArrayList<>();

    String query = "SELECT * FROM orders ORDER BY order_date DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        while (rs.next()) {
            Order order = new Order(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getDouble("total_amount"),
                    rs.getTimestamp("order_date").toLocalDateTime(),
                    rs.getString("status")
            );

            orders.add(order);
        }

    } catch (SQLException e) {
        System.out.println("[ERROR] Error fetching all orders: " + e.getMessage());
    }

    return orders;
}
    
}