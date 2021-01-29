package tests;

import java.sql.SQLException;

import org.junit.jupiter.api.*;

import bankApp.*;

public class AppTest {
	Employee e = new Employee("testEmployee", "password");
	Account acc = new Account(2);
	
	/*@BeforeAll
	static void setup(){
		Employee e = new Employee("testEmployee", "password");
		Account acc = new Account(2);
		System.out.println("a");
	}*/
	
	@Test
	void testDepositSuccess() {
		try {
			Assertions.assertEquals(1, acc.deposit(55.55));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testDepositFail() {
		try {
			Assertions.assertEquals(0, acc.deposit(-55.55));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testWithdrawSuccess() {
		try {
			Assertions.assertEquals(1, acc.withdraw(0.05));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testWithdrawFail() {
		try {
			Assertions.assertEquals(0, acc.withdraw(-55.55));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testRegisterFail() {
		try {
			Assertions.assertEquals(0, e.register("TestUser", "password", "Tester", "test"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
