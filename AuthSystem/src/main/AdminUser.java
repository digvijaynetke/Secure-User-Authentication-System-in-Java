public class AdminUser extends AbstractUser {
    public AdminUser(String username, String hashedPassword) {
        super(username, hashedPassword, Role.ADMIN);
    }

    @Override
    public void showMenu() {
        System.out.println("Admin menu");
        System.out.println("1) View users");
        System.out.println("2) View logs");
        System.out.println("3) Logout");
    }
}
