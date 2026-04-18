import java.io.Serializable;
import java.util.Objects;

public abstract class AbstractUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String username;
    private String hashedPassword;
    private Role role;

    protected AbstractUser(String username, String hashedPassword, Role role) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public abstract void showMenu();

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractUser)) {
            return false;
        }
        AbstractUser that = (AbstractUser) other;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
