package scot.ianmacdonald.cakemgr.restclient.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import scot.ianmacdonald.cakemgr.restclient.model.Cake;
import scot.ianmacdonald.cakemgr.restclient.model.CakeService;
import scot.ianmacdonald.cakemgr.restclient.model.CakeServiceError;
import scot.ianmacdonald.cakemgr.restclient.model.CakeServiceModel;

@WebMvcTest(CakeManagerClientController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CakeManagerClientControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CakeService mockCakeService;

	@MockBean
	private RestTemplate cakeServiceRestTemplate;

	// static test data
	private static InOrder orderVerifier = null;

	// instance test data
	private final Cake banoffeePie = new Cake("Banoffee Pie", "Is it banana or toffee?  Who cares? Its deelishuss!",
			"http://www.banoffepiepics.com");
	private final Cake lemonCheesecake = new Cake("Lemon Cheesecake", "Lemony creamy cheesey goodness",
			"http://www.lemoncheesecake.org");
	private final Cake reesesDonut = new Cake("Reeses Donut", "Peanut butter choclate heaven",
			"http://www.reesesdonut.scot");
	private final Cake chocolateCake = new Cake("Chocolate Cake", "Delish chok let lovliness",
			"http://www.chokletcake.org");
	private final ArrayList<Cake> getCakesList = new ArrayList<>(Arrays.asList(banoffeePie, lemonCheesecake));
	private final ArrayList<Cake> saveCakeList = new ArrayList<>(
			Arrays.asList(banoffeePie, lemonCheesecake, reesesDonut));
	private final ArrayList<Cake> saveCakeTwiceList = new ArrayList<>(
			Arrays.asList(banoffeePie, lemonCheesecake, reesesDonut, chocolateCake));

	@BeforeAll
	public static void setUpStaticTestData(@Autowired CakeService mockCakeService) {

		orderVerifier = inOrder(mockCakeService);
	}

	@Test
	public void testGetCakesRequest() throws Exception {

		CakeServiceModel cakeServiceModel = new CakeServiceModel(getCakesList, null);

		when(mockCakeService.getCakes()).thenReturn(cakeServiceModel);

		MvcResult result = mockMvc.perform(get("/cakes")).andExpect(status().isOk())
				.andExpect(model().attribute("cakeList", equalTo(getCakesList)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertTrue(content.contains("<h2>List of Cakes that are Stored in the Database:</h2>"));
		assertTrue(content.contains("<td>Banoffee Pie</td>"));
		assertTrue(content.contains("<td>Is it banana or toffee?  Who cares? Its deelishuss!</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.banoffepiepics.com\">Banoffee Pie</a></td>"));
		assertTrue(content.contains("<td>Lemon Cheesecake</td>"));
		assertTrue(content.contains("<td>Lemony creamy cheesey goodness</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.lemoncheesecake.org\">Lemon Cheesecake</a></td>"));
		assertFalse(content.contains("<td>Reeses Donut</td>"));
		assertFalse(content.contains("<td>Peanut butter choclate heaven</td>"));
		assertFalse(content.contains("<td><a href=\"http://www.reesesdonut.scot\">Reeses Donut</a></td>"));
		assertFalse(content.contains("<td>Chocolate Cake</td>"));
		assertFalse(content.contains("<td>Delish chok let lovliness</td>"));
		assertFalse(content.contains("<td><a href=\"http://www.chokletcake.org\">Chocolate Cake</a></td>"));

		assertTrue(content.contains("<h2>Add a Cake to the Database:</h2>"));

		assertFalse(content.contains("<h2>An Error Was Encountered with the Cake Service:</h2>"));

		orderVerifier.verify(mockCakeService).getCakes();
	}

	@Test
	public void testPostCakeRequest() throws Exception {

		CakeServiceModel cakeServiceModel = new CakeServiceModel(saveCakeList, null);

		when(mockCakeService.saveCake(reesesDonut)).thenReturn(cakeServiceModel);

		MvcResult result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", "Reeses Donut").param("description", "Peanut butter choclate heaven")
						.param("image", "http://www.reesesdonut.scot"))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(model().attribute("cakeList", equalTo(saveCakeList)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertTrue(content.contains("<h2>List of Cakes that are Stored in the Database:</h2>"));
		assertTrue(content.contains("<td>Banoffee Pie</td>"));
		assertTrue(content.contains("<td>Is it banana or toffee?  Who cares? Its deelishuss!</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.banoffepiepics.com\">Banoffee Pie</a></td>"));
		assertTrue(content.contains("<td>Lemon Cheesecake</td>"));
		assertTrue(content.contains("<td>Lemony creamy cheesey goodness</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.lemoncheesecake.org\">Lemon Cheesecake</a></td>"));
		assertTrue(content.contains("<td>Reeses Donut</td>"));
		assertTrue(content.contains("<td>Peanut butter choclate heaven</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.reesesdonut.scot\">Reeses Donut</a></td>"));
		assertFalse(content.contains("<td>Chocolate Cake</td>"));
		assertFalse(content.contains("<td>Delish chok let lovliness</td>"));
		assertFalse(content.contains("<td><a href=\"http://www.chokletcake.org\">Chocolate Cake</a></td>"));

		assertTrue(content.contains("<h2>Add a Cake to the Database:</h2>"));

		assertFalse(content.contains("<h2>An Error Was Encountered with the Cake Service:</h2>"));

		orderVerifier.verify(mockCakeService).saveCake(reesesDonut);
	}

	@Test
	public void testPostCakeTwiceRequest() throws Exception {

		CakeServiceModel cakeServiceModelOne = new CakeServiceModel(saveCakeList, null);
		CakeServiceModel cakeServiceModelTwo = new CakeServiceModel(saveCakeTwiceList, null);

		when(mockCakeService.saveCake(reesesDonut)).thenReturn(cakeServiceModelOne);
		when(mockCakeService.saveCake(chocolateCake)).thenReturn(cakeServiceModelTwo);

		MvcResult result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", "Reeses Donut").param("description", "Peanut butter choclate heaven")
						.param("image", "http://www.reesesdonut.scot"))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(model().attribute("cakeList", equalTo(saveCakeList)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertTrue(content.contains("<h2>List of Cakes that are Stored in the Database:</h2>"));
		assertTrue(content.contains("<td>Banoffee Pie</td>"));
		assertTrue(content.contains("<td>Is it banana or toffee?  Who cares? Its deelishuss!</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.banoffepiepics.com\">Banoffee Pie</a></td>"));
		assertTrue(content.contains("<td>Lemon Cheesecake</td>"));
		assertTrue(content.contains("<td>Lemony creamy cheesey goodness</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.lemoncheesecake.org\">Lemon Cheesecake</a></td>"));
		assertTrue(content.contains("<td>Reeses Donut</td>"));
		assertTrue(content.contains("<td>Peanut butter choclate heaven</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.reesesdonut.scot\">Reeses Donut</a></td>"));
		assertFalse(content.contains("<td>Chocolate Cake</td>"));
		assertFalse(content.contains("<td>Delish chok let lovliness</td>"));
		assertFalse(content.contains("<td><a href=\"http://www.chokletcake.org\">Chocolate Cake</a></td>"));

		assertTrue(content.contains("<h2>Add a Cake to the Database:</h2>"));

		assertFalse(content.contains("<h2>An Error Was Encountered with the Cake Service:</h2>"));

		result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", "Chocolate Cake").param("description", "Delish chok let lovliness")
						.param("image", "http://www.chokletcake.org"))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(model().attribute("cakeList", equalTo(saveCakeTwiceList)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		content = result.getResponse().getContentAsString();

		assertTrue(content.contains("<h2>List of Cakes that are Stored in the Database:</h2>"));
		assertTrue(content.contains("<td>Banoffee Pie</td>"));
		assertTrue(content.contains("<td>Is it banana or toffee?  Who cares? Its deelishuss!</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.banoffepiepics.com\">Banoffee Pie</a></td>"));
		assertTrue(content.contains("<td>Lemon Cheesecake</td>"));
		assertTrue(content.contains("<td>Lemony creamy cheesey goodness</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.lemoncheesecake.org\">Lemon Cheesecake</a></td>"));
		assertTrue(content.contains("<td>Reeses Donut</td>"));
		assertTrue(content.contains("<td>Peanut butter choclate heaven</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.reesesdonut.scot\">Reeses Donut</a></td>"));
		assertTrue(content.contains("<td>Chocolate Cake</td>"));
		assertTrue(content.contains("<td>Delish chok let lovliness</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.chokletcake.org\">Chocolate Cake</a></td>"));

		assertTrue(content.contains("<h2>Add a Cake to the Database:</h2>"));

		assertFalse(content.contains("<h2>An Error Was Encountered with the Cake Service:</h2>"));

		orderVerifier.verify(mockCakeService).saveCake(reesesDonut);
		orderVerifier.verify(mockCakeService).saveCake(chocolateCake);

	}

	@Test
	public void testPostSameCakeTwiceRequest() throws Exception {

		CakeServiceModel cakeServiceModelOne = new CakeServiceModel(saveCakeList, null);
		CakeServiceError cakeServiceError = new CakeServiceError(HttpStatus.FORBIDDEN,
				"It is forbidden to create a Cake with a duplicate title", new Throwable(
						"could not execute statement; SQL [n/a]; constraint [\"PUBLIC.UK_O5VGXH55G2VXMKU8W39A88WH0_INDEX_1 ON PUBLIC.CAKE(TITLE) VALUES 11\"; SQL statement: insert into cake (description, image, title, id) values (?, ?, ?, ?) [23505-200]]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement"));
		CakeServiceModel cakeServiceModelTwo = new CakeServiceModel(saveCakeList, cakeServiceError);

		when(mockCakeService.saveCake(reesesDonut)).thenReturn(cakeServiceModelOne, cakeServiceModelTwo);

		MvcResult result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", "Reeses Donut").param("description", "Peanut butter choclate heaven")
						.param("image", "http://www.reesesdonut.scot"))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(model().attribute("cakeList", equalTo(saveCakeList)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertTrue(content.contains("<h2>List of Cakes that are Stored in the Database:</h2>"));
		assertTrue(content.contains("<td>Banoffee Pie</td>"));
		assertTrue(content.contains("<td>Is it banana or toffee?  Who cares? Its deelishuss!</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.banoffepiepics.com\">Banoffee Pie</a></td>"));
		assertTrue(content.contains("<td>Lemon Cheesecake</td>"));
		assertTrue(content.contains("<td>Lemony creamy cheesey goodness</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.lemoncheesecake.org\">Lemon Cheesecake</a></td>"));
		assertTrue(content.contains("<td>Reeses Donut</td>"));
		assertTrue(content.contains("<td>Peanut butter choclate heaven</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.reesesdonut.scot\">Reeses Donut</a></td>"));
		assertFalse(content.contains("<td>Chocolate Cake</td>"));
		assertFalse(content.contains("<td>Delish chok let lovliness</td>"));
		assertFalse(content.contains("<td><a href=\"http://www.chokletcake.org\">Chocolate Cake</a></td>"));

		assertTrue(content.contains("<h2>Add a Cake to the Database:</h2>"));

		assertFalse(content.contains("<h2>An Error Was Encountered with the Cake Service:</h2>"));

		result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", "Reeses Donut").param("description", "Peanut butter choclate heaven")
						.param("image", "http://www.reesesdonut.scot"))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(model().attribute("cakeList", equalTo(saveCakeList)))
				.andExpect(model().attribute("cakeServiceError", equalTo(cakeServiceError)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		content = result.getResponse().getContentAsString();

		assertTrue(content.contains("<h2>List of Cakes that are Stored in the Database:</h2>"));
		assertTrue(content.contains("<td>Banoffee Pie</td>"));
		assertTrue(content.contains("<td>Is it banana or toffee?  Who cares? Its deelishuss!</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.banoffepiepics.com\">Banoffee Pie</a></td>"));
		assertTrue(content.contains("<td>Lemon Cheesecake</td>"));
		assertTrue(content.contains("<td>Lemony creamy cheesey goodness</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.lemoncheesecake.org\">Lemon Cheesecake</a></td>"));
		assertTrue(content.contains("<td>Reeses Donut</td>"));
		assertTrue(content.contains("<td>Peanut butter choclate heaven</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.reesesdonut.scot\">Reeses Donut</a></td>"));
		assertFalse(content.contains("<td>Chocolate Cake</td>"));
		assertFalse(content.contains("<td>Delish chok let lovliness</td>"));
		assertFalse(content.contains("<td><a href=\"http://www.chokletcake.org\">Chocolate Cake</a></td>"));

		assertTrue(content.contains("<h2>Add a Cake to the Database:</h2>"));

		assertTrue(content.contains("<h2>An Error Was Encountered with the Cake Service:</h2>"));
		assertTrue(content.contains("<td>403 FORBIDDEN</td>"));
		assertTrue(content.contains("<td>It is forbidden to create a Cake with a duplicate title</td>"));
		assertTrue(content.contains(
				"<td>could not execute statement; SQL [n/a]; constraint [&quot;PUBLIC.UK_O5VGXH55G2VXMKU8W39A88WH0_INDEX_1 ON PUBLIC.CAKE(TITLE) VALUES 11&quot;; SQL statement: insert into cake (description, image, title, id) values (?, ?, ?, ?) [23505-200]]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement</td>"));
		assertFalse(content.contains("<td>500 INTERNAL_SERVER_ERROR</td>"));
		assertFalse(content.contains("<td>Unable to parse error from server while attempting to read Cake objects</td>"));
		assertFalse(content.contains("<td>Unable to parse error from server while attempting to save Cake object</td>"));
		assertFalse(content.contains("<td>Unrecognized token &#39;unparseable&#39;: was expecting (JSON String, Number, Array, Object or token &#39;null&#39;, &#39;true&#39; or &#39;false&#39;)\n"
				+ " at [Source: (String)&quot;unparseable&quot;; line: 1, column: 12]</td>"));
		
		orderVerifier.verify(mockCakeService, times(2)).saveCake(reesesDonut);

	}

	@Test
	public void testGetCakesErrorIsUnparseableJson() throws Exception {

		CakeServiceError unparseableGetCakesExceptionError = new CakeServiceError(HttpStatus.INTERNAL_SERVER_ERROR,
				"Unable to parse error from server while attempting to read Cake objects",
				new Throwable(
						"Unrecognized token 'unparseable': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n"
								+ " at [Source: (String)\"unparseable\"; line: 1, column: 12]"));

		CakeServiceModel cakeServiceModel = new CakeServiceModel(getCakesList, unparseableGetCakesExceptionError);

		when(mockCakeService.getCakes()).thenReturn(cakeServiceModel);

		MvcResult result = mockMvc.perform(get("/cakes")).andDo(print()).andExpect(status().isOk())
				.andExpect(model().attribute("cakeList", equalTo(getCakesList)))
				.andExpect(model().attribute("cakeServiceError", equalTo(unparseableGetCakesExceptionError)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertTrue(content.contains("<h2>List of Cakes that are Stored in the Database:</h2>"));
		assertTrue(content.contains("<td>Banoffee Pie</td>"));
		assertTrue(content.contains("<td>Is it banana or toffee?  Who cares? Its deelishuss!</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.banoffepiepics.com\">Banoffee Pie</a></td>"));
		assertTrue(content.contains("<td>Lemon Cheesecake</td>"));
		assertTrue(content.contains("<td>Lemony creamy cheesey goodness</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.lemoncheesecake.org\">Lemon Cheesecake</a></td>"));
		assertFalse(content.contains("<td>Reeses Donut</td>"));
		assertFalse(content.contains("<td>Peanut butter choclate heaven</td>"));
		assertFalse(content.contains("<td><a href=\"http://www.reesesdonut.scot\">Reeses Donut</a></td>"));
		assertFalse(content.contains("<td>Chocolate Cake</td>"));
		assertFalse(content.contains("<td>Delish chok let lovliness</td>"));
		assertFalse(content.contains("<td><a href=\"http://www.chokletcake.org\">Chocolate Cake</a></td>"));

		assertTrue(content.contains("<h2>Add a Cake to the Database:</h2>"));

		assertTrue(content.contains("<h2>An Error Was Encountered with the Cake Service:</h2>"));
		assertFalse(content.contains("<td>403 FORBIDDEN</td>"));
		assertFalse(content.contains("<td>It is forbidden to create a Cake with a duplicate title</td>"));
		assertFalse(content.contains(
				"<td>could not execute statement; SQL [n/a]; constraint [&quot;PUBLIC.UK_O5VGXH55G2VXMKU8W39A88WH0_INDEX_1 ON PUBLIC.CAKE(TITLE) VALUES 11&quot;; SQL statement: insert into cake (description, image, title, id) values (?, ?, ?, ?) [23505-200]]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement</td>"));
		assertTrue(content.contains("<td>500 INTERNAL_SERVER_ERROR</td>"));
		assertTrue(content.contains("<td>Unable to parse error from server while attempting to read Cake objects</td>"));
		assertFalse(content.contains("<td>Unable to parse error from server while attempting to save Cake object</td>"));
		assertTrue(content.contains("<td>Unrecognized token &#39;unparseable&#39;: was expecting (JSON String, Number, Array, Object or token &#39;null&#39;, &#39;true&#39; or &#39;false&#39;)\n"
				+ " at [Source: (String)&quot;unparseable&quot;; line: 1, column: 12]</td>"));

		orderVerifier.verify(mockCakeService).getCakes();
	}

	@Test
	public void testPostCakeErrorIsUnparseableJson() throws Exception {

		CakeServiceError unparseableSaveCakeExceptionError = new CakeServiceError(HttpStatus.INTERNAL_SERVER_ERROR,
				"Unable to parse error from server while attempting to save Cake object",
				new Throwable(
						"Unrecognized token 'unparseable': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n"
								+ " at [Source: (String)\"unparseable\"; line: 1, column: 12]"));

		CakeServiceModel cakeServiceModel = new CakeServiceModel(saveCakeList, unparseableSaveCakeExceptionError);

		when(mockCakeService.saveCake(reesesDonut)).thenReturn(cakeServiceModel);

		MvcResult result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", "Reeses Donut").param("description", "Peanut butter choclate heaven")
						.param("image", "http://www.reesesdonut.scot"))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(model().attribute("cakeList", equalTo(saveCakeList)))
				.andExpect(model().attribute("cakeServiceError", equalTo(unparseableSaveCakeExceptionError)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertTrue(content.contains("<h2>List of Cakes that are Stored in the Database:</h2>"));
		assertTrue(content.contains("<td>Banoffee Pie</td>"));
		assertTrue(content.contains("<td>Is it banana or toffee?  Who cares? Its deelishuss!</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.banoffepiepics.com\">Banoffee Pie</a></td>"));
		assertTrue(content.contains("<td>Lemon Cheesecake</td>"));
		assertTrue(content.contains("<td>Lemony creamy cheesey goodness</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.lemoncheesecake.org\">Lemon Cheesecake</a></td>"));
		assertTrue(content.contains("<td>Reeses Donut</td>"));
		assertTrue(content.contains("<td>Peanut butter choclate heaven</td>"));
		assertTrue(content.contains("<td><a href=\"http://www.reesesdonut.scot\">Reeses Donut</a></td>"));
		assertFalse(content.contains("<td>Chocolate Cake</td>"));
		assertFalse(content.contains("<td>Delish chok let lovliness</td>"));
		assertFalse(content.contains("<td><a href=\"http://www.chokletcake.org\">Chocolate Cake</a></td>"));

		assertTrue(content.contains("<h2>Add a Cake to the Database:</h2>"));

		assertTrue(content.contains("<h2>An Error Was Encountered with the Cake Service:</h2>"));
		assertFalse(content.contains("<td>403 FORBIDDEN</td>"));
		assertFalse(content.contains("<td>It is forbidden to create a Cake with a duplicate title</td>"));
		assertFalse(content.contains(
				"<td>could not execute statement; SQL [n/a]; constraint [&quot;PUBLIC.UK_O5VGXH55G2VXMKU8W39A88WH0_INDEX_1 ON PUBLIC.CAKE(TITLE) VALUES 11&quot;; SQL statement: insert into cake (description, image, title, id) values (?, ?, ?, ?) [23505-200]]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement</td>"));
		assertTrue(content.contains("<td>500 INTERNAL_SERVER_ERROR</td>"));
		assertFalse(content.contains("<td>Unable to parse error from server while attempting to read Cake objects</td>"));
		assertTrue(content.contains("<td>Unable to parse error from server while attempting to save Cake object</td>"));
		assertTrue(content.contains("<td>Unrecognized token &#39;unparseable&#39;: was expecting (JSON String, Number, Array, Object or token &#39;null&#39;, &#39;true&#39; or &#39;false&#39;)\n"
				+ " at [Source: (String)&quot;unparseable&quot;; line: 1, column: 12]</td>"));

		orderVerifier.verify(mockCakeService).saveCake(reesesDonut);
	}

}
