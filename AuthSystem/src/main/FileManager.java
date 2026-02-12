import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
	private static final String FIELD_SEPARATOR = "|";

	public static void saveUser(User user, String usersFilePath) throws IOException {
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

	public static List<User> readAllUsers(String usersFilePath) throws IOException {
		Path path = Paths.get(usersFilePath);
		if (!Files.exists(path)) {
			return new ArrayList<>();
		}

		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		List<User> users = new ArrayList<>();

		for (String line : lines) {
			if (line == null || line.trim().isEmpty()) {
				continue;
			}
			User user = parseUser(line);
			if (user != null) {
				users.add(user);
			}
		}

		return users;
	}

	public static boolean userExists(String username, String usersFilePath) throws IOException {
		for (User user : readAllUsers(usersFilePath)) {
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

	private static void ensureParentDir(Path path) throws IOException {
		Path parent = path.getParent();
		if (parent != null && !Files.exists(parent)) {
			Files.createDirectories(parent);
		}
	}

	private static String serializeUser(User user) {
		return user.getUsername()
				+ FIELD_SEPARATOR + user.getHashedPassword()
				+ FIELD_SEPARATOR + user.getRole();
	}

	private static User parseUser(String line) {
		String[] parts = line.split("\\|", -1);
		if (parts.length < 3) {
			return null;
		}
		String username = parts[0];
		String hashedPassword = parts[1];
		String role = parts[2];
		return new User(username, hashedPassword, role);
	}
}
