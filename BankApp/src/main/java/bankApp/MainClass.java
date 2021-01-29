package bankApp;


import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainClass {
	private static final Logger LOGGER = LogManager.getLogger(MainClass.class.getName());
	
	public static void main(String[] args) {
		LOGGER.info("Application started.");
		
		Navigate navigate = new Navigate();
		try {
			navigate.start();
		}
		catch (SQLException e) {
			System.out.println("An error has occured.");
			LOGGER.error("SQLException error in main class.");
		}
	}
}
