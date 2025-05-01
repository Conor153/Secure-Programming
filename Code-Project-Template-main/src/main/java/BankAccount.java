public class BankAccount {
	private final String accountEmail;
	private final String password;
	private double balance;

	// Constructor
	protected BankAccount(final String accountEmail, final double balance, final String password) {
		this.accountEmail = accountEmail;
		this.balance = balance;
		this.password = password;
	}

	// Accessor methods
	protected String getAccountEmail() {
		return accountEmail;
	}

	protected double getBalance() {
		return balance;
	}

	protected String getPassword() {
		return password;
	}
}
