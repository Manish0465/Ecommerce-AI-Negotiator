# DATABASE SCHEMA - E-Commerce Mini Application

Comprehensive documentation of the database structure and relationships.

---

## 📊 Database Overview

**Database Name:** `ecommerce_db`

**Character Set:** UTF-8 (Default)

**Total Tables:** 5

**Total Relationships:** 4 Foreign Keys

---

## 📋 Table Schemas

### 1. USERS TABLE

**Purpose:** Stores user account information for both regular users and admins.

**Location:** `database/ecommerce_db_setup.sql` (Lines 16-25)

```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('user', 'admin') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### Column Details

| Column | Type | Constraints | Description |
|--------|------|-----------|-------------|
| `id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| `name` | VARCHAR(100) | NOT NULL | User's full name |
| `email` | VARCHAR(100) | NOT NULL, UNIQUE | Email address (login identifier) |
| `password` | VARCHAR(255) | NOT NULL | SHA-256 hashed password |
| `role` | ENUM | DEFAULT 'user' | User role: 'user' or 'admin' |
| `created_at` | TIMESTAMP | AUTO | Record creation timestamp |
| `updated_at` | TIMESTAMP | AUTO | Last update timestamp |

#### Indexes
```sql
CREATE INDEX idx_user_email ON users(email);
```
- **Purpose:** Fast lookup by email during login
- **Performance:** O(log n) instead of O(n)

#### Sample Data
```sql
INSERT INTO users (name, email, password, role) VALUES 
('Admin User', 'admin@ecommerce.com', '[SHA256_HASH]', 'admin'),
('John Doe', 'john@example.com', '[SHA256_HASH]', 'user'),
('Jane Smith', 'jane@example.com', '[SHA256_HASH]', 'user');
```

#### Java Connection
```java
// UserDAO.java - createUser() method
public boolean createUser(User user) {
    String query = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
    // Implementation...
}
```

---

### 2. PRODUCTS TABLE

**Purpose:** Stores product information with pricing and inventory management.

**Location:** `database/ecommerce_db_setup.sql` (Lines 28-36)

```sql
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### Column Details

| Column | Type | Constraints | Description |
|--------|------|-----------|-------------|
| `id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique product identifier |
| `name` | VARCHAR(150) | NOT NULL | Product name |
| `price` | DECIMAL(10,2) | NOT NULL | Product price (₹0.00 - ₹99999999.99) |
| `stock` | INT | NOT NULL, DEFAULT 0 | Available quantity |
| `description` | TEXT | | Detailed product description |
| `created_at` | TIMESTAMP | AUTO | Record creation timestamp |
| `updated_at` | TIMESTAMP | AUTO | Last update timestamp |

#### Pricing Logic
- **Decimal(10,2)**: 10 total digits, 2 decimal places
- **Range**: ₹0.01 to ₹99,999,999.99
- **Example**: Price 75000.00 = ₹75,000

#### Stock Management
- Default: 0 (out of stock)
- Incremented when admin adds product
- Decremented when order is placed
- Alerts when stock < threshold (can be extended)

#### Sample Data
```sql
INSERT INTO products (name, price, stock, description) VALUES 
('Laptop Dell XPS', 75000.00, 10, 'High-performance laptop'),
('iPhone 13', 60000.00, 15, 'Latest Apple iPhone'),
('Samsung Galaxy S21', 50000.00, 20, 'Flagship Android smartphone');
-- ... Total 12 products
```

#### Java Connection
```java
// ProductDAO.java
public Product getProductById(int productId) {
    // Fetches product details
    // Used in cart and order operations
}
```

---

### 3. CART TABLE

**Purpose:** Stores shopping cart items for each user.

**Location:** `database/ecommerce_db_setup.sql` (Lines 39-50)

```sql
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
```

#### Column Details

| Column | Type | Constraints | Description |
|--------|------|-----------|-------------|
| `id` | INT | PRIMARY KEY, AUTO_INCREMENT | Cart item identifier |
| `user_id` | INT | NOT NULL, FK | Reference to users table |
| `product_id` | INT | NOT NULL, FK | Reference to products table |
| `quantity` | INT | NOT NULL, DEFAULT 1 | Item quantity in cart |
| `created_at` | TIMESTAMP | AUTO | When item added to cart |
| `updated_at` | TIMESTAMP | AUTO | When quantity updated |

