package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import bss.ATM;
import bss.Account;
import bss.Bank;
import bss.Customer;
import bss.Session;
import bss.Teller;
import enums.*;
import log.DepositLog;
import log.Log;
import log.TransferLog;
import log.WithdrawLog;

import java.io.File;
import java.util.Scanner;

public class Server {
    private static final String LOG_FILE_PATH = "transactions.txt";

	
	public static void main(String[] args) {
		
		ServerSocket server = null;
		try {
			Server serverObj = new Server();
			
			// server is listening on port 1234
			server = new ServerSocket(1234);
			server.setReuseAddress(true);
			Bank bank = new Bank();
			Teller firstTeller = new Teller("password");
			bank.addTeller(firstTeller);

			try {
				bank.loadData(bank.getCustomers(), bank.getAccounts(), "output.txt");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// running infinite loop for getting
			// client request
			while (true) {
				// socket object to receive incoming client
				// requests
				Socket client = server.accept();

				// Displaying that new client is connected
				// to server
				System.out.println("New client connected" + client.getInetAddress().getHostAddress());

				// create a new thread object
				ClientHandler clientSock = serverObj.new ClientHandler(bank, client);

				// This thread will handle the client
				// separately
				new Thread(clientSock).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class ClientHandler implements Runnable {
		private OutputHandler outputHandler;
		private final Socket clientSocket;
		private Bank bank;
		private UserType userType;
		private boolean loggedIn;
		private ATM atm;
		private Session session;
		private Teller teller;

		public ClientHandler(Bank bank, Socket socket) {
			atm = new ATM();
			//			this.loggedIn = false;
			//this.bank = bank;
			loggedIn = false;

			this.bank = bank;
			this.clientSocket = socket;
		}

		public void run() {
			/*
			 * creating accounts for debugging purposes
			 */
			

//			Account testAccount = firstTeller.createAccount("123");
//			bank.addAccount(testAccount);

			for (Account account : bank.getAccounts()) {
				System.out.println("account #" + account.getId() + " account pin: " + account.getPin());
			}
			for (Teller teller : bank.getTellers()) {
				System.out.println("teller #" + teller.getId());
			}
			for (Customer customerinst : bank.getCustomers()) {
				System.out.println("customer #" + customerinst.getId());
			}
			/*
			 * Establish input and output streams and inputHandler and outputHandler,
			 * initialize threads for them and process requests in a regular interval
			 */
			try {
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
				ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

				InputHandler inputHandler = new InputHandler(objectInputStream);
				Thread inputThread = new Thread(inputHandler);
				inputThread.start();

				OutputHandler outHandler = new OutputHandler(objectOutputStream);
				outputHandler = outHandler;
				Thread outputThread = new Thread(outHandler);
				outputThread.start();

				// process requests every 200ms
				while (true) {
					List<Request> req = inputHandler.getNextRequest();
					if (req != null) {
						processRequest(req);
					}
					Thread.sleep(200);
				}

			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			
		}

		/*
		 * Switch statement for processing incoming requests
		 */


		private synchronized void processRequest(List<Request> req) {
			

			// for every request in the list of requests that was received
			for (Request request : req) {
				RequestType type = request.getType();
				switch (type) {
				case LOGIN:
					doLogin(request);
					break;
				case LOGOUT:
					doLogout(request);
					break;
				case DEPOSIT:
					doDeposit(request);
					break;
				case WITHDRAW:
					doWithdraw(request);
					break;
				case TRANSFER:
					doTransfer(request);
					break;
				case FREEZE:
					doFreeze(request);
					break;
				case UNFREEZE:
					doUnfreeze(request);
					break;
				case TEXT:
					doReadLogs(request);
					break;
				case ENTER:
					doEnter(request);
					break;
				case CREATECUSTOMER:
					doCreateCustomer(request);
					break;
				case CREATEACCOUNT:
					doCreateAccount(request);
					break;
				case LEAVE:
					doLeave(request);
					break;
				default:
					break;
				}

			}
			//"/Users/edgarromero/eclipse-workspace/Banking-System/BSS/src/TestCustomers.txt
			if (!bank.getCustomers().isEmpty()) {
				//System.out.println("CUSTOMERS NOT EMPTY");
				try {
					bank.saveData(bank.getCustomers(), "output.txt");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

		private synchronized void sendResponse(UserType userType, RequestType requestType, Status status, double balance) {
			List<Request> responses = new ArrayList<>();
			Request response = new Request(userType, requestType, status, balance);
			responses.add(response);
			outputHandler.enqueueRequest(responses);
		}

		private synchronized void sendResponse(RequestType requestType, Status status, double balance) {
			List<Request> responses = new ArrayList<>();
			Request response = new Request(requestType, status, balance);
			responses.add(response);
			outputHandler.enqueueRequest(responses);
		}

		private synchronized void sendResponse(ArrayList<String> messages, RequestType requestType, Status status, double balance) {
			List<Request> responses = new ArrayList<>();
			Request response = new Request(messages, requestType, status, balance);
			responses.add(response);
			outputHandler.enqueueRequest(responses);
		}
		
		private synchronized void sendResponse(UserType userType, RequestType requestType, Status status) {
			List<Request> responses = new ArrayList<>();
			Request response = new Request(userType, requestType, status);
			responses.add(response);
			outputHandler.enqueueRequest(responses);
		}

		private synchronized void sendResponse(RequestType requestType, Status status) {
			List<Request> responses = new ArrayList<>();
			Request response = new Request(requestType, status);
			responses.add(response);
			outputHandler.enqueueRequest(responses);
		}

		private synchronized void sendResponse(ArrayList<String> messages, RequestType requestType, Status status) {
			List<Request> responses = new ArrayList<>();
			Request response = new Request(messages, requestType, status);
			responses.add(response);
			outputHandler.enqueueRequest(responses);
		}

		private synchronized void doLogin(Request request) {
			
			int username = Integer.parseInt(request.getTexts().get(0));
			String password = request.getTexts().get(1);

			userType = determineUserType(bank, username);
			
			if (userType == UserType.CUSTOMER) {
				Account acc = bank.findAccount(username);
				if (acc.checkCredentials(username, password)) {
					loggedIn = true;

					// initialized global session variable
					session = atm.logIn(acc);
					sendResponse(UserType.CUSTOMER, RequestType.LOGIN, Status.SUCCESS, acc.getAmount());
					System.out.println("Balance: $" + session.getAccount().getAmount());
					
				} else {
					// user credentials were incorrect, send failure response
					sendResponse(UserType.CUSTOMER, RequestType.LOGIN, Status.FAILURE);
				}
			} else if (userType == UserType.TELLER) {
				Teller teller = bank.findTeller(username);
				if (teller.checkCredentials(username, password)) {
					this.teller = teller;
					loggedIn = true;
					sendResponse(UserType.TELLER, RequestType.LOGIN, Status.SUCCESS);
					
				}
				
			}
			// user isn't a teller or a customer, send failure response
			else {
				sendResponse(UserType.CUSTOMER, RequestType.LOGIN, Status.FAILURE);
			}
			
		}


		private synchronized void doDeposit(Request request) {
			if (loggedIn) {

				// getOccupied is for checking if the account is currently processing something
				// so two people can't interact with an account at once
				if (!session.getAccount().getOccupied() && !session.getAccount().getFrozen()) {
					// set the frozen flag to true on account whilst interacting with it
					session.getAccount().setFrozen(true);
					session.getAccount().deposit(request.getAmount());
					session.getAccount().setFrozen(false);

					sendResponse(RequestType.DEPOSIT, Status.SUCCESS, session.getAccount().getAmount());
					DepositLog depositLog = new DepositLog(session.getAccount().getId(), "Deposit", session.getAccount().getId());
                    logTransaction(depositLog);
					
				} else {
					// send deposit failure response if the account is occupied
					ArrayList<String> errorMessage = new ArrayList<String>(
							Arrays.asList("Account Occupied or Frozen\n"));
					sendResponse(errorMessage, RequestType.DEPOSIT, Status.FAILURE);
				}
			}
			System.out.println("Balance: $" + session.getAccount().getAmount());
		}


		private synchronized void doWithdraw(Request request) {
			if (loggedIn) {
				if (!session.getAccount().getOccupied() && !session.getAccount().getFrozen()) {


					// if they have insufficient funds
					if (session.getAccount().getAmount() < request.getAmount()) {
						ArrayList<String> errorMessage = new ArrayList<String>(Arrays.asList("Insufficient Funds"));
						sendResponse(errorMessage, RequestType.WITHDRAW, Status.FAILURE);
					} else {
						session.getAccount().withdraw(request.getAmount());
						// Account has sufficient funds, withdraw and send success
						sendResponse(RequestType.WITHDRAW, Status.SUCCESS, session.getAccount().getAmount());
						WithdrawLog withdrawLog = new WithdrawLog(session.getAccount().getId(), "Withdraw", session.getAccount().getId());
                        logTransaction(withdrawLog);
					}
				} else {
					// send deposit failure response if the account is occupied
					ArrayList<String> errorMessage = new ArrayList<String>(
							Arrays.asList("Account Occupied or Frozen\n"));
					sendResponse(errorMessage, RequestType.WITHDRAW, Status.FAILURE);
				}
			}
			System.out.println("Balance: $" + session.getAccount().getAmount());
		}


		private synchronized void doTransfer(Request request) {
			if (loggedIn == true) {
				if (session.getAccount().getOccupied() == false && session.getAccount().getFrozen() == false) {


					// Account has insufficient funds, send failure
					if (session.getAccount().getAmount() < request.getAmount()) {
						ArrayList<String> errorMessage = new ArrayList<String>(Arrays.asList("Insufficient Funds\n"));
						sendResponse(errorMessage, RequestType.TRANSFER, Status.FAILURE);
					} else {
						// to_account wasn't found, send failure
						session.getAccount().withdraw(request.getAmount());
						Account to_account = bank.findAccount(Integer.parseInt(request.getTexts().get(0)));
						if (to_account == null) {
							ArrayList<String> errorMessage = new ArrayList<String>(
									Arrays.asList("Account Not Found\n"));
							sendResponse(errorMessage, RequestType.TRANSFER, Status.FAILURE);
						} else {
							// Account has sufficient funds, transfer and send success
							to_account.deposit(request.getAmount());
							sendResponse(RequestType.TRANSFER, Status.SUCCESS, session.getAccount().getAmount());
							TransferLog transferLog = new TransferLog(session.getAccount().getId(), "Transfer", session.getAccount().getId(), to_account.getId());
                            logTransaction(transferLog);
						}
					}
				} else {
					// send deposit failure response if the account is occupied
					ArrayList<String> errorMessage = new ArrayList<String>(
							Arrays.asList("Account Occupied or Frozen\n"));
					sendResponse(errorMessage, RequestType.TRANSFER, Status.FAILURE);
				}
			}
			System.out.println("Balance: $" + session.getAccount().getAmount());
		}

		private synchronized void doFreeze(Request request) {
			if (loggedIn) {
				if (userType == UserType.TELLER) {
					int acc_ID = Integer.parseInt(request.getTexts().get(0));
					Account account = bank.findAccount(acc_ID);
					account.setFrozen(true);
					sendResponse(RequestType.FREEZE, Status.SUCCESS);
				}
			}
		}
		
		private synchronized void doUnfreeze(Request request) {
			if (loggedIn) {
				if (userType == UserType.TELLER) {
					int acc_ID = Integer.parseInt(request.getTexts().get(0));
					Account account = bank.findAccount(acc_ID);
					account.setFrozen(false);
					sendResponse(RequestType.UNFREEZE, Status.SUCCESS);
				}
			}
			
		}

		private synchronized void doReadLogs(Request request) {
			if (loggedIn && userType == UserType.TELLER) {
		        try {
		            LocalDateTime start = LocalDateTime.parse(request.getTexts().get(0));
		            LocalDateTime end = LocalDateTime.parse(request.getTexts().get(1));

		            File logFile = new File("transactions.txt");
		            if (!logFile.exists()) {
		                sendResponse(new ArrayList<>(Arrays.asList("Log file not found")), RequestType.TEXT, Status.FAILURE);
		                return;
		            }

		            ArrayList<String> logContents = new ArrayList<>();
		            try (Scanner scanner = new Scanner(logFile)) {
		                while (scanner.hasNextLine()) {
		                    String line = scanner.nextLine();
		                    LocalDateTime logTime = LocalDateTime.parse(line.split(" : ")[0]);
		                    if (!logTime.isBefore(start) && !logTime.isAfter(end)) {
		                        logContents.add(line);
		                    }
		                }
		            }

		            sendResponse(logContents, RequestType.TEXT, Status.SUCCESS);
		        } catch (Exception e) {
		           
		        	e.printStackTrace();
		            sendResponse(new ArrayList<>(Arrays.asList("Error reading logs")), RequestType.TEXT, Status.FAILURE);
		        }
		    } else {
		        
		    	sendResponse(new ArrayList<>(Arrays.asList("Unauthorized or Not Logged In")), RequestType.TEXT, Status.FAILURE);
		    }
		}

		private synchronized void doEnter(Request request) {
			if (loggedIn) {
				if (userType == UserType.TELLER) {
					int acc_ID = Integer.parseInt(request.getTexts().get(0));
					Account acc = bank.findAccount(acc_ID);
					session = atm.logIn(acc);
					
					sendResponse(RequestType.ENTER, Status.SUCCESS, acc.getAmount());
				}
			}
		}
		

		private synchronized void doCreateAccount(Request request) {
			if (loggedIn) {
				if (userType == UserType.TELLER) {
					Account account = teller.createAccount(request.getTexts().get(0));
					int customerID = Integer.parseInt(request.getTexts().get(1));
					account.addUser(customerID);
					Customer customer = bank.findCustomer(customerID);
					customer.addAccount(account);
					bank.addAccount(account);
					sendResponse(RequestType.CREATEACCOUNT, Status.SUCCESS);
				}
			}
		}
		

		private void doCreateCustomer(Request request) {
			if (loggedIn) {
				if (userType == UserType.TELLER) {
					Customer customer = new Customer();
					bank.addCustomer(customer);
					ArrayList<String> customerID = new ArrayList<String>(Arrays.asList(customer.getId() + ""));
					for (Customer customerinst : bank.getCustomers()) {
						System.out.println("customer #" + customerinst.getId());
					}
					sendResponse(customerID, RequestType.CREATECUSTOMER, Status.SUCCESS);
				}
			}
		}

		private synchronized void doLeave(Request request) {
			if (loggedIn) {
				if (userType == UserType.TELLER) {

					atm.logOut();
					sendResponse(RequestType.LEAVE, Status.SUCCESS);
				}
			}
		}

		private synchronized void doLogout(Request request) {
			if (loggedIn) {
				atm.logOut();
				sendResponse(RequestType.LOGOUT, Status.SUCCESS);
			}
		}

		// based on the given username, determine if it is for an account or a teller
		private synchronized UserType determineUserType(Bank bank, int userID) {
			Teller teller;
			Account acc;
			acc = bank.findAccount(userID);
			if (acc == null) {
				teller = bank.findTeller(userID);
				if (teller == null) {
					System.out.println("Account Undefined\n");
					return UserType.UNDEFINED;
				}
				return UserType.TELLER;
			}
			return UserType.CUSTOMER;
		}
		
		
		private static void logTransaction(Log log) {
            File logFile = new File(LOG_FILE_PATH);
            log.writeLogToFile(logFile);
        }
		
		
	}
}