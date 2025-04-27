public class BankAccount {
    private final String accountNumber;
    private double balance;

    // Constructor
    public BankAccount(final String accountNumber, final double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    // Accessor methods
    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }
}
