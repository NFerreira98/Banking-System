package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bss.Account;
import bss.Session;
import bss.Teller;
import log.AccountCreationLog;
import log.DepositLog;
import log.Log;
import log.SessionLog;
import log.TransferLog;
import log.WithdrawLog;

class SessionTest {
	   private static Session session;
	   private static Teller teller;
	   private static Account account;
	   private static Account account2;
	   private static LocalDateTime timeStamp;

	  
	@BeforeAll
	static void setUp() throws Exception {
		
		session = new Session();
		account = new Account();
		account.setID(1);
        account2 = new Account();
        account2.setID(2);
        session.startSession(account);
        
        teller = new Teller("123");
        timeStamp = LocalDateTime.now(); 
	}

	@Test
	void testStartSession() {
		assertNotNull(account);
		assertTrue(session.startSession(account));
	}
	@Test
	void testEndSession() {
		session.startSession(account);
		session.endSession();
		assertNull(session.getAccount());
	}
	@Test
	void testSessionGetAccount() {
		assertNotNull(session.startSession(account));
	}
	@Test
	void testSessionGetLogs() {
		assertTrue(session.getLogs().isEmpty());
	}
	@Test
	void testGetStartTime() {
		assertNotNull(session.getStart_time());
	}
	@Test
	void testGetEndTime() {
		assertNotNull(session.getEnd_time());
	}
	@Test
	void testSetStartTime() {
		session.setStart_time(timeStamp);
		assertNotNull(session.getStart_time());
		assertTrue(session.getStart_time() == timeStamp);
	}
	@Test
	void testSetEndTime() {
		session.setEnd_time(timeStamp);
		assertNotNull(session.getEnd_time());
		assertTrue(session.getEnd_time() == timeStamp);
	}
}
