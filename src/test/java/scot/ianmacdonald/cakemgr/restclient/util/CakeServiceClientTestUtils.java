package scot.ianmacdonald.cakemgr.restclient.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import scot.ianmacdonald.cakemgr.restclient.model.Cake;
import scot.ianmacdonald.cakemgr.restclient.model.CakeServiceError;

public interface CakeServiceClientTestUtils {

	// public static final test data

	HttpHeaders httpHeaders = new HttpHeaders();
	Cake banoffeePie = new Cake("Banoffee Pie", "Is it banana or toffee?  Who cares? Its deelishuss!",
			"http://www.banoffepiepics.com");
	Cake lemonCheesecake = new Cake("Lemon Cheesecake", "Lemony creamy cheesey goodness",
			"http://www.lemoncheesecake.org");
	Cake reesesDonut = new Cake("Reeses Donut", "Peanut butter choclate heaven", "http://www.reesesdonut.scot");
	Cake chocolateCake = new Cake("Chocolate Cake", "Delish chok let loveliness", "http://www.chokletcake.org");
	ArrayList<Cake> getCakesList = new ArrayList<>(Arrays.asList(banoffeePie, lemonCheesecake));
	ArrayList<Cake> saveCakeList = new ArrayList<>(Arrays.asList(banoffeePie, lemonCheesecake, reesesDonut));
	ArrayList<Cake> saveCakeTwiceList = new ArrayList<>(
			Arrays.asList(banoffeePie, lemonCheesecake, reesesDonut, chocolateCake));
	CollectionModel<Cake> getCakesCollectionModel = CollectionModel.of(getCakesList);
	CollectionModel<Cake> saveCakeCollectionModel = CollectionModel.of(saveCakeList);
	CollectionModel<Cake> saveCakeTwiceCollectionModel = CollectionModel.of(saveCakeTwiceList);
	ResponseEntity<CollectionModel<Cake>> getCakesResponse = new ResponseEntity<CollectionModel<Cake>>(
			getCakesCollectionModel, HttpStatus.OK);
	ResponseEntity<CollectionModel<Cake>> saveCakeGetCakesResponse = new ResponseEntity<CollectionModel<Cake>>(
			saveCakeCollectionModel, HttpStatus.OK);
	ResponseEntity<CollectionModel<Cake>> saveCakeGetCakesTwiceResponse = new ResponseEntity<CollectionModel<Cake>>(
			saveCakeTwiceCollectionModel, HttpStatus.OK);
	ResponseEntity<Cake> saveCakeResponse = new ResponseEntity<>(reesesDonut, HttpStatus.OK);
	ResponseEntity<Cake> saveCakeTwiceResponse = new ResponseEntity<>(reesesDonut, HttpStatus.OK);
	HttpEntity<String> stringEntity = new HttpEntity<String>("", httpHeaders);
	HttpEntity<Cake> reesDonutEntity = new HttpEntity<Cake>(reesesDonut, httpHeaders);
	HttpEntity<Cake> chocolateCakeEntity = new HttpEntity<Cake>(chocolateCake, httpHeaders);
	CakeServiceError duplicateTitleCakeServiceError = new CakeServiceError(HttpStatus.FORBIDDEN,
			"It is forbidden to create a Cake with a duplicate title", new Throwable(
					"could not execute statement; SQL [n/a]; constraint [\"PUBLIC.UK_O5VGXH55G2VXMKU8W39A88WH0_INDEX_1 ON PUBLIC.CAKE(TITLE) VALUES 11\"; SQL statement: insert into cake (description, image, title, id) values (?, ?, ?, ?) [23505-200]]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement"));
	byte[] exceptionResponseBody = getByteArrayForDuplicateCakeError(duplicateTitleCakeServiceError);
	HttpClientErrorException duplicateCakeException = new HttpClientErrorException(HttpStatus.FORBIDDEN, null,
			exceptionResponseBody, null);
	CakeServiceError unparseableGetCakesExceptionError = new CakeServiceError(HttpStatus.INTERNAL_SERVER_ERROR,
			"Unable to parse error from server while attempting to read Cake objects",
			new Throwable(
					"Unrecognized token 'unparseable': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n"
							+ " at [Source: (String)\"unparseable\"; line: 1, column: 12]"));
	HttpClientErrorException unparseableGetCakesException = new HttpClientErrorException(
			HttpStatus.INTERNAL_SERVER_ERROR, null, "unparseable".getBytes(), null);
	CakeServiceError unparseableSaveCakeExceptionError = new CakeServiceError(HttpStatus.INTERNAL_SERVER_ERROR,
			"Unable to parse error from server while attempting to save Cake object",
			new Throwable(
					"Unrecognized token 'unparseable': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n"
							+ " at [Source: (String)\"unparseable\"; line: 1, column: 12]"));
	HttpClientErrorException unparseableSaveCakeException = new HttpClientErrorException(
			HttpStatus.INTERNAL_SERVER_ERROR, null, "unparseable".getBytes(), null);

