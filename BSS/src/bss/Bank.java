package bss;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Bank {
	private static ArrayList<Account> accounts;
	private static ArrayList<Customer> customers;
	private static ArrayList<Teller> tellers;
	private static boolean debug = false;
	
	public Bank() {
		//
		Bank.accounts = new ArrayList<Account>();
		Bank.customers = new ArrayList<Customer>();
		Bank.tellers = new ArrayList<Teller>();
	}
	
	public ArrayList<Account> getAccounts() {
		return Bank.accounts;
	}
	public ArrayList<Customer> getCustomers() {
		return Bank.customers;
	}
	public void setAccounts(ArrayList<Account> accountData) {
		Bank.accounts = accountData;
	}
	public void setCustomers(ArrayList<Customer> customerData) {
		Bank.customers = customerData;
	}
	
	public ArrayList<Teller> getTellers() {
		return Bank.tellers;
	}
	
	// add an account to the bank
	public void addAccount(Account account) {
		Bank.accounts.add(account);
	}
	// add a customer to the bank
	public void addCustomer(Customer customer) {
		Bank.customers.add(customer);
	}
	// add a teller to the bank
	public void addTeller(Teller teller) {
		Bank.tellers.add(teller);
	}
	// find an account by account ID
	public Account findAccount(int account_id){
		for (Account account : accounts) 
		{
	        if (account.getAccountID() == account_id) 
	        {
	            return account;
	        }
	    }
		
	    return null;
	}
	public Teller findTeller(int teller_id){
		for (Teller teller : tellers) 
		{
	        if (teller.getId() == teller_id) 
	        {
	            return teller;
	        }
	    }
	    return null;
	}
	public Customer findCustomer(int customer_id) {
		for (Customer customer : customers) 
		{
	        if (customer.getId() == customer_id) 
	        {
	            return customer;
	        }
	    }
	    return null;
	}

	// save bank data to a file //ArrayList<Customer> customers, 
	public void saveData(ArrayList<Customer> customers, String filePath) throws IOException{
		
		FileWriter fileWriter = new FileWriter(filePath);
        BufferedWriter writer = new BufferedWriter(fileWriter);
        for (Customer customer : customers) {
            // Write Customer ID
            writer.write("Customer_ID: " + customer.getId() + "\n");
            if(debug) {
            	System.out.println("Writing Customer_ID: " + customer.getId());
            }
            
            
            // Write each account belonging to this customer
            for (Account account : customer.getAccounts()) {
                writer.write("\tAccount_ID: " + account.getAccountID() + "\n");
                writer.write("\t\tPin: \"" + account.getPin() + "\"\n");

                // Write Users
                writer.write("\t\tUsers: [");
                for (int i = 0; i < account.getUsers().size(); i++) {
                    writer.write("Customer_ID! " + account.getUsers().get(i));
                    if (i < account.getUsers().size() - 1) {
                        writer.write(", ");
                    }
                }
                writer.write("]\n");  

                // Write other account details
                writer.write("\t\tFrozen: " + account.getFrozen() + "\n");
                writer.write("\t\tAmount: " + account.getAmount() + "\n");
                writer.write("\t\tAccountType: " + account.getAccountType() + "\n");
            }
            
        }
        // Close the writer to flush data to the file
        writer.close();
	}
	
	//load bank (Customers, their info, their Accounts and acc info) data from file
	public void loadData(ArrayList<Customer> customers, ArrayList<Account> accounts, String filePath) throws IOException {
		//first create objects to fill with Customer info from .txt file
				HashMap<Integer, Customer> a = countCustomers(filePath);
				//do the same for Account info from .txt file
				HashMap<Integer, Account> b = countAccounts(filePath);
				//readFromFile will populate each Customer with the accounts that belong to it
				customers = readFromFile(filePath, accounts, a, b); 
				setCustomers(customers);
	}
	
	//helper functions for loadData
	
	// record the number of customers, create the Customer Objects to fill later
    public static HashMap<Integer, Customer> countCustomers(String filePath) throws IOException {
    	HashMap<Integer, Customer> customerCount = new HashMap<Integer, Customer>();
  
        FileReader fileReader = new FileReader(filePath); 
        BufferedReader reader = new BufferedReader(fileReader);
        String line;
        Customer currentCustomer = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.startsWith("Customer_ID:")) {
            	int temp = Integer.parseInt(line.split(":")[1].trim());
            	// create a new customer to later store customer account data into
                currentCustomer = new Customer(temp);
                //add into customer into HASHMAP
                customerCount.put(temp, currentCustomer);
                if(debug) {
                	System.out.println("Customer_ID: " + temp + " -> " + currentCustomer);
                }
                
            } 
        }
        reader.close();
        //return hashmap of new Customer Objects
        return customerCount;
    }
    // record the number of Accounts, create the Account Objects to fill later
    public static HashMap<Integer, Account> countAccounts(String filePath) throws IOException {
    	HashMap<Integer, Account> accountCount = new HashMap<Integer, Account>();
    	
        FileReader fileReader = new FileReader(filePath); 
        BufferedReader reader = new BufferedReader(fileReader);
        String line;
        Account currentAccount = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim(); 

            if (line.startsWith("Account_ID:")) {
            	int temp = Integer.parseInt(line.split(":")[1].trim());
            	// create a new account to store own data into
            	currentAccount= new Account(temp);
                //add account object and its ID into HASHMAP
            	accountCount.put(temp, currentAccount);
            	if(debug) {
            		System.out.println("Account_ID: " + temp + " -> " + currentAccount);
            	}
            	
            } 
        }
        reader.close();
        //return hashmap of new Customer Objects
        return accountCount;
    }
    
    // Method to populate Customer objects, Populate Account objects
    //accounts is the Bank's ArrayList<Account
    //customerCount is the map of premade objects from first pass of file which we will populate
    //accountCount is the map of premade objects of all accounts from first pass to put into customers
    public static ArrayList<Customer> readFromFile(String filePath, ArrayList<Account> accounts, 
    	HashMap<Integer, Customer> customerCount, HashMap<Integer, Account> accountCount) 
    	throws IOException {
    	//get the total number of accounts
    	//int numCustomers = customerCount.size();
    	//ArrayList to store accounts for Customer
    	ArrayList<Account> custAccounts = new ArrayList<Account>();
    	//arrayList to store customers for Bank
        ArrayList<Customer> bankCustomers = new ArrayList<Customer>();
        //ArrayList to store accounts for Bank is passed in as an argument "accounts"
        
        FileReader fileReader = new FileReader(filePath); 
        BufferedReader reader = new BufferedReader(fileReader);
        
        String line;
     
        Customer currentCustomer = null;
        int temp;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            
            if (line.startsWith("Customer_ID:")) {
            	temp = Integer.parseInt(line.split(":")[1].trim());
            	// get the corresponding existing customer from the map via key aka Customer ID
                currentCustomer = customerCount.get(temp);
                if (currentCustomer == null) {
                    throw new IllegalStateException("Customer_ID not found: " + temp);
                }
            } 
            
            if (currentCustomer != null) {
                	// get accounts' info
                	custAccounts = parseAccount(reader, accountCount);
                	if (currentCustomer.getAccounts().isEmpty()) {
                	// add these accounts to previously made customer
                    currentCustomer.setAccounts(custAccounts);
                    // add accounts to Bank's passed in accounts ArrayList
                    for (Account a : custAccounts) {
                    	//accounts.add(a);
                    	if (!accounts.contains(a)) { // Avoid duplicate entries
                            accounts.add(a);
                        }
                    }
                    // add complete Customer into bank's customers arrayList after its been populated with its accounts info
                	if (!bankCustomers.contains(currentCustomer)) { // Avoid duplicate entries
                		bankCustomers.add(currentCustomer);
                    }
                	}
                	// Reset for the next customer
                    custAccounts = new ArrayList<>(); // Reset for the next customer
             }
        }
        reader.close();
        return bankCustomers;
    }
    
    // Helper method to parse and popluate Accounts from file
    private static ArrayList<Account> parseAccount(BufferedReader reader, HashMap<Integer, Account> accountCount)
    		throws IOException {
 
    	ArrayList<Account> accounts = new ArrayList<Account>();
    	Account temp = null;
        String line;
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            // End of account block
            if (line.isEmpty()) break; 
            // Grab the value after the colon on the line read to assign values to appropriate account from MAP 
            if (line.startsWith("Account_ID:")) {   
            	int id = Integer.parseInt(line.split(":")[1].trim());
                temp = accountCount.get(id);
                //System.out.println("Account_ID: " + temp.getAccountID() + " assigned to customer.");
                //System.out.println(accountCount.get(id));
            } 
            // Pin
            if (line.startsWith("Pin:")) {
                temp.setPin(line.split(":")[1].trim().replace("\"", ""));
            } 
            // Customers ArrayList
            if (line.startsWith("Users:")) {
                String usersStr = line.split(":")[1].trim();

                // remove the brackets [] that denote the arrayList of Customers w/ access to this account
                usersStr = usersStr.substring(1, usersStr.length() - 1); 

                // split by , to properly read each customer's ID if there are multiple customer ID's
                if (usersStr.contains(",")) {
                    String[] userIDs = usersStr.split(",");
                    for (String userID : userIDs) {
                    	//get each customer ID in the line
                        int customerID = Integer.parseInt(userID.split("!")[1].trim());
                        //add it to the account's userIDs ArrayList
                        temp.getUsers().add(customerID);
                    }
                }
                // handle case of only one entry in userIDs
                else {
                	int customerID = Integer.parseInt(usersStr.split("!")[1].trim());
                	temp.getUsers().add(customerID);
                }
               
            }
            // Frozen
            else if (line.startsWith("Frozen:")) {
                temp.setFrozen(Boolean.parseBoolean(line.split(":")[1].trim()));
            }
            // Amount
            else if (line.startsWith("Amount:")) {
                temp.setAmount(Double.parseDouble(line.split(":")[1].trim()));
            }
            // Account type
            else if (line.startsWith("AccountType:")) {
            	if(line.split(":")[1].trim().equals("Undefined")) {
            		temp.setAccountTypeUndefined();
            	}
            	if(line.split(":")[1].trim().equals("Checkings")) {
            		temp.setAccountTypeCheckings();
            	}            	
            	if(line.split(":")[1].trim().equals("Savings")) {
            		temp.setAccountTypeSavings();
            	}
            }
            //add the current account to the arraylist which will hold accounts for each customer
            if (!accounts.contains(temp)) {
                accounts.add(temp);
            }
        }
        return accounts;
    }
    
    public static void writeToFile(ArrayList<Customer> customers, String filePath) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath);
        BufferedWriter writer = new BufferedWriter(fileWriter);

        for (Customer customer : customers) {
            // Write Customer ID
            writer.write("Customer_ID: " + customer.getId() + "\n");
            if(debug) {
            	System.out.println("Writing Customer_ID: " + customer.getId());
            }
            
            
            // Write each account belonging to this customer
            for (Account account : customer.getAccounts()) {
                writer.write("\tAccount_ID: " + account.getAccountID() + "\n");
                writer.write("\t\tPin: \"" + account.getPin() + "\"\n");

                // Write Users
                writer.write("\t\tUsers: [");
                for (int i = 0; i < account.getUsers().size(); i++) {
                    writer.write("Customer_ID! " + account.getUsers().get(i));
                    if (i < account.getUsers().size() - 1) {
                        writer.write(", ");
                    }
                }
                writer.write("]\n");  

                // Write other account details
                writer.write("\t\tFrozen: " + account.getFrozen() + "\n");
                writer.write("\t\tAmount: " + account.getAmount() + "\n");
                writer.write("\t\tAccountType: " + account.getAccountType() + "\n");
            }
        }
        // Close the writer to flush data to the file
        writer.close();
    }
}

