package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
//import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	TellerTest.class, 
	TestInputHandler.class,
	TestOutputHandler.class,
	RequestTest.class
})
public class AllTests {
	
}
