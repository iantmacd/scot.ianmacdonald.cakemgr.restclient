package scot.ianmacdonald.cakemgr.restclient.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class CakeTest {
	
	private final Cake lemonCheeseCake_1 = new Cake("Lemon Cheesecake", "Cheesey and Lemoney Goodness", "http://www.cakes.org/photos/lemoncheesecake.jpg");
	
	private final Cake lemonCheeseCake_2 = new Cake("Lemon Cheesecake", "Cheesey and Lemoney Goodness", "http://www.cakes.org/photos/lemoncheesecake.jpg");
	
	private final Cake lemonCheeseCake_3 = new Cake("Lemon Cheesecayk", "Cheesey and Lemoney Goodness", "http://www.cakes.org/photos/lemoncheesecake.jpg");
	
	private final Cake lemonCheeseCake_4 = new Cake("Lemon Cheesecake", "Cheesey and Lemoney Goodnesssss", "http://www.cakes.org/photos/lemoncheesecake.jpg");
	
	private final Cake lemonCheeseCake_5 = new Cake("Lemon Cheesecake", "Cheesey and Lemoney Goodness", "http://www.cakes.org/photos/lemon_cheesecake.jpg");
	
	private final Cake lemonCheeseCake_6 = new Cake(null, "Cheesey and Lemoney Goodness", "http://www.cakes.org/photos/lemoncheesecake.jpg");
	
	private final Cake lemonCheeseCake_7 = new Cake(null, "Cheesey and Lemoney Goodness", "http://www.cakes.org/photos/lemoncheesecake.jpg");
	
	private final Cake lemonCheeseCake_8 = new Cake("Lemon Cheesecake", null, "http://www.cakes.org/photos/lemoncheesecake.jpg");
	
	private final Cake lemonCheeseCake_9 = new Cake("Lemon Cheesecake", null, "http://www.cakes.org/photos/lemoncheesecake.jpg");
	
	private final Cake lemonCheeseCake_10 = new Cake("Lemon Cheesecake", "Cheesey and Lemoney Goodness", null);
	
	private final Cake lemonCheeseCake_11 = new Cake("Lemon Cheesecake", "Cheesey and Lemoney Goodness", null);

	@Test
	void testEqualsMethod() {
		
		// simple case where the cakes are equal by value
		assertEquals(lemonCheeseCake_1, lemonCheeseCake_2);
		
		// test difference in each of the properties causes them not to be equal by value
		assertNotEquals(lemonCheeseCake_1, lemonCheeseCake_3);
		assertNotEquals(lemonCheeseCake_1, lemonCheeseCake_4);
		assertNotEquals(lemonCheeseCake_1, lemonCheeseCake_5);
	}
	
	@Test
	void testEqualsHandlesNullTitle() {
		
		assertEquals(lemonCheeseCake_6, lemonCheeseCake_7);
		assertNotEquals(lemonCheeseCake_6, lemonCheeseCake_1);
		assertNotEquals(lemonCheeseCake_1, lemonCheeseCake_6);
	}
	
	@Test
	void testEqualsHandlesNullDescription() {
		
		assertEquals(lemonCheeseCake_8, lemonCheeseCake_9);
		assertNotEquals(lemonCheeseCake_8, lemonCheeseCake_1);
		assertNotEquals(lemonCheeseCake_1, lemonCheeseCake_8);
	}
	
	@Test
	void testEqualsHandlesNullImage() {
		
		assertEquals(lemonCheeseCake_10, lemonCheeseCake_11);
		assertNotEquals(lemonCheeseCake_10, lemonCheeseCake_1);
		assertNotEquals(lemonCheeseCake_1, lemonCheeseCake_10);
	}

}