#### Foreign Keys
```sql
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
```
- **Relationship:** Many-to-Many (Users to Products)
- **ON DELETE CASCADE:** Delete cart items when user/product deleted

#### Unique Constraint
```sql
UNIQUE KEY unique_cart_item (user_id, product_id)
```
- **Purpose:** Prevent duplicate entries for same user-product combination
- **Effect:** Same product added multiple times updates quantity instead

#### Indexes
```sql
CREATE INDEX idx_cart_user ON cart(user_id);
```
- **Purpose:** Fast retrieval of all items for a user
- **Usage:** View cart operation

#### Sample Queries
```sql
-- Get user's cart
SELECT * FROM cart WHERE user_id = 5;

-- Get cart total
SELECT SUM(p.price * c.quantity) as total 
FROM cart c 
JOIN products p ON c.product_id = p.id 
WHERE c.user_id = 5;

-- Add/update item
INSERT INTO cart (user_id, product_id, quantity) 
VALUES (5, 3, 2)
ON DUPLICATE KEY UPDATE quantity = quantity + 2;
```

#### Java Connection
```java
// CartDAO.java
public List<CartItem> getCartItems(int userId) {
    // Joins cart with products to get complete info
}

public boolean addItemToCart(CartItem cartItem) {
    // INSERT or UPDATE based on uniqueness
}
```

---

### 4. ORDERS TABLE

**Purpose:** Stores order header information (not individual items).

**Location:** `database/ecommerce_db_setup.sql` (Lines 53-64)

```sql
CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Pending', 'Processing', 'Shipped', 'Delivered') DEFAULT 'Pending',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

#### Column Details

| Column | Type | Constraints | Description |
|--------|------|-----------|-------------|
| `id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique order identifier |
| `user_id` | INT | NOT NULL, FK | Reference to users table |
| `total_amount` | DECIMAL(10,2) | NOT NULL | Total order amount (₹) |
| `order_date` | TIMESTAMP | AUTO | When order was placed |
| `status` | ENUM | DEFAULT 'Pending' | Order status (4 values) |

#### Order Status Values

| Status | Meaning |
|--------|---------|
| `Pending` | Order placed, awaiting processing |
| `Processing` | Admin is preparing order |
| `Shipped` | Order dispatched to customer |
| `Delivered` | Order received by customer |

#### Foreign Keys
```sql
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
```
- **One-to-Many:** One user can have multiple orders
- **ON DELETE CASCADE:** Delete user's orders when user deleted

#### Indexes
```sql
CREATE INDEX idx_orders_user ON orders(user_id);
```
- **Purpose:** Fast retrieval of orders for a user

#### Sample Queries
```sql
-- Get order details
SELECT * FROM orders WHERE id = 10;

-- Get user's order history
SELECT * FROM orders WHERE user_id = 5 ORDER BY order_date DESC;

-- Get all pending orders
SELECT * FROM orders WHERE status = 'Pending';

-- Calculate total sales
SELECT SUM(total_amount) as total_sales FROM orders;
```

#### Java Connection
```java
// OrderDAO.java
public int createOrder(Order order) {
    // Creates order record
    // Returns generated order ID
}

public List<Order> getOrdersByUserId(int userId) {
    // Retrieves all orders for a user
}
```

---

### 5. ORDER_ITEMS TABLE

**Purpose:** Stores individual items within an order (line items).

**Location:** `database/ecommerce_db_setup.sql` (Lines 67-75)

```sql
CREATE TABLE order_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
);
```

#### Column Details

| Column | Type | Constraints | Description |
|--------|------|-----------|-------------|
| `id` | INT | PRIMARY KEY, AUTO_INCREMENT | Item identifier |
| `order_id` | INT | NOT NULL, FK | Reference to orders table |
| `product_id` | INT | NOT NULL, FK | Reference to products table |
| `quantity` | INT | NOT NULL | Quantity of product in order |

#### Foreign Keys

**To Orders Table:**
```sql
FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
```
- Delete order_items when order is deleted

**To Products Table:**
```sql
FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
```
- Prevent product deletion if referenced in any order
- Maintains historical data integrity

#### Indexes
```sql
CREATE INDEX idx_order_items_order ON order_items(order_id);
```
- **Purpose:** Fast retrieval of items for an order

#### Sample Queries
```sql
-- Get all items for an order
SELECT * FROM order_items WHERE order_id = 10;

-- Get order details with product info
SELECT oi.*, p.name, p.price 
FROM order_items oi 
JOIN products p ON oi.product_id = p.id 
WHERE oi.order_id = 10;

-- Calculate item subtotal
SELECT product_id, quantity, 
       (SELECT price FROM products WHERE id = oi.product_id) * quantity as subtotal
FROM order_items oi 
WHERE order_id = 10;
```

