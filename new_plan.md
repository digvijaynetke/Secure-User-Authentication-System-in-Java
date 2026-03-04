
* Interfaces
* Abstract classes
* Collections
* Custom Exceptions
* Packages
* Static
* Enums
* Proper OOP

We are NOT changing architecture.
We are **evolving it cleanly**.

---

# 🔥 INTEGRATED ROADMAP (OLD PLAN + FULL OOP)

You completed:

✔ Phase 1 – Structure
✔ Phase 2 – File handling
✔ Phase 3 – Password hashing

Now we continue from **Phase 4**, but integrated properly.

---

# 🔹 PHASE 4 – Registration (Now with Interfaces + Exceptions + Collections)

### Upgrade what you already have:

### ✅ Add

* `Role` enum
* Custom exceptions
* Interface for AuthService
* Use `Map<String, User>` for user cache

---

### What happens now:

1. On program start:

   * Load users from file
   * Store inside `HashMap<String, User>`

2. Registration flow:

   * Check `map.containsKey(username)`
   * If yes → throw `UserAlreadyExistsException`
   * Hash password
   * Create `User` object
   * Save in:

     * Map
     * File
   * Log event

---

### OOP Added Here:

* Interface `IAuthService`
* Custom Exception
* Enum `Role`
* Collection `Map`

We are NOT replacing your logic.
We are wrapping it properly.

---

# 🔹 PHASE 5 – Login (Add Locking + Proper Exception Hierarchy)

Upgrade your login logic.

---

### Add:

* `Set<String> lockedUsers`
* `Map<String, Integer> failedAttempts`

---

### Login Flow Now:

1. If username in `lockedUsers`
   → throw `AccountLockedException`

2. If user not in Map
   → throw `InvalidCredentialsException`

3. Hash password

4. Compare hash

If incorrect:

* Increase counter in `failedAttempts`
* If attempts >= MAX_LOGIN_ATTEMPTS:

  * Add to lockedUsers
  * Save to file
  * Log event
  * Throw `AccountLockedException`

If correct:

* Reset attempts
* Return User object

---

### OOP Added:

* Custom Exceptions hierarchy
* Static constant for MAX_LOGIN_ATTEMPTS
* Proper state tracking using Collections

---

# 🔹 PHASE 6 – Convert User to Abstract + Polymorphism

Now we upgrade your `User` class.

---

### Step 1: Make `AbstractUser`

Contains:

* username
* hashedPassword
* role
* abstract method: `showMenu()`

---

### Step 2: Create:

* `AdminUser`
* `NormalUser`

Each overrides `showMenu()`

---

### Important:

When loading from file:

If role == ADMIN → create AdminUser
If role == USER → create NormalUser

Store in:

```java
Map<String, AbstractUser>
```

---

### What changed?

We removed:

```java
if(role == ADMIN)
```

Now polymorphism handles behavior.

Old plan intact. Just cleaner.

---

# 🔹 PHASE 7 – Logging Upgrade (Interface + Static Singleton)

Keep your Logger.

Now improve it:

* Create `ILogger` interface
* Implement `FileLogger`
* Make it Singleton (optional)
* Add `LogLevel` enum

Example:

```
log(INFO, "Login success")
log(ERROR, "Account locked")
```

---

# 🔹 PHASE 8 – Package Structure (Organize Everything)

Now reorganize files:

```
authsystem.model
authsystem.service
authsystem.repository
authsystem.exception
authsystem.util
authsystem.logging
```

This does NOT change logic.
Only improves structure.

---

# 🔹 PHASE 9 – Dependency Injection (Clean Construction)

Instead of:

```java
AuthService auth = new AuthService();
```

Use:

```java
IUserRepository repo = new FileUserRepository();
ILogger logger = new FileLogger();
IAuthService auth = new AuthService(repo, logger);
```

Same logic. Better design.

---

# 🔹 PHASE 10 – Final Hardening

Add:

* Proper try-catch in Main
* Graceful error messages
* File corruption handling
* Override equals & hashCode in User
* Implement Serializable (optional)

---

# 🧠 FINAL RESULT

We did NOT change the project.

We:

* Kept registration
* Kept login
* Kept locking
* Kept hashing
* Kept file storage

We just wrapped everything with:

* Interfaces
* Abstract classes
* Collections
* Enums
* Custom Exceptions
* Packages
* Static
* Polymorphism

---

# 🔥 What You Have Now

Not a college demo.

A mini enterprise authentication engine.

---

Now tell me:

1️⃣ Show exact dependency flow diagram
2️⃣ Show final class relationship overview
3️⃣ Give build order so Copilot doesn’t break
4️⃣ Review your current structure if you paste it

We continue cleanly.
