
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BankSystem {

    private static final String URL = "jdbc:mysql://localhost:3306/bank_system";  // Change to your MySQL username
    private static final String USER = "root"; // Change to your MySQL username
    private static final String PASSWORD = "password"; // Change to your MySQL password

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Sample customers
        System.out.println("Welcome to the ATU Bank System");

        while (true) {
            System.out.println("\n1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1: {
                    System.out.println("Create Acc - call you Create Account Method here..");
                    scanner.nextLine();
                    createAccount();
                }
                case 2: {
                    System.out.println("Login Acc - call you Login Method here..");
                    login();
                }
                case 3: {
                    System.out.println("Thank you for using the ATU Bank System. Goodbye!");
                    return;
                }
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    // this method will create a users account
    public static void createAccount() {
        boolean loggedIn = false;
        String email;
        String password;
        double balance = 0;

        // enter AccountNo
        // enter password
        // store in database

        while (true) {
            System.out.print("Enter your AccountNo ");
            if (scanner.hasNextLine()) {
            	email = scanner.nextLine();
                if (!email.isEmpty()) {
                    System.out.println("AccountNo: " + email);
                    break;
                } else {
                    System.out.println("Invalid input. Please enter a valid AccountNo.");
                }
            }
        }
//        String PASSWORDREGEX = "[A-Za-z]+#[0-9]+";
//        Pattern passwordPattern = Pattern.compile(PASSWORDREGEX);
//        && !passwordMatcher.matches()
        while (true) {
            
            System.out.print("Enter your password ");
            if (scanner.hasNextLine()) {
                password = scanner.nextLine();
                if (!password.isEmpty()) {
                    System.out.println("Password: " +password);
                    break;
                } else {
                    System.out.println("Invalid input. Please enter a valid name.");
                }
            }
        }

        while (true) {
            System.out.print("Enter initial deposit: ");
            if (scanner.hasNextDouble()) {
                balance = scanner.nextDouble();
                scanner.nextLine();
                if (balance >= 0) {
                    System.out.println("Balance: " + balance);
                    break;
                } else {
                    System.out.println("Invalid input. Please enter a valid Balance.");
                }
            }
        }

        // try connect to database
        // add the user details
        // catch any exceptions
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement stmt = conn.createStatement();) {

        	String sql = "INSERT INTO customers (`email`, `password`, `balance`) VALUES (?, ?, ?)";
            PreparedStatement query = conn.prepareStatement(sql);
            		query.setString(1, email);
            query.setString(2, new String(password));  // Convert char[] to String
            query.setDouble(3, balance);
            System.out.println(sql);
            stmt.executeUpdate(sql);
            System.out.println("Account successfully created for " + email);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // this method will valid the users login
    public static void login() {

        // enter username
        // enter password
        // try connect to database
        // validate the AccountNo and password
        //Password should be the char[]
        System.out.print("Enter AccountNo: ");
        String email = scanner.next();

        System.out.print("Enter Password: ");
        String password = scanner.next();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement stmt = conn.createStatement();) {

            String sql = "SELECT * FROM customers WHERE email = '"+email+"' AND password ='"+password+"';";
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            System.out.println(rs);
            //check user matches
            // if(users.containsKey(username) && users.get(username).equals(password))
            // if(rs.containsKey(accountNo) && rs.get(accountNo).equals(password))
            // {
            //     System.out.println("Logged in correctly");
            // }
            // else
            // {
            //     System.out.println(" Incorrect");
            // }

            if (rs.next()) {
                System.out.println("Login successful!");
                validCustomer(email); // the are a valid customer
            } else {
                System.out.println("Invalid username or password.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this method is called when the login has been successfull
    public static void validCustomer(String email) {
        while (true) {
            System.out.println("\n1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Logout");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    checkBalance(email);
                    break;
                case 2:
                    System.out.print("Enter deposit amount: ");
                    double depositAmount = scanner.nextDouble();
                    updateBalance(email, depositAmount, true);
                    break;
                case 3:
                    System.out.print("Enter withdrawal amount: ");
                    double withdrawAmount = scanner.nextDouble();
                    updateBalance(email, withdrawAmount, false);
                    break;
                case 4:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    public static void checkBalance(String email) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement stmt = conn.createStatement();) {

            String sql = "SELECT balance FROM customers WHERE email = '"+email+"';";
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                System.out.println("Current balance: $" + rs.getDouble("balance"));
            } else {
                System.out.println("User not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateBalance(String email, double amount, boolean isDeposit) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement stmt = conn.createStatement();) {

            // Check current balance
            double currentBalance = 0;
            String sql = "SELECT balance FROM customers WHERE email = '"+email+"';";
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                currentBalance = rs.getDouble("balance");
            }

            if (!isDeposit && amount > currentBalance) {
                System.out.println("Insufficient funds.");
                return;
            }

            double newBalance;
            if (isDeposit) // if true
            {
                newBalance = currentBalance + amount;
            } else { // else withdraw money
                newBalance = currentBalance - amount;
            }

            // Update balance
            Statement stmtupdate = conn.createStatement();
            String sqlupdate = "UPDATE customers SET balance =" + newBalance + "WHERE email = '" +email+"';";

            stmtupdate.executeUpdate(sqlupdate);
            System.out.println("successful! New balance: $" + newBalance);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
