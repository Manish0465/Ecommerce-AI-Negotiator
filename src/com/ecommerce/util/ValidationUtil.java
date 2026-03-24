package com.ecommerce.util;

/**
 * Utility class for input validation
 * Validates user inputs for various fields
 */
public class ValidationUtil {

    /**
     * Validate email format
     * 
     * @param email Email to validate
     * @return True if valid email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty())
            return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Validate name (alphabetic and space only)
     * 
     * @param name Name to validate
     * @return True if valid name format
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty())
            return false;
        return name.matches("^[A-Za-z\\s]+$") && name.length() >= 2;
    }

    /**
     * Validate that a value is positive number
     * 
     * @param value Value to validate
     * @return True if value is positive
     */
    public static boolean isPositive(double value) {
        return value > 0;
    }

    /**
     * Validate that a value is positive integer
     * 
     * @param value Value to validate
     * @return True if value is positive
     */
    public static boolean isPositiveInt(int value) {
        return value > 0;
    }

    /**
     * Validate that a value is non-negative
     * 
     * @param value Value to validate
     * @return True if value is non-negative
     */
    public static boolean isNonNegative(int value) {
        return value >= 0;
    }

    /**
     * Check if string is not empty or null
     * 
     * @param str String to check
     * @return True if string is valid
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Validate password strength
     * 
     * @param password Password to validate
     * @return True if password is strong (minimum 6 characters)
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.isEmpty())
            return false;
        return password.length() >= 6;
    }
}
