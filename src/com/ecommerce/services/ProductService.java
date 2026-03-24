package com.ecommerce.services;

import com.ecommerce.dao.ProductDAO;
import com.ecommerce.models.Product;
import com.ecommerce.util.ValidationUtil;
import java.util.List;

/**
 * ProductService class handles product-related business logic
 * Manages product operations for admin and user functionality
 */
public class ProductService {
    private ProductDAO productDAO = new ProductDAO();

    /**
     * Add a new product (admin functionality)
     * 
     * @param name        Product name
     * @param price       Product price
     * @param stock       Product stock
     * @param description Product description
     * @return true if product added successfully
     */
    public boolean addProduct(String name, double price, int stock, String description) {
        // Validate inputs
        if (!ValidationUtil.isNotEmpty(name)) {
            System.out.println("[ERROR] Product name cannot be empty.");
            return false;
        }

        if (!ValidationUtil.isPositive(price)) {
            System.out.println("[ERROR] Price must be positive.");
            return false;
        }

        if (!ValidationUtil.isNonNegative(stock)) {
            System.out.println("[ERROR] Stock cannot be negative.");
            return false;
        }

        Product product = new Product(name, price, stock, description);
        if (productDAO.createProduct(product)) {
            System.out.println("[SUCCESS] Product added successfully!");
            return true;
        }

        return false;
    }

    /**
     * Get product by ID
     * 
     * @param productId Product ID
     * @return Product object if found
     */
    public Product getProduct(int productId) {
        Product product = productDAO.getProductById(productId);
        if (product == null) {
            System.out.println("[ERROR] Product not found.");
        }
        return product;
    }

    /**
     * Get all products
     * 
     * @return List of all products
     */
    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    /**
     * Get only available products (stock > 0)
     * 
     * @return List of available products
     */
    public List<Product> getAvailableProducts() {
        return productDAO.getAvailableProducts();
    }

    /**
     * Update product (admin functionality)
     * 
     * @param productId   Product ID to update
     * @param name        New product name
     * @param price       New price
     * @param stock       New stock
     * @param description New description
     * @return true if update successful
     */
    public boolean updateProduct(int productId, String name, double price, int stock, String description) {
        // Validate inputs
        if (!ValidationUtil.isNotEmpty(name)) {
            System.out.println("[ERROR] Product name cannot be empty.");
            return false;
        }

        if (!ValidationUtil.isPositive(price)) {
            System.out.println("[ERROR] Price must be positive.");
            return false;
        }

        if (!ValidationUtil.isNonNegative(stock)) {
            System.out.println("[ERROR] Stock cannot be negative.");
            return false;
        }

        Product product = new Product(productId, name, price, stock, description);
        if (productDAO.updateProduct(product)) {
            System.out.println("[SUCCESS] Product updated successfully!");
            return true;
        }

        return false;
    }

    /**
     * Delete product (admin functionality)
     * 
     * @param productId Product ID to delete
     * @return true if deletion successful
     */
    public boolean deleteProduct(int productId) {
        if (!productDAO.productExists(productId)) {
            System.out.println("[ERROR] Product not found.");
            return false;
        }

        if (productDAO.deleteProduct(productId)) {
            System.out.println("[SUCCESS] Product deleted successfully!");
            return true;
        }

        return false;
    }

    /**
     * Check if product exists
     * 
     * @param productId Product ID
     * @return true if product exists
     */
    public boolean productExists(int productId) {
        return productDAO.productExists(productId);
    }

    /**
     * Check if product is available
     * 
     * @param productId Product ID
     * @return true if product is available (stock > 0)
     */
    public boolean isProductAvailable(int productId) {
        Product product = getProduct(productId);
        return product != null && product.isAvailable();
    }

    /**
     * Check if sufficient stock available
     * 
     * @param productId Product ID
     * @param quantity  Quantity needed
     * @return true if sufficient stock available
     */
    public boolean hasSufficientStock(int productId, int quantity) {
        Product product = getProduct(productId);
        return product != null && product.getStock() >= quantity;
    }

    /**
     * Update product stock
     * 
     * @param productId Product ID
     * @param newStock  New stock value
     * @return true if update successful
     */
    public boolean updateStock(int productId, int newStock) {
        if (!ValidationUtil.isNonNegative(newStock)) {
            System.out.println("[ERROR] Stock cannot be negative.");
            return false;
        }

        return productDAO.updateStock(productId, newStock);
    }

    /**
     * Display all products with details
     */
    public void displayAllProducts() {
        List<Product> products = getAllProducts();

        if (products.isEmpty()) {
            System.out.println("[ERROR] No products available.");
            return;
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.println(String.format("%-5s %-25s %-10s %-10s %-40s",
                "ID", "Name", "Price", "Stock", "Description"));
        System.out.println("=".repeat(100));

        for (Product p : products) {
            System.out.println(String.format("%-5d %-25s Rs.%-7.2f %-10d %-40s",
                    p.getId(), p.getName(), p.getPrice(), p.getStock(),
                    p.getDescription() != null ? p.getDescription().substring(0,
                            Math.min(40, p.getDescription().length())) : "N/A"));
        }

        System.out.println("=".repeat(100) + "\n");
    }

    
    public boolean updateProductPrice(int productId, double newPrice) {
    Product product = productDAO.getProductById(productId);
    if (product == null) {
        System.out.println("[ERROR] Product not found.");
        return false;
    }
    product.setPrice(newPrice);
    boolean updated = productDAO.updateProduct(product);
    if (updated) System.out.println("[SUCCESS] Price updated to Rs." + newPrice);
    return updated;
}
}

