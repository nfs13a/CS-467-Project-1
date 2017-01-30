package step_definitions;

import cucumber.api.java.en.*;
import cucumber.api.PendingException;
import static org.junit.Assert.*;

public class SackTest {
	
	@Given("^the default house$")
	public void theDefaultHouse() throws Throwable {
	    System.out.println("eclipse");
	    assertEquals(true,true);
	}
}