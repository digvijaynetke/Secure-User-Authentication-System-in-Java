import java.io.IOException;
import java.util.Scanner;

public class Main {
	private static final String USERS_FILE = "AuthSystem/data/users.txt";
	private static final String LOCKED_USERS_FILE = "AuthSystem/data/locked_users.txt";
	private static final String LOGS_FILE = "AuthSystem/data/logs.txt";

	public static void main(String[] args) {
		try {
			ILogger logger = FileLogger.getInstance(LOGS_FILE);
			IAuthService authService = new AuthService(USERS_FILE, LOCKED_USERS_FILE, logger);
			runConsole(authService);
		} catch (IOException ex) {
			System.out.println("Startup failed. Check data file paths and permissions.");
		}
	}

	private static void runConsole(IAuthService authService) {
		Scanner scanner = new Scanner(System.in);
		boolean running = true;

		while (running) {
			System.out.println();
			System.out.println("1) Register");
			System.out.println("2) Login");
			System.out.println("3) Exit");
			System.out.print("Choose option: ");

			String choice = scanner.nextLine().trim();
			switch (choice) {
				case "1":
					handleRegister(authService, scanner);
					break;
				case "2":
					handleLogin(authService, scanner);
					break;
				case "3":
					running = false;
					break;
				default:
					System.out.println("Invalid option. Try again.");
					break;
			}
		}
	}

	private static void handleRegister(IAuthService authService, Scanner scanner) {
		System.out.print("Username: ");
		String username = scanner.nextLine().trim();
		System.out.print("Password: ");
		String password = scanner.nextLine();
		Role role = readRole(scanner);
		if (role == null) {
			System.out.println("Invalid role. Use ADMIN or USER.");
			return;
		}

		try {
			authService.registerUser(username, password, role);
			System.out.println("Registration successful.");
		} catch (UserAlreadyExistsException ex) {
			System.out.println("Registration failed: user already exists.");
		} catch (IOException ex) {
			System.out.println("Registration failed: unable to save user data.");
		}
	}

	private static void handleLogin(IAuthService authService, Scanner scanner) {
		System.out.print("Username: ");
		String username = scanner.nextLine().trim();
		System.out.print("Password: ");
		String password = scanner.nextLine();

		try {
			AbstractUser user = authService.loginUser(username, password);
			System.out.println("Login successful. Welcome, " + user.getUsername() + ".");
			user.showMenu();
		} catch (AccountLockedException ex) {
			System.out.println("Login blocked: account is locked.");
		} catch (InvalidCredentialsException ex) {
			System.out.println("Login failed: invalid credentials.");
		} catch (IOException ex) {
			System.out.println("Login failed: unable to read data files.");
		}
	}

	private static Role readRole(Scanner scanner) {
		System.out.print("Role (ADMIN/USER): ");
		String input = scanner.nextLine().trim().toUpperCase();
		try {
			return Role.valueOf(input);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
}
