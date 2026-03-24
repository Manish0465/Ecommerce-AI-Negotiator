package com.ecommerce.models;

/**
 * OrderItem model class representing items in a placed order
 * Links a product with quantity in a specific order
 */
public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private String productName;
    private double productPrice;
    private int quantity;

    // Constructor with all fields
    public OrderItem(int id, int orderId, int productId, String productName, 
                     double productPrice, int quantity) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    // Constructor for new order item
    public OrderItem(int orderId, int productId, String productName, 
                     double productPrice, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    // Calculate subtotal for this item
    public double getSubtotal() {
        return productPrice * quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                ", quantity=" + quantity +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}
