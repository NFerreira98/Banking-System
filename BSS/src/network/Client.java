package network;

import bss.BSSConsoleUI;
import bss.GUI;
import bss.Teller;
import enums.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {
	private final InputHandler inputHandler;
	private final OutputHandler outputHandler;
	private boolean alive = true;
	private boolean loggedIn = false;
	private boolean isProcessing = false;
	private String responseMessage;
	private UserType userType;
	private boolean accountAccessed;
	private double balance;

	public Client(InputHandler inputHandler, OutputHandler outputHandler) {
		this.inputHandler = inputHandler;
		this.outputHandler = outputHandler;
	}

	public static void main(String[] args) {
		
		startConnection();
	}
	
	private synchronized static void startConnection() {
		//final String HOST = "localhost;

		final String HOST = "localhost";
		final int PORT = 1234;

		Socket socket = null;
		try {
			// Establishing the socket connection
			socket = new Socket(HOST, PORT);

			// Output stream setup
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			// Input stream setup
			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

			// Handlers
			InputHandler inputHandler = new InputHandler(objectInputStream);
			OutputHandler outputHandler = new OutputHandler(objectOutputStream);

			// Threads for handlers
			Thread inputThread = new Thread(inputHandler);
			Thread outputThread = new Thread(outputHandler);
			inputThread.start();
			outputThread.start();

			// Create client instance
			Client client = new Client(inputHandler, outputHandler);

			// Start GUI
			GUI UI = new GUI(client);
			//BSSConsoleUI UI = new BSSConsoleUI(client);
			Thread consoleThread = new Thread(UI);
			consoleThread.start();

			// Process server responses
			while (client.alive) {
				List<Request> req = inputHandler.getNextRequest();
				if (req != null) {
					client.processResponse(req);
				}
				Thread.sleep(200);
			}

			System.out.println("Closing socket...");

		} catch (IOException | InterruptedException e) {
			//e.printStackTrace();
		} finally {
			// Ensuring the socket is closed, even if an exception occurred
			try {
				if (socket != null && !socket.isClosed()) {
					socket.close();
				}
			} catch (IOException e) {
				//e.printStackTrace(); // Handle error closing the socket
			}
		}
	}

	/*
	 * RESPONSE PROCESSING Added empty checks
	 */
	private synchronized void processResponse(List<Request> req) {
		isProcessing = false;
		for (Request request : req) {
			RequestType requestType = request.getType();
			switch (requestType) {
			case LOGIN:
				if (request.getStatus() == Status.SUCCESS) {
					loggedIn = true;
					responseMessage = "Login Successful";
					setBal(request.getBal());
					userType = request.getUserType();
				} else {
					responseMessage = "Incorrect Credentials";
				}
				break;
			case LOGOUT:
				if (request.getStatus() == Status.SUCCESS) {
					if (outputHandler != null) outputHandler.stop();
					if (inputHandler != null) inputHandler.stop();
					alive = false;
				}
				//startConnection();
				break;
			case DEPOSIT:
				if (request.getStatus() == Status.SUCCESS) {
					setBal(request.getBal());
					responseMessage = "Deposit Successful";
				}
				if (request.getStatus() == Status.FAILURE) {
					responseMessage = request.getTexts().get(0);
				}
				break;
			case WITHDRAW:
				if (request.getStatus() == Status.SUCCESS) {
					setBal(request.getBal());
					responseMessage = "Withdraw Successful";
				} else if (request.getTexts() != null && !request.getTexts().isEmpty()) {
					responseMessage = request.getTexts().get(0);
				}
				break;
			case TRANSFER:
				if (request.getStatus() == Status.SUCCESS) {
					setBal(request.getBal());
					responseMessage = "Transfer Successful";
				} else if (request.getStatus() == Status.FAILURE) {
					responseMessage = request.getTexts().get(0);
				}
				break;
			case ENTER:
				if (request.getStatus() == Status.SUCCESS) {
					setBal(request.getBal());
					accountAccessed = true;
					responseMessage = "Account Enter Successful";
				}
				break;
			case FREEZE:
				if (request.getStatus() == Status.SUCCESS) {
					responseMessage = "Freeze Successful";
				}
				break;
			case UNFREEZE:
				if (request.getStatus() == Status.SUCCESS) {
					responseMessage = "Account Unfrozen";
				}
				break;
			case TEXT:
				if (request.getStatus() == Status.SUCCESS) {
					responseMessage = "";
					for (String log : request.getTexts()) {
						responseMessage += log + "\n";
					}
					
//			        System.out.println("Logs:");
//			        for (String log : request.getTexts()) {
//			            System.out.println(log);
//			        }
			    } else {
			        System.out.println("Failed to retrieve logs: " + request.getTexts().get(0));
			    }
			    break;
			case CREATEACCOUNT:
				if (request.getStatus() == Status.SUCCESS) {
					responseMessage = "Account Creation Successful";
				}
				break;
			case CREATECUSTOMER:
				if(request.getStatus() == Status.SUCCESS) {
					responseMessage = "Customer #" + request.getTexts().get(0) + " Creation Successful";
				}
				break;
			case LEAVE:
				if (request.getStatus() == Status.SUCCESS) {
					accountAccessed = false;
					responseMessage = "Account Left Successfully";
				}
				break;
			default:
				break;
			}
		}
	}

	/*
	 * METHODS FOR GUI
	 */
	public synchronized UserType getUserType() {
		return userType;
	}

	public synchronized boolean getIsProcessing() {
		return isProcessing;
	}

	public synchronized String getResponseMessage() {
		return responseMessage;
	}

	public synchronized boolean getLoggedIn() {
		return loggedIn;
	}

	public synchronized boolean getAccountAccessed() {
		return accountAccessed;
	}
	
	public void setBal(double amt) {
		this.balance = amt;
	}
	public double getBal()
	{
		return this.balance;
	}
	public synchronized void createLoginRequest(String username, String password) {
		isProcessing = true;
		ArrayList<String> userAndPass = new ArrayList<>();
		userAndPass.add(username);
		userAndPass.add(password);
		sendRequest(userAndPass, RequestType.LOGIN, Status.REQUEST, this.balance);
	}

	public synchronized void createDepositRequest(double amount) {
		isProcessing = true;
		sendRequest(amount, RequestType.DEPOSIT, Status.REQUEST, this.balance);
	}

	public synchronized void createWithdrawRequest(double amount) {
		isProcessing = true;
		sendRequest(amount, RequestType.WITHDRAW, Status.REQUEST, this.balance);
	}

	public synchronized void createTransferRequest(int toAccountID, double amount) {
		isProcessing = true;
		ArrayList<String> ID = new ArrayList<>(Arrays.asList(toAccountID + ""));
		sendRequest(ID, amount, RequestType.TRANSFER, Status.REQUEST, this.balance);
	}

	public synchronized void createFreezeRequest(int acc_ID) {
		isProcessing = true;
		ArrayList<String> ID = new ArrayList<>(Arrays.asList(acc_ID + ""));
		sendRequest(ID, RequestType.FREEZE, Status.REQUEST, this.balance);
	}
	

	public void createUnfreezeRequest(int acc_ID) {
		isProcessing = true;
		ArrayList<String> ID = new ArrayList<>(Arrays.asList(acc_ID + ""));
		sendRequest(ID, RequestType.UNFREEZE, Status.REQUEST, this.balance);
		
	}

	public synchronized void createEnterAccountRequest(int acc_ID) {
		isProcessing = true;
		ArrayList<String> ID = new ArrayList<>(Arrays.asList(acc_ID + ""));
		sendRequest(ID, RequestType.ENTER, Status.REQUEST, this.balance);
	}

	public synchronized void createReadLogsRequest(String startDate, String endDate) {
	    isProcessing = true;
	    ArrayList<String> dateRange = new ArrayList<>();
	    dateRange.add(startDate);
	    dateRange.add(endDate);
	    sendRequest(dateRange, RequestType.TEXT, Status.REQUEST, this.balance);
	}
	

	public void createCustomerCreationRequest() {
		isProcessing = true;
		sendRequest(RequestType.CREATECUSTOMER, Status.REQUEST, this.balance);
	}
	
	public synchronized void createAccountCreationRequest(String password, String customerID) {
		
		isProcessing = true;
		ArrayList<String> passwordAndCustomerID = new ArrayList<>();
		passwordAndCustomerID.add(password);
		passwordAndCustomerID.add(customerID);
		sendRequest(passwordAndCustomerID, RequestType.CREATEACCOUNT, Status.REQUEST, this.balance);
		
	}

	public synchronized void createLeaveRequest() {
		isProcessing = true;
		sendRequest(RequestType.LEAVE, Status.REQUEST, this.balance);
	}




	public synchronized void createLogoutRequest() {
		sendRequest(RequestType.LOGOUT, Status.REQUEST, this.balance);
		isProcessing = true;
		// Shutdown
		
	}

	private synchronized void sendRequest(RequestType requestType, Status status, double balance) {

		List<Request> responses = new ArrayList<>();
		Request response = new Request(requestType, status, balance);
		responses.add(response);
		outputHandler.enqueueRequest(responses);
	}

	private synchronized void sendRequest(ArrayList<String> messages, RequestType requestType, Status status, double balance) {
		List<Request> responses = new ArrayList<>();
		Request response = new Request(messages, requestType, status, balance);
		responses.add(response);
		outputHandler.enqueueRequest(responses);
	}

	private synchronized void sendRequest(ArrayList<String> messages, double amt, RequestType requestType, Status status, double balance) {
		List<Request> responses = new ArrayList<>();
		Request response = new Request(messages, amt, requestType, status, balance);
		responses.add(response);
		outputHandler.enqueueRequest(responses);
	}

	private synchronized void sendRequest(double amt, RequestType requestType, Status status, double balance) {
		List<Request> responses = new ArrayList<>();
		Request response = new Request(amt, requestType, status, balance);
		responses.add(response);
		outputHandler.enqueueRequest(responses);
	}
}
