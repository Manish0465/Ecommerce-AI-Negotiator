package com.ecommerce.models;

/**
 * CartItem model class representing items in a shopping cart
 * Links a product with quantity in a user's cart
 */
public class CartItem {
    private int cartId;
    private int userId;
    private int productId;
    private String productName;
    private double productPrice;
    private int quantity;

    // Constructor with all fields
    public CartItem(int cartId, int userId, int productId, String productName, 
                    double productPrice, int quantity) {
        this.cartId = cartId;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    // Constructor for adding new item to cart
    public CartItem(int userId, int productId, String productName, 
                    double productPrice, int quantity) {
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    // Getters
    public int getCartId() {
        return cartId;
    }

    public int getUserId() {
        return userId;
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
    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        this.quantity = quantity;
    }

    // Calculate subtotal for this item
    public double getSubtotal() {
        return productPrice * quantity;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "cartId=" + cartId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                ", quantity=" + quantity +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}
