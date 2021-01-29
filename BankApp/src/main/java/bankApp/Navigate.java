package bankApp;
import java.math.BigDecimal;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.InputMismatchException;
import java.util.Scanner;
public class Navigate {
	
	Scanner scnr = new Scanner(System.in);
	Connection conn = CreateConnection.getConnection();
	Customer c;
	Employee e;
	Account acc;
	String username;
	String password;
	String firstname;
	String lastname;
	int status;
	int statusTwo;
	
	private static final Logger LOGGER = LogManager.getLogger(Navigate.class.getName());
	
	public void start() throws SQLException{
		String choice;
		System.out.println("Welcome to the Bank App!");
		boolean loopFlag = true;
		while (loopFlag == true) {
			System.out.println("Enter 1 to log in, 2 to sign up, or 0 to exit");
			choice = scnr.nextLine();
			
			switch (choice) {
			case "1":
				System.out.println("Enter username: ");
				username = scnr.nextLine();
				System.out.println("Enter password: ");
				password = scnr.nextLine();
				status = login(username,password);
				switch (status) {
					case 1:
						c = new Customer(username, password);
						System.out.println("Welcome " + c.getFirstname() + " " + c.getLastname() + "!");
						loopFlag = false;
						customerMenu();
						break;
					case 2:
						e = new Employee(username, password);
						System.out.println("Welcome " + e.getFirstname() + " " + e.getLastname() + "!");
						loopFlag = false;
						employeeMenu();
						break;
					case 0:
						System.out.println("Invalid username or password.");
						break;
				}
				break;
				
			case "2":
				System.out.println("Enter username: ");
				username = scnr.nextLine();
				System.out.println("Enter password: ");
				password = scnr.nextLine();
				System.out.println("Enter first name: ");
				String firstname = scnr.nextLine();
				System.out.println("Enter last name: ");
				String lastname = scnr.nextLine();
				statusTwo = register(username, password, firstname, lastname);
				switch (statusTwo) {
					case 0:
						System.out.println("Username already taken.");
						System.out.println(choice);
						break;
					case 1:
						System.out.println("Customer account created.");
						break;
				}
				break;
				
			case "0":
				System.out.println(choice);
				System.out.println("Goodbye.");
				LOGGER.info("Application ended.");
				System.exit(0);
				
			default:
				LOGGER.info("Invalid input in beginning menu.");
				System.out.println("Invalid input");
				break;
			}
		}
		
	}
	
	
	public void customerMenu() throws SQLException{
		System.out.println("What would you like to do?");
		boolean loopFlag = true;
		while (loopFlag == true) {
			System.out.println("1. View accounts | 2. Apply for an account | 3. Account menu | 4. Log out | 0. Exit");
			String choice = scnr.nextLine();
			switch (choice) {
			case "1":
				c.viewAccounts();
				break;
			case "2":
				double startbalance = -1;
				System.out.println("Enter a starting balance: ");
				try {
				startbalance = scnr.nextDouble();
				scnr.nextLine();
			
				}catch(InputMismatchException e) {
					System.out.println("Invalid input.");
				}
				//Do checks to make sure balance entered is valid
				if (startbalance > 0 && BigDecimal.valueOf(startbalance).scale() <= 2) {
					c.applyAccount(startbalance);
					System.out.println("Application successful. Please wait for an employee to approve.");
				}
				else {
					System.out.println("Invalid input.");
				}
				break;
			case "3":
				int accnum = -1;
				System.out.println("Enter acount number: ");
				try {
				accnum = scnr.nextInt();
				scnr.nextLine();
				}catch(InputMismatchException e) {
					System.out.println("Invalid input.");

					scnr.nextLine();
					continue;
				}
				acc = new Account(accnum);
				if (acc.getStatus() == 1 && acc.getcid() == c.getid()) {
					loopFlag = false;
					accountMenu();
				}
				else {
					System.out.println("Please enter a valid and approved account.");
				}
				break;
			case "4":
				//logging out will bring you to the beginning of navigate
				loopFlag = false;
				start();
				break;
			case "0":
				System.out.println("Goodbye.");
				LOGGER.info("Application ended.");
				System.exit(0);
			default:
				LOGGER.info("Invalid in customer menu.");
				System.out.println("Invalid input.");
				break;
			}
		}

	}
	
