import java.io.IOException;

public interface IAuthService {
    void registerUser(String username, String plainPassword, Role role)
            throws UserAlreadyExistsException, IOException;

    AbstractUser loginUser(String username, String plainPassword)
            throws AccountLockedException, InvalidCredentialsException, IOException;

    void changePassword(AbstractUser user, String newPlainPassword) throws IOException;

    java.util.List<AbstractUser> listUsers();

    java.util.List<String> readLogs() throws IOException;
}
