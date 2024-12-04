package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import enums.RequestType;
import enums.Status;
import enums.UserType;
import network.Request;

class RequestTest {
    
    private Request reqDefault;
    private Request req;

    @BeforeEach
    void setUp() {
    	reqDefault = new Request();
    	ArrayList<String> arr = new ArrayList<String>();
    	arr.add("test");
    	req = new Request(UserType.CUSTOMER, arr, RequestType.ENTER, Status.REQUEST, 0.0);
    }

    // Check if the teller can create an account
    @Test
    void testDefaultConstructor() {
        assertNull(reqDefault.getTexts());
        assertEquals(reqDefault.getStatus(), Status.UNDEFINED);
        assertEquals(reqDefault.getAmount(), -1);
        assertEquals(reqDefault.getBal(), 0);
        assertEquals(reqDefault.getUserType(), UserType.UNDEFINED);
        assertEquals(reqDefault.getType(), RequestType.UNDEFINED);
        
    }
    
    // Check if the teller can check credentials
    @Test
	void testGetters() {
    	assertNotNull(req.getTexts());
    	assertEquals(req.getTexts().get(0), "test");
        assertEquals(req.getStatus(), Status.REQUEST);
        assertEquals(req.getAmount(), -1);
        assertEquals(req.getBal(), 0.0);
        assertEquals(req.getUserType(), UserType.CUSTOMER);
        assertEquals(req.getType(), RequestType.ENTER);
		
	}
    
}
