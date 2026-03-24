package com.ecommerce;

import com.ecommerce.models.CartItem;
import com.ecommerce.models.Product;
import com.ecommerce.services.*;
import com.ecommerce.util.DatabaseConnection;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class for E-commerce Mini Application
 * Provides menu-driven console interface for user interactions
 * 
 * Features:
 * - User Registration and Login
 * - Product Management (Admin)
 * - Shopping Cart Management
 * - Order Placement and History
 */
public class ECommerceApp {
    private UserService userService;
    private ProductService productService;
    private CartService cartService;
    private OrderService orderService;
    private Scanner scanner;

    /**
     * Constructor - Initialize all services
     */
    public ECommerceApp() {
        this.userService = new UserService();
        this.productService = new ProductService();
        this.cartService = new CartService();
        this.orderService = new OrderService();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Main method - Start the application
     */
    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println(" ".repeat(20) + "WELCOME TO E-COMMERCE APPLICATION");
        System.out.println("=".repeat(80) + "\n");

        // Test database connection
        DatabaseConnection.testConnection();

        // Create and start application
        ECommerceApp app = new ECommerceApp();
        try {
            app.showMainMenu();
        } finally {
            // Always close resources
            if (app.scanner != null) {
                app.scanner.close();
            }
            DatabaseConnection.closeConnection();
        }
    }

