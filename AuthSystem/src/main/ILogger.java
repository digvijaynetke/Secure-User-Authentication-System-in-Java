import java.io.IOException;

public interface ILogger {
    void log(LogLevel level, String message) throws IOException;
}
