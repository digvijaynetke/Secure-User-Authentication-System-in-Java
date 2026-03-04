🗺️ PROJECT ROADMAP (HOW THINGS WILL WORK)
🔹 PHASE 0 – Understand the Flow (Big Picture)
User → Register/Login → Authentication
     → Authorization (Role Check)
     → Allowed / Denied Action
     → Log Everything

Keep this flow in mind throughout the project.

🔹 PHASE 1 – Project Setup & Structure
1️⃣ Create Project Structure
AuthSystem/
│
├── src/
│   ├── main/
│   │   ├── Main.java
│   │   ├── User.java
│   │   ├── AuthService.java
│   │   ├── PasswordUtil.java
│   │   ├── FileManager.java
│   │   └── Logger.java
│
├── data/
│   ├── users.txt
│   ├── locked_users.txt
│   └── logs.txt

👉 Goal: separation of responsibilities

🔹 PHASE 2 – User & File Handling (Foundation)
2️⃣ Define User Model

Fields:

username

hashedPassword

role

This sets up OOP basics.

3️⃣ File Handling Logic

Implement read/write methods:

Save user

Read all users

Check if user exists

Lock user

👉 At this stage:

No security logic yet

Just file handling correctness

🔹 PHASE 3 – Password Security
4️⃣ Password Hashing

Take plaintext password

Convert → SHA-256

Store only hash in file

Flow:

Password → Hash → Save
Password → Hash → Compare

Cybersecurity win ✔️

🔹 PHASE 4 – Registration Workflow
5️⃣ Register User

Steps:

Take username & password

Check if username exists

Hash password

Save to users.txt

Log registration

Output:

User registered successfully
🔹 PHASE 5 – Authentication (Login Logic)
6️⃣ Login User

Steps:

Check if user is locked

Validate username exists

Hash entered password

Compare hashes

Track failed attempts

7️⃣ Brute-Force Protection

Max attempts = 3

On failure:

Increment counter

On exceed:

Lock account

Log security event

This is real security logic, not dummy code.

🔹 PHASE 6 – Authorization (Access Control)
8️⃣ Role-Based Access

After login:

If USER:

View profile

Logout

If ADMIN:

View logs

Unlock users

Logic:

if (role == ADMIN) → allow
else → deny
🔹 PHASE 7 – Logging & Monitoring
9️⃣ Security Logs

Log events like:

LOGIN_SUCCESS

LOGIN_FAILED

ACCOUNT_LOCKED

ADMIN_ACTION

This simulates audit logging / SIEM basics.

🔹 PHASE 8 – Testing & Hardening
🔟 Test Scenarios

Test cases you MUST try:

Wrong password 3 times

Locked user trying login

Admin unlocking user

Duplicate registration

File missing / corrupted

🔹 PHASE 9 – Documentation & Resume
1️⃣1️⃣ Documentation

Write:

Project overview

Security features

File structure

Future enhancements