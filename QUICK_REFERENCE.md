# QUICK REFERENCE GUIDE

Fast reference for common tasks and commands.

---

## ⚡ QUICK START (Copy-Paste Ready)

### Step 1: Database Setup
```sql
-- Open MySQL Command Prompt
mysql -u root -p

-- Enter password (if you have one)

-- Run setup script
SOURCE D:/Manish/Collage\ project/E-commerce\ Mini\ Application/database/ecommerce_db_setup.sql;

-- Verify
SHOW DATABASES;
USE ecommerce_db;
SHOW TABLES;
```

### Step 2: Edit Configuration
**File:** `src/com/ecommerce/util/DatabaseConnection.java`

```java
// Update these lines (around line 10-13):
private static final String DB_URL = "jdbc:mysql://localhost:3306/ecommerce_db";
private static final String DB_USERNAME = "root";        // Your MySQL username
private static final String DB_PASSWORD = "";            // Your MySQL password
```

### Step 3: Compile
```bash
cd D:\Manish\Collage project\E-commerce Mini Application
mkdir bin
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar src/com/ecommerce/**/*.java src/com/ecommerce/*.java
```

### Step 4: Run
```bash
java -cp bin;lib/mysql-connector-java-8.0.33.jar com.ecommerce.ECommerceApp
```

---

## 🔑 DEFAULT TEST ACCOUNTS

| Role | Email | Password | Notes |
|------|-------|----------|-------|
| Admin | admin@ecommerce.com | password123 | Full access |
| User | john@example.com | password123 | Shopping only |
| User | jane@example.com | password123 | Shopping only |

---

## 📋 DATABASE INFO

**Database Name:** ecommerce_db

**Tables:**
| Table | Records | Purpose |
|-------|---------|---------|
| users | 3 | User accounts |
| products | 12 | Product catalog |
| cart | 0 | Shopping carts |
| orders | 0 | Order history |
| order_items | 0 | Order details |

---

## 📁 FILE LOCATIONS

