package bss;

import java.util.*;

/**
 *  This class is an implementation of DVDUserInterface
 *  that uses the console to display the menu of command choices
 */


public class BSSConsoleUI implements BSSUserInterface {
	
	private Scanner scan;
	private Client client;
	
	public BSSConsoleUI() {
		scan = new Scanner(System.in);
	}
	
	public void processCommands()
	{
		
		 String[] commands = {"Deposit",
	 			  "Withdraw",
	 			  "Transfer",
	 			  "Logout"
	 			  };

	}
	
	public ArrayList<String> login() {
		ArrayList<String> userAndPass = new ArrayList<String>();
		System.out.println("Enter username");
		String username = scan.nextLine();
		
		System.out.println("Enter password");
		String password = scan.nextLine();
		userAndPass.add(username);
		userAndPass.add(password);
		return userAndPass;
	}
	private void doLogIn() {
		
	}
	
}
