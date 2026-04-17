public class NormalUser extends AbstractUser {
    public NormalUser(String username, String hashedPassword) {
        super(username, hashedPassword, Role.USER);
    }

    @Override
    public void showMenu() {
        System.out.println("User menu");
        System.out.println("1) View profile");
        System.out.println("2) Change password");
        System.out.println("3) Logout");
    }
}
