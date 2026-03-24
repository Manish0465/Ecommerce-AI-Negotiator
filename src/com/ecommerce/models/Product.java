package com.ecommerce.models;

/**
 * Product model class representing a product in the e-commerce system
 * Encapsulates product information with data validation
 */
public class Product {
    private int id;
    private String name;
    private double price;
    private int stock; // quantity available
    private String description;

    // Constructor with all fields
    public Product(int id, String name, double price, int stock, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }

    // Constructor for new product (without ID)
    public Product(String name, double price, int stock, String description) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        this.stock = stock;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Method to check if product is available
    public boolean isAvailable() {
        return stock > 0;
    }

    // Method to reduce stock when item is added to cart
    public void reduceStock(int quantity) throws IllegalArgumentException {
        if (quantity > stock) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        this.stock -= quantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", description='" + description + '\'' +
                '}';
    }
}
