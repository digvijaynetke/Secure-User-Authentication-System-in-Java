import java.io.IOException;

public interface IAuthService {
    void registerUser(String username, String plainPassword, Role role)
            throws UserAlreadyExistsException, IOException;
}
