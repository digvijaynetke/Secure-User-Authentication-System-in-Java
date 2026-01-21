package com.auth.system;

/**
 * Represents a user in the authentication system
 */
public class User {
    private String username;
    private String hashedPassword;
    private int failedLoginAttempts;
    private boolean isLocked;
    
    public User(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.failedLoginAttempts = 0;
        this.isLocked = false;
    }
    
    public User(String username, String hashedPassword, int failedLoginAttempts, boolean isLocked) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.failedLoginAttempts = failedLoginAttempts;
        this.isLocked = isLocked;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getHashedPassword() {
        return hashedPassword;
    }
    
    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }
    
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
    }
    
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }
    
    public boolean isLocked() {
        return isLocked;
    }
    
    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }
    
    @Override
    public String toString() {
        return username + ":" + hashedPassword + ":" + failedLoginAttempts + ":" + isLocked;
    }
    
    /**
     * Parse a user from a string representation
     * @param line The string in format "username:hashedPassword:attempts:locked"
     * @return User object or null if parsing fails
     */
    public static User fromString(String line) {
        try {
            String[] parts = line.split(":");
            if (parts.length >= 2) {
                String username = parts[0];
                String hashedPassword = parts[1];
                int attempts = parts.length >= 3 ? Integer.parseInt(parts[2]) : 0;
                boolean locked = parts.length >= 4 ? Boolean.parseBoolean(parts[3]) : false;
                return new User(username, hashedPassword, attempts, locked);
            }
        } catch (Exception e) {
            // Invalid format
        }
        return null;
    }
}
