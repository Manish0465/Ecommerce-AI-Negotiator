# CONFIGURATION FILE TEMPLATE

Template for configuring the E-Commerce application.

---

## 📝 DatabaseConnection.java - Configuration

**File Location:** `src/com/ecommerce/util/DatabaseConnection.java`

### Default Configuration
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/ecommerce_db";
private static final String DB_USERNAME = "root";
private static final String DB_PASSWORD = "";
private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
```

### Configuration Examples

#### Example 1: Standard Setup (Default)
```java
// Default localhost MySQL with no password
private static final String DB_URL = "jdbc:mysql://localhost:3306/ecommerce_db";
private static final String DB_USERNAME = "root";
private static final String DB_PASSWORD = "";
private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
```

#### Example 2: With Password
```java
// If MySQL has password
private static final String DB_URL = "jdbc:mysql://localhost:3306/ecommerce_db";
private static final String DB_USERNAME = "root";
private static final String DB_PASSWORD = "your_password_here";
private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
```

#### Example 3: Different Port
```java
// If MySQL running on different port (e.g., 3307)
private static final String DB_URL = "jdbc:mysql://localhost:3307/ecommerce_db";
private static final String DB_USERNAME = "root";
private static final String DB_PASSWORD = "";
private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
```

#### Example 4: Remote Server
```java
// If connecting to remote MySQL server
private static final String DB_URL = "jdbc:mysql://192.168.1.100:3306/ecommerce_db";
private static final String DB_USERNAME = "ecommerce_user";
private static final String DB_PASSWORD = "secure_password";
private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
```

#### Example 5: Different Database Name
```java
// If using different database name
private static final String DB_URL = "jdbc:mysql://localhost:3306/my_ecommerce";
private static final String DB_USERNAME = "root";
private static final String DB_PASSWORD = "";
private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
```

---

## 🔐 Secure Configuration (Environment Variables)

### For Production Use

Instead of hardcoding credentials, use environment variables:

```java
public class DatabaseConnection {
    private static final String DB_URL = System.getenv("DB_URL") != null 
        ? System.getenv("DB_URL") 
        : "jdbc:mysql://localhost:3306/ecommerce_db";
    
    private static final String DB_USERNAME = System.getenv("DB_USERNAME") != null 
        ? System.getenv("DB_USERNAME") 
        : "root";
    
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD") != null 
        ? System.getenv("DB_PASSWORD") 
        : "";
}
```

### Set Environment Variables

**Windows Command Prompt:**
```batch
set DB_URL=jdbc:mysql://localhost:3306/ecommerce_db
set DB_USERNAME=root
set DB_PASSWORD=
java -cp bin;lib/mysql-connector-java-8.0.33.jar com.ecommerce.ECommerceApp
```

**Windows PowerShell:**
```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/ecommerce_db"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = ""
java -cp "bin;lib/mysql-connector-java-8.0.33.jar" com.ecommerce.ECommerceApp
```

**Linux/Mac:**
```bash
export DB_URL="jdbc:mysql://localhost:3306/ecommerce_db"
export DB_USERNAME="root"
export DB_PASSWORD=""
java -cp bin:lib/mysql-connector-java-8.0.33.jar com.ecommerce.ECommerceApp
```

---

## 🗂️ build.properties (Optional Build Configuration)

Create `build.properties` in project root:

```properties
# MySQL Database Configuration
db.hostname=localhost
db.port=3306
db.name=ecommerce_db
db.username=root
db.password=

# Java Compiler Configuration
java.source.version=1.8
java.target.version=1.8
build.dir=bin
src.dir=src
lib.dir=lib

# Application Configuration
app.name=E-Commerce Application
app.version=1.0.0
app.main.class=com.ecommerce.ECommerceApp
```

---

## 🛠️ System Requirements Configuration

### Minimum Requirements
```
Java: JDK 8+
MySQL: 5.7+
RAM: 512 MB
Disk: 100 MB
```

### Recommended Requirements
```
Java: JDK 11+
MySQL: 8.0+
RAM: 1 GB
Disk: 500 MB
```

---

## 🗄️ Database Configuration Verification

### Check MySQL Installation
```bash
# Windows
mysql --version

# Linux/Mac
which mysql
mysql --version
```

### Test MySQL Connection
```bash
# Connect to MySQL
mysql -u root -p

# Inside MySQL:
SHOW VARIABLES LIKE 'port';
SHOW VARIABLES LIKE 'version';
```

### Verify Database
```sql
-- Check if database exists
SHOW DATABASES LIKE 'ecommerce_db';

-- Check tables
USE ecommerce_db;
SHOW TABLES;

-- Check data
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM products;
```

---

## 📊 JDBC Driver Configuration

### Download Location
- **Official:** https://dev.mysql.com/downloads/connector/j/
- **Maven:** Add to pom.xml if using Maven

### Supported Versions
```
MySQL Connector/J 8.0.x (Recommended)
MySQL Connector/J 5.1.x (Legacy)
```

### Driver Class Names
```
MySQL 8.0+: com.mysql.cj.jdbc.Driver
MySQL 5.1: com.mysql.jdbc.Driver
```

### Classpath Configuration

**Windows:**
```
bin;lib/mysql-connector-java-8.0.33.jar
```

**Linux/Mac:**
```
bin:lib/mysql-connector-java-8.0.33.jar
```

---

## 🎯 Application Configuration

### Input Validation Settings (ValidationUtil.java)

```java
// Email pattern (modify to be more/less strict)
public static boolean isValidEmail(String email) {
    return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
}

// Minimum password length (currently 6 characters)
public static boolean isPasswordStrong(String password) {
    return password != null && password.length() >= 6;
    // Change >= 6 to >= 8 for stronger passwords
}

// Name validation (currently letters and spaces only)
public static boolean isValidName(String name) {
    return name.matches("^[A-Za-z\\s]+$") && name.length() >= 2;
    // Add more characters if needed
}
```

### Password Hashing Algorithm (PasswordUtil.java)

```java
// Current: SHA-256
MessageDigest md = MessageDigest.getInstance("SHA-256");

// Alternative options:
// MessageDigest.getInstance("SHA-512")  // Stronger
// MessageDigest.getInstance("MD5")      // Legacy (not recommended)
```

---

## 🌐 Network Configuration

### For Remote Database
```java
// If database on different machine
private static final String DB_URL = "jdbc:mysql://192.168.1.50:3306/ecommerce_db";

// Connection timeout (add if needed)
private static final String DB_URL = 
    "jdbc:mysql://localhost:3306/ecommerce_db?connectTimeout=10000&socketTimeout=20000";
```

### Connection Pool Settings (Advanced)
```java
// For production, add connection pooling:
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:mysql://localhost:3306/ecommerce_db");
config.setUsername("root");
config.setPassword("");
config.setMaximumPoolSize(10);  // Max connections
config.setMinimumIdle(2);       // Min connections
```

---

## 📝 Logging Configuration (Optional)

Add logging to track application behavior:

```java
// In DatabaseConnection.java
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger logger = Logger.getLogger(
        DatabaseConnection.class.getName());
    
    public static Connection getConnection() {
        try {
            logger.info("Attempting database connection...");
            Connection conn = DriverManager.getConnection(...);
            logger.info("Database connected successfully!");
            return conn;
        } catch (SQLException e) {
            logger.severe("Database connection failed: " + e.getMessage());
            return null;
        }
    }
}
```

---

## 🔄 Backup Configuration

### Regular Backup Script

**Windows (backup.bat):**
```batch
@echo off
REM Backup MySQL database
set BACKUP_DIR=backups
if not exist %BACKUP_DIR% mkdir %BACKUP_DIR%

REM Current date/time
for /f "tokens=2-4 delims=/ " %%a in ('date /t') do (set mydate=%%c%%a%%b)
for /f "tokens=1-2 delims=/:" %%a in ('time /t') do (set mytime=%%a%%b)

REM Create backup
mysqldump -u root -p ecommerce_db > %BACKUP_DIR%\backup_%mydate%_%mytime%.sql

echo Backup completed: %BACKUP_DIR%\backup_%mydate%_%mytime%.sql
```

**Linux/Mac (backup.sh):**
```bash
#!/bin/bash

BACKUP_DIR="backups"
mkdir -p $BACKUP_DIR

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="$BACKUP_DIR/backup_$TIMESTAMP.sql"

mysqldump -u root -p ecommerce_db > $BACKUP_FILE

echo "Backup completed: $BACKUP_FILE"
```

---

## ✅ Configuration Checklist

Before running application, verify:

**Database Configuration:**
- [ ] MySQL Server installed
- [ ] MySQL Server running
- [ ] Database 'ecommerce_db' created
- [ ] All tables created
- [ ] Sample data inserted

**Java Configuration:**
- [ ] JDK 8+ installed
- [ ] JAVA_HOME set correctly
- [ ] Classpath includes JDBC driver
- [ ] All source files present

**Application Configuration:**
- [ ] DatabaseConnection.java credentials set
- [ ] JDBC driver version compatible
- [ ] Build output directory (bin) created
- [ ] Compilation successful

---

## 🐛 Configuration Troubleshooting

### Issue: Connection Timeout
```java
// Add timeout parameters
private static final String DB_URL = 
    "jdbc:mysql://localhost:3306/ecommerce_db?connectTimeout=30000";
```

### Issue: SSL Warning
```java
// Disable SSL (development only)
private static final String DB_URL = 
    "jdbc:mysql://localhost:3306/ecommerce_db?useSSL=false";
```

### Issue: Character Encoding
```java
// Set UTF-8 encoding
private static final String DB_URL = 
    "jdbc:mysql://localhost:3306/ecommerce_db?characterEncoding=utf8mb4";
```

---

## 🎯 Quick Configuration Template

```java
// MINIMAL CONFIGURATION (Copy-Paste)
package com.ecommerce.util;
import java.sql.*;

public class DatabaseConnection {
    // CHANGE THESE THREE LINES ONLY:
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ecommerce_db";
    private static final String DB_USERNAME = "root";           // Your username
    private static final String DB_PASSWORD = "";               // Your password
    
    // Don't change below this line
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static Connection connection = null;
    
    private DatabaseConnection() {}
    
    public static Connection getConnection() {
        try {
            Class.forName(DRIVER);
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                System.out.println("✓ Database connected successfully!");
            }
            return connection;
        } catch (Exception e) {
            System.out.println("❌ Database connection failed!");
            e.printStackTrace();
            return null;
        }
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

---

**For more help, refer to other documentation files!**