| File | Purpose |
|------|---------|
| database/ecommerce_db_setup.sql | Database creation |
| src/com/ecommerce/models/*.java | Data classes |
| src/com/ecommerce/dao/*.java | Database operations |
| src/com/ecommerce/services/*.java | Business logic |
| src/com/ecommerce/util/*.java | Utilities |
| src/com/ecommerce/ECommerceApp.java | Main application |
| lib/mysql-connector-java-*.jar | JDBC driver |

---

## 🔄 USER WORKFLOWS

### User Registration
```
Main Menu (3) → Enter: Name, Email, Password → Validation → Success/Failure
```

### User Login
```
Main Menu (2) → Enter: Email, Password → Validation → User Menu/Admin Menu
```

### Shopping
```
User Menu (1) → View Products → (2) Add to Cart → (3) View Cart → (6) Place Order
```

### Admin Operations
```
Admin Menu (1) → View Products → (2) Add Product → (3) Update → (4) Delete
```

---

## 🖥️ MENU OPTIONS

### Main Menu
| # | Option |
|---|--------|
| 1 | Register |
| 2 | Login |
| 3 | View Products (Guest) |
| 4 | Exit |

### User Menu
| # | Option |
|---|--------|
| 1 | View Products |
| 2 | Add Item to Cart |
| 3 | View Cart |
| 4 | Update Cart Quantity |
| 5 | Remove Item from Cart |
| 6 | Place Order |
| 7 | View Order History |
| 8 | View Order Details |
| 9 | Logout |

### Admin Menu
| # | Option |
|---|--------|
| 1 | View All Products |
| 2 | Add Product |
| 3 | Update Product |
| 4 | Delete Product |
| 5 | View All Orders |
| 6 | Update Order Status |
| 7 | Logout |

---

## 🧪 QUICK TESTS

### Test User Registration
1. Choose option 1 (Register)
2. Enter:
   - Name: Test User
   - Email: test@test.com
   - Password: test123456
3. Should see: ✓ Registration successful!

### Test User Login
1. Choose option 2 (Login)
2. Enter:
   - Email: john@example.com
   - Password: password123
3. Should see: ✓ Login successful!

### Test Add to Cart
1. Login as john@example.com
2. Choose option 2 (Add to Cart)
3. Enter:
   - Product ID: 1
   - Quantity: 2
4. Should see: ✓ Item added to cart!

### Test Place Order
1. Add items to cart (from above)
2. Choose option 6 (Place Order)
3. Confirm: yes
4. Should see: ✓ Order placed successfully!

---

## 🔧 COMMON ISSUES & SOLUTIONS

### Issue: Can't connect to database
**Solution:**
1. Start MySQL service
2. Check credentials in DatabaseConnection.java
3. Verify database exists: `SHOW DATABASES;`
4. Run setup script again

### Issue: JDBC driver not found
**Solution:**
1. Download mysql-connector-java-8.0.33.jar
2. Place in lib/ folder
3. Use -cp flag when compiling
4. Restart IDE if using IDE

### Issue: Compilation errors
**Solution:**
1. Check all files in src/
2. Verify package structure is correct
3. Use full -cp command with all jars
4. Clean bin folder and recompile

### Issue: Application won't start
**Solution:**
1. Check database is running
2. Verify database connection details
3. Check Java version: `java -version`
4. Verify all .class files in bin/

---

## 📊 SQL QUICK QUERIES

### Check Users
```sql
SELECT id, name, email, role FROM users;
```

### Check Products
```sql
SELECT id, name, price, stock FROM products;
```

### Check Orders
```sql
SELECT o.id, o.user_id, u.name, o.total_amount, o.order_date, o.status 
FROM orders o 
JOIN users u ON o.user_id = u.id;
```

### Check Order Items
```sql
SELECT oi.id, oi.order_id, p.name, oi.quantity 
FROM order_items oi 
JOIN products p ON oi.product_id = p.id;
```

### Reset Database
```sql
DROP DATABASE ecommerce_db;
SOURCE database/ecommerce_db_setup.sql;
```

---

## 🎯 KEYBOARD SHORTCUTS

| Action | Shortcut |
|--------|----------|
| Build Project | Ctrl+B (Eclipse) / Ctrl+F9 (IntelliJ) |
| Run App | Ctrl+F11 (Eclipse) / Shift+F10 (IntelliJ) |
| Find | Ctrl+F |
| Replace | Ctrl+H |
| Comment | Ctrl+/ |

---

## 📞 KEY NUMBERS

| Item | Count |
|------|-------|
| Java Classes | 17 |
| Database Tables | 5 |
| Service Classes | 4 |
| DAO Classes | 4 |
| Model Classes | 5 |
| Utility Classes | 3 |
| Menu Options | 19 |
| Default Products | 12 |
| Default Users | 3 |

---

## 🔗 IMPORTANT FILES

| File | Lines | Type |
|------|-------|------|
| ECommerceApp.java | 600 | Main |
| OrderDAO.java | 220 | DAO |
| ProductDAO.java | 180 | DAO |
| OrderService.java | 210 | Service |
| DatabaseConnection.java | 80 | Util |
| ecommerce_db_setup.sql | 150 | SQL |

---

## 🎓 KEY CONCEPTS

**OOP:**
- Encapsulation (private fields, public getters/setters)
- Inheritance (potential for extending classes)
- Polymorphism (method overloading)
- Abstraction (hiding implementation details)

**JDBC:**
- Connection management
- Prepared Statements
- ResultSet processing
- Exception handling

**Database:**
- Relationships (1:Many, Many:Many)
- Foreign keys
- Constraints
- Indexes

**Design Patterns:**
- Singleton (DatabaseConnection)
- DAO Pattern (Data Access)
- Service Layer (Business Logic)
- Layered Architecture

---

## ⚙️ COMPILATION VARIATIONS

### Compile Individual Packages
```bash
# Models
javac -d bin src/com/ecommerce/models/*.java

# Utilities
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar:bin src/com/ecommerce/util/*.java

# DAO
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar:bin src/com/ecommerce/dao/*.java

# Services
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar:bin src/com/ecommerce/services/*.java

# Main
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar:bin src/com/ecommerce/*.java
```

### Compile with Warnings Suppressed
```bash
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar -nowarn src/com/ecommerce/**/*.java
```

---

## 📱 TERMINAL COMMANDS

### Windows Command Prompt
```bash
cd D:\Manish\Collage project\E-commerce Mini Application
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar src/com/ecommerce/**/*.java src/com/ecommerce/*.java
java -cp bin;lib/mysql-connector-java-8.0.33.jar com.ecommerce.ECommerceApp
```

### Linux/Mac Terminal
```bash
cd ~/E-commerce\ Mini\ Application
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar src/com/ecommerce/**/*.java src/com/ecommerce/*.java
java -cp bin:lib/mysql-connector-java-8.0.33.jar com.ecommerce.ECommerceApp
```

### PowerShell (Windows)
```powershell
cd "D:\Manish\Collage project\E-commerce Mini Application"
$classpath = "lib/mysql-connector-java-8.0.33.jar"
javac -d bin -cp $classpath src/com/ecommerce/**/*.java src/com/ecommerce/*.java
java -cp "bin;$classpath" com.ecommerce.ECommerceApp
```

---

## 🎯 GETTING HELP

**Refer to these documents:**
1. **README.md** - Project overview
2. **SETUP_GUIDE.md** - Installation steps
3. **DATABASE_SCHEMA.md** - Database structure
4. **COMPILATION_GUIDE.md** - Build instructions
5. **CODE_ARCHITECTURE.md** - Code structure
6. **PROJECT_SUMMARY.md** - Complete checklist

**For Specific Issues:**
- Compilation: See COMPILATION_GUIDE.md
- Database: See DATABASE_SCHEMA.md
- Setup: See SETUP_GUIDE.md
- Code: See CODE_ARCHITECTURE.md

---

## 🏁 CHECKLIST BEFORE RUNNING

- [ ] MySQL Server running
- [ ] Database created from SQL script
- [ ] DatabaseConnection.java configured
- [ ] JDBC driver in lib/ folder
- [ ] All Java files in correct folders
- [ ] bin/ folder created
- [ ] Compilation successful (no errors)
- [ ] All .class files in bin/

---

**Save this file and come back to it for quick reference!**
