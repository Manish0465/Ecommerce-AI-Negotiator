package com.ecommerce.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order model class representing a placed order
 * Contains order details and associated order items
 */
public class Order {
    private int id;
    private int userId;
    private double totalAmount;
    private LocalDateTime orderDate;
    private String status; // "pending", "processing", "shipped", "delivered"
    private List<OrderItem> orderItems;

    // Constructor with all fields
    public Order(int id, int userId, double totalAmount, LocalDateTime orderDate, String status) {
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
        this.orderItems = new ArrayList<>();
    }

    // Constructor for new order (without ID)
    public Order(int userId, double totalAmount, LocalDateTime orderDate, String status) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
        this.orderItems = new ArrayList<>();
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addOrderItem(OrderItem item) {
        this.orderItems.add(item);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", totalAmount=" + totalAmount +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                ", itemCount=" + orderItems.size() +
                '}';
    }
}
