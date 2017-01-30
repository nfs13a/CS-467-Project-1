package step_definitions;

import cucumber.api.java.en.*;
import cucumber.api.PendingException;
import static org.junit.Assert.*;
//import ../src/bounds/Phase1.java;
import implementation.Thief;

public class SackTest {
	
	Thief t;
	
	@Given("^the default house$")
	public void theDefaultHouse() throws Throwable {
		System.out.println("hi");
		t = new Thief("");
	}
}