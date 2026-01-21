package com.auth.system;

import java.util.Scanner;

/**
 * Main class for the User Authentication System
 */
public class Main {
    
    public static void main(String[] args) {
        AuthService authService = new AuthService();
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("===========================================");
        System.out.println("  User Authentication & Access Control");
        System.out.println("===========================================");
        
        while (true) {
            System.out.println("\nPlease select an option:");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    handleRegistration(authService, scanner);
                    break;
                case "2":
                    handleLogin(authService, scanner);
                    break;
                case "3":
                    System.out.println("Thank you for using the system. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /**
     * Handle user registration
     */
    private static void handleRegistration(AuthService authService, Scanner scanner) {
        System.out.println("\n--- User Registration ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Enter password (min 6 characters): ");
        String password = scanner.nextLine();
        
        authService.register(username, password);
    }
    
    /**
     * Handle user login
     */
    private static void handleLogin(AuthService authService, Scanner scanner) {
        System.out.println("\n--- User Login ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        authService.login(username, password);
    }
}
