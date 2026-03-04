import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthService implements IAuthService {
	private final Map<String, User> userCache = new HashMap<>();
	private final String usersFilePath;
	private final String logsFilePath;

	public AuthService(String usersFilePath, String logsFilePath) throws IOException {
		this.usersFilePath = usersFilePath;
		this.logsFilePath = logsFilePath;
		loadUsers();
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

	public Map<String, User> getUserCache() {
		return new HashMap<>(userCache);
	}

	private void loadUsers() throws IOException {
		List<User> users = FileManager.readAllUsers(usersFilePath);
		for (User user : users) {
			userCache.put(user.getUsername(), user);
		}
	}
}