#### Java Connection
```java
// OrderDAO.java
public boolean addOrderItem(OrderItem orderItem) {
    // Adds item to order
    // Called for each cart item during order creation
}

public List<OrderItem> getOrderItems(int orderId) {
    // Retrieves all items in an order
    // Joins with products for complete info
}
```

---

## 🔗 Entity Relationships

### Relationship Diagram

```
    USERS (1) ──────────────────── (Many) ORDERS
     │                                      │
     │                                      │
     │                              (Many) ORDER_ITEMS
     │                                      │
     │                                      │
     └──────────────── (Many) CART         │
                          │                 │
                          └─── (Many) PRODUCTS (1) ───┘
```

### Relationship Types

#### 1. Users to Orders (1:Many)
- One user can have multiple orders
- Enforced by: `user_id` in orders table
- Cascade: Delete user → Delete their orders

#### 2. Users to Cart (1:Many)
- One user can have multiple cart items
- Enforced by: `user_id` in cart table
- Cascade: Delete user → Clear their cart

#### 3. Products to Cart (1:Many)
- One product can be in multiple users' carts
- Enforced by: `product_id` in cart table
- Cascade: Delete product → Remove from all carts

#### 4. Orders to OrderItems (1:Many)
- One order has multiple items
- Enforced by: `order_id` in order_items table
- Cascade: Delete order → Delete its items

#### 5. Products to OrderItems (1:Many)
- One product can be ordered multiple times
- Enforced by: `product_id` in order_items table
- Restrict: Cannot delete product if in any order

---

## 🔐 Integrity Constraints

### Primary Keys
- Ensure uniqueness for each table
- Auto-increment for easy ID generation
- Used in all foreign key relationships

### Unique Constraints
```sql
-- Users: Email must be unique (no duplicate emails)
email VARCHAR(100) NOT NULL UNIQUE

-- Cart: User can have only one cart item per product
UNIQUE KEY unique_cart_item (user_id, product_id)
```

### Foreign Keys
- Maintain referential integrity
- Prevent orphaned records
- Enforce relationship consistency

### Default Values
```sql
-- Cart quantity defaults to 1
quantity INT NOT NULL DEFAULT 1

-- User role defaults to 'user' (not admin)
role ENUM('user', 'admin') DEFAULT 'user'

-- Order status defaults to 'Pending'
status ENUM(...) DEFAULT 'Pending'

-- Stock defaults to 0 (out of stock)
stock INT NOT NULL DEFAULT 0
```

---

## 📊 Data Types Explanation

### INT
- Whole number from -2,147,483,648 to 2,147,483,647
- Used for: IDs, quantities, stock
- Storage: 4 bytes

### VARCHAR(n)
- Variable-length string, maximum n characters
- Used for: name, email, product description
- Storage: 1 byte per character + overhead

### TEXT
- Large variable-length string (up to 65,535 chars)
- Used for: product descriptions
- Storage: 1 byte per character + overhead

### DECIMAL(10,2)
- Fixed-point decimal number
- 10 total digits, 2 after decimal point
- Range: -99,999,999.99 to 99,999,999.99
- Used for: prices, amounts
- Accurate for financial calculations

### ENUM
- Enumeration (one value from fixed list)
- Used for: role (user/admin), status (Pending/Processing/etc)
- Efficient storage: 1-2 bytes

### TIMESTAMP
- Date and time: YYYY-MM-DD HH:MM:SS
- DEFAULT CURRENT_TIMESTAMP: Auto-fills with current time
- Used for: created_at, updated_at
- Automatic update with ON UPDATE

---

## 🔄 Data Flow Examples

### Example 1: User Registration
```
User Registration Form
         ↓
  Java validation
         ↓
  Password hashing (SHA-256)
         ↓
  INSERT INTO users...
         ↓
  ✓ Record created in USERS table
```

### Example 2: Adding Item to Cart
```
View Products → Select Product → Enter Quantity
         ↓
  Validate product exists & has stock
         ↓
  Check if item already in cart
         ↓
  IF EXISTS: UPDATE quantity
  ELSE: INSERT new cart item
         ↓
  ✓ Item in CART table
```

