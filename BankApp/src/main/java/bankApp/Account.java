  package bankApp;


import java.sql.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Account {
	private int accnum;
	private double balance;
	private int status;
	private int cid;
	
	private static final Logger LOGGER = LogManager.getLogger(Account.class.getName());
	
	Connection conn = CreateConnection.getConnection();
	
	public Account(int accnum) {
		super();
		this.accnum = accnum;
		try {
			retrieveAccountInfo();
			retrieveCid();
		} catch (SQLException e) {
			System.out.println("An error has occured.");
		}
	}
	public Account(int accnum, double balance, int status) {
		super();
		this.accnum = accnum;
		this.balance = balance;
		this.status = status;
		try {
			retrieveCid();
		} catch (SQLException e) {
			System.out.println("An error has occured.");
		}
	}
	
	public int getStatus() {
		return status;
	}
	public int getcid() {
		return cid;
	}
	
	
	public void retrieveAccountInfo() throws SQLException{
		CallableStatement cstmt=conn.prepareCall("Call retrieve_account_info(?,?,?)");
		cstmt.setInt(1, accnum);
		cstmt.setDouble(2, -1);
		cstmt.setInt(3, -1);
		cstmt.registerOutParameter(2, java.sql.Types.DOUBLE);
		cstmt.registerOutParameter(3, java.sql.Types.INTEGER);
		cstmt.execute();
		balance = cstmt.getDouble(2);
		status = cstmt.getInt(3);
	}
	
	public void retrieveCid() throws SQLException{
		CallableStatement cstmt=conn.prepareCall("Call retrieve_cid(?,?)");
		cstmt.setInt(1, accnum);
		cstmt.setInt(2, -1);
		cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
		cstmt.execute();
		cid = cstmt.getInt(2);
	}
	
	public void viewBalance() throws SQLException{
		CallableStatement cstmt=conn.prepareCall("CALL view_balance(?,?)");
		cstmt.setInt(1, accnum);
		cstmt.setDouble(2, -1);
		cstmt.registerOutParameter(2, java.sql.Types.DOUBLE);
		cstmt.execute();
		double viewBalance = cstmt.getDouble(2);
		System.out.printf("Balance: %.2f\n", viewBalance);
	}
	
	public int deposit(double amount) throws SQLException{
		CallableStatement cstmt=conn.prepareCall("CALL deposit(?,?,?)");
		cstmt.setDouble(1, amount);
		cstmt.setInt(2, accnum);
		cstmt.setInt(3, -1);
		cstmt.registerOutParameter(3, java.sql.Types.INTEGER);
		cstmt.execute();
		int status = cstmt.getInt(3);
		//status = 0 means deposit failed, 1 = successful
		if (status == 0) {
			System.out.println("Deposit failed. Please enter a valid amount.");
		}
		else if (status == 1) {
			System.out.println("Deposit complete.");
		}
		
		LOGGER.info("Deposited " + amount + " into account " + accnum + ". Status: " + status);
		return status;
	}
	
	public int withdraw(double amount) throws SQLException{
		CallableStatement cstmt=conn.prepareCall("CALL withdraw(?,?,?)");
		cstmt.setDouble(1, amount);
		cstmt.setInt(2, accnum);
		cstmt.setInt(3, -1);
		cstmt.registerOutParameter(3, java.sql.Types.INTEGER);
		cstmt.execute();
		int status = cstmt.getInt(3);
		//status = 0 means withdraw failed, 1 = successful
		if (status == 0) {
			System.out.println("Withdrawal failed. Please enter a valid amount.");
		}
		else if (status == 1) {
			System.out.println("Withdrawal complete.");
		}
		
		LOGGER.info("Withdrew " + amount + " from account " + accnum + ". Status: " + status);
		return status;
	}
	
	public void viewPostedTransfers() throws SQLException{
		System.out.println("List of posted transfers:");
		PreparedStatement statement = conn.prepareStatement("SELECT receive_accnum, amount, status FROM money_transfer WHERE post_accnum = ?");
		statement.setInt(1, accnum);
		ResultSet rs = statement.executeQuery();
        while (rs.next()) {
        	if (rs.getInt(3) == 0)
        		System.out.println("Transfer to account number: " + rs.getInt(1) + "\tAmount: " + rs.getDouble(2) + "\tStatus: PENDING");
        	else if (rs.getInt(3) == 1)
        		System.out.println("Transfer to account number: " + rs.getInt(1) + "\tAmount: " + rs.getDouble(2) + "\tStatus: ACCEPTED");
        	else if (rs.getInt(3) == 2)
        		System.out.println("Transfer to account number: " + rs.getInt(1) + "\tAmount: " + rs.getDouble(2) + "\tStatus: REJECTED");
        }
	}
	
	public void viewAcceptableTransfers() throws SQLException{
		System.out.println("List of pending transfers:");
		PreparedStatement statement = conn.prepareStatement("SELECT tid, post_accnum, amount FROM money_transfer WHERE receive_accnum = ? AND status = 0");
		statement.setInt(1, accnum);
		ResultSet rs = statement.executeQuery();
        while (rs.next()) {
        		System.out.println("Transfer ID: " + rs.getInt(1) + "\tAccount: " + rs.getInt(2) + "\tAmount: " + rs.getDouble(3));
        }
	}
	
	public void postTransfer(double amount, int receivingAcc) throws SQLException{
		CallableStatement cstmt=conn.prepareCall("CALL post_transfer(?,?,?,?)");
		cstmt.setInt(1, accnum);
		cstmt.setInt(2, receivingAcc);
		cstmt.setDouble(3, amount);
		cstmt.setInt(4, -1);
		cstmt.registerOutParameter(4, java.sql.Types.INTEGER);
		cstmt.execute();
		int check = cstmt.getInt(4);
		if (check == 1)
			System.out.println("Transfer posted.");
		else
			System.out.println("Invalid amount or account number.");
		
		LOGGER.info("Posted transfer of " + amount + " from account " + accnum + " to " + receivingAcc + ". Status: " + check);
	}
	
	public void acceptTransfer(int transferid) throws SQLException{
		CallableStatement cstmt=conn.prepareCall("CALL accept_transfer(?,?)");
		cstmt.setInt(1, transferid);
		cstmt.setInt(2, -1);
		cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
		cstmt.execute();
		int check = cstmt.getInt(2);
		if (check == 0)
			System.out.println("Invalid transfer id");
		else if (check == 1)
			System.out.println("Transfer accepted.");
		
		LOGGER.info("Accepted transfer transfer ID " + transferid + ". Status: " + status);
	}
	
	public void rejectTransfer(int transferid) throws SQLException{
		CallableStatement cstmt=conn.prepareCall("CALL reject_transfer(?,?)");
		cstmt.setInt(1, transferid);
		cstmt.setInt(2, -1);
		cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
		cstmt.execute();
		int check = cstmt.getInt(2);
		if (check == 0)
			System.out.println("Invalid transfer id");
		else if (check == 1)
			System.out.println("Transfer rejected.");
		
		LOGGER.info("Rejected transfer transfer ID " + transferid + ". Status: " + status);
	}
	
}