	// html test data strings

	String HTML_H2_CAKE_LIST = "<h2>List of Cakes that are Stored in the Database:</h2>";
	String HTML_TD_BANOFFEE_TITLE = "<td>Banoffee Pie</td>";
	String HTML_TD_BANOFFEE_DESCRIPTION = "<td>Is it banana or toffee?  Who cares? Its deelishuss!</td>";
	String HTML_TD_BANOFFEE_IMAGE = "<td><a href=\"http://www.banoffepiepics.com\">Banoffee Pie</a></td>";
	String HTML_TD_LEMON_TITLE = "<td>Lemon Cheesecake</td>";
	String HTML_TD_LEMON_DESCRIPTION = "<td>Lemony creamy cheesey goodness</td>";
	String HTML_TD_LEMON_IMAGE = "<td><a href=\"http://www.lemoncheesecake.org\">Lemon Cheesecake</a></td>";
	String HTML_TD_REES_TITLE = "<td>Reeses Donut</td>";
	String HTML_TD_REES_DESCRIPTION = "<td>Peanut butter choclate heaven</td>";
	String HTML_TD_REES_IMAGE = "<td><a href=\"http://www.reesesdonut.scot\">Reeses Donut</a></td>";
	String HTML_TD_CHOCOLATE_TITLE = "<td>Chocolate Cake</td>";
	String HTML_TD_CHOCOLATE_DESCRIPTION = "<td>Delish chok let loveliness</td>";
	String HTML_TD_CHOCOLATE_IMAGE = "<td><a href=\"http://www.chokletcake.org\">Chocolate Cake</a></td>";

	String HTML_H2_ADD_CAKE = "<h2>Add a Cake to the Database:</h2>";

	String HTML_H2_ERROR = "<h2>An Error Was Encountered with the Cake Service:</h2>";
	String HTML_TD_ERROR_403 = "<td>403 FORBIDDEN</td>";
	String HTML_TD_ERROR_DUPLICATE = "<td>It is forbidden to create a Cake with a duplicate title</td>";
	String HTML_TD_ERROR_DUPLICATE_TRACE = "<td>could not execute statement; SQL [n/a]; constraint [&quot;PUBLIC.UK_O5VGXH55G2VXMKU8W39A88WH0_INDEX_1 ON PUBLIC.CAKE(TITLE) VALUES 11&quot;; SQL statement: insert into cake (description, image, title, id) values (?, ?, ?, ?) [23505-200]]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement</td>";
	String HTML_TD_ERROR_500 = "<td>500 INTERNAL_SERVER_ERROR</td>";
	String HTML_TD_ERROR_READ_PARSE = "<td>Unable to parse error from server while attempting to read Cake objects</td>";
	String HTML_TD_ERR0R_SAVE_PARSE = "<td>Unable to parse error from server while attempting to save Cake object</td>";
	String HTML_TD_ERROR_PARSE_TRACE = "<td>Unrecognized token &#39;unparseable&#39;: was expecting (JSON String, Number, Array, Object or token &#39;null&#39;, &#39;true&#39; or &#39;false&#39;)\n"
			+ " at [Source: (String)&quot;unparseable&quot;; line: 1, column: 12]</td>";
	
