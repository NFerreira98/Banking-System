package bss;
import java.io.*;
import java.util.*;
import java.util.ArrayList;

import enums.AccountType;
/* Text Format for our Bank's Customer/Account Info
 Customer:
 	Accounts:
 		Account_ID: 0
 		Pin: "1234"
 		Users: [Customer_ID: 1, Customer_ID: 2]
 		Frozen: false
 		Amount: 1500.75
 		AccountType: Savings
 		
 	Accounts:
 		Account_ID: 1
 		Pin: "5678"
 		Users: [Customer_ID: 3, Customer_ID: 4]
 		Frozen: false
 		Amount: 100.35
 		AccountType: Checking
 */

public class Parser {

    // Method to read file and populate Customer objects
    public static ArrayList<Customer> readFromFile(String filePath) throws IOException {
        ArrayList<Customer> customers = new ArrayList<>();
        FileReader fileReader = new FileReader(filePath);
        BufferedReader reader = new BufferedReader(fileReader);
        String line;
        Customer currentCustomer = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.equals("Customer:")) {
            	// create a new customer to store customer data into
                currentCustomer = new Customer();
                //add into customers arrayList
                customers.add(currentCustomer);
            } 
            else if (line.startsWith("Accounts:")) {
                if (currentCustomer != null) {
                	// get account info
                    Account account = parseAccount(reader);
                    // add account to previously made customer
                    currentCustomer.accounts.add(account);
                    
                }
            }
        }
        reader.close();
        return customers;
    }

    // Helper method to parse an Account from lines
    private static Account parseAccount(BufferedReader reader) throws IOException {
        Account account = new Account();
        String line;
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            // End of account block
            if (line.isEmpty()) break; 
            // Grab the value after the colon on the line read and assign to appropriate attribute
            // Account
            if (line.startsWith("Account_ID:")) {   
            	//account for deleted accounts when assigning account ID's using static
                account.matchUpAccountID(Integer.parseInt(line.split(":")[1].trim()));
            } 
            // Pin
            else if (line.startsWith("Pin:")) {
                account.setPin(line.split(":")[1].trim().replace("\"", ""));
            } 
            // Customers ArrayList
            else if (line.startsWith("Users:")) {
                String usersStr = line.split(":")[1].trim();
                // remove the brackets [] that denote the arrayList of Customers w/ access to this account
                usersStr = usersStr.substring(1, usersStr.length() - 1); 
                // split by comma to properly read each customer's ID
                String[] userIDs = usersStr.split(",");
                for (String userID : userIDs) {
                    int customerID = Integer.parseInt(userID.split(":")[1].trim());
                    //account for removed Customers when assigning account ID's using static
                    Customer customerToAdd = new Customer(customerID);
                    account.getUsers().add(customerToAdd);
                }
            }
            // Frozen
            else if (line.startsWith("Frozen:")) {
                account.setFrozen(Boolean.parseBoolean(line.split(":")[1].trim()));
            }
            // Amount
            else if (line.startsWith("Amount:")) {
                account.setAmount(Double.parseDouble(line.split(":")[1].trim()));
            }
            // Account type
            else if (line.startsWith("AccountType:")) {
            	if(line.split(":")[1].trim().equals("Undefined")) {
            		account.setAccountTypeUndefined();
            	}
            	if(line.split(":")[1].trim().equals("Checkings")) {
            		account.setAccountTypeCheckings();
            	}            	
            	if(line.split(":")[1].trim().equals("Savings")) {
            		account.setAccountTypeSavings();
            	}
            }
        }
        return account;
    }

}
