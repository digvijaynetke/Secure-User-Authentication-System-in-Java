import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AuthService implements IAuthService {
	public static final int MAX_LOGIN_ATTEMPTS = 3;

	private final Map<String, User> userCache = new HashMap<>();
	private final Map<String, Integer> failedAttempts = new HashMap<>();
	private final Set<String> lockedUsers = new HashSet<>();
	private final String usersFilePath;
	private final String lockedUsersFilePath;
	private final String logsFilePath;

	public AuthService(String usersFilePath, String lockedUsersFilePath, String logsFilePath) throws IOException {
		this.usersFilePath = usersFilePath;
		this.lockedUsersFilePath = lockedUsersFilePath;
		this.logsFilePath = logsFilePath;
		loadUsers();
		loadLockedUsers();
	}

	@Override
	public void registerUser(String username, String plainPassword, Role role)
			throws UserAlreadyExistsException, IOException {
		if (userCache.containsKey(username)) {
			throw new UserAlreadyExistsException("User already exists: " + username);
		}

		String hashedPassword = PasswordUtil.hashPassword(plainPassword);
		User user = new User(username, hashedPassword, role);

		userCache.put(username, user);
		FileManager.saveUser(user, usersFilePath);
		Logger.logEvent("REGISTER | " + username + " | " + role.name(), logsFilePath);
	}

	@Override
	public User loginUser(String username, String plainPassword)
			throws AccountLockedException, InvalidCredentialsException, IOException {
		if (lockedUsers.contains(username)) {
			throw new AccountLockedException("Account locked: " + username);
		}

		User user = userCache.get(username);
		if (user == null) {
			Logger.logEvent("LOGIN_FAIL | " + username + " | user_not_found", logsFilePath);
			throw new InvalidCredentialsException("Invalid credentials");
		}

		boolean isValid = PasswordUtil.verifyPassword(plainPassword, user.getHashedPassword());
		if (!isValid) {
			int attempts = failedAttempts.getOrDefault(username, 0) + 1;
			failedAttempts.put(username, attempts);
			Logger.logEvent("LOGIN_FAIL | " + username + " | attempt=" + attempts, logsFilePath);

			if (attempts >= MAX_LOGIN_ATTEMPTS) {
				lockedUsers.add(username);
				FileManager.lockUser(username, lockedUsersFilePath);
				Logger.logEvent("ACCOUNT_LOCKED | " + username, logsFilePath);
				throw new AccountLockedException("Account locked: " + username);
			}

			throw new InvalidCredentialsException("Invalid credentials");
		}

		failedAttempts.remove(username);
		Logger.logEvent("LOGIN_SUCCESS | " + username, logsFilePath);
		return user;
	}

	public Map<String, User> getUserCache() {
		return new HashMap<>(userCache);
	}

	public Set<String> getLockedUsers() {
		return new HashSet<>(lockedUsers);
	}

	private void loadUsers() throws IOException {
		List<User> users = FileManager.readAllUsers(usersFilePath);
		for (User user : users) {
			userCache.put(user.getUsername(), user);
		}
	}

	private void loadLockedUsers() throws IOException {
		lockedUsers.clear();
		lockedUsers.addAll(FileManager.readLockedUsers(lockedUsersFilePath));
	}
}
