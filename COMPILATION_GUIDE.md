# COMPILATION & EXECUTION GUIDE

Quick reference for compiling and running the E-commerce application.

---

## 📦 Pre-requisites Checklist

Before compiling, ensure you have:

- [ ] Java JDK 8+ installed (`java -version` works)
- [ ] MySQL Server running (`mysql -u root` connects)
- [ ] Database created (run `ecommerce_db_setup.sql`)
- [ ] MySQL JDBC driver in `lib/` folder
- [ ] All Java files in correct folder structure

---

## 🔨 Compilation Methods

### Method 1: Using Command Prompt (Recommended)

#### Step 1: Navigate to Project Root
```bash
cd D:\Manish\Collage project\E-commerce Mini Application
```

#### Step 2: Create Output Directory
```bash
mkdir bin
```

#### Step 3: Compile All Files
```bash
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar src/com/ecommerce/**/*.java src/com/ecommerce/*.java
```

**If using PowerShell (Windows):**
```powershell
$sources = Get-ChildItem -Recurse -Include "*.java" "src"
javac -d bin -cp "lib/mysql-connector-java-8.0.33.jar" $sources.FullName
```

#### Step 4: Verify Compilation
Check if `bin` folder contains `.class` files:
```bash
dir bin\com\ecommerce\
```

---

### Method 2: Using Eclipse IDE

#### Step 1: Create New Project
1. File → New → Java Project
2. Project name: "ECommerceApp"
3. Click Finish

#### Step 2: Add Source Files
1. Expand project in Package Explorer
2. Right-click `src` → Import
3. Select "File System"
4. Navigate to project's `src` folder
5. Select all and finish

#### Step 3: Add JDBC Driver
1. Right-click Project → Build Path → Configure Build Path
2. Go to Libraries tab
3. Click "Add External JARs"
4. Select `mysql-connector-java-8.0.33.jar` from `lib` folder
5. Click Apply and Close

#### Step 4: Compile
- Right-click Project → Build Project (Ctrl+B)
- Check Console for compilation errors

---

### Method 3: Using IntelliJ IDEA

#### Step 1: Open Project
1. File → Open
2. Select the project root folder
3. Click OK

#### Step 2: Configure Project Structure
1. File → Project Structure
2. Go to "Project" section
3. Set Project SDK to Java JDK
4. Click Apply

#### Step 3: Add JDBC Driver
1. Go to Modules → Dependencies
2. Click "+" → JARs or Directories
3. Select `mysql-connector-java-8.0.33.jar`
4. Click Apply

#### Step 4: Build
1. Build → Build Project (Ctrl+F9)
2. Check Build window for errors

---

### Method 4: Using VS Code with Extensions

#### Step 1: Install Extensions
- Extension Pack for Java (Microsoft)
- MySQL/Database Client (Weijan Chen)

#### Step 2: Configure classpath
Create `.vscode/settings.json`:
```json
{
    "java.project.sourcePaths": ["src"],
    "java.project.outputPath": "bin",
    "java.project.referencedLibraries": ["lib/**/*.jar"]
}
```

#### Step 3: Compile
- Run Testing or Debugging (F5) will auto-compile
- Or: Terminal → Run Task → Java Compile

---

## ▶️ Execution Methods

### Method 1: Command Prompt (Windows)

```bash
java -cp bin;lib/mysql-connector-java-8.0.33.jar com.ecommerce.ECommerceApp
```

**Note:** Use `;` as classpath separator on Windows

### Method 2: Command Line (Linux/Mac)

```bash
java -cp bin:lib/mysql-connector-java-8.0.33.jar com.ecommerce.ECommerceApp
```

**Note:** Use `:` as classpath separator on Linux/Mac

### Method 3: Using PowerShell (Windows)

```powershell
$classpath = "bin;lib/mysql-connector-java-8.0.33.jar"
java -cp $classpath com.ecommerce.ECommerceApp
```

### Method 4: Eclipse IDE

1. Right-click `ECommerceApp.java`
2. Run As → Java Application
3. Output appears in Console view

### Method 5: IntelliJ IDEA

1. Open `ECommerceApp.java`
2. Click green Run button (▶) next to main method
3. Or press Shift+F10
4. Output in Run window

### Method 6: VS Code

1. Open `ECommerceApp.java`
2. Click "Run" above main method
3. Or press Ctrl+F5

---

## ✅ Expected Startup Output

When application starts correctly, you should see:

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

## 🐛 Troubleshooting Compilation Errors

### Error 1: cannot find symbol

**Message:** `error: cannot find symbol`

**Causes:**
- Wrong package name
- Missing import statement
- Misspelled class name

**Solution:**
```bash
# Check all java files compile
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar src/com/ecommerce/**/*.java
```

### Error 2: Class file too large

**Message:** `class file too large`

**Cause:** Method too long (>64KB of bytecode)

**Solution:** Split large methods into smaller ones

### Error 3: JDBC Driver not found during compilation

**Message:** `error: package com.mysql does not exist`

**Cause:** Not using -cp flag with JDBC jar

**Solution:**
```bash
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar ...
```

---

## 🐛 Troubleshooting Runtime Errors

### Error 1: ClassNotFoundException: com.mysql.cj.jdbc.Driver

**Message:** Exception during execution

**Cause:** JDBC driver not in classpath

**Solution:**
```bash
# Ensure proper syntax for your OS
# Windows (use semicolon)
java -cp bin;lib/mysql-connector-java-8.0.33.jar ...

# Linux/Mac (use colon)
java -cp bin:lib/mysql-connector-java-8.0.33.jar ...
```

### Error 2: Connection refused

**Message:** `java.sql.SQLException: Access denied for user 'root'@'localhost'`

**Cause:** Wrong credentials or MySQL not running

**Solution:**
1. Start MySQL service
2. Check credentials in `DatabaseConnection.java`
3. Verify database exists: `SHOW DATABASES;`

### Error 3: Database does not exist

**Message:** `Unknown database 'ecommerce_db'`

**Cause:** SQL script not executed

**Solution:**
```sql
-- In MySQL:
SOURCE database/ecommerce_db_setup.sql;
```

---

## 📝 Build Script (Batch file for Windows)

Create `build.bat`:

```batch
@echo off
echo Compiling E-Commerce Application...

REM Create output directory
if not exist bin mkdir bin

REM Compile Java files
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar ^
    src/com/ecommerce/models/*.java ^
    src/com/ecommerce/dao/*.java ^
    src/com/ecommerce/services/*.java ^
    src/com/ecommerce/util/*.java ^
    src/com/ecommerce/*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Compilation successful!
    echo.
    echo Running application...
    java -cp bin;lib/mysql-connector-java-8.0.33.jar com.ecommerce.ECommerceApp
) else (
    echo.
    echo Compilation failed!
    echo Check errors above
)

pause
```

**Usage:**
1. Save in project root as `build.bat`
2. Double-click to run
3. Application starts automatically if compilation succeeds

---

## 📝 Build Script (Shell script for Linux/Mac)

Create `build.sh`:

```bash
#!/bin/bash

echo "Compiling E-Commerce Application..."

# Create output directory
mkdir -p bin

# Compile Java files
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar \
    src/com/ecommerce/models/*.java \
    src/com/ecommerce/dao/*.java \
    src/com/ecommerce/services/*.java \
    src/com/ecommerce/util/*.java \
    src/com/ecommerce/*.java

if [ $? -eq 0 ]; then
    echo ""
    echo "Compilation successful!"
    echo ""
    echo "Running application..."
    java -cp bin:lib/mysql-connector-java-8.0.33.jar com.ecommerce.ECommerceApp
else
    echo ""
    echo "Compilation failed!"
    echo "Check errors above"
fi
```

**Usage:**
```bash
chmod +x build.sh
./build.sh
```

---

## 🔍 Verifying Jar File

Ensure JDBC jar is correct:

```bash
# View contents of jar
jar tf lib/mysql-connector-java-8.0.33.jar | find "Driver"

# Example output should include:
# com/mysql/cj/jdbc/Driver.class
```

---

## 📊 Project Size Information

After compilation:

| Component | Approx Size |
|-----------|-------------|
| Source files (.java) | ~100 KB |
| Compiled files (.class) | ~80 KB |
| JDBC driver jar | ~2.0 MB |
| Total | ~2.2 MB |

---

## 🎯 Quick Command Reference

| Task | Command |
|------|---------|
| Compile | `javac -d bin -cp lib/mysql-connector-java-8.0.33.jar src/com/ecommerce/**/*.java src/com/ecommerce/*.java` |
| Run (Windows) | `java -cp bin;lib/mysql-connector-java-8.0.33.jar com.ecommerce.ECommerceApp` |
| Run (Linux) | `java -cp bin:lib/mysql-connector-java-8.0.33.jar com.ecommerce.ECommerceApp` |
| Verify Database | `mysql -u root -p -e "use ecommerce_db; show tables;"` |
| Clean binaries | `rmdir /s bin` (Windows) or `rm -rf bin` (Linux) |
| Check classes | `dir bin` (Windows) or `ls -la bin` (Linux) |

---

## 🎓 Manual Compilation (Step-by-step)

If you want to compile each package individually:

```bash
# Step 1: Compile models
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar src/com/ecommerce/models/*.java

# Step 2: Compile utilities
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar:bin src/com/ecommerce/util/*.java

# Step 3: Compile DAO
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar:bin src/com/ecommerce/dao/*.java

# Step 4: Compile services
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar:bin src/com/ecommerce/services/*.java

# Step 5: Compile main app
javac -d bin -cp lib/mysql-connector-java-8.0.33.jar:bin src/com/ecommerce/*.java
```

---

## ✨ Pro Tips

1. **Add to System PATH**
   - Add Java bin folder to System PATH
   - Then can use `javac` and `java` from any folder

2. **Use IDE for Development**
   - Easier compilation and debugging
   - Better error highlighting
   - Integrated debugging tools

3. **Keep bin Folder Clean**
   - Delete old .class files before recompiling
   - `rm -rf bin && mkdir bin`

4. **Test Compilation Before Running**
   - Always compile first
   - Check for errors before execution

5. **Use Relative Paths**
   - Use `./lib/` instead of absolute paths
   - Makes project portable

---

Happy Coding! 🚀
