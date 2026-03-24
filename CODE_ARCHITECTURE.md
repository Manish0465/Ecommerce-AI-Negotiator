# APPLICATION ARCHITECTURE & CODE GUIDE

Complete guide to understanding the application structure and design.

---

## 🏗️ Architecture Overview

The application follows a **Layered Architecture Pattern** with clear separation of concerns.

```
┌─────────────────────────────────────────────┐
│       PRESENTATION LAYER                    │
│   (Console UI - ECommerceApp.java)         │
│   - Main Menu Display                       │
│   - User Input Handling                     │
│   - Output Formatting                       │
└────────────┬────────────────────────────────┘
             │ Uses
┌────────────▼────────────────────────────────┐
│       BUSINESS LOGIC LAYER                  │
│   (Service Classes)                         │
│   - UserService                             │
│   - ProductService                          │
│   - CartService                             │
│   - OrderService                            │
│   - Input Validation                        │
│   - Business Rules                          │
└────────────┬────────────────────────────────┘
             │ Uses
┌────────────▼────────────────────────────────┐
│       DATA ACCESS LAYER                     │
│   (DAO Classes)                             │
│   - UserDAO                                 │
│   - ProductDAO                              │
│   - CartDAO                                 │
│   - OrderDAO                                │
│   - SQL Queries                             │
└────────────┬────────────────────────────────┘
             │ Uses
┌────────────▼────────────────────────────────┐
│       UTILITIES LAYER                       │
│   - DatabaseConnection                      │
│   - PasswordUtil                            │
│   - ValidationUtil                          │
└────────────┬────────────────────────────────┘
             │ Uses
┌────────────▼────────────────────────────────┐
│       DATABASE LAYER                        │
│   (MySQL Database)                          │
│   - Tables                                  │
│   - Relationships                           │
│   - Stored Data                             │
└─────────────────────────────────────────────┘
```

---

## 📦 Package Structure

### Package: `com.ecommerce.models`

**Purpose:** Data model classes (POJO - Plain Old Java Objects)

**Classes:**
- `User.java` - User entity
- `Product.java` - Product entity
- `CartItem.java` - Shopping cart item
- `Order.java` - Order entity
- `OrderItem.java` - Order line item

**Key Characteristics:**
- Encapsulation using private fields
- Public getters and setters
- `toString()` for debugging
- Simple data containers
- No business logic

**Example Usage:**
```java
// Create a user object
User user = new User("John Doe", "john@example.com", "password123", "user");

// Use getters
String email = user.getEmail();

// Use setters
user.setName("Jane Doe");
```

---

### Package: `com.ecommerce.dao`

**Purpose:** Data Access Objects - Database operations layer

**Classes:**
- `UserDAO.java` - User database operations
- `ProductDAO.java` - Product database operations
- `CartDAO.java` - Shopping cart database operations
- `OrderDAO.java` - Order database operations

**Key Responsibilities:**
- CRUD operations (Create, Read, Update, Delete)
- SQL query execution
- ResultSet processing
- Exception handling

**Pattern: Data Access Object (DAO)**
```
┌─────────── Service Layer ───────────┐
│  (Receives requests)                │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  DAO Layer                          │
│  - Converts to SQL queries          │
│  - Executes queries                 │
│  - Converts ResultSet to objects    │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  Database Layer                     │
│  (Executes SQL, Returns data)       │
└─────────────────────────────────────┘
```

**Example: UserDAO Pattern**
```java
public class UserDAO {
    // SQL queries encapsulated in this class
    public boolean createUser(User user) {
        String query = "INSERT INTO users ...";
        // Execute and return result
    }
    
    public User getUserById(int userId) {
        String query = "SELECT * FROM users WHERE id = ?";
        // Execute and return User object
    }
}
```

**Key Features:**
- Prepared Statements (prevent SQL injection)
- Connection management
- Exception handling
- Result mapping to objects

---

### Package: `com.ecommerce.services`

**Purpose:** Business logic and validation layer

**Classes:**
- `UserService.java` - User operations
- `ProductService.java` - Product operations
- `CartService.java` - Shopping cart operations
- `OrderService.java` - Order operations

**Key Responsibilities:**
- Business logic implementation
- Input validation
- Service coordination
- Error handling
- User feedback

