package com.auth.system;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger class for recording authentication events
 */
public class Logger {
    private static final String LOG_FILE = "logs.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Log a message with timestamp
     * @param message The message to log
     */
    public static void log(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            writer.write("[" + timestamp + "] " + message);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
    
    /**
     * Log a successful login attempt
     * @param username The username
     */
    public static void logSuccessfulLogin(String username) {
        log("SUCCESSFUL LOGIN: User '" + username + "' logged in successfully");
    }
    
    /**
     * Log a failed login attempt
     * @param username The username
     * @param reason The reason for failure
     */
    public static void logFailedLogin(String username, String reason) {
        log("FAILED LOGIN: User '" + username + "' - " + reason);
    }
    
    /**
     * Log a user registration
     * @param username The username
     */
    public static void logRegistration(String username) {
        log("REGISTRATION: New user '" + username + "' registered");
    }
    
    /**
     * Log an account lock event
     * @param username The username
     */
    public static void logAccountLocked(String username) {
        log("ACCOUNT LOCKED: User '" + username + "' has been locked due to multiple failed login attempts");
    }
}
