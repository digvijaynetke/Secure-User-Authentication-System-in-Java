import java.io.IOException;

public class Logger {
	public static void logEvent(String message, String logsFilePath) throws IOException {
		log(LogLevel.INFO, message, logsFilePath);
	}

	public static void log(LogLevel level, String message, String logsFilePath) throws IOException {
		FileLogger.getInstance(logsFilePath).log(level, message);
	}
}
