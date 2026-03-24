# SETUP GUIDE - E-Commerce Mini Application

Complete step-by-step instructions to set up and run the E-commerce application.

---

## 📋 Prerequisites

Before starting, ensure you have installed:

1. **Java Development Kit (JDK)**
   - Version: JDK 8 or higher
   - Download: https://www.oracle.com/java/technologies/downloads/
   - Verify: `java -version` (in command prompt)

2. **MySQL Server**
   - Version: MySQL 5.7 or higher
   - Download: https://dev.mysql.com/downloads/mysql/
   - Service Status: MySQL should be running

3. **MySQL JDBC Driver**
   - File: mysql-connector-java-8.0.33.jar
   - Download: https://dev.mysql.com/downloads/connector/j/

---

## 🔧 Step 1: Database Setup

### Option A: Using MySQL Command Prompt (Windows)

1. **Open MySQL Command Prompt**
   - Search "MySQL Command Line Client" in Windows
   - Enter your MySQL password when prompted

2. **Execute SQL Script**
   ```sql
   SOURCE D:/Manish/Collage\ project/E-commerce\ Mini\ Application/database/ecommerce_db_setup.sql;
   ```

3. **Verify Installation**
   ```sql
   SHOW DATABASES;
   USE ecommerce_db;
   SHOW TABLES;
   ```

### Option B: Using MySQL Workbench

1. **Open MySQL Workbench**
2. **Create Connection** (if not exists)
   - Host: localhost
   - Port: 3306
   - User: root
   - Password: [your password]

3. **Open and Execute SQL Script**
   - File → Open SQL Script
   - Select `ecommerce_db_setup.sql`
   - Click Execute (⚡ icon)

4. **Verify Tables Created**
   ```sql
   SELECT * FROM users;
   SELECT * FROM products;
   ```

---

## 📝 Step 2: Update Database Configuration

Edit `src/com/ecommerce/util/DatabaseConnection.java`

