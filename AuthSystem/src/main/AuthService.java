import java.io.IOException;
import java.util.ArrayList;
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
	private final String logsFilePath;
	private final ILogger logger;

	public AuthService(String usersFilePath, String lockedUsersFilePath, String logsFilePath) throws IOException {
		this(usersFilePath, lockedUsersFilePath, logsFilePath, FileLogger.getInstance(logsFilePath));
	}

	public AuthService(String usersFilePath, String lockedUsersFilePath, String logsFilePath, ILogger logger)
			throws IOException {
		this.usersFilePath = usersFilePath;
		this.lockedUsersFilePath = lockedUsersFilePath;
		this.logsFilePath = logsFilePath;
		this.logger = logger;
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

	@Override
	public void changePassword(AbstractUser user, String newPlainPassword) throws IOException {
		if (user == null) {
			return;
		}
		String hashedPassword = PasswordUtil.hashPassword(newPlainPassword);
		user.setHashedPassword(hashedPassword);
		userCache.put(user.getUsername(), user);
		persistUsers();
		logger.log(LogLevel.INFO, "PASSWORD_CHANGED | " + user.getUsername());
	}

	@Override
	public List<AbstractUser> listUsers() {
		return new ArrayList<>(userCache.values());
	}

	@Override
	public List<String> readLogs() throws IOException {
		return FileManager.readLogs(logsFilePath);
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

	private void persistUsers() throws IOException {
		FileManager.writeAllUsers(new ArrayList<>(userCache.values()), usersFilePath);
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
