package bankApp;


import java.sql.*;

public class CreateConnection {
	private static Connection conn;
	static {
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://localhost/bankapp", "postgres", "admin");
		}catch (SQLException e) {
			e.printStackTrace();
		}
	
	}
		
	public static Connection getConnection() {
		return conn;
	}
}