Find these lines (around line 10-13):
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/ecommerce_db";
private static final String DB_USERNAME = "root";
private static final String DB_PASSWORD = "";
private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
```

**Update with your credentials:**
- `DB_URL`: Change if MySQL is on different host/port
- `DB_USERNAME`: Your MySQL username (usually "root")
- `DB_PASSWORD`: Your MySQL password (leave empty if no password)

---

## 📦 Step 3: Add MySQL JDBC Driver

### A. Locate Driver Jar
1. Download `mysql-connector-java-8.0.33.jar` from MySQL website
2. Place it in `lib` folder:
   ```
   E-commerce Mini Application/lib/mysql-connector-java-8.0.33.jar
   ```

### B. Add to IDE Classpath

**For Eclipse:**
1. Right-click Project → Build Path → Configure Build Path
2. Go to Libraries tab
3. Click "Add External JARs"
4. Select mysql-connector-java-8.0.33.jar
5. Click Apply and Close

**For IntelliJ IDEA:**
1. File → Project Structure
2. Go to Libraries
3. Click "+" → Java
4. Select mysql-connector-java-8.0.33.jar
5. Click OK

**For VS Code:**
1. Add to `.classpath` or `settings.json`
2. Or compile with: `-cp lib/mysql-connector-java-8.0.33.jar`

---

## 🎯 Step 4: Compile the Application

### Using Command Prompt (Windows)

1. **Open Command Prompt** at project root
2. **Create bin folder**
   ```bash
   mkdir bin
   ```

3. **Compile all Java files**
   ```bash
   javac -d bin -cp lib/mysql-connector-java-8.0.33.jar src/com/ecommerce/**/*.java src/com/ecommerce/*.java
   ```

### Using IDE

**Eclipse:**
- Right-click Project → Build Project (Ctrl+B)

**IntelliJ:**
- Build → Build Project (Ctrl+F9)

---

## ▶️ Step 5: Run the Application

### Using Command Prompt

```bash
java -cp bin;lib/mysql-connector-java-8.0.33.jar com.ecommerce.ECommerceApp
```

**Note:** Use `;` on Windows, use `:` on Linux/Mac

### Using IDE

**Eclipse:**
- Right-click ECommerceApp.java → Run As → Java Application

**IntelliJ:**
- Main Menu → Run → Run 'ECommerceApp'

---

## 📸 Expected Output

When you run the application, you should see:

```
================================================================================
                    WELCOME TO E-COMMERCE APPLICATION
================================================================================

✓ Database connected successfully!

╔════════════════════════════════════════╗
║         MAIN MENU                      ║
╠════════════════════════════════════════╣
║ 1. Register                            ║
║ 2. Login                               ║
║ 3. View Products (without login)       ║
║ 4. Exit                                ║
╚════════════════════════════════════════╝
Enter your choice:
```

---

## 🧪 Testing the Application

### Test 1: View Products (Guest)
1. Choose option 3 from main menu
2. See list of available products

### Test 2: User Registration
1. Choose option 1
2. Enter: Name, Email (new), Password (min 6 chars)
3. Should see "✓ Registration successful!"

### Test 3: User Login
1. Choose option 2
2. Use credentials:
   - Email: `john@example.com`
   - Password: `password123`
3. Should see "✓ Login successful!"

### Test 4: Shopping (After Login)
1. Choose option 1 - View Products
2. Choose option 2 - Add to Cart
3. Enter Product ID (1-12) and Quantity
4. Choose option 3 - View Cart
5. Should see cart with items and total

### Test 5: Place Order
1. Add items to cart (from Test 4)
2. Choose option 6 - Place Order
3. Confirm order placement
4. Should see "✓ Order placed successfully!"

### Test 6: Admin Login
1. Register as admin or use default:
   - Email: `admin@ecommerce.com`
   - Password: `password123`
2. Should see Admin Menu
3. Can add/update/delete products
4. Can view all orders and update status

---

## ⚠️ Common Issues & Solutions

### Issue 1: "Can't connect to MySQL server on 'localhost'"

**Causes:**
- MySQL is not running
- Wrong password or username
- Database doesn't exist

**Solutions:**
1. Start MySQL Service:
   - Windows: Services app → MySQL80 → Start
   - Or: `mysql --version` to verify installation

2. Check credentials in DatabaseConnection.java

3. Run SQL script again to recreate database

### Issue 2: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"

**Causes:**
- JDBC driver not in classpath
- Wrong driver filename

**Solutions:**
1. Verify jar file exists in `lib` folder
2. Check driver filename matches exactly
3. Add to classpath properly
4. Restart IDE after adding to classpath

### Issue 3: "Table doesn't exist"

**Causes:**
- SQL script not executed
- Database name mismatch
- Wrong database selected

**Solutions:**
1. Re-run SQL script from `database/ecommerce_db_setup.sql`
2. Check database is "ecommerce_db"
3. Verify tables with: `SHOW TABLES;`

### Issue 4: "Access denied for user 'root'@'localhost'"

**Causes:**
- Wrong MySQL password
- User doesn't exist

**Solutions:**
1. Check your MySQL password
2. Update DB_PASSWORD in DatabaseConnection.java
3. Reset MySQL root password if forgotten

### Issue 5: Compilation Errors

**Common Errors:**
- "cannot find symbol" - Check imports, class names
- "unchecked warning" - Ignore or use @SuppressWarnings
- "unreachable code" - Check logic flow

**Solutions:**
1. Clean and rebuild: `javac -clean`
2. Check all Java files are in src folder
3. Use IDE's error highlighting for guidance

---

## 📊 Default Test Data

After database setup, you have:

**Users:**
- Admin: admin@ecommerce.com / password123
- User 1: john@example.com / password123
- User 2: jane@example.com / password123

**Products:**
- 12 sample products (Laptops, Phones, Accessories, etc.)
- Price range: ₹1,000 to ₹75,000
- Varying stock quantities

**Initial Data:**
- Empty shopping carts
- No orders

---

## 🔐 Important Notes

1. **Password Storage**
   - All passwords are hashed using SHA-256
   - Never store plain text passwords
   - Always hash before comparison

2. **Database Backup**
   - Keep backup of ecommerce_db_setup.sql
   - Regular backups of MySQL database recommended

3. **Security**
   - Don't share root password
   - In production, use environment variables for credentials
   - Enable MySQL user privileges system

4. **Performance**
   - Indexes created for frequently searched columns
   - Foreign keys ensure data integrity

---

## 📱 Using the Application

### Main Menu Options
- **1. Register** - Create new account
- **2. Login** - Sign in with credentials
- **3. View Products** - Browse products without login
- **4. Exit** - Close application

### User Menu (After Login)
- **1. View Products** - Browse all products
- **2. Add to Cart** - Add items to shopping cart
- **3. View Cart** - See current cart contents
- **4. Update Quantity** - Modify item quantities
- **5. Remove Item** - Delete items from cart
- **6. Place Order** - Checkout and create order
- **7. Order History** - View past orders
- **8. Order Details** - See specific order details
- **9. Logout** - Sign out

### Admin Menu (After Admin Login)
- **1. View Products** - List all products
- **2. Add Product** - Create new product
- **3. Update Product** - Modify product details
- **4. Delete Product** - Remove product
- **5. View All Orders** - List all customer orders
- **6. Update Status** - Change order status
- **7. Logout** - Sign out

---

## 🎓 Learning Resources

1. **Java Documentation**
   - Official: https://docs.oracle.com/javase/
   
2. **JDBC Documentation**
   - https://docs.oracle.com/javase/tutorial/jdbc/

3. **MySQL Documentation**
   - https://dev.mysql.com/doc/

4. **Design Patterns in Java**
   - https://www.oracle.com/java/technologies/

---

## ✅ Verification Checklist

Before considering setup complete:

- [ ] Java JDK installed and verified
- [ ] MySQL Server running
- [ ] Database created successfully
- [ ] All 5 tables created
- [ ] Sample data inserted
- [ ] JDBC driver downloaded and placed in lib folder
- [ ] DatabaseConnection.java configured with correct credentials
- [ ] Project compiles without errors
- [ ] Application starts successfully
- [ ] Database connection successful message appears
- [ ] Main menu displays correctly

---

## 🎉 You're Ready!

If all steps completed successfully, you can now:
1. Register new user accounts
2. Login and shop
3. Add products to cart
4. Place orders
5. Manage products (as admin)

**Enjoy the application!** 🚀

---

For any issues not covered here, refer to README.md or check error messages carefully.
