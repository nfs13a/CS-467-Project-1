package step_definitions;

import cucumber.api.java.en.*;
import cucumber.api.PendingException;
import static org.junit.Assert.*;
//import ../src/bounds/Phase1.java;
import bounds.Phase1;

public class SackTest {
	
	Phase1 t;
	
	@Given("^the default house$")
	public void theDefaultHouse() throws Throwable {
		t = new Phase1();
	}
}