package com.ecommerce.dao;

import com.ecommerce.models.User;
import com.ecommerce.util.DatabaseConnection;
import com.ecommerce.util.PasswordUtil;
import java.sql.*;

/**
 * UserDAO class handles all user-related database operations
 */
public class UserDAO {

    // ✅ CREATE USER
    public boolean createUser(User user) {

        if (user == null || user.getName() == null || user.getEmail() == null) {
            System.out.println("[ERROR] Invalid user data.");
            return false;
        }

        String query = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            // ✅ Fix: check BEFORE using connection
            if (conn == null) {
                System.out.println("[ERROR] Database connection failed.");
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
                ps.setString(3, PasswordUtil.hashPassword(user.getPassword()));
                ps.setString(4, user.getRole());

                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] Error creating user: " + e.getMessage());
            return false;
        }
    }

    // ✅ LOGIN
    public User getUserByEmailAndPassword(String email, String password) {

        String query = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn == null) {
                return null;
            }

            try (PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String storedPassword = rs.getString("password");

                    if (PasswordUtil.verifyPassword(password, storedPassword)) {
                        return new User(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                password,
                                rs.getString("role"));
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] Error during login: " + e.getMessage());
        }

        return null;
    }

    // ✅ GET USER BY ID
    public User getUserById(int userId) {

        String query = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn == null) {
                return null;
            }

            try (PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role"));
                }
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] Error fetching user: " + e.getMessage());
        }

        return null;
    }

    // ✅ CHECK EMAIL EXISTS
    public boolean emailExists(String email) {

        if (email == null || email.isEmpty()) {
            return false;
        }

        String query = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn == null) {
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] Error checking email: " + e.getMessage());
        }

        return false;
    }

    // ✅ UPDATE USER
    public boolean updateUser(User user) {

        String query = "UPDATE users SET name = ?, email = ?, role = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn == null) {
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getRole());
                ps.setInt(4, user.getId());

                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] Error updating user: " + e.getMessage());
        }

        return false;
    }

    // ✅ DELETE USER
    public boolean deleteUser(int userId) {

        String query = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn == null) {
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setInt(1, userId);
                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] Error deleting user: " + e.getMessage());
        }

        return false;
    }
}