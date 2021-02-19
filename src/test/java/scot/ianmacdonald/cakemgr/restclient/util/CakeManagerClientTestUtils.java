package scot.ianmacdonald.cakemgr.restclient.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import scot.ianmacdonald.cakemgr.restclient.model.Cake;
import scot.ianmacdonald.cakemgr.restclient.model.CakeServiceError;

public interface CakeManagerClientTestUtils {

	// public static final test data

	String CAKE_MANAGER_WS_URL = "http://localhost:8081/cakes";
	
	Cake BANOFFEE_PIE = new Cake("Banoffee Pie", "Is it banana or toffee?  Who cares? Its deelishuss!",
			"http://www.banoffepiepics.com");
	
	Cake LEMON_CHEESECAKE = new Cake("Lemon Cheesecake", "Lemony creamy cheesey goodness",
			"http://www.lemoncheesecake.org");
	Cake REESES_DONUT = new Cake("Reeses Donut", "Peanut butter chocolate heaven", "http://www.reesesdonut.scot");
	Cake CHOCOLATE_CAKE = new Cake("Chocolate Cake", "Delish chok let loveliness", "http://www.chokletcake.org");
	List<Cake> GET_CAKES_LIST = new ArrayList<>(Arrays.asList(BANOFFEE_PIE, LEMON_CHEESECAKE));
	List<Cake> SAVE_CAKE_LIST = new ArrayList<>(Arrays.asList(BANOFFEE_PIE, LEMON_CHEESECAKE, REESES_DONUT));
	List<Cake> SAVE_CAKE_TWICE_LIST = new ArrayList<>(
			Arrays.asList(BANOFFEE_PIE, LEMON_CHEESECAKE, REESES_DONUT, CHOCOLATE_CAKE));
	CollectionModel<Cake> GET_CAKES_COLLECTION_MODEL = CollectionModel.of(GET_CAKES_LIST);
	CollectionModel<Cake> SAVE_CAKE_COLLECTION_MODEL = CollectionModel.of(SAVE_CAKE_LIST);
	CollectionModel<Cake> SAVE_CAKE_TWICE_COLLECTION_MODEL = CollectionModel.of(SAVE_CAKE_TWICE_LIST);
	ResponseEntity<CollectionModel<Cake>> GET_CAKES_RESPONSE = new ResponseEntity<CollectionModel<Cake>>(
			GET_CAKES_COLLECTION_MODEL, HttpStatus.OK);
	ResponseEntity<CollectionModel<Cake>> SAVE_CAKE_GET_CAKES_RESPONSE = new ResponseEntity<CollectionModel<Cake>>(
			SAVE_CAKE_COLLECTION_MODEL, HttpStatus.OK);
	ResponseEntity<CollectionModel<Cake>> SAVE_CAKE_GET_CAKES_TWICE_RESPONSE = new ResponseEntity<CollectionModel<Cake>>(
			SAVE_CAKE_TWICE_COLLECTION_MODEL, HttpStatus.OK);
	ResponseEntity<Cake> SAVE_CAKE_RESPONSE = new ResponseEntity<>(REESES_DONUT, HttpStatus.OK);
	ResponseEntity<Cake> SAVE_CAKE_TWICE_RESPONSE = new ResponseEntity<>(REESES_DONUT, HttpStatus.OK);
	HttpHeaders HTTP_HEADERS = initHttpHeaders();
	HttpEntity<String> STRING_ENTITY = new HttpEntity<String>("", HTTP_HEADERS);
	HttpEntity<Cake> REESES_DONUT_ENTITY = new HttpEntity<Cake>(REESES_DONUT, HTTP_HEADERS);
	HttpEntity<Cake> CHOCOLATE_CAKE_ENTITY = new HttpEntity<Cake>(CHOCOLATE_CAKE, HTTP_HEADERS);
	String DUPLICATE_CAKE_ERROR_MESSAGE = "It is forbidden to create a Cake with a duplicate title";
	String DUPLICATE_CAKE_ERROR_TRACE = "could not execute statement; SQL [n/a]; constraint [\"PUBLIC.UK_O5VGXH55G2VXMKU8W39A88WH0_INDEX_1 ON PUBLIC.CAKE(TITLE) VALUES 6\"; SQL statement: insert into cake (description, image, title, id) values (?, ?, ?, ?) [23505-200]]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement";
	CakeServiceError DUPLICATE_TITLE_CAKE_SERVICE_ERROR = new CakeServiceError(HttpStatus.FORBIDDEN,
			DUPLICATE_CAKE_ERROR_MESSAGE, new Throwable(DUPLICATE_CAKE_ERROR_TRACE));
	byte[] EXCEPTION_RESPONSE_BODY = getByteArrayForCakeError(DUPLICATE_TITLE_CAKE_SERVICE_ERROR);
	HttpClientErrorException DUPLICATE_CAKE_EXCEPTION = new HttpClientErrorException(HttpStatus.FORBIDDEN, null,
			EXCEPTION_RESPONSE_BODY, null);
	String UNPARSEABLE_GET_CAKES_ERROR_MESSAGE = "Unable to parse error from server while attempting to read Cake objects";
	String UNPARSEABLE_ERROR_TRACE = "Unrecognized token 'unparseable': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n"
			+ " at [Source: (String)\"unparseable\"; line: 1, column: 12]";
	CakeServiceError UNPARSEABLE_GET_CAKES_ERROR = new CakeServiceError(HttpStatus.INTERNAL_SERVER_ERROR,
			UNPARSEABLE_GET_CAKES_ERROR_MESSAGE, new Throwable(UNPARSEABLE_ERROR_TRACE));
	HttpClientErrorException UNPARSEABLE_GET_CAKES_EXCEPTION = new HttpClientErrorException(
			HttpStatus.INTERNAL_SERVER_ERROR, null, "unparseable".getBytes(), null);
	String UNPARSEABLE_SAVE_CAKE_ERROR_MESSAGE = "Unable to parse error from server while attempting to save Cake object";
	CakeServiceError UNPARSEABLE_SAVE_CAKE_ERROR = new CakeServiceError(HttpStatus.INTERNAL_SERVER_ERROR,
			UNPARSEABLE_SAVE_CAKE_ERROR_MESSAGE, new Throwable(UNPARSEABLE_ERROR_TRACE));
	HttpClientErrorException UNPARSEABLE_SAVE_CAKE_EXCEPTION = new HttpClientErrorException(
			HttpStatus.INTERNAL_SERVER_ERROR, null, "unparseable".getBytes(), null);

	// html test data strings

	String HTML_H2_CAKE_LIST = "<h2>List of Cakes that are Stored in the Database:</h2>";
	String HTML_TD_BANOFFEE_TITLE = "<td>" + BANOFFEE_PIE.getTitle() + "</td>";
	String HTML_TD_BANOFFEE_DESCRIPTION = "<td>" + BANOFFEE_PIE.getDescription() + "</td>";
	String HTML_TD_BANOFFEE_IMAGE = "<td><a href=\"" + BANOFFEE_PIE.getImage() + "\">" + BANOFFEE_PIE.getTitle()
			+ "</a></td>";
	String HTML_TD_LEMON_TITLE = "<td>" + LEMON_CHEESECAKE.getTitle() + "</td>";
	String HTML_TD_LEMON_DESCRIPTION = "<td>" + LEMON_CHEESECAKE.getDescription() + "</td>";
	String HTML_TD_LEMON_IMAGE = "<td><a href=\"" + LEMON_CHEESECAKE.getImage() + "\">" + LEMON_CHEESECAKE.getTitle()
			+ "</a></td>";
	String HTML_TD_REESES_TITLE = "<td>" + REESES_DONUT.getTitle() + "</td>";
	String HTML_TD_REESES_DESCRIPTION = "<td>" + REESES_DONUT.getDescription() + "</td>";
	String HTML_TD_REESES_IMAGE = "<td><a href=\"" + REESES_DONUT.getImage() + "\">" + REESES_DONUT.getTitle()
			+ "</a></td>";
	String HTML_TD_CHOCOLATE_TITLE = "<td>" + CHOCOLATE_CAKE.getTitle() + "</td>";
	String HTML_TD_CHOCOLATE_DESCRIPTION = "<td>" + CHOCOLATE_CAKE.getTitle() + "</td>";
	String HTML_TD_CHOCOLATE_IMAGE = "<td><a href=\"" + CHOCOLATE_CAKE.getImage() + "\">" + CHOCOLATE_CAKE.getTitle()
			+ "</a></td>";

	String HTML_H2_ADD_CAKE = "<h2>Add a Cake to the Database:</h2>";

	String HTML_H2_ERROR = "<h2>An Error Was Encountered with the Cake Service:</h2>";
	String HTML_TD_ERROR_403 = "<td>" + HttpStatus.FORBIDDEN + "</td>";
	String HTML_TD_ERROR_DUPLICATE = "<td>" + DUPLICATE_CAKE_ERROR_MESSAGE + "</td>";
	String HTML_TD_ERROR_DUPLICATE_TRACE = "<td>" + DUPLICATE_CAKE_ERROR_TRACE.replace("\"", "&quot;") + "</td>";
	String HTML_TD_ERROR_500 = "<td>" + HttpStatus.INTERNAL_SERVER_ERROR + "</td>";
	String HTML_TD_ERROR_READ_PARSE = "<td>" + UNPARSEABLE_GET_CAKES_ERROR_MESSAGE + "</td>";
	String HTML_TD_ERR0R_SAVE_PARSE = "<td>" + UNPARSEABLE_SAVE_CAKE_ERROR_MESSAGE + "</td>";
	String HTML_TD_ERROR_PARSE_TRACE = "<td>" + UNPARSEABLE_ERROR_TRACE.replace("\"", "&quot;").replace("'", "&#39;") + "</td>";

	static HttpHeaders initHttpHeaders() {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaTypes.HAL_JSON);
		httpHeaders.setAccept(Collections.singletonList(MediaTypes.HAL_JSON));
		return httpHeaders;
	}

	static byte[] getByteArrayForCakeError(CakeServiceError cakeServiceError) {

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
		assertFalse(html.contains(HTML_TD_REESES_TITLE));
		assertFalse(html.contains(HTML_TD_REESES_DESCRIPTION));
		assertFalse(html.contains(HTML_TD_REESES_IMAGE));
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
		assertFalse(html.contains(HTML_TD_REESES_TITLE));
		assertFalse(html.contains(HTML_TD_REESES_DESCRIPTION));
		assertFalse(html.contains(HTML_TD_REESES_IMAGE));
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
		assertTrue(html.contains(HTML_TD_REESES_TITLE));
		assertTrue(html.contains(HTML_TD_REESES_DESCRIPTION));
		assertTrue(html.contains(HTML_TD_REESES_IMAGE));
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
		assertTrue(html.contains(HTML_TD_REESES_TITLE));
		assertTrue(html.contains(HTML_TD_REESES_DESCRIPTION));
		assertTrue(html.contains(HTML_TD_REESES_IMAGE));
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
