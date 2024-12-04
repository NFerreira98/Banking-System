package testing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import log.*;

class LogTest {
    private static final String LOG_FILE_PATH = "test_logs.txt";
    private File logFile;

    @BeforeEach
    void setUp() throws IOException {
        logFile = new File(LOG_FILE_PATH);
        if (logFile.exists()) {
            logFile.delete();
        }
        logFile.createNewFile();
    }

    // Tests for whether logging to database file is successful 
    
    // Test session write logging
    @Test
    void testSessionLog() throws IOException {
        SessionLog sessionLog = new SessionLog(1, "Session started", LocalDateTime.now(), LocalDateTime.now(), 1);
        sessionLog.writeLogToFile(logFile);
        String content = new String(Files.readAllBytes(Paths.get(LOG_FILE_PATH)));
        assertTrue(content.contains("Session | Log ID: 1 | Account ID: 1"));
    }

    // Test deposit logging
    @Test
    void testDepositLog() throws IOException {
        DepositLog depositLog = new DepositLog(2, "Deposit", 2);
        depositLog.writeLogToFile(logFile);
        String content = new String(Files.readAllBytes(Paths.get(LOG_FILE_PATH)));
        assertTrue(content.contains("Deposit | Log ID: 2 | Account ID: 2"));
    }

    // Test withdraw logging
    @Test
    void testWithdrawLog() throws IOException {
        WithdrawLog withdrawLog = new WithdrawLog(3, "Withdraw", 3);
        withdrawLog.writeLogToFile(logFile);
        String content = new String(Files.readAllBytes(Paths.get(LOG_FILE_PATH)));
        assertTrue(content.contains("Withdraw | Log ID: 3 | Account ID: 3"));
    }

    // Test transfer logging
    @Test
    void testTransferLog() throws IOException {
        TransferLog transferLog = new TransferLog(4, "Transfer", 4, 5);
        transferLog.writeLogToFile(logFile);
        String content = new String(Files.readAllBytes(Paths.get(LOG_FILE_PATH)));
        assertTrue(content.contains("Transfer | Log ID: 4 | From Account ID: 4 | To Account ID: 5"));
    }
    
    // Test session toLogString
    @Test
    public void testSessionLogToLogString() {
        SessionLog sessionLog = new SessionLog(1, "Session started", LocalDateTime.now(), LocalDateTime.now().plusHours(1), 100);
        String logString = sessionLog.toLogString();
        assertTrue(logString.contains("Session | Log ID: 1 | Account ID: 100 | Session started"));
    }

    // Test session getTimeStamp
    @Test
    public void testSessionLogGetTimeStamp() {
        LocalDateTime now = LocalDateTime.now();
        SessionLog sessionLog = new SessionLog(1, "Session started", now, now.plusHours(1), 100);
        assertEquals(now, sessionLog.getTimeStamp());
    }
    
    
    // Test transfer toLogString
    @Test
    public void testTransferLogToLogString() {
        TransferLog transferLog = new TransferLog(2, "Transfer completed", 200, 300);
        String logString = transferLog.toLogString();
        assertTrue(logString.contains("Transfer | Log ID: 2 | From Account ID: 200 | To Account ID: 300 | Transfer completed"));
    }

    // Test transfer getTimeStamp
    @Test
    public void testTransferLogGetTimeStamp() {
    	LocalDateTime before = LocalDateTime.now();
        TransferLog transferLog = new TransferLog(2, "Transfer completed", 200, 300);
        LocalDateTime after = LocalDateTime.now();

        LocalDateTime timeStamp = transferLog.getTimeStamp();
        
        // Check if the timestamp is within an interval since no parameter for timestamp is given
        assertTrue((!timeStamp.isBefore(before)) && (!timeStamp.isAfter(after)));
    }
    

	 // Test withdraw toLogString
	 @Test
	 public void testWithdrawLogToLogString() {
	     WithdrawLog withdrawLog = new WithdrawLog(3, "Withdraw completed", 300);
	     String logString = withdrawLog.toLogString();
	     assertTrue(logString.contains("Withdraw | Log ID: 3 | Account ID: 300 | Withdraw completed"));
	 }
	
	 // Test withdraw getTimeStamp
	 @Test
	 public void testWithdrawLogGetTimeStamp() {
	     LocalDateTime before = LocalDateTime.now();
	     WithdrawLog withdrawLog = new WithdrawLog(3, "Withdraw completed", 300);
	     LocalDateTime after = LocalDateTime.now();
	
	     LocalDateTime timeStamp = withdrawLog.getTimeStamp();
	
	     // Check if the timestamp is within an interval since no parameter for timestamp is given
	     assertTrue((!timeStamp.isBefore(before)) && (!timeStamp.isAfter(after)));
	 }
	
	 // Test deposit toLogString
	 @Test
	 public void testDepositLogToLogString() {
	     DepositLog depositLog = new DepositLog(4, "Deposit completed", 400);
	     String logString = depositLog.toLogString();
	     assertTrue(logString.contains("Deposit | Log ID: 4 | Account ID: 400 | Deposit completed"));
	 }
	
	 // Test deposit getTimeStamp
	 @Test
	 public void testDepositLogGetTimeStamp() {
	     LocalDateTime before = LocalDateTime.now();
	     DepositLog depositLog = new DepositLog(4, "Deposit completed", 400);
	     LocalDateTime after = LocalDateTime.now();
	
	     LocalDateTime timeStamp = depositLog.getTimeStamp();
	
	     // Check if the timestamp is within an interval since no parameter for timestamp is given
	     assertTrue((!timeStamp.isBefore(before)) && (!timeStamp.isAfter(after)));
	 }

    
    
}
