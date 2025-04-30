import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BankSystemTest {

	@Test
	public void testCreateAccount() {
		BankAccount ba = new BankAccount("test@sample2.com", 50.0, "Abc1234$");
		assertEquals(true, BankSystem.createAccount(ba.getAccountEmail(), ba.getPassword(), ba.getBalance()));
	}
	
	@Test
	public void testLogin() {
		BankAccount ba = new BankAccount("test@sample2.com", 50.0, "Abc1234$");
		assertEquals(true, BankSystem.login(ba.getAccountEmail(), ba.getPassword()));
	}

	@Test
	public void testUpdateBalanceWithrawl() {
		BankAccount ba = new BankAccount("test@example.com", 50.0, "Abc1234$");
		assertEquals(20.0, BankSystem.updateBalance(ba.getAccountEmail(), 30, false, false));
	}
	
	@Test
	public void testUpdateBalanceDeposit() {
		BankAccount ba = new BankAccount("test@example.com", 50.0, "Abc1234$");
		assertEquals(40.0, BankSystem.updateBalance(ba.getAccountEmail(), 20, true, true));
	}

	@Test
	public void testCheckBalance() {
		BankAccount ba = new BankAccount("test@example.com", 50.0, "Abc1234$");
		assertEquals(40.0, BankSystem.checkBalance(ba.getAccountEmail(), 0));
	}

}