	static byte[] getByteArrayForDuplicateCakeError(CakeServiceError cakeServiceError) {
		
		byte[] returnBytes = null;
		try {
			returnBytes = new ObjectMapper().writeValueAsBytes(cakeServiceError);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return returnBytes;
	}
	
	static void assertNoCakes(String html) {

		assertTrue(html.contains(HTML_H2_CAKE_LIST));
		assertFalse(html.contains(HTML_TD_BANOFFEE_TITLE));
		assertFalse(html.contains(HTML_TD_BANOFFEE_DESCRIPTION));
		assertFalse(html.contains(HTML_TD_BANOFFEE_IMAGE));
		assertFalse(html.contains(HTML_TD_LEMON_TITLE));
		assertFalse(html.contains(HTML_TD_LEMON_DESCRIPTION));
		assertFalse(html.contains(HTML_TD_LEMON_IMAGE));
		assertFalse(html.contains(HTML_TD_REES_TITLE));
		assertFalse(html.contains(HTML_TD_REES_DESCRIPTION));
		assertFalse(html.contains(HTML_TD_REES_IMAGE));
		assertFalse(html.contains(HTML_TD_CHOCOLATE_TITLE));
		assertFalse(html.contains(HTML_TD_CHOCOLATE_DESCRIPTION));
		assertFalse(html.contains(HTML_TD_CHOCOLATE_IMAGE));

		assertTrue(html.contains(HTML_H2_ADD_CAKE));

	}

	static void assertTwoCakes(String html) {

		assertTrue(html.contains(HTML_H2_CAKE_LIST));
		assertTrue(html.contains(HTML_TD_BANOFFEE_TITLE));
		assertTrue(html.contains(HTML_TD_BANOFFEE_DESCRIPTION));
		assertTrue(html.contains(HTML_TD_BANOFFEE_IMAGE));
		assertTrue(html.contains(HTML_TD_LEMON_TITLE));
		assertTrue(html.contains(HTML_TD_LEMON_DESCRIPTION));
		assertTrue(html.contains(HTML_TD_LEMON_IMAGE));
		assertFalse(html.contains(HTML_TD_REES_TITLE));
		assertFalse(html.contains(HTML_TD_REES_DESCRIPTION));
		assertFalse(html.contains(HTML_TD_REES_IMAGE));
		assertFalse(html.contains(HTML_TD_CHOCOLATE_TITLE));
		assertFalse(html.contains(HTML_TD_CHOCOLATE_DESCRIPTION));
		assertFalse(html.contains(HTML_TD_CHOCOLATE_IMAGE));

		assertTrue(html.contains(HTML_H2_ADD_CAKE));

	}

	static void assertThreeCakes(String html) {

		assertTrue(html.contains(HTML_H2_CAKE_LIST));
		assertTrue(html.contains(HTML_TD_BANOFFEE_TITLE));
		assertTrue(html.contains(HTML_TD_BANOFFEE_DESCRIPTION));
		assertTrue(html.contains(HTML_TD_BANOFFEE_IMAGE));
		assertTrue(html.contains(HTML_TD_LEMON_TITLE));
		assertTrue(html.contains(HTML_TD_LEMON_DESCRIPTION));
		assertTrue(html.contains(HTML_TD_LEMON_IMAGE));
		assertTrue(html.contains(HTML_TD_REES_TITLE));
		assertTrue(html.contains(HTML_TD_REES_DESCRIPTION));
		assertTrue(html.contains(HTML_TD_REES_IMAGE));
		assertFalse(html.contains(HTML_TD_CHOCOLATE_TITLE));
		assertFalse(html.contains(HTML_TD_CHOCOLATE_DESCRIPTION));
		assertFalse(html.contains(HTML_TD_CHOCOLATE_IMAGE));

		assertTrue(html.contains(HTML_H2_ADD_CAKE));

	}

	static void assertFourCakes(String html) {

		assertTrue(html.contains(HTML_H2_CAKE_LIST));
		assertTrue(html.contains(HTML_TD_BANOFFEE_TITLE));
		assertTrue(html.contains(HTML_TD_BANOFFEE_DESCRIPTION));
		assertTrue(html.contains(HTML_TD_BANOFFEE_IMAGE));
		assertTrue(html.contains(HTML_TD_LEMON_TITLE));
		assertTrue(html.contains(HTML_TD_LEMON_DESCRIPTION));
		assertTrue(html.contains(HTML_TD_LEMON_IMAGE));
		assertTrue(html.contains(HTML_TD_REES_TITLE));
		assertTrue(html.contains(HTML_TD_REES_DESCRIPTION));
		assertTrue(html.contains(HTML_TD_REES_IMAGE));
		assertTrue(html.contains(HTML_TD_CHOCOLATE_TITLE));
		assertTrue(html.contains(HTML_TD_CHOCOLATE_DESCRIPTION));
		assertTrue(html.contains(HTML_TD_CHOCOLATE_IMAGE));

		assertTrue(html.contains(HTML_H2_ADD_CAKE));

	}

	static void assertNoError(String html) {
		assertFalse(html.contains(HTML_H2_ERROR));
		assertFalse(html.contains(HTML_TD_ERROR_403));
		assertFalse(html.contains(HTML_TD_ERROR_DUPLICATE));
		assertFalse(html.contains(HTML_TD_ERROR_DUPLICATE_TRACE));
		assertFalse(html.contains(HTML_TD_ERROR_500));
		assertFalse(html.contains(HTML_TD_ERROR_READ_PARSE));
		assertFalse(html.contains(HTML_TD_ERR0R_SAVE_PARSE));
		assertFalse(html.contains(HTML_TD_ERROR_PARSE_TRACE));
	}
	
	static void assertDuplicateError(String html) {
		
		assertTrue(html.contains(HTML_H2_ERROR));
		assertTrue(html.contains(HTML_TD_ERROR_403));
		assertTrue(html.contains(HTML_TD_ERROR_DUPLICATE));
		assertTrue(html.contains(HTML_TD_ERROR_DUPLICATE_TRACE));
		assertFalse(html.contains(HTML_TD_ERROR_500));
		assertFalse(html.contains(HTML_TD_ERROR_READ_PARSE));
		assertFalse(html.contains(HTML_TD_ERR0R_SAVE_PARSE));
		assertFalse(html.contains(HTML_TD_ERROR_PARSE_TRACE));
	}
	
	static void assertReadCakesParseError(String html) {
		
		assertTrue(html.contains(HTML_H2_ERROR));
		assertFalse(html.contains(HTML_TD_ERROR_403));
		assertFalse(html.contains(HTML_TD_ERROR_DUPLICATE));
		assertFalse(html.contains(HTML_TD_ERROR_DUPLICATE_TRACE));
		assertTrue(html.contains(HTML_TD_ERROR_500));
		assertTrue(html.contains(HTML_TD_ERROR_READ_PARSE));
		assertFalse(html.contains(HTML_TD_ERR0R_SAVE_PARSE));
		assertTrue(html.contains(HTML_TD_ERROR_PARSE_TRACE));
	}
	
	static void assertSaveCakeParseError(String html) {

		assertTrue(html.contains(HTML_H2_ERROR));
		assertFalse(html.contains(HTML_TD_ERROR_403));
		assertFalse(html.contains(HTML_TD_ERROR_DUPLICATE));
		assertFalse(html.contains(HTML_TD_ERROR_DUPLICATE_TRACE));
		assertTrue(html.contains(HTML_TD_ERROR_500));
		assertFalse(html.contains(HTML_TD_ERROR_READ_PARSE));
		assertTrue(html.contains(HTML_TD_ERR0R_SAVE_PARSE));
		assertTrue(html.contains(HTML_TD_ERROR_PARSE_TRACE));
	}

}
