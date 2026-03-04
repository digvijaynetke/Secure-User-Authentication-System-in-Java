import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
	private static final DateTimeFormatter TIMESTAMP_FORMAT =
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static void logEvent(String message, String logsFilePath) throws IOException {
		Path path = Paths.get(logsFilePath);
		ensureParentDir(path);
		String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);

		try (BufferedWriter writer = Files.newBufferedWriter(
				path,
				StandardCharsets.UTF_8,
				StandardOpenOption.CREATE,
				StandardOpenOption.APPEND)) {
			writer.write(timestamp + " | " + message);
			writer.newLine();
		}
	}

	private static void ensureParentDir(Path path) throws IOException {
		Path parent = path.getParent();
		if (parent != null && !Files.exists(parent)) {
			Files.createDirectories(parent);
		}
	}
}
