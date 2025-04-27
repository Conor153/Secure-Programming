
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.*;
import java.security.SecureRandom;

public class BankSystem {

	// SQL Connection Details
	private static final String URL = "jdbc:mysql://localhost:3306/bank_system";
	private static final String USER = "root";
	private static final String PASSWORD = "password";
	// Scanner
	private static final Scanner scanner = new Scanner(System.in);

	//
	// MAIN Method
	//
	public static void main(String[] args) {
		System.out.println("Welcome to the ATU Bank System");

		// While loop to present menu details
		while (true) {
			System.out.println("*** Main Menu ***");
			System.out.println("\n1. Create Account");
			System.out.println("2. Login");
			System.out.println("3. Exit");
			System.out.print("Select an option: ");
			// Try to check that the user enters a valid number
			try {
				int choice = scanner.nextInt();
				// Switch to allow user to select their option and call
				// appropriate method
				switch (choice) {
				case 1: {
					// Create Account
					System.out.println("Create Account");
					// Clear Scanner Buffer
					scanner.nextLine();
					createAccount();
					// Break out and prevent run on into other cases
					break;
				}
				case 2: {
					// Login to Account
					System.out.println("Login Account");
					scanner.nextLine();
					login();
					break;
				}
				case 3: {
					// Terminate Program
					System.out.println("Thank you for using the ATU Bank System. Goodbye!");
					return;
				}
				default:
					//
					System.out.println("Invalid option. Try again.");
				}

			} catch (InputMismatchException e) {
				// Catch a InputMismatchException if user enters a letter or
				// special character instead of a number
				System.out.println("Invalid Entry. Please enter a valid integer number");
				scanner.nextLine();
			}
		}
	}