### Example 3: Placing Order
```
View Cart
         ↓
  Calculate total from CART + PRODUCTS join
         ↓
  INSERT INTO orders...
         ↓
  Get generated order_id
         ↓
  FOR EACH cart_item:
    INSERT INTO order_items...
    UPDATE products SET stock = stock - quantity
         ↓
  DELETE FROM cart WHERE user_id = ...
         ↓
  ✓ Order created (ORDERS + ORDER_ITEMS)
  ✓ Stock updated (PRODUCTS)
  ✓ Cart cleared (CART)
```

### Example 4: Order History
```
SELECT * FROM orders 
         ↓
  FOR EACH order:
    SELECT * FROM order_items 
           ↓
    SELECT * FROM products (for name, price)
           ↓
    Display order details
```

---

## 📈 Performance Optimization

### Indexes Created
1. **idx_user_email** - Fast login by email
2. **idx_cart_user** - Fast cart retrieval
3. **idx_orders_user** - Fast order history lookup
4. **idx_order_items_order** - Fast item retrieval for order

### Query Optimization Tips
```sql
-- Use EXPLAIN to analyze queries
EXPLAIN SELECT * FROM orders WHERE user_id = 5;

-- Use LIMIT for paging
SELECT * FROM orders LIMIT 10 OFFSET 0;

-- Avoid SELECT * when possible
SELECT id, total_amount, order_date FROM orders;

-- Use INNER JOIN over multiple queries
SELECT o.*, COUNT(oi.id) as item_count
FROM orders o
LEFT JOIN order_items oi ON o.id = oi.order_id
GROUP BY o.id;
```

---

## 🧪 Test Queries

### Create Sample Data
```sql
-- Add a test user
INSERT INTO users (name, email, password, role) 
VALUES ('Test User', 'test@test.com', '[HASH]', 'user');

-- Get the inserted user ID
SELECT id FROM users WHERE email = 'test@test.com';

-- Add item to cart (using user_id = 4, product_id = 1)
INSERT INTO cart (user_id, product_id, quantity) 
VALUES (4, 1, 2);

-- View cart
SELECT c.id, c.user_id, c.product_id, p.name, p.price, c.quantity,
       (p.price * c.quantity) as subtotal
FROM cart c
JOIN products p ON c.product_id = p.id
WHERE c.user_id = 4;
```

### Verification Queries
```sql
-- Check all users
SELECT * FROM users;

-- Check all products and stock
SELECT id, name, price, stock FROM products;

-- Check cart items for specific user
SELECT * FROM cart WHERE user_id = 4;

-- Check orders
SELECT * FROM orders ORDER BY order_date DESC;

-- See detailed order information
SELECT o.id, o.user_id, u.name, o.total_amount, o.order_date, o.status,
       COUNT(oi.id) as num_items
FROM orders o
JOIN users u ON o.user_id = u.id
LEFT JOIN order_items oi ON o.id = oi.order_id
GROUP BY o.id;
```

---

## 📝 Important Notes

1. **Password Storage**
   - All passwords stored as SHA-256 hashes
   - Original password never stored
   - Never query password directly

2. **Foreign Key Constraints**
   - Order status is controlled by enum
   - Product references protected
   - User deletion cascades to orders and cart

3. **Decimal Precision**
   - Always use DECIMAL for financial values
   - Prevents floating-point precision errors
   - MySQL stores exact values

4. **Timestamps**
   - `created_at` never changes after insert
   - `updated_at` automatically updates on modification
   - Useful for auditing

5. **Scaling Considerations**
   - Current schema suitable for small to medium applications
   - For large scale: Consider partitioning, archiving old orders
   - Add caching layer (Redis) for frequently accessed data

---

## 🔍 Database Maintenance

### Regular Backups
```bash
# Backup entire database
mysqldump -u root -p ecommerce_db > backup.sql

# Restore from backup
mysql -u root -p ecommerce_db < backup.sql
```

### Database Statistics
```sql
-- Check table sizes
SELECT 
    TABLE_NAME, 
    ROUND(((data_length + index_length) / 1024 / 1024), 2) as 'Size in MB'
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'ecommerce_db';

-- Count records in each table
SELECT 'Users' as table_name, COUNT(*) from users
UNION ALL
SELECT 'Products', COUNT(*) from products
UNION ALL
SELECT 'Orders', COUNT(*) from orders;
```

---

This document provides comprehensive understanding of the database structure. Refer to it while reviewing the Java code to understand how each class interacts with the database.
