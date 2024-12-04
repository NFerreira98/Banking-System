package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bss.Account;
import bss.Bank;
import bss.Customer;
import bss.Teller;

class BankTest {
	
    private Bank bank;
    private Account account;
    private Customer customer;
    private Teller teller;
    private final String testFilePath = "TestCustomers.txt";

    
    @BeforeEach
    void setUp() {
        bank = new Bank();
        account = new Account();
        customer = new Customer();
        teller = new Teller("password");
    }

    // Check if an account can be added to the bank
    @Test
    void testAddAccount() {
        bank.addAccount(account);
        assertTrue(bank.getAccounts().contains(account));
    }

    // Check if a customer can be added to the bank
    @Test
    void testAddCustomer() {
        bank.addCustomer(customer);
        assertTrue(bank.getCustomers().contains(customer));
    }

    // Check if a teller can be added to the bank
    @Test
    void testAddTeller() {
        bank.addTeller(teller);
        assertTrue(bank.getTellers().contains(teller));
    }

    // Check if an account can be found by ID
    @Test
    void testFindAccount() {
        bank.addAccount(account);
        assertEquals(account, bank.findAccount(account.getAccountID()));
    }

    // Check if a customer can be found by ID
    @Test
    void testFindCustomer() {
        bank.addCustomer(customer);
        assertEquals(customer, bank.findCustomer(customer.getId()));
    }

    // Check if a teller can be found by ID
    @Test
    void testFindTeller() {
        bank.addTeller(teller);
        assertEquals(teller, bank.findTeller(teller.getId()));
    }
    
    @Test
    void testCountCustomers() throws IOException {
        createTestFile();

        HashMap<Integer, Customer> customerCount = Bank.countCustomers(testFilePath);
        assertEquals(1, customerCount.size());
        assertNotNull(customerCount.get(1));
    }
    @Test
    void testCountAccounts() throws IOException {
        createTestFile();

        HashMap<Integer, Account> accountCount = Bank.countAccounts(testFilePath);

        assertEquals(3, accountCount.size());
        assertNotNull(accountCount.get(2));
        assertNotNull(accountCount.get(3));
        assertNotNull(accountCount.get(4));
    }
    @Test
    void testReadFromFile() throws IOException {
        createTestFile();

        HashMap<Integer, Customer> customerCount = Bank.countCustomers(testFilePath);
        HashMap<Integer, Account> accountCount = Bank.countAccounts(testFilePath);
        ArrayList<Customer> customers = new ArrayList<>();
        customers = Bank.readFromFile(testFilePath, bank.getAccounts(), customerCount, accountCount);
        bank.setCustomers(customers);
        assertEquals(1, customers.size());
        assertEquals(3, bank.getAccounts().size());
    }
    @Test
    void testLoadData() throws IOException {
        createTestFile();
        bank.loadData(bank.getCustomers(), bank.getAccounts(), testFilePath);

        assertEquals(1, bank.getCustomers().size());
        assertEquals(3, bank.getAccounts().size());
    }
    //this method is a helper to test the parsing code in bank.java
    private void createTestFile() throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFilePath))) {
            writer.write("Customer_ID: 1\n");
            writer.write("\tAccount_ID: 2\n");
            writer.write("\t\tPin: \"1234\"\n");
            writer.write("\t\tUsers: [Customer_ID! 1]\n");
            writer.write("\t\tFrozen: false\n");
            writer.write("\t\tAmount: 5000.00\n");
            writer.write("\t\tAccountType: Savings\n");
            writer.write("\tAccount_ID: 3\n");
            writer.write("\t\tPin: \"5678\"\n");
            writer.write("\t\tUsers: [Customer_ID! 1]\n");
            writer.write("\t\tFrozen: false\n");
            writer.write("\t\tAmount: 3000.00\n");
            writer.write("\t\tAccountType: Checkings\n");
            writer.write("\tAccount_ID: 4\n");
            writer.write("\t\tPin: \"3333\"\n");
            writer.write("\t\tUsers: [Customer_ID! 2]\n");
            writer.write("\t\tFrozen: true\n");
            writer.write("\t\tAmount: 1000.00\n");
            writer.write("\t\tAccountType: Savings\n");
        }
    }
}

