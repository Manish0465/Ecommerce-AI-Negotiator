package com.ecommerce.services;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.models.User;
import com.ecommerce.util.ValidationUtil;

/**
 * UserService class handles user-related business logic
 * Bridges between UI and DAO layer
 */
public class UserService {
    private UserDAO userDAO = new UserDAO();
    private User currentUser; // Stores logged-in user

    public boolean registerUser(String name, String email, String password, String role) {
        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            System.out.println("[ERROR] Name cannot be empty.");
            return false;
        }

        if (!ValidationUtil.isValidName(name)) {
            System.out.println("[ERROR] Invalid name. Name must contain only letters and spaces.");
            return false;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            System.out.println("[ERROR] Invalid email format.");
            return false;
        }

        if (!ValidationUtil.isPasswordStrong(password)) {
            System.out.println("[ERROR] Password too weak. Minimum 6 characters required.");
            return false;
        }

        // Check if email already exists
        if (userDAO.emailExists(email)) {
            System.out.println("[ERROR] Email already registered. Please use a different email.");
            return false;
        }

        // Create user
        User user = new User(name, email, password, role);
        if (userDAO.createUser(user)) {
            System.out.println("[SUCCESS] Registration successful! You can now login.");
            return true;
        }

        return false;
    }

    /**
     * Login user with email and password
     * 
     * @param email    User email
     * @param password User password
     * @return true if login successful, false otherwise
     */
    public boolean loginUser(String email, String password) {
        if (!ValidationUtil.isValidEmail(email)) {
            System.out.println("[ERROR] Invalid email format.");
            return false;
        }

        if (password == null || password.isEmpty()) {
            System.out.println("[ERROR] Password cannot be empty.");
            return false;
        }

        User user = userDAO.getUserByEmailAndPassword(email.trim(), password.trim());
        if (user != null) {
            this.currentUser = user;
            System.out.println("[SUCCESS] Login successful! Welcome, " + user.getName());
            return true;
        }

        System.out.println("[ERROR] Invalid email or password.");
        return false;
    }

    /**
     * Get currently logged-in user
     * 
     * @return Current user object or null if no user logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if user is logged in
     * 
     * @return true if user is logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        return currentUser != null;
    }

    /**
     * Check if current user is admin
     * 
     * @return true if user is admin, false otherwise
     */
    public boolean isCurrentUserAdmin() {
        return currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Logout current user
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("[SUCCESS] Logged out successfully. Goodbye, " + currentUser.getName() + "!");
            currentUser = null;
        }
    }

    /**
     * Get user by ID
     * 
     * @param userId User ID
     * @return User object if found
     */
    public User getUserById(int userId) {
        return userDAO.getUserById(userId);
    }

    /**
     * Update user profile
     * 
     * @param user Updated user object
     * @return true if update successful
     */
    public boolean updateUserProfile(User user) {
        if (userDAO.updateUser(user)) {
            this.currentUser = user;
            System.out.println("[SUCCESS] Profile updated successfully!");
            return true;
        }
        return false;
    }
}
