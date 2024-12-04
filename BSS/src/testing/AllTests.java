package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
//import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	TellerTest.class, 
	TestInputHandler.class,
	TestOutputHandler.class,
	CustomerTest.class,
	SessionTest.class,
	BankTest.class,
	AccountTest.class,
	ATMTest.class,
	BankTest.class,
	LogTest.class,
	
})
public class AllTests {
}
