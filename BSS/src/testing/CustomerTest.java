package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bss.Account;
import bss.Customer;

class CustomerTest {
    private Account account;
    private Customer customer;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setPin("1234");
        customer = new Customer();
        customer.getAccounts().add(account);
    }  
	@Test
	void testCreateCustomer() {
		assertNotNull(customer);
	}
	@Test
	void testAccountInCust() {
		assertNotNull(customer.getAccounts(), "Accounts should not be null");
        assertTrue(!customer.getAccounts().isEmpty(), "Accounts should not be empty");	
	}
	@Test
	void testGetAccount() {
		Account a = new Account();
		a.setPin("5");
		a.setID(5);
		customer.addAccount(a);
        a = customer.getAccount(5, "5");
        assertNotNull(a);
        assertEquals(5, a.getAccountID());
        assertEquals("5", a.getPin());	
    }
	@Test
	void testAddAccount() {
		Account a = new Account();
		a.setPin("5");
		a.setID(5);
		customer.addAccount(a);
        a = customer.getAccount(5, "5");
        assertTrue(customer.getAccountsSize() > 1);
        assertEquals(5, a.getAccountID());
        assertEquals("5", a.getPin());
	}
	@Test
	void testGetAccountsSize() {
		int accountsSize = 1; //one Account "account" to customer 
		assertNotNull(customer.getAccounts());
		assertEquals(1, customer.getAccountsSize());
	}
	@Test
	void testGetAccounts() { 
		ArrayList<Account> A = new ArrayList<Account>();
		A = customer.getAccounts();
		assertNotNull(A);
	}
	@Test
	void testSetAccounts() { 
		ArrayList<Account> A = new ArrayList<Account>();
		customer.setAccounts(A);
		assertTrue(customer.getAccounts().isEmpty());
		
		Account a = new Account();
		Account b = new Account();
		Account c = new Account();
		A.add(a);
		A.add(b);
		A.add(c);
		
		customer.setAccounts(A);
		assertFalse(customer.getAccounts().isEmpty());
	}
	
	

	
}
