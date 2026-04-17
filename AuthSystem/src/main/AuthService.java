import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AuthService implements IAuthService {
	public static final int MAX_LOGIN_ATTEMPTS = 3;

	private final Map<String, AbstractUser> userCache = new HashMap<>();
	private final Map<String, Integer> failedAttempts = new HashMap<>();
	private final Set<String> lockedUsers = new HashSet<>();
	private final String usersFilePath;
	private final String lockedUsersFilePath;
	private final ILogger logger;

	public AuthService(String usersFilePath, String lockedUsersFilePath, String logsFilePath) throws IOException {
		this.usersFilePath = usersFilePath;
		this.lockedUsersFilePath = lockedUsersFilePath;
		this.logger = FileLogger.getInstance(logsFilePath);
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
		AbstractUser user = createUser(role, username, hashedPassword);

		userCache.put(username, user);
		FileManager.saveUser(user, usersFilePath);
		logger.log(LogLevel.INFO, "REGISTER | " + username + " | " + role.name());
	}

	@Override
	public AbstractUser loginUser(String username, String plainPassword)
			throws AccountLockedException, InvalidCredentialsException, IOException {
		if (lockedUsers.contains(username)) {
			throw new AccountLockedException("Account locked: " + username);
		}

		AbstractUser user = userCache.get(username);
		if (user == null) {
			logger.log(LogLevel.WARN, "LOGIN_FAIL | " + username + " | user_not_found");
			throw new InvalidCredentialsException("Invalid credentials");
		}

		boolean isValid = PasswordUtil.verifyPassword(plainPassword, user.getHashedPassword());
		if (!isValid) {
			int attempts = failedAttempts.getOrDefault(username, 0) + 1;
			failedAttempts.put(username, attempts);
			logger.log(LogLevel.WARN, "LOGIN_FAIL | " + username + " | attempt=" + attempts);

			if (attempts >= MAX_LOGIN_ATTEMPTS) {
				lockedUsers.add(username);
				FileManager.lockUser(username, lockedUsersFilePath);
				logger.log(LogLevel.ERROR, "ACCOUNT_LOCKED | " + username);
				throw new AccountLockedException("Account locked: " + username);
			}

			throw new InvalidCredentialsException("Invalid credentials");
		}

		failedAttempts.remove(username);
		logger.log(LogLevel.INFO, "LOGIN_SUCCESS | " + username);
		return user;
	}

	public Map<String, AbstractUser> getUserCache() {
		return new HashMap<>(userCache);
	}

	public Set<String> getLockedUsers() {
		return new HashSet<>(lockedUsers);
	}

	private void loadUsers() throws IOException {
		List<AbstractUser> users = FileManager.readAllUsers(usersFilePath);
		for (AbstractUser user : users) {
			userCache.put(user.getUsername(), user);
		}
	}

	private void loadLockedUsers() throws IOException {
		lockedUsers.clear();
		lockedUsers.addAll(FileManager.readLockedUsers(lockedUsersFilePath));
	}

	private AbstractUser createUser(Role role, String username, String hashedPassword) {
		switch (role) {
			case ADMIN:
				return new AdminUser(username, hashedPassword);
			case USER:
				return new NormalUser(username, hashedPassword);
			default:
				throw new IllegalArgumentException("Unsupported role: " + role);
		}
	}
}
