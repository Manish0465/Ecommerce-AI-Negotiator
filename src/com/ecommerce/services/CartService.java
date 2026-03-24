package com.ecommerce.services;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.models.CartItem;
import com.ecommerce.models.Product;
import java.util.List;

/**
 * CartService class handles shopping cart operations
 */
public class CartService {
    private CartDAO cartDAO = new CartDAO();
    private ProductDAO productDAO = new ProductDAO();

    /**
     * Normal Add to Cart (uses original product price)
     */
    public boolean addItemToCart(int userId, int productId, int quantity) {

        if (quantity <= 0) {
            System.out.println("[ERROR] Quantity must be greater than 0.");
            return false;
        }

        Product product = productDAO.getProductById(productId);
        if (product == null) {
            System.out.println("[ERROR] Product not found.");
            return false;
        }

        if (product.getStock() < quantity) {
            System.out.println("[ERROR] Insufficient stock. Available: " + product.getStock());
            return false;
        }

        CartItem cartItem = new CartItem(
                userId,
                productId,
                product.getName(),
                product.getPrice(),   // ✅ original price
                quantity
        );

        if (cartDAO.addItemToCart(cartItem)) {
            System.out.println("[SUCCESS] Item added to cart!");
            return true;
        }

        return false;
    }

    /**
     * 🔥 Add to Cart with Negotiated Price (IMPORTANT)
     */
    public boolean addItemWithCustomPrice(int userId, int productId, int quantity, double customPrice) {

        if (quantity <= 0) {
            System.out.println("[ERROR] Quantity must be greater than 0.");
            return false;
        }

        Product product = productDAO.getProductById(productId);
        if (product == null) {
            System.out.println("[ERROR] Product not found.");
            return false;
        }

        if (product.getStock() < quantity) {
            System.out.println("[ERROR] Insufficient stock. Available: " + product.getStock());
            return false;
        }

        // ✅ Use negotiated price instead of product price
        CartItem cartItem = new CartItem(
                userId,
                productId,
                product.getName(),
                customPrice,   // 🔥 KEY CHANGE
                quantity
        );

        if (cartDAO.addItemToCart(cartItem)) {
            System.out.println("[SUCCESS] Item added with negotiated price!");
            return true;
        }

        return false;
    }

    /**
     * Update quantity
     */
    public boolean updateItemQuantity(int cartId, int newQuantity) {
        if (newQuantity < 0) {
            System.out.println("[ERROR] Quantity cannot be negative.");
            return false;
        }

        if (!cartDAO.cartItemExists(cartId)) {
            System.out.println("[ERROR] Cart item not found.");
            return false;
        }

        if (cartDAO.updateCartItemQuantity(cartId, newQuantity)) {
            System.out.println("[SUCCESS] Cart updated!");
            return true;
        }

        return false;
    }

    /**
     * Remove item
     */
    public boolean removeItemFromCart(int cartId) {
        if (!cartDAO.cartItemExists(cartId)) {
            System.out.println("[ERROR] Cart item not found.");
            return false;
        }

        if (cartDAO.removeItemFromCart(cartId)) {
            System.out.println("[SUCCESS] Item removed!");
            return true;
        }

        return false;
    }

    /**
     * Get cart items
     */
    public List<CartItem> getCartItems(int userId) {
        return cartDAO.getCartItems(userId);
    }

    /**
     * Get total
     */
    public double getCartTotal(int userId) {
        return cartDAO.getCartTotal(userId);
    }

    /**
     * Clear cart
     */
    public boolean clearCart(int userId) {
        if (cartDAO.clearCart(userId)) {
            System.out.println("[SUCCESS] Cart cleared!");
            return true;
        }
        return false;
    }

    /**
     * Check empty
     */
    public boolean isCartEmpty(int userId) {
        return getCartItems(userId).isEmpty();
    }

    /**
     * Display cart
     */
    public void displayCart(int userId) {
        List<CartItem> items = getCartItems(userId);

        if (items.isEmpty()) {
            System.out.println("[INFO] Your cart is empty!");
            return;
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.println(String.format("%-5s %-25s %-10s %-10s %-15s",
                "ID", "Product", "Price", "Qty", "Subtotal"));
        System.out.println("=".repeat(100));

        double total = 0;
        for (CartItem item : items) {
            double subtotal = item.getSubtotal();
            total += subtotal;

            System.out.println(String.format("%-5d %-25s Rs.%-8.2f %-10d Rs.%-12.2f",
                    item.getCartId(),
                    item.getProductName(),
                    item.getProductPrice(),   // ✅ shows negotiated or normal price
                    item.getQuantity(),
                    subtotal));
        }

        System.out.println("=".repeat(100));
        System.out.println(String.format("%60s Rs.%-12.2f\n", "Total:", total));
    }
}