# Secure User Authentication System in Java

A console-based authentication system that simulates how real applications handle login securely.

## Features

- **User Registration**: Register new users with secure password storage
- **User Login**: Authenticate users with username and password
- **Password Hashing**: All passwords are hashed using SHA-256 before storage
- **Brute-Force Protection**: Accounts are automatically locked after 3 failed login attempts
- **Activity Logging**: All authentication events are logged with timestamps

## Project Structure

```
src/
├── com/auth/system/
│   ├── Main.java           # Main application entry point
│   ├── User.java           # User model class
│   ├── AuthService.java    # Authentication service
│   └── Logger.java         # Logging utility
└── com/auth/utils/
    └── PasswordUtils.java  # Password hashing utilities
```

## Java Concepts Used

- **Object-Oriented Programming**: User, AuthService, PasswordUtils, Logger classes
- **File Handling**: 
  - `users.txt` - Stores username + hashed password + login attempts + locked status
  - `logs.txt` - Records login attempts with timestamps
- **Security Concepts**:
  - Password hashing (SHA-256)
  - Brute-force protection (account locking)
  - Authentication logic

## How to Run

### Compile the Project
```bash
./compile.sh
```

### Run the Application
```bash
./run.sh
```

Or manually:
```bash
javac -d out src/com/auth/utils/*.java src/com/auth/system/*.java
java -cp out com.auth.system.Main
```

## Usage

1. **Register**: Create a new user account
   - Enter username
   - Enter password (minimum 6 characters)

2. **Login**: Authenticate with existing credentials
   - Enter username
   - Enter password
   - Account locks after 3 failed attempts

3. **Exit**: Close the application

## Security Features

- Passwords are never stored in plain text
- SHA-256 hashing algorithm for password security
- Account lockout after 3 failed login attempts
- All login attempts are logged for auditing
- Timestamps on all log entries

## Files Generated

- `users.txt` - User database (excluded from git)
- `logs.txt` - Activity logs (excluded from git)