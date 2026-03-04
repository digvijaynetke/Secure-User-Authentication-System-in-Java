public class User {
	private final String username;
	private String hashedPassword;
	private Role role;

	public User(String username, String hashedPassword, Role role) {
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
}
