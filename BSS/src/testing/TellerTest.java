package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bss.Account;
import bss.Customer;
import bss.Teller;

class TellerTest {
    private Teller teller = new Teller("password");
    private Account account = new Account();
    private Customer customer = new Customer();

    @BeforeEach
    void setUp() {
        account = new Account();
        customer = new Customer();
    }

    // Check if the teller can create an account
    @Test
    void testCreateAccount() {
        String pin = "1234";
        Account newAccount = teller.createAccount(pin);
        assertNotNull(newAccount);
        assertEquals(pin, newAccount.getPin());
    }
        
    
    // Check if the teller can check credentials
    @Test
	void testCheckCredentials() {
    	// Teller id is 0 based on the User class
		boolean result = teller.checkCredentials(0, "password");
		assertTrue(result);
	}
    
	// Check if the teller can freeze an account
	@Test
	void testFreezeAccount() {
		account.setFrozen(false);
		teller.freezeAccount(account);
		assertTrue(account.getFrozen());
	}

	// Check if the teller can add an account to a customer
	@Test
	void testAddAccountToCustomer() {
		teller.addAccountToCustomer(customer, account);
		assertTrue(customer.getAccounts().contains(account));
	}


	// Check if the teller can recover a pin
	@Test
	void testRecoverPin() {
		account.setPin("1234");
		assertEquals("1234", teller.recoverPin(account));
	}
	
	// Check if the date created was set
	@Test
	void testDateCreated() {
		assertNotNull(customer.getCreated());
	}

    
    
}