	// Create Account method
	private static void createAccount() {
		String email;
		String password;
		double balance = 0;

		// Regex added to further prevent sql injection attack by
		// Ensuring the data is in the correct format before
		// Executed to the database
		// Check that enetred email matches following format
		// example@gmail.com
		final String EMAILREGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		final Pattern pattern = Pattern.compile(EMAILREGEX);
		// Check that enetred password matches following format
		// Letterkenny#2025
		final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}+$";
		final Pattern passwordPattern = Pattern.compile(PASSWORD_REGEX);
		try {
			// Get user to enter their Account Email
			while (true) {
				System.out.print("Enter your Account Email ");
				// Validate that the user has entered a string
				if (scanner.hasNextLine()) {
					email = scanner.nextLine();
					// Check that the email has not already been taken
					boolean available = checkEmail(email);
					// Check the email meets the required format
					Matcher matcher = pattern.matcher(email);
					// Check entered email is valid
					if (!email.isEmpty() && matcher.matches() && available) {
						// Inform user their selected email
						System.out.println("Account Email: " + email);
						// Break out of while loop
						break;
					} else if (!available) {
						System.out.println("An account with this email already exists. Please use a different email.");
					} else {
						System.out.println("Invalid input. Please enter a valid email.");
					}
				}
			}
			while (true) {
				System.out.print("Enter your password ");
				// Validate that the user has entered a string
				if (scanner.hasNextLine()) {
					password = scanner.nextLine();
					// Check that password is in the correct format
					Matcher matcher = passwordPattern.matcher(password);
					if (!password.isEmpty() && matcher.matches()) {
						System.out.println("Password: " + password);
						// Break out of while loop
						break;
					} else {
						// Inform user of the correct format needed
						System.out.println("Invalid input. Please enter a password that contains the following.");
						System.out.println("At Least 8 Characters long");
						System.out.println("1 Captial Letter");
						System.out.println("1 Special Character");
					}
				}
			}

			while (true) {
				System.out.print("Enter initial deposit: ");
				// Check that user has entered a valid number
				if (scanner.hasNextDouble()) {
					balance = scanner.nextDouble();
					scanner.nextLine();
					// Check that the number is not less than 0
					if (balance >= 0) {
						System.out.println("Balance: " + balance);
						break;
					}
					// Inform the user that the input is invalid
				} else {
					System.out.println("Invalid input. Please enter a number.");
					scanner.nextLine();
				}
			}
			// try connect to database
			Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
			// Create a prepared statement to prevent invalid data being submitted to db
			// and prevent injection attack
			final String sql = "INSERT INTO customers (`email`, `password`, `balance`) VALUES (?, ?, ?)";
			PreparedStatement query = conn.prepareStatement(sql);
			// Attach users details to the prepared statement
			query.setString(1, email);
			query.setString(2, password);
			query.setDouble(3, balance);
			// Execute SQL statement
			query.executeUpdate();
			System.out.println("Account successfully created for " + email);
			// Catch exceptions that may have occurred when selecting email, password or
			// balance
			// Or any issue with the SQL query or DB connection
		} catch (InputMismatchException e) {
			System.out.println("An invalid value was entered. Try Again and Please re-enter the data correctly");
		} catch (NullPointerException e) {
			System.out.println("An value was entered empty. Try Again and Please re-enter the data correctly");
		} catch (SQLIntegrityConstraintViolationException e) {
			System.out.println("An account with this email already exists. Try Again using a different email.");
		} catch (SQLSyntaxErrorException e) {
			System.out.println("An error occured while creatig your account try again");
		} catch (SQLTimeoutException e) {
			System.out.println("Server took to long to respond. Try Again");
		} catch (SQLException e) {
			System.out.println("Database error occurred.");
		} catch (Exception e) {
			System.out.println("An Error occurred try again.");
		}

	}

	// Login Method
	private static void login() {
		// Give the user 3 attempts to login to the system
		int attempts = 3;
		String email;
		String password;
		final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		final Pattern pattern = Pattern.compile(emailRegex);

		// When attempts are less than 1 break and return to main menu
		while (attempts > 0) {
			try {
				while (true) {
					// Get user to enter their Account Email
					System.out.print("Enter your Account Email");
					// Check that they have entered a string
					if (scanner.hasNextLine()) {
						email = scanner.nextLine();
						Matcher matcher = pattern.matcher(email);
						if (!email.isEmpty() && matcher.matches()) {
							System.out.println("Account Email: " + email);
							break;
						} else {
							System.out.println("Invalid input. Please enter a valid AccountNo.");
						}
					}
				}
				// Get the user to enter a password
				while (true) {
					System.out.print("Enter your password ");
					// Check that they have entered a string
					if (scanner.hasNextLine()) {
						password = scanner.nextLine();
						if (!password.isEmpty()) {
							System.out.println("Password: " + password);
							break;
						} else {
							System.out.println("Invalid input. Please enter a valid name.");
						}
					}
				}

				// Connect to the DB
				final Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				// Create an SQL query which finds a user with the same email
				// And password that they have entered
				final String sql = "Select * FROM customers WHERE email = ? AND password = ?;";
				PreparedStatement query = conn.prepareStatement(sql);
				query.setString(1, email);
				query.setString(2, password);
				ResultSet rs = query.executeQuery();
				// check user matches
				// If a record is returned then the correct details have been entered
				if (rs.next()) {
					System.out.println("Login successful!");
//					double balance = rs.getDouble("balance");
//					BankAccount ba = new BankAccount(email, balance);
					// Call the valid Customer method
					validCustomer(email);
					// When logged out of the validCustomer method
					// break out of the login method and return to the main method
					break;
				} else {
					// If user has entered incorrect details inform
					// the user that they have entered incorrect details
					System.out.println("Invalid username or password.");
					// Reduce the number of attempts they have left by 1
					attempts--;
				}

			} catch (InputMismatchException e) {
				System.out.println("An invalid value was entered. Try Again and Please re-enter the data correctly");
			} catch (NullPointerException e) {
				System.out.println("An value was entered empty. Try Again and Please re-enter the data correctly");
			} catch (SQLIntegrityConstraintViolationException e) {
				System.out.println("An account with this email already exists. Try Again using a different email.");
			} catch (SQLSyntaxErrorException e) {
				System.out.println("An error occured while creatig your account try again");
			} catch (SQLTimeoutException e) {
				System.out.println("Server took to login to respond. Try Again");
			} catch (SQLException e) {
				System.out.println("Database error occurred.");
			} catch (Exception e) {
				System.out.println("An Error occurred try again.");
			}
			// Inform user the number of attempts remaining
			System.out.println("Attempts remaining " + attempts);
		}
	}

	// this method is called when the login has been successful
	private static void validCustomer(final String email) {
		// Try to check that the user enters a valid number

		while (true) {
			try {
				System.out.println("\n1. Check Balance");
				System.out.println("2. Deposit");
				System.out.println("3. Withdraw");
				System.out.println("4. Logout");
				System.out.print("Select an option: ");
				// Switch to allow user to select their option and call
				// appropriate method
				int choice = scanner.nextInt();

				switch (choice) {
				case 1:
					// Allow the user to select
					checkBalance(email);
					break;
				case 2:
					// Allow the user to enter they amount that they want to deposit
					double depositAmount;
					while (true) {
						System.out.print("Enter deposit amount: ");
						// Check that user has entered a valid number
						if (scanner.hasNextDouble()) {
							depositAmount = scanner.nextDouble();
							scanner.nextLine();
							// Check that the number is greater than 0
							if (depositAmount > 0) {
								break;
							}
							// Inform the user that the input is invalid
						} else {
							System.out.println("Invalid input. Please enter a number.");
							scanner.next();
						}
					}
					// Call the method to update the users balance
					updateBalance(email, depositAmount, true);
					break;
				case 3:
					double withdrawAmount;
					while (true) {
						System.out.print("Enter withdrawal amount: ");
						// Check that user has entered a valid number
						if (scanner.hasNextDouble()) {
							withdrawAmount = scanner.nextDouble();
							scanner.nextLine();
							// Check that the number is greater than 0
							if (withdrawAmount > 0) {
								break;
							}
							// Inform the user that the input is invalid
						} else {
							System.out.println("Invalid input. Please enter a number.");
							scanner.next();
						}
					}
					// Call the method to update the users balance
					updateBalance(email, withdrawAmount, false);
					break;
				case 4:
					// Log out of users account and return to menu
					System.out.println("Logging out...");
					return;
				default:
					// User entered an invalid number
					System.out.println("Invalid option. Try again.");
				}

				// Catch an input error
			} catch (InputMismatchException e) {
				// Catch a InputMismatchException if user enters a letter or
				// special character instead of a number
				System.out.println("Invalid Entry. Please enter a valid integer number");
				scanner.next();
			}
		}
	}

	// Method to check that email is not already in use
	private static boolean checkEmail(final String email) {
		try {
			// Connect to DB
			final Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
			// Check for a DB record that contains the entered email
			// Create a prepared statement to prevent invalid data being submitted to db
			// and prevent injection attack
			final String sql = "SELECT * FROM customers WHERE email = ?;";
			PreparedStatement query = conn.prepareStatement(sql);
			query.setString(1, email);
			final ResultSet rs = query.executeQuery();

			if (rs.next()) {
				// If a record is returned then the email is in use
				// Return false
				return false;
			} else {
				// Else if no record is returned the email is not in use
				// Return true
				return true;
			}
			// If an exception is thrown then inform the user of an error
			// Return false so that the entered email cannot be used
		} catch (InputMismatchException e) {
			System.out.println("An invalid value was entered. Try Again and Please re-enter the data correctly");
			return false;
		} catch (NullPointerException e) {
			System.out.println("An value was entered empty. Try Again and Please re-enter the data correctly");
			return false;
		} catch (SQLIntegrityConstraintViolationException e) {
			System.out.println("An account with this email already exists. Try Again using a different email.");
			return false;
		} catch (SQLSyntaxErrorException e) {
			System.out.println("An error occured while creatig your account try again");
			return false;
		} catch (SQLTimeoutException e) {
			System.out.println("Server took to login to respond. Try Again");
			return false;
		} catch (SQLException e) {
			System.out.println("Database error occurred.");
			return false;
		} catch (Exception e) {
			System.out.println("An Error occurred try again.");
			return false;
		}
//		finally 
//		{
//			return false;
//		}
	}

	private static void checkBalance(final String email) {
		try {
			// Connect to DB
			final Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
			// Return the balance of the logged in user
			// Create a prepared statement to prevent invalid data being submitted to db
			// and prevent injection attack
			final String sql = "SELECT balance FROM customers WHERE email = ? ;";
			PreparedStatement query = conn.prepareStatement(sql);
			query.setString(1, email);
			final ResultSet rs = query.executeQuery();

			if (rs.next()) {
				// Display the users balance
				System.out.println("Current balance: $" + rs.getDouble("balance"));
			} else {
				System.out.println("User not found.");
			}
			// If an exception is thrown then inform the user that an error has occurred
		} catch (InputMismatchException e) {
			System.out.println("An invalid value was entered. Try Again and Please re-enter the data correctly");
		} catch (NullPointerException e) {
			System.out.println("An value was entered empty. Try Again and Please re-enter the data correctly");
		} catch (SQLIntegrityConstraintViolationException e) {
			System.out.println("An account with this email already exists. Try Again using a different email.");
		} catch (SQLSyntaxErrorException e) {
			System.out.println("An error occured while creatig your account try again");
		} catch (SQLTimeoutException e) {
			System.out.println("Server took to login to respond. Try Again");
		} catch (SQLException e) {
			System.out.println("Database error occurred.");
		} catch (Exception e) {
			System.out.println("An Error occurred try again.");
		}
	}

	// Update balance method to Deposit or Withdraw money
	private static void updateBalance(final String email, final double amount, final boolean isDeposit) {
		try {
			// Connect to DB
			final Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

			double currentBalance = 0;
			// Get the users current balance
			// Create a prepared statement to prevent invalid data being submitted to db
			// and prevent injection attack
			final String sql = "SELECT balance FROM customers WHERE email = ? ;";
			PreparedStatement query = conn.prepareStatement(sql);
			query.setString(1, email);
			final ResultSet rs = query.executeQuery();

			if (rs.next()) {
				// set the users current balance so that
				// it can add to the deposit
				// or subtract the withdraw
				currentBalance = rs.getDouble("balance");
			}

			// If the user is withdrawing
			// And they are withdrawing more than their balance
			// return to menu
			if (!isDeposit && amount > currentBalance) {
				System.out.println("Insufficient funds.");
				return;
			}

			// Calculate the users new balance
			double newBalance;
			if (isDeposit) // if true deposit money
			{
				newBalance = currentBalance + amount;
			} else { // else withdraw money
				newBalance = currentBalance - amount;
			}

			// Set the logged in users balance to the newBalance
			// Create a prepared statement to prevent invalid data being submitted to db
			// and prevent injection attack
			final String sql2 = "UPDATE customers SET balance = ? WHERE email = ? ;";
			PreparedStatement query2 = conn.prepareStatement(sql2);
			query2.setDouble(1, newBalance);
			query2.setString(2, email);
			query2.executeUpdate();
			// Display their balance
			System.out.println("successful! New balance: $" + newBalance);
			// Catch any exception that has occurred
		} catch (ArithmeticException e) {
			System.out.println("An arithmetic error has occured. Try Again");
		} catch (InputMismatchException e) {
			System.out.println("An invalid value was entered. Try Again and Please re-enter the data correctly");
		} catch (NullPointerException e) {
			System.out.println("An value was entered empty. Try Again and Please re-enter the data correctly");
		} catch (SQLIntegrityConstraintViolationException e) {
			System.out.println("An account with this email already exists. Try Again using a different email.");
		} catch (SQLSyntaxErrorException e) {
			System.out.println("An error occured while creatig your account try again");
		} catch (SQLTimeoutException e) {
			System.out.println("Server took to login to respond. Try Again");
		} catch (SQLException e) {
			System.out.println("Database error occurred.");
		} catch (Exception e) {
			System.out.println("An Error occurred try again.");
		}
	}
}