	public void employeeMenu() throws SQLException{
		System.out.println("What would you like to do?");
		boolean loopFlag = true;
		int tempId = -1;
		while (loopFlag == true) {
			System.out.println("1. View a customer's accounts | 2. Register a customer | 3. View pending accounts | 4. Accept an account | 5. Reject an account | 6. View Transaction Log | 7. View Transfer Log | 8. Log out | 0. Exit");
			String choice = scnr.nextLine();
			switch (choice) {
			case "1":
				System.out.println("What's the customer's username?");
				username = scnr.nextLine();
				e.viewCustomer(username);
				break;
			case "2":
				System.out.println("Enter the customer's first name: ");
				firstname = scnr.nextLine();
				System.out.println("Enter the customer's last name: ");
				lastname = scnr.nextLine();
				System.out.println("Enter the customer's username: ");
				username = scnr.nextLine();
				System.out.println("Enter the customer's password: ");
				password = scnr.nextLine();
				int temp = e.register(username, password, firstname, lastname);
				if (temp == 0) {
					System.out.println("Username already taken. Please try again.");
				}
				else {
					double newBalance = -1;
					System.out.println("Enter the customer's starting balance: ");
					try {
					newBalance = scnr.nextDouble();
					scnr.nextLine();
					
					}catch(InputMismatchException e) {
						System.out.println("Invalid input.");
						scnr.nextLine();
						continue;
					}
					//Check for 2 decimal places
					if(BigDecimal.valueOf(newBalance).scale() > 2)
						System.out.println("Invalid input.");
					else {
						c = new Customer(username, password, firstname, lastname);
						c.applyAccount(newBalance);
					}
				}
				break;
			case "3":
				e.viewPending();
				break;
			case "4": 
				System.out.println("Enter the account id: ");
				try {
				tempId = scnr.nextInt();
				scnr.nextLine();
				}catch(InputMismatchException e) {
					System.out.println("Invalid input.");
					scnr.nextLine();
					continue;
				}
				e.acceptAccount(tempId);
				break;
			case "5":
				System.out.println("Enter the account id: ");
				try {
				tempId = scnr.nextInt();
				scnr.nextLine();
				}catch(InputMismatchException e) {
					System.out.println("Invalid input.");
					scnr.nextLine();
					continue;
				}
				e.rejectAccount(tempId);
				break;
			case "6":
				e.viewTransactionLog();
				break;
			case "7":
				e.viewTransferLog();
				break;
			case "8":
				loopFlag = false;
				start();
				break;
			case "0":
				System.out.println("Goodbye.");
				LOGGER.info("Application ended.");
				System.exit(0);
			default:
				LOGGER.info("Invalid input in employee menu.");
				System.out.println("Invalid input.");
				break;
				
			}
		}
	}
	public void accountMenu() throws SQLException{
		System.out.println("What would you like to do?");
		boolean loopFlag = true;
		double amount = -1;
		int transferId = -1;
		while (loopFlag == true) {
			System.out.println("1. View balance | 2. Deposit | 3. Withdraw | 4. View posted transfers | 5. View incoming transfers | 6. Post transfer | 7. Accept transfer | 8. Reject transfer | 9. Go back | 0. Exit");
			String choice = scnr.nextLine();
			switch (choice) {
			case "1":
				acc.viewBalance();
				break;
			case "2":
				System.out.println("How much would you like to deposit?");
				try {
				amount = scnr.nextDouble();
				scnr.nextLine();
				
				}catch(InputMismatchException e) {
					System.out.println("Invalid input.");
					scnr.nextLine();
					continue;
				}
				//Check for 2 decimal places
				if(BigDecimal.valueOf(amount).scale() > 2)
					System.out.println("Invalid input.");
				else {
					acc.deposit(amount);
				}
				break;
			case "3":
				System.out.println("How much would you like to withdraw?");
				try {
				amount = scnr.nextDouble();
				scnr.nextLine();
				}catch(InputMismatchException e) {
					System.out.println("Invalid input.");
					scnr.nextLine();
					continue;
				}
				//Check for 2 decimal places
				if(BigDecimal.valueOf(amount).scale() > 2)
					System.out.println("Invalid input.");
				else {
					acc.withdraw(amount);
				}
				break;
			case "4":
				acc.viewPostedTransfers();
				break;
			case "5":
				acc.viewAcceptableTransfers();
				break;
			case "6": 
				int transferAccNum = -1;
				System.out.println("Enter account number to transfer to: ");
				try {
				transferAccNum = scnr.nextInt();
				scnr.nextLine();
				System.out.println("Enter amount to transfer: ");
				amount = scnr.nextDouble();
				scnr.nextLine();
				
				}catch(InputMismatchException e) {
					System.out.println("Invalid input.");
					scnr.nextLine();
					continue;
				}
				//Check for 2 decimal places
				if(BigDecimal.valueOf(amount).scale() > 2)
					System.out.println("Invalid input.");
				else {
				acc.postTransfer(amount, transferAccNum);
				}
				break;
			case "7":
				System.out.println("Enter Transfer ID: ");
				try {
				transferId = scnr.nextInt();
				scnr.nextLine();
				}catch(InputMismatchException e) {
					System.out.println("Invalid input.");
					scnr.nextLine();
					continue;
				}
				acc.acceptTransfer(transferId);
				break;
			case "8":
				System.out.println("Enter Transfer ID: ");
				try {
				transferId = scnr.nextInt();
				scnr.nextLine();
				}catch(InputMismatchException e) {
					System.out.println("Invalid input.");
					scnr.nextLine();
					continue;
				}
				acc.rejectTransfer(transferId);
				break;
			case "9":
				loopFlag = false;
				customerMenu();
				break;
			case "0":
				System.out.println("Goodbye.");
				LOGGER.info("Application ended.");
				System.exit(0);
			default:
				LOGGER.info("Invalid input in account menu.");
				System.out.println("Invalid input.");
				break;
			}
		}	
		
	}
	
	
	public int login(String username, String password) throws SQLException{
		//first check to see if its a valid username and password
		CallableStatement cstmt=conn.prepareCall("CALL check_valid_login(?,?,?)");
		cstmt.setString(1, username);
		cstmt.setString(2, password);
		cstmt.setInt(3, -1);
		cstmt.registerOutParameter(3, java.sql.Types.INTEGER);
		cstmt.execute();
		int status = cstmt.getInt(3);
		
		LOGGER.info("Login executed. Status: " + status);
		return status;
		//status 0 = not valid, 1 = valid customer, 2 = valid employee
	}
	
	public int register(String username, String password, String firstname, String lastname) throws SQLException{
		//first check to see if its an unused username
		CallableStatement cstmt=conn.prepareCall("CALL register_user(?,?,?,?,?)");
		cstmt.setString(1, username);
		cstmt.setString(2, password);
		cstmt.setString(3, firstname);
		cstmt.setString(4, lastname);
		cstmt.setInt(5, -1);
		cstmt.registerOutParameter(5, java.sql.Types.INTEGER);
		cstmt.execute();
		int status = cstmt.getInt(5);
		
		LOGGER.info("Registered for customer. Status: " + status);
		return status;
		//status 0 = not made, status 1 = made
		
	}
}

