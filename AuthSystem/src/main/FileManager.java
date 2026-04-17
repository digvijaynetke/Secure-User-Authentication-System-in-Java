import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileManager {
	private static final String FIELD_SEPARATOR = "|";

	public static void saveUser(AbstractUser user, String usersFilePath) throws IOException {
		Path path = Paths.get(usersFilePath);
		ensureParentDir(path);

		try (BufferedWriter writer = Files.newBufferedWriter(
				path,
				StandardCharsets.UTF_8,
				StandardOpenOption.CREATE,
				StandardOpenOption.APPEND)) {
			writer.write(serializeUser(user));
			writer.newLine();
		}
	}

	public static List<AbstractUser> readAllUsers(String usersFilePath) throws IOException {
		Path path = Paths.get(usersFilePath);
		if (!Files.exists(path)) {
			return new ArrayList<>();
		}

		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		List<AbstractUser> users = new ArrayList<>();

		for (String line : lines) {
			if (line == null || line.trim().isEmpty()) {
				continue;
			}
			AbstractUser user = parseUser(line);
			if (user != null) {
				users.add(user);
			}
		}

		return users;
	}

	public static boolean userExists(String username, String usersFilePath) throws IOException {
		for (AbstractUser user : readAllUsers(usersFilePath)) {
			if (user.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}

	public static void lockUser(String username, String lockedUsersFilePath) throws IOException {
		Path path = Paths.get(lockedUsersFilePath);
		ensureParentDir(path);

		List<String> existing = Files.exists(path)
				? Files.readAllLines(path, StandardCharsets.UTF_8)
				: new ArrayList<>();

		for (String line : existing) {
			if (username.equals(line)) {
				return;
			}
		}

		try (BufferedWriter writer = Files.newBufferedWriter(
				path,
				StandardCharsets.UTF_8,
				StandardOpenOption.CREATE,
				StandardOpenOption.APPEND)) {
			writer.write(username);
			writer.newLine();
		}
	}

	public static Set<String> readLockedUsers(String lockedUsersFilePath) throws IOException {
		Path path = Paths.get(lockedUsersFilePath);
		Set<String> locked = new HashSet<>();
		if (!Files.exists(path)) {
			return locked;
		}

		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		for (String line : lines) {
			if (line == null || line.trim().isEmpty()) {
				continue;
			}
			locked.add(line.trim());
		}

		return locked;
	}

	private static void ensureParentDir(Path path) throws IOException {
		Path parent = path.getParent();
		if (parent != null && !Files.exists(parent)) {
			Files.createDirectories(parent);
		}
	}

	private static String serializeUser(AbstractUser user) {
		return user.getUsername()
				+ FIELD_SEPARATOR + user.getHashedPassword()
				+ FIELD_SEPARATOR + user.getRole().name();
	}

	private static AbstractUser parseUser(String line) {
		String[] parts = line.split("\\|", -1);
		if (parts.length < 3) {
			return null;
		}
		String username = parts[0];
		String hashedPassword = parts[1];
		String roleText = parts[2];
		try {
			Role role = Role.valueOf(roleText);
			return createUser(role, username, hashedPassword);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	private static AbstractUser createUser(Role role, String username, String hashedPassword) {
		switch (role) {
			case ADMIN:
				return new AdminUser(username, hashedPassword);
			case USER:
				return new NormalUser(username, hashedPassword);
			default:
				return null;
		}
	}
}