    /**
     * Display main menu and handle user choice
     */
    private void showMainMenu() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║         MAIN MENU                      ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ 1. Register                            ║");
            System.out.println("║ 2. Login                               ║");
            System.out.println("║ 3. View Products (without login)       ║");
            System.out.println("║ 4. Exit                                ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        handleRegistration();
                        break;
                    case 2:
                        handleLogin();
                        break;
                    case 3:
                        viewProductsGuest();
                        break;
                    case 4:
                        System.out.println("\n[SUCCESS] Thank you for using our application. Goodbye!");
                        return;
                    default:
                        System.out.println("[ERROR] Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear buffer
            }
        }
    }

    /**
     * Handle user registration
     */
    private void handleRegistration() {
        System.out.println("\n--- User Registration ---");
        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password (min 6 characters): ");
        String password = scanner.nextLine().trim();

        // For registration, user role is always "user"
        String role = "user";

        if (userService.registerUser(name, email, password, role)) {
            System.out.println("You can now login with your credentials.");
        }
    }

    /**
     * Handle user login
     */
    private void handleLogin() {
        System.out.println("\n--- User Login ---");
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (userService.loginUser(email, password)) {
            // Show user-specific menu after successful login
            if (userService.isCurrentUserAdmin()) {
                showAdminMenu();
            } else {
                showUserMenu();
            }
        }
    }

    /**
     * View products without login
     */
    private void viewProductsGuest() {
        System.out.println("\n--- Available Products ---");
        productService.displayAllProducts();
    }

    /**
     * Show menu for regular user
     */
    private void showUserMenu() {
        while (userService.isUserLoggedIn()) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║         USER MENU                      ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ 1. View Products                       ║");
            System.out.println("║ 2. Add Item to Cart                    ║");
            System.out.println("║ 3. View Cart                           ║");
            System.out.println("║ 4. Update Cart Item Quantity           ║");
            System.out.println("║ 5. Remove Item from Cart               ║");
            System.out.println("║ 6. Place Order                         ║");
            System.out.println("║ 7. View Order History                  ║");
            System.out.println("║ 8. View Order Details                  ║");
            System.out.println("║ 9. Logout                              ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        productService.displayAllProducts();
                        break;
                    case 2:
                        addItemToCart();
                        break;
                    case 3:
                        cartService.displayCart(userService.getCurrentUser().getId());
                        break;
                    case 4:
                        updateCartQuantity();
                        break;
                    case 5:
                        removeFromCart();
                        break;
                    case 6:
                        placeOrder();
                        break;
                    case 7:
                        orderService.displayUserOrderHistory(userService.getCurrentUser().getId());
                        break;
                    case 8:
                        viewOrderDetails();
                        break;
                    case 9:
                        userService.logout();
                        break;
                    default:
                        System.out.println("[ERROR] Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    /**
     * Show menu for admin user
     */
    private void showAdminMenu() {
        while (userService.isUserLoggedIn() && userService.isCurrentUserAdmin()) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║         ADMIN MENU                     ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ 1. View All Products                   ║");
            System.out.println("║ 2. Add New Product                     ║");
            System.out.println("║ 3. Update Product                      ║");
            System.out.println("║ 4. Delete Product                      ║");
            System.out.println("║ 5. View All Orders                     ║");
            System.out.println("║ 6. Update Order Status                 ║");
            System.out.println("║ 7. Logout                              ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        productService.displayAllProducts();
                        break;
                    case 2:
                        addNewProduct();
                        break;
                    case 3:
                        updateProduct();
                        break;
                    case 4:
                        deleteProduct();
                        break;
                    case 5:
                        viewAllOrders();
                        break;
                    case 6:
                        updateOrderStatus();
                        break;
                    case 7:
                        userService.logout();
                        break;
                    default:
                        System.out.println("[ERROR] Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    /**
     * Add item to cart
     */
    private void addItemToCart() {
        System.out.println("\n--- Add Item to Cart ---");
        productService.displayAllProducts();

        System.out.print("Enter Product ID: ");
        int productId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        cartService.addItemToCart(userService.getCurrentUser().getId(), productId, quantity);
    }

    /**
     * Update cart item quantity
     */
    private void updateCartQuantity() {
        System.out.println("\n--- Update Cart Item Quantity ---");
        cartService.displayCart(userService.getCurrentUser().getId());

        System.out.print("Enter Cart Item ID: ");
        int cartId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter New Quantity (0 to remove): ");
        int newQuantity = scanner.nextInt();
        scanner.nextLine();

        cartService.updateItemQuantity(cartId, newQuantity);
    }

    /**
     * Remove item from cart
     */
    private void removeFromCart() {
        System.out.println("\n--- Remove Item from Cart ---");
        cartService.displayCart(userService.getCurrentUser().getId());

        System.out.print("Enter Cart Item ID to remove: ");
        int cartId = scanner.nextInt();
        scanner.nextLine();

        cartService.removeItemFromCart(cartId);
    }

    /**
     * Place order from cart
     */
    private void placeOrder() {
        System.out.println("\n--- Place Order ---");

        List<CartItem> cartItems = cartService.getCartItems(userService.getCurrentUser().getId());

        if (cartItems.isEmpty()) {
            System.out.println("[ERROR] Your cart is empty. Add items before placing order.");
            return;
        }

        cartService.displayCart(userService.getCurrentUser().getId());

        System.out.print("Do you want to proceed with this order? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes")) {
            orderService.placeOrder(userService.getCurrentUser().getId());
        } else {
            System.out.println("✓ Order cancelled.");
        }
    }

    /**
     * View order details
     */
    private void viewOrderDetails() {
        System.out.println("\n--- View Order Details ---");
        System.out.print("Enter Order ID: ");
        int orderId = scanner.nextInt();
        scanner.nextLine();

        orderService.displayOrderDetails(orderId);
    }

    /**
     * Add new product (admin)
     */
    private void addNewProduct() {
        System.out.println("\n--- Add New Product ---");
        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Price: ");
        double price = scanner.nextDouble();

        System.out.print("Enter Stock: ");
        int stock = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Description: ");
        String description = scanner.nextLine().trim();

        productService.addProduct(name, price, stock, description);
    }

    /**
     * Update product (admin)
     */
    private void updateProduct() {
        System.out.println("\n--- Update Product ---");
        productService.displayAllProducts();

        System.out.print("Enter Product ID to update: ");
        int productId = scanner.nextInt();
        scanner.nextLine();

        Product product = productService.getProduct(productId);
        if (product == null) {
            return;
        }

        System.out.print("Enter New Name (current: " + product.getName() + "): ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter New Price (current: " + product.getPrice() + "): ");
        double price = scanner.nextDouble();

        System.out.print("Enter New Stock (current: " + product.getStock() + "): ");
        int stock = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter New Description (current: " + product.getDescription() + "): ");
        String description = scanner.nextLine().trim();

        productService.updateProduct(productId, name, price, stock, description);
    }

    /**
     * Delete product (admin)
     */
    private void deleteProduct() {
        System.out.println("\n--- Delete Product ---");
        productService.displayAllProducts();

        System.out.print("Enter Product ID to delete: ");
        int productId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Are you sure? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes")) {
            productService.deleteProduct(productId);
        } else {
            System.out.println("✓ Operation cancelled.");
        }
    }

    /**
     * View all orders (admin)
     */
    private void viewAllOrders() {
        System.out.println("\n--- All Orders ---");
        List<com.ecommerce.models.Order> orders = orderService.getAllOrders();

        if (orders.isEmpty()) {
            System.out.println("[ERROR] No orders found.");
            return;
        }

        System.out.println("\n" + "=".repeat(130));
        System.out.println(String.format("%-8s %-8s %-15s %-20s %-25s %-15s",
                "Order ID", "User ID", "Order Date", "Status", "Total Amount", "Items"));
        System.out.println("=".repeat(130));

        for (com.ecommerce.models.Order order : orders) {
            System.out.println(String.format("%-8d %-8d %-15s %-20s ₹%-24.2f %-15d",
                    order.getId(),
                    order.getUserId(),
                    order.getOrderDate().toLocalDate().toString(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    order.getOrderItems().size()));
        }

        System.out.println("=".repeat(130) + "\n");
    }

    /**
     * Update order status (admin)
     */
    private void updateOrderStatus() {
        System.out.println("\n--- Update Order Status ---");
        System.out.print("Enter Order ID: ");
        int orderId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Valid statuses: Pending, Processing, Shipped, Delivered");
        System.out.print("Enter New Status: ");
        String status = scanner.nextLine().trim();

        orderService.updateOrderStatus(orderId, status);
    }
}
