package com.auth.system;

import com.auth.utils.PasswordUtils;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class handling authentication operations
 */
public class AuthService {
    private static final String USERS_FILE = "users.txt";
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private Map<String, User> users;
    
    public AuthService() {
        this.users = new HashMap<>();
        loadUsers();
    }
    
    /**
     * Load users from the users file
     */
    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = User.fromString(line);
                if (user != null) {
                    users.put(user.getUsername(), user);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users file: " + e.getMessage());
        }
    }
    
    /**
     * Save users to the users file
     */
    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users.values()) {
                writer.write(user.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to users file: " + e.getMessage());
        }
    }
    
    /**
     * Register a new user
     * @param username The username
     * @param password The plain text password
     * @return true if registration successful, false otherwise
     */
    public boolean register(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username cannot be empty");
            return false;
        }
        
        if (password == null || password.length() < 6) {
            System.out.println("Password must be at least 6 characters long");
            return false;
        }
        
        if (users.containsKey(username)) {
            System.out.println("Username already exists");
            return false;
        }
        
        String hashedPassword = PasswordUtils.hashPassword(password);
        User newUser = new User(username, hashedPassword);
        users.put(username, newUser);
        saveUsers();
        
        Logger.logRegistration(username);
        System.out.println("User registered successfully!");
        return true;
    }
    
    /**
     * Login a user
     * @param username The username
     * @param password The plain text password
     * @return true if login successful, false otherwise
     */
    public boolean login(String username, String password) {
        User user = users.get(username);
        
        if (user == null) {
            Logger.logFailedLogin(username, "User not found");
            System.out.println("Invalid username or password");
            return false;
        }
        
        if (user.isLocked()) {
            Logger.logFailedLogin(username, "Account is locked");
            System.out.println("Account is locked. Please contact administrator.");
            return false;
        }
        
        if (PasswordUtils.verifyPassword(password, user.getHashedPassword())) {
            user.resetFailedLoginAttempts();
            saveUsers();
            Logger.logSuccessfulLogin(username);
            System.out.println("Login successful! Welcome, " + username + "!");
            return true;
        } else {
            user.incrementFailedLoginAttempts();
            
            if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setLocked(true);
                Logger.logAccountLocked(username);
                System.out.println("Account has been locked due to multiple failed login attempts.");
            } else {
                int remainingAttempts = MAX_FAILED_ATTEMPTS - user.getFailedLoginAttempts();
                Logger.logFailedLogin(username, "Invalid password (Attempts remaining: " + remainingAttempts + ")");
                System.out.println("Invalid username or password. Attempts remaining: " + remainingAttempts);
            }
            
            saveUsers();
            return false;
        }
    }
    
    /**
     * Check if a user exists
     * @param username The username
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String username) {
        return users.containsKey(username);
    }
}
