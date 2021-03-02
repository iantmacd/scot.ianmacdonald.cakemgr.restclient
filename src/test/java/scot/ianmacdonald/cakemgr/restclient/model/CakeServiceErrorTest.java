package scot.ianmacdonald.cakemgr.restclient.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CakeServiceErrorTest {
	
	private final CakeServiceError cakeServiceError_1 = new CakeServiceError(HttpStatus.NOT_FOUND, "Error message", new Throwable("Throwable message"));
	
	private final CakeServiceError cakeServiceError_2 = new CakeServiceError(HttpStatus.NOT_FOUND, "Error message", new Throwable("Throwable message"));
	
	private final CakeServiceError cakeServiceError_3 = new CakeServiceError(HttpStatus.FORBIDDEN, "Error message", new Throwable("Throwable message"));
	
	private final CakeServiceError cakeServiceError_4 = new CakeServiceError(HttpStatus.NOT_FOUND, "Different error message", new Throwable("Throwable message"));
	
	private final CakeServiceError cakeServiceError_5 = new CakeServiceError(HttpStatus.NOT_FOUND, "Error message", new Throwable("Different Throwable message"));
	
	private final CakeServiceError cakeServiceError_6 = new CakeServiceError(null, "Error message", new Throwable("Throwable message"));
	
	private final CakeServiceError cakeServiceError_7 = new CakeServiceError(null, "Error message", new Throwable("Throwable message"));
	
	private final CakeServiceError cakeServiceError_8 = new CakeServiceError(HttpStatus.NOT_FOUND, null, new Throwable("Throwable message"));
	
	private final CakeServiceError cakeServiceError_9 = new CakeServiceError(HttpStatus.NOT_FOUND, null, new Throwable("Throwable message"));
	
	private final String nullDebugMessage = null;
	
	private final CakeServiceError cakeServiceError_10 = new CakeServiceError(HttpStatus.NOT_FOUND, "Error message", new Throwable(nullDebugMessage));
	
	private final CakeServiceError cakeServiceError_11 = new CakeServiceError(HttpStatus.NOT_FOUND, "Error message", new Throwable(nullDebugMessage));

	@Test
	void testEqualsMethod() {
		
		// simple case where the cakes are equal by value
		assertEquals(cakeServiceError_1, cakeServiceError_2);
		
		// test difference in each of the properties causes them not to be equal by value
		assertNotEquals(cakeServiceError_1, cakeServiceError_3);
		assertNotEquals(cakeServiceError_1, cakeServiceError_4);
		assertNotEquals(cakeServiceError_1, cakeServiceError_5);
	}
	
	@Test
	void testEqualsHandlesNullStatus() {
		
		assertEquals(cakeServiceError_6, cakeServiceError_7);
		assertNotEquals(cakeServiceError_6, cakeServiceError_1);
		assertNotEquals(cakeServiceError_1, cakeServiceError_6);
	}
	
	@Test
	void testEqualsHandlesNullMessage() {
		
		assertEquals(cakeServiceError_8, cakeServiceError_9);
		assertNotEquals(cakeServiceError_8, cakeServiceError_1);
		assertNotEquals(cakeServiceError_1, cakeServiceError_8);
	}
	
	@Test
	void testEqualsHandlesNullDebugMessage() {
		
		assertEquals(cakeServiceError_10, cakeServiceError_11);
		assertNotEquals(cakeServiceError_10, cakeServiceError_1);
		assertNotEquals(cakeServiceError_1, cakeServiceError_10);
	}

}
