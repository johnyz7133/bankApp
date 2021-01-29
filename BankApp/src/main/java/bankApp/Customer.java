package bankApp;


import java.sql.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Customer {
	private String username;
	private String password;
	private String firstname;
	private String lastname;
	private int id;
	private static final Logger LOGGER = LogManager.getLogger(Customer.class.getName());
	
	Connection conn = CreateConnection.getConnection();
	
	public Customer(String username, String password) {
		super();
		this.username = username;
		this.password = password;
		try{
			retrieveCustomerName();
			retrieveId();
		}catch (SQLException e) {
			System.out.println("An error has occured.");
		}
	}
	
	
	public Customer(String username, String password, String firstname, String lastname) {
		super();
		this.username = username;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		try {
			retrieveId();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}
	public int getid() {
		return id;
	}

	public void retrieveCustomerName() throws SQLException{
		CallableStatement cstmt=conn.prepareCall("Call retrieve_customer_name(?,?,?)");
		cstmt.setString(1, username);
		cstmt.setString(2, null);
		cstmt.setString(3, null);
		cstmt.registerOutParameter(2, java.sql.Types.VARCHAR);
		cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);
		cstmt.execute();
		firstname = cstmt.getString(2);
		lastname = cstmt.getString(3);
	}
	
	public void retrieveId() throws SQLException{
		CallableStatement cstmt=conn.prepareCall("Call retrieve_id(?,?)");
		cstmt.setString(1, username);
		cstmt.setInt(2, -1);
		cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
		cstmt.execute();
		id = cstmt.getInt(2);
	}

	public void viewAccounts() throws SQLException{
		System.out.println("List of accounts:");
		PreparedStatement statement = conn.prepareStatement("SELECT accnum, status FROM account WHERE cid = (SELECT cid FROM customer WHERE username = ?)");
		statement.setString(1, username);
		
		ResultSet rs = statement.executeQuery();
        while (rs.next()) {
        	if (rs.getInt(2) == 0)
        		System.out.println("Account number: " + rs.getInt(1) + "\tStatus: PENDING");
        	else if (rs.getInt(2) == 1)
        		System.out.println("Account number: " + rs.getInt(1) + "\tStatus: APPROVED");
        	else if (rs.getInt(2) == 2)
        		System.out.println("Account number: " + rs.getInt(1) + "\tStatus: REJECTED");
        }
        
        LOGGER.info("Viewed customer bank accounts.");
	
	}
	
	public void applyAccount(double startBalance) throws SQLException{
		//newly applied accounts start with a status of 0, meaning it is waiting for an employee to either accept or reject the account
		CallableStatement cstmt=conn.prepareCall("CALL apply_account(?,?)");
		cstmt.setString(1, username);
		cstmt.setDouble(2, startBalance);
		cstmt.execute();
		
		
	}
}
