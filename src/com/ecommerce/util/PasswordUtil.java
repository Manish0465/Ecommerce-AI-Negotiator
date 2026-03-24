package com.ecommerce.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for password hashing and validation
 * Uses SHA-256 algorithm with Base64 encoding for password security
 */
public class PasswordUtil {

    /**
     * Hash a password using SHA-256 algorithm
     * @param password Plain text password
     * @return Hashed password encoded in Base64
     */
    public static String hashPassword(String password) {
        try {
            // Create SHA-256 message digest
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // Hash the password
            byte[] hashBytes = md.digest(password.getBytes());
            
            // Encode to Base64 for storage
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Verify if a plain password matches a hashed password
     * @param plainPassword Plain text password to verify
     * @param hashedPassword Previously hashed password
     * @return True if passwords match, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        // Hash the plain password and compare with stored hash
        String plainPasswordHash = hashPassword(plainPassword);
        return plainPasswordHash.equals(hashedPassword);
    }

    /**
     * Validate password strength
     * @param password Password to validate
     * @return True if password meets requirements, false otherwise
     * Requirements: At least 6 characters
     */
    public static boolean isPasswordStrong(String password) {
        return password != null && password.length() >= 6;
    }
}