**Pattern: Service Layer**
```
┌───────────────────────────────┐
│  Request from UI              │
│  (user input)                 │
└────────────┬──────────────────┘
             │
┌────────────▼──────────────────┐
│  Service Layer                │
│  1. Validate input            │
│  2. Apply business rules      │
│  3. Call DAO layer            │
│  4. Handle exceptions         │
│  5. Return response to UI     │
└────────────┬──────────────────┘
             │
┌────────────▼──────────────────┐
│  DAO Layer (if needed)        │
│  (Database operations)        │
└───────────────────────────────┘
```

**Example: UserService Pattern**
```java
public class UserService {
    private UserDAO userDAO = new UserDAO();
    
    public boolean registerUser(String email, String password, String role) {
        // Validation
        if (!isValidEmail(email)) {
            System.out.println("Invalid email");
            return false;
        }
        
        // Check if exists
        if (userDAO.emailExists(email)) {
            System.out.println("Email already registered");
            return false;
        }
        
        // Hash password and create
        User user = new User(email, password, role);
        return userDAO.createUser(user);
    }
}
```

**Key Features:**
- Input validation
- Error messages
- Exception handling
- Stateful operations (current user)

---

### Package: `com.ecommerce.util`

**Purpose:** Utility and helper classes

**Classes:**
1. **DatabaseConnection.java**
   - Manages JDBC connections
   - Singleton pattern
   - Connection pooling basics
   - Database testing

2. **PasswordUtil.java**
   - SHA-256 password hashing
   - Password verification
   - Password strength validation

3. **ValidationUtil.java**
   - Email format validation
   - Name validation
   - Numeric validation
   - Regex pattern matching

**Design Pattern: Utility Classes**
```
Static Methods
    ↓
No instances needed
    ↓
Direct method calls: ValidationUtil.isValidEmail(email)
```

---

### Main Class: `ECommerceApp.java`

**Purpose:** Entry point and UI controller

**Key Methods:**
- `main(String[] args)` - Application entry point
- `showMainMenu()` - Main menu loop
- `handleRegistration()` - Registration process
- `handleLogin()` - Login process
- `showUserMenu()` - User dashboard
- `showAdminMenu()` - Admin dashboard
- Various operation handlers (add product, place order, etc.)

**Flow:**
```
main()
  ↓
showMainMenu()
  ├─ Register → handleRegistration()
  ├─ Login → handleLogin() → showUserMenu() or showAdminMenu()
  ├─ View Products → viewProductsGuest()
  └─ Exit → Cleanup and terminate

showUserMenu()
  ├─ View Products
  ├─ Add to Cart
  ├─ View Cart
  ├─ Place Order
  └─ View Order History

showAdminMenu()
  ├─ Add Product
  ├─ Update Product
  ├─ Delete Product
  ├─ View Orders
  └─ Update Order Status
```

---

## 🔄 Data Flow Examples

### Example 1: User Registration Flow

```
User chooses "Register" from Main Menu
                ↓
Scanner reads input (name, email, password)
                ↓
ECommerceApp → UserService.registerUser()
                ↓
UserService validates input
  - Check email format
  - Check password strength
  - Check email not already registered
                ↓
If validation passes:
  - Hash password using PasswordUtil
  - Create User object
  - Call UserDAO.createUser()
                ↓
UserDAO executes INSERT query
                ↓
Database stores new user
                ↓
ResultSet analyzed - Success or Failure
                ↓
Return to UserService
                ↓
Display message to user
```

### Example 2: Place Order Flow

```
User chooses "Place Order"
                ↓
ECommerceApp calls OrderService.placeOrder(userId)
                ↓
OrderService:
  1. Get cart items from CartDAO
  2. Validate cart not empty
  3. Calculate total using CartItem.getSubtotal()
  4. Create Order object
  5. Call OrderDAO.createOrder() - Get order ID
  6. For each cart item:
     - Create OrderItem object
     - Call OrderDAO.addOrderItem()
     - Update product stock in ProductDAO
  7. Clear cart using CartDAO.clearCart()
  8. Return order ID
                ↓
Display order confirmation
```

### Example 3: Login Flow

```
User chooses "Login"
                ↓
ECommerceApp reads email and password
                ↓
UserService.loginUser(email, password)
  1. Validate email format
  2. Call UserDAO.getUserByEmailAndPassword()
                ↓
UserDAO:
  1. Query: SELECT * FROM users WHERE email = ?
  2. Get result, extract password hash
  3. Compare with PasswordUtil.verifyPassword()
  4. Return User object if match
                ↓
If user returned:
  - Store in UserService.currentUser
  - Check role (user or admin)
  - Display appropriate menu
                ↓
If login succeeds → showUserMenu() or showAdminMenu()
If login fails → Display error, return to main menu
```

---

## 🔐 Security Implementation

### 1. Password Hashing

**Before:**
```
plainPassword = "password123"
stored = "password123"  ← INSECURE!
```

**After:**
```
plainPassword = "password123"
          ↓
SHA-256 hashing
          ↓
stored = "X8wEHM0Dq6...Z8Z0"  ← Secure hash

During login:
plainPassword = "password123"
          ↓
SHA-256 hashing
          ↓
"X8wEHM0Dq6...Z8Z0"
          ↓
Compare with stored hash
          ↓
Match → Login success
```

**Code:**
```java
// Hashing
String hashedPassword = PasswordUtil.hashPassword("password123");
// Result: "X8wEHM0Dq6U8I1U9Q1P5W7Y9Z4X6C8V0L2K4J6H8F0D2B4N6M8P0R2T4V6X8Z0"

// Verification
boolean match = PasswordUtil.verifyPassword("password123", storedHash);
// Result: true if matches
```

### 2. SQL Injection Prevention

**Vulnerable Way:**
```java
String query = "SELECT * FROM users WHERE email = '" + email + "'";
// If email = "' OR '1'='1", query becomes: SELECT * FROM users WHERE email = '' OR '1'='1'
// This returns all users!
```

**Safe Way (Used in App):**
```java
String query = "SELECT * FROM users WHERE email = ?";
PreparedStatement ps = conn.prepareStatement(query);
ps.setString(1, email);  // Safely binds email value
ResultSet rs = ps.executeQuery();
// Parameters are treated as data, not code
```

### 3. Input Validation

```java
// Email validation using regex
public static boolean isValidEmail(String email) {
    return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
}

// Password strength validation
public static boolean isPasswordStrong(String password) {
    return password != null && password.length() >= 6;
}

// Name validation
public static boolean isValidName(String name) {
    return name.matches("^[A-Za-z\\s]+$") && name.length() >= 2;
}
```

---

## 💡 Design Patterns Used

### 1. Singleton Pattern

**Location:** `DatabaseConnection.java`

**Purpose:** Ensure only one database connection instance

```java
public class DatabaseConnection {
    private static Connection connection = null;
    
    private DatabaseConnection() {
        // Private constructor - can't instantiate
    }
    
    public static Connection getConnection() {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(...);
        }
        return connection;
    }
}

// Usage
Connection conn = DatabaseConnection.getConnection();  // Always same instance
```

### 2. Data Access Object (DAO) Pattern

**Location:** `com.ecommerce.dao` package

**Purpose:** Isolate database operations from business logic

```java
// Service doesn't need to know about SQL
public class UserService {
    private UserDAO userDAO = new UserDAO();
    
    public boolean registerUser(User user) {
        return userDAO.createUser(user);  // DAO handles SQL
    }
}

// DAO encapsulates all database details
public class UserDAO {
    public boolean createUser(User user) {
        String query = "INSERT INTO users ...";
        // Execute SQL here
    }
}
```

### 3. Separation of Concerns

- **Models:** Data representation only
- **DAO:** Database interaction only
- **Service:** Business logic only
- **UI:** User interaction only

### 4. Entity-Relationship Pattern

```java
Order
 ├─ User → userId (Foreign Key)
 └─ OrderItems (One-to-Many)
    ├─ ProductId → Product (Foreign Key)
    └─ Quantity, Price

Cart
 ├─ User → userId (Foreign Key)
 └─ Product → productId (Foreign Key)
```

---

## 🧪 Code Quality Features

### 1. Exception Handling

```java
try {
    Connection conn = DatabaseConnection.getConnection();
    PreparedStatement ps = conn.prepareStatement(query);
    // Execute query
} catch (SQLException e) {
    System.out.println("❌ Error: " + e.getMessage());
    e.printStackTrace();  // Log for debugging
    return false;
} 
```

### 2. Input Validation

