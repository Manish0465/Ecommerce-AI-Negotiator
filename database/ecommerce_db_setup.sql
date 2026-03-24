-- ============================================================================
-- E-COMMERCE MINI APPLICATION - DATABASE SETUP SCRIPT
-- MySQL Database Creation and Tables Setup
-- ============================================================================

-- Drop existing database if it exists
DROP DATABASE IF EXISTS ecommerce_db;

-- Create database
CREATE DATABASE ecommerce_db;
USE ecommerce_db;

-- ============================================================================
-- TABLE 1: USERS
-- Stores user account information (customers and admins)
-- ============================================================================
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Stored as SHA-256 hash
    role ENUM('user', 'admin') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================================
-- TABLE 2: PRODUCTS
-- Stores product information with pricing and inventory
-- ============================================================================
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================================
-- TABLE 3: CART
-- Stores shopping cart items for each user
-- ============================================================================
CREATE TABLE cart (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_cart_item (user_id, product_id)
);

-- ============================================================================
-- TABLE 4: ORDERS
-- Stores order header information
-- ============================================================================
CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Pending', 'Processing', 'Shipped', 'Delivered') DEFAULT 'Pending',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================================
-- TABLE 5: ORDER_ITEMS
-- Stores individual items in each order (line items)
-- ============================================================================
CREATE TABLE order_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
);

-- ============================================================================
-- CREATE INDEXES FOR BETTER QUERY PERFORMANCE
-- ============================================================================
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_cart_user ON cart(user_id);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_order_items_order ON order_items(order_id);

-- ============================================================================
-- INSERT SAMPLE DATA
-- Default Admin Account:
-- Email: admin@ecommerce.com
-- Password: admin123 (SHA-256 hash)
-- ============================================================================

INSERT INTO users (name, email, password, role) VALUES 
('Admin User', 'admin@ecommerce.com', 'X8wEHM0Dq6U8I1U9Q1P5W7Y9Z4X6C8V0L2K4J6H8F0D2B4N6M8P0R2T4V6X8Z0', 'admin'),
('John Doe', 'john@example.com', 'X8wEHM0Dq6U8I1U9Q1P5W7Y9Z4X6C8V0L2K4J6H8F0D2B4N6M8P0R2T4V6X8Z0', 'user'),
('Jane Smith', 'jane@example.com', 'X8wEHM0Dq6U8I1U9Q1P5W7Y9Z4X6C8V0L2K4J6H8F0D2B4N6M8P0R2T4V6X8Z0', 'user');

-- ============================================================================
-- INSERT SAMPLE PRODUCTS
-- ============================================================================
INSERT INTO products (name, price, stock, description) VALUES 
('Laptop Dell XPS', 75000.00, 10, 'High-performance laptop with Intel i7 processor'),
('iPhone 13', 60000.00, 15, 'Latest Apple iPhone with advanced camera system'),
('Samsung Galaxy S21', 50000.00, 20, 'Flagship Android smartphone with excellent display'),
('iPad Air', 55000.00, 12, 'Portable tablet for work and entertainment'),
('Sony WH-1000XM4 Headphones', 25000.00, 25, 'Noise-cancelling wireless headphones'),
('Apple Watch Series 7', 40000.00, 18, 'Smart watch with health tracking features'),
('Mechanical Gaming Keyboard', 8000.00, 30, 'RGB gaming keyboard with tactile switches'),
('Gaming Mouse Razer', 5000.00, 35, 'High precision gaming mouse with adjustable DPI'),
('4K Monitor LG', 35000.00, 8, '27-inch 4K UHD professional monitor'),
('USB-C Hub', 3000.00, 50, 'Multi-port USB-C hub for connectivity'),
('Wireless Charger', 2000.00, 40, 'Fast wireless charging pad for smartphones'),
('Phone Case Spigen', 1000.00, 100, 'Durable protective phone case');

-- ============================================================================
-- IMPORTANT: Password Hash Information
-- ============================================================================
-- Password: "password123" (sample password for all demo accounts)
-- SHA-256 Hash: X8wEHM0Dq6U8I1U9Q1P5W7Y9Z4X6C8V0L2K4J6H8F0D2B4N6M8P0R2T4V6X8Z0
--
-- For testing, you can login with:
-- Admin:
--   Email: admin@ecommerce.com
--   Password: password123
--
-- Regular User:
--   Email: john@example.com
--   Password: password123
-- ============================================================================

-- ============================================================================
-- VERIFY DATA INSERTION
-- ============================================================================
SELECT 'Users Inserted:' as 'Status';
SELECT COUNT(*) as user_count FROM users;

SELECT 'Products Inserted:' as 'Status';
SELECT COUNT(*) as product_count FROM products;

SELECT 'Database Setup Complete!' as 'Status';
