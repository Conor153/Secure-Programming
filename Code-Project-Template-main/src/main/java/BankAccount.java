public class BankAccount {
	private final String accountEmail;
	private final String password;
	private double balance;

	// Constructor
	public BankAccount(final String accountEmail, final double balance, final String password) {
		this.accountEmail = accountEmail;
		this.balance = balance;
		this.password = password;
	}

	// Accessor methods
	public String getAccountEmail() {
		return accountEmail;
	}

	public double getBalance() {
		return balance;
	}

	public String getPassword() {
		return password;
	}
}