```java
// Before processing, validate
if (!ValidationUtil.isValidEmail(email)) {
    System.out.println("❌ Invalid email format.");
    return false;
}

if (!ValidationUtil.isPasswordStrong(password)) {
    System.out.println("❌ Password too weak. Minimum 6 characters required.");
    return false;
}

// Only proceed if all validations pass
```

### 3. User Feedback

```java
// Success messages
System.out.println("✓ Registration successful!");
System.out.println("✓ Product added successfully!");

// Error messages  
System.out.println("❌ Email already registered.");
System.out.println("❌ Insufficient stock.");

// Information messages
System.out.println("ℹ️ No products available.");
```

### 4. Error Handling Strategy

```
User Error
    ↓
Validate Input
    ↓
Show User-Friendly Message
    ↓
Return Error Status
    ↓
No exception - graceful failure

System Error
    ↓
Catch SQLException
    ↓
Log Error Details
    ↓
Show Generic Message to User
    ↓
Continue Execution
```

---

## 🎯 OOP Principles Implementation

### 1. Encapsulation

```java
public class Product {
    private int id;           // Private
    private String name;      // Private
    private double price;     // Private
    private int stock;        // Private
    
    public int getId() { }           // Public getter
    public String getName() { }      // Public getter
    
    public void setPrice(double price) {
        if (price < 0) {             // Validation in setter
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }
}
```

### 2. Inheritance (Not heavily used, but possible)

```java
// Could extend to create hierarchy
public abstract class Entity {
    protected int id;
    protected LocalDateTime createdAt;
    
    public abstract String getDisplayName();
}

public class User extends Entity {
    @Override
    public String getDisplayName() {
        return name;
    }
}
```

### 3. Polymorphism

```java
// Service methods can accept different types
public void displayEntity(Object entity) {
    if (entity instanceof Product) {
        System.out.println(((Product)entity).getName());
    } else if (entity instanceof Order) {
        System.out.println(((Order)entity).getId());
    }
}
```

### 4. Abstraction

```java
// Users work with high-level services
// Implementation details hidden in DAO
public class UserService {
    private UserDAO userDAO;  // Implementation abstracted
    
    public boolean registerUser(String email, String password) {
        // User doesn't know how data is stored
        return userDAO.createUser(new User(...));
    }
}
```

---

## 📊 Class Interactions

```
ECommerceApp (UI)
    │
    ├─→ UserService → UserDAO → Database
    │
    ├─→ ProductService → ProductDAO → Database
    │
    ├─→ CartService → CartDAO → Database
    │
    ├─→ OrderService → OrderDAO → Database
    │
    └─→ ValidationUtil, PasswordUtil, DatabaseConnection (Utilities)

All communicate through Models (User, Product, Order, etc.)
```

---

## 🚀 Extending the Application

### Adding New Feature: Product Reviews

**Step 1: Create Model**
```java
public class Review {
    private int id;
    private int productId;
    private int userId;
    private int rating; // 1-5
    private String comment;
    
    // Getters and setters...
}
```

**Step 2: Create DAO**
```java
public class ReviewDAO {
    public boolean createReview(Review review) { }
    public List<Review> getProductReviews(int productId) { }
    // ...
}
```

**Step 3: Create Service**
```java
public class ReviewService {
    private ReviewDAO reviewDAO = new ReviewDAO();
    
    public boolean addReview(Review review) {
        // Validate rating between 1-5
        if (review.getRating() < 1 || review.getRating() > 5) {
            return false;
        }
        return reviewDAO.createReview(review);
    }
}
```

**Step 4: Add to Database Schema**
```sql
CREATE TABLE reviews (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    user_id INT NOT NULL,
    rating INT,
    comment TEXT,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

**Step 5: Integrate in UI**
```java
public void showProductReviews(int productId) {
    ReviewService reviewService = new ReviewService();
    List<Review> reviews = reviewService.getProductReviews(productId);
    // Display reviews
}
```

---

## 🎓 Learning Path

**Beginner Level:**
1. Understand models (User, Product)
2. Review Model classes and getters/setters
3. Trace through simple operations (view products, register)

**Intermediate Level:**
1. Study DAO pattern (UserDAO, ProductDAO)
2. Understand SQL queries and ResultSet processing
3. Learn JDBC operations

**Advanced Level:**
1. Implement service layer logic
2. Add business rules and validation
3. Create new features (reviews, ratings, etc.)

---

This guide provides comprehensive understanding of the application architecture. Refer to specific classes as you explore the codebase.

