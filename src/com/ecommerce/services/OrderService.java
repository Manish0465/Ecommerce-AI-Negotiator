package com.ecommerce.services;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.dao.OrderDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.models.CartItem;
import com.ecommerce.models.Order;
import com.ecommerce.models.OrderItem;
import java.time.LocalDateTime;
import java.util.List;

/**
 * OrderService class handles order-related business logic
 * Manages order placement and order history viewing
 */
public class OrderService {
    private OrderDAO orderDAO = new OrderDAO();
    private CartDAO cartDAO = new CartDAO();
    private ProductDAO productDAO = new ProductDAO();

    /**
     * Place order from cart
     * 
     * @param userId User ID
     * @return Order ID if successful, -1 otherwise
     */
    public int placeOrder(int userId) {
        // Get cart items
        List<CartItem> cartItems = cartDAO.getCartItems(userId);

        if (cartItems.isEmpty()) {
            System.out.println("[ERROR] Cannot place order: Cart is empty.");
            return -1;
        }

        // Calculate total
        double totalAmount = 0;
        for (CartItem item : cartItems) {
            totalAmount += item.getSubtotal();
        }

        // Create order
        Order order = new Order(userId, totalAmount, LocalDateTime.now(), "Pending");
        int orderId = orderDAO.createOrder(order);

        if (orderId == -1) {
            System.out.println("[ERROR] Failed to place order.");
            return -1;
        }

        // Add order items and update stock
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(orderId, cartItem.getProductId(),
                    cartItem.getProductName(),
                    cartItem.getProductPrice(),
                    cartItem.getQuantity());

            if (!orderDAO.addOrderItem(orderItem)) {
                System.out.println("[ERROR] Failed to add items to order.");
                return -1;
            }

            // Update product stock
            int currentStock = productDAO.getProductById(cartItem.getProductId()).getStock();
            productDAO.updateStock(cartItem.getProductId(), currentStock - cartItem.getQuantity());
        }

        // Clear cart after successful order
        cartDAO.clearCart(userId);
        System.out.println("[SUCCESS] Order placed successfully! Order ID: " + orderId);

        return orderId;
    }

    /**
     * Get order by ID
     * 
     * @param orderId Order ID
     * @return Order object if found
     */
    public Order getOrder(int orderId) {
        List<Order> orders = orderDAO.getAllOrders();
        for (Order order : orders) {
            if (order.getId() == orderId) {
                return order;
            }
        }
        System.out.println("[ERROR] Order not found.");
        return null;
    }

    /**
     * Get all orders for a user
     * 
     * @param userId User ID
     * @return List of orders
     */
    public List<Order> getUserOrders(int userId) {
        return orderDAO.getOrdersByUserId(userId);
    }

    /**
     * Get all orders (admin functionality)
     * 
     * @return List of all orders
     */
    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }

    /**
     * Update order status (admin functionality)
     * 
     * @param orderId Order ID
     * @param status  New status
     * @return true if update successful
     */
    public boolean updateOrderStatus(int orderId, String status) {
        if (!isValidStatus(status)) {
            System.out.println("[ERROR] Invalid status. Valid statuses: Pending, Processing, Shipped, Delivered");
            return false;
        }

        if (orderDAO.updateOrderStatus(orderId, status)) {
            System.out.println("[SUCCESS] Order status updated successfully!");
            return true;
        }

        return false;
    }

    /**
     * Check if status is valid
     * 
     * @param status Status to validate
     * @return true if valid status
     */
    private boolean isValidStatus(String status) {
        return status.equalsIgnoreCase("Pending") ||
                status.equalsIgnoreCase("Processing") ||
                status.equalsIgnoreCase("Shipped") ||
                status.equalsIgnoreCase("Delivered");
    }

    /**
     * Display order history for a user
     * 
     * @param userId User ID
     */
    public void displayUserOrderHistory(int userId) {
        List<Order> orders = getUserOrders(userId);

        if (orders.isEmpty()) {
            System.out.println("[ERROR] No orders found.");
            return;
        }

        System.out.println("\n" + "=".repeat(130));
        System.out.println(String.format("%-8s %-15s %-20s %-25s %-15s",
                "Order ID", "Order Date", "Status", "Total Amount", "Items"));
        System.out.println("=".repeat(130));

        for (Order order : orders) {
            System.out.println(String.format("%-8d %-15s %-20s ₹%-14.2f %-15d",
                    order.getId(),
                    order.getOrderDate().toLocalDate().toString(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    order.getOrderItems().size()));
        }

        System.out.println("=".repeat(130) + "\n");
    }

    /**
     * Display order details
     * 
     * @param orderId Order ID
     */
    public void displayOrderDetails(int orderId) {
        Order order = getOrder(orderId);

        if (order == null) {
            return;
        }

        System.out.println("\n" + "=".repeat(130));
        System.out.println("ORDER DETAILS");
        System.out.println("=".repeat(130));
        System.out.println("Order ID: " + order.getId());
        System.out.println("Status: " + order.getStatus());
        System.out.println("Order Date: " + order.getOrderDate());
        System.out.println("\n" + "=".repeat(130));
        System.out.println(String.format("%-30s %-15s %-12s %-15s",
                "Product Name", "Price", "Quantity", "Subtotal"));
        System.out.println("=".repeat(130));

        double total = 0;
        for (OrderItem item : order.getOrderItems()) {
            double subtotal = item.getSubtotal();
            total += subtotal;
            System.out.println(String.format("%-30s Rs.%-12.2f %-12d Rs.%-12.2f",
                    item.getProductName(), item.getProductPrice(),
                    item.getQuantity(), subtotal));
        }

        System.out.println("=".repeat(130));
        System.out.println(String.format("%67s Rs.%-12.2f\n", "Total Amount:", total));
    }
}
