# Secure User Authentication System in Java

## Table of Contents
- Overview
- Big Picture Flow
- Runtime Flow Details
- Data Files and Formats
- Build and Run
- Architecture Overview
- File-by-File Responsibilities
- Dependency Injection
- OOP Concepts Used (Where and Why)
- Security Features and Notes
- Problems Faced and How They Were Solved (Interview Notes)
- Testing Summary
- Known Limitations
- Future Improvements

## Overview
This project is a console-based authentication and access control system implemented in Java.
It supports registration, login, password hashing, account locking after multiple failed attempts,
role-based behavior through polymorphism, and audit logging to disk.

The project was built iteratively with a focus on clean OOP design, file-based persistence, and
security-minded flows.

## Big Picture Flow
User -> Register/Login -> Authentication -> Authorization (Role Check) -> Allowed/Denied Action -> Log Everything

## Runtime Flow Details
### Registration Flow
1) User chooses Register in the console
2) System checks in-memory cache for existing username
3) If exists, throw UserAlreadyExistsException
4) Hash plaintext password with SHA-256
5) Create role-specific user (AdminUser or NormalUser)
6) Save user to file and in-memory cache
7) Log event to logs.txt

### Login Flow
1) User chooses Login in the console
2) If username is in locked_users set, throw AccountLockedException
3) If username not found, log and throw InvalidCredentialsException
4) Hash input password and compare to stored hash
5) If mismatch:
   - Increment failed-attempt counter
   - If attempts >= MAX_LOGIN_ATTEMPTS:
     - Persist lock to locked_users.txt
     - Log account lock
     - Throw AccountLockedException
   - Otherwise, throw InvalidCredentialsException
6) If match:
   - Reset failed-attempt counter
   - Log login success
   - Return AbstractUser and show role-specific menu

## Data Files and Formats
### users.txt
Each line stores a single user:
username|hashedPassword|role

Example:
admin|5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8|ADMIN

### locked_users.txt
Each line stores a locked username:
username

### logs.txt
Each line stores a timestamped log entry:
timestamp | LEVEL | message

Example:
2026-04-18 10:20:30 | INFO | LOGIN_SUCCESS | admin

## Build and Run
From the repository root:

Compile:
- javac AuthSystem/src/main/*.java

Run:
- java -cp AuthSystem/src/main Main

Note: The console expects data files under AuthSystem/data/*.txt.

## Architecture Overview
The system is organized into several concerns (not yet separated into packages):
- Model: AbstractUser and its subclasses
- Service: AuthService and IAuthService
- Utilities: PasswordUtil
- Persistence: FileManager
- Logging: ILogger, FileLogger, LogLevel, Logger
- Exceptions: AuthException hierarchy

## File-by-File Responsibilities
### Main.java
- Console entry point
- Creates dependencies (ILogger) and injects into AuthService
- Wraps operations in try/catch for user-friendly messages
- Routes to register and login flows

### AuthService.java
- Core authentication service
- Loads users and locked users at startup
- Handles registration and login logic
- Maintains in-memory cache and failed-attempt counters
- Logs all security-relevant events

### IAuthService.java
- Interface for authentication operations
- Exposes registerUser and loginUser

### AbstractUser.java
- Abstract base for all user types
- Stores username, hashed password, and role
- Declares showMenu() for polymorphic behavior
- Implements equals/hashCode and Serializable

### AdminUser.java
- Admin-specific user class
- Overrides showMenu() with admin options

### NormalUser.java
- Standard user class
- Overrides showMenu() with normal user options

### Role.java
- Enum for role types (ADMIN, USER)

### PasswordUtil.java
- SHA-256 hashing
- Constant-time hash comparison

### FileManager.java
- Reads and writes users to users.txt
- Reads and writes locked users to locked_users.txt
- Validates input to avoid crashes from corrupted files

### ILogger.java
- Logging interface

### FileLogger.java
- Logger implementation that writes to logs.txt
- Singleton instance for a single log target

### LogLevel.java
- Enum for INFO, WARN, ERROR log levels

### Logger.java
- Backwards-compatible facade that delegates to FileLogger

### AuthException.java
- Base exception for authentication errors

### InvalidCredentialsException.java
- Thrown on wrong username/password

### AccountLockedException.java
- Thrown when account is locked

### UserAlreadyExistsException.java
- Thrown when attempting to register an existing user

### Data Files
- AuthSystem/data/users.txt
- AuthSystem/data/locked_users.txt
- AuthSystem/data/logs.txt

## Dependency Injection
### What It Is
Dependency Injection (DI) is a design technique where a class receives its dependencies
from the outside instead of creating them internally. This improves testability, reduces
coupling, and makes it easier to swap implementations.

### How It Is Used Here
- Main creates a concrete logger:
  ILogger logger = FileLogger.getInstance(LOGS_FILE);
- AuthService receives ILogger via constructor injection:
  new AuthService(USERS_FILE, LOCKED_USERS_FILE, logger);

This means AuthService depends on the ILogger abstraction, not a specific logger
implementation. In tests, a mock logger could be injected instead of FileLogger.

## OOP Concepts Used (Where and Why)
### Encapsulation
- Private fields with getters/setters in AbstractUser
- Internal state like userCache and failedAttempts inside AuthService

### Abstraction
- AbstractUser defines common fields and behavior for all user types
- IAuthService and ILogger hide implementation details behind interfaces

### Inheritance
- AdminUser and NormalUser extend AbstractUser
- AuthException is the base class for specific auth errors

### Polymorphism
- showMenu() is overridden by each user type
- AuthService returns AbstractUser, but calls behave based on actual runtime type

### Interfaces
- IAuthService and ILogger define contracts for services

### Enums
- Role and LogLevel provide safe, readable constants

### Composition
- AuthService uses ILogger and FileManager to perform its work

### Exception Handling
- Custom exception hierarchy simplifies error handling in Main

### Collections
- Map for user cache and failed attempts
- Set for locked users

## Security Features and Notes
- Passwords are hashed with SHA-256
- No plaintext passwords are stored
- Login failures are tracked and accounts are locked after MAX_LOGIN_ATTEMPTS
- Audit logging records all key events

Security limitations (for future improvement):
- Hashing is unsalted (add per-user salt for stronger security)
- Console passwords are not masked
- No password complexity rules

## Problems Faced and How They Were Solved (Interview Notes)
1) File corruption caused crashes during startup
   - Solution: Add validation and skip malformed lines during file parsing

2) Account lock state did not persist across restarts
   - Solution: Introduced locked_users.txt and load it on startup

3) Repeated role checks using if/else created brittle code
   - Solution: Moved to AbstractUser + AdminUser/NormalUser with polymorphism

4) Logging grew inconsistent and hard to manage
   - Solution: Added ILogger, FileLogger, LogLevel, and centralized log format

5) Tightly coupled services made testing harder
   - Solution: Introduced constructor injection for ILogger in AuthService

6) Brute-force protection required shared state across login attempts
   - Solution: Used Map<String, Integer> to track failed attempts per user

## Testing Summary
- Compiled all Java sources with javac without errors
- Manual console flow validated registration and login behaviors

## Known Limitations
- File persistence is append-only (no user update/remove yet)
- No account unlock workflow except manual file edit
- Single-threaded console usage only
- No unit tests yet

## Future Improvements
- Add salt + stronger password hashing (PBKDF2, bcrypt, scrypt)
- Add user update/reset functionality
- Add real unit tests for AuthService and PasswordUtil
- Improve CLI UX and input validation
- Move to packages (authsystem.model, authsystem.service, etc.)
- Replace file storage with database repository
