package scot.ianmacdonald.cakemgr.restclient.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeManagerClientTestUtils.assertDuplicateError;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeManagerClientTestUtils.assertFourCakes;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeManagerClientTestUtils.assertNoCakes;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeManagerClientTestUtils.assertNoError;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeManagerClientTestUtils.assertReadCakesParseError;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeManagerClientTestUtils.assertSaveCakeParseError;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeManagerClientTestUtils.assertThreeCakes;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeManagerClientTestUtils.assertTwoCakes;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import scot.ianmacdonald.cakemgr.restclient.model.Cake;
import scot.ianmacdonald.cakemgr.restclient.model.CakeService;
import scot.ianmacdonald.cakemgr.restclient.model.CakeServiceModel;
import scot.ianmacdonald.cakemgr.restclient.util.CakeManagerClientTestUtils;

@WebMvcTest(CakeManagerClientController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CakeManagerClientControllerTest implements CakeManagerClientTestUtils {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CakeService mockCakeService;

	@MockBean
	private RestTemplate cakeServiceRestTemplate;

	// static test data specific to this test
	private static InOrder cakeServiceOrderVerifier = null;

	@BeforeAll
	public static void setUpStaticTestData(@Autowired CakeService mockCakeService) {

		cakeServiceOrderVerifier = inOrder(mockCakeService);
	}

	@Test
	public void testGetCakesRequest() throws Exception {

		CakeServiceModel cakeServiceModel = new CakeServiceModel(GET_CAKES_LIST, null);

		when(mockCakeService.getCakes()).thenReturn(cakeServiceModel);

		MvcResult result = mockMvc.perform(get("/cakes")).andExpect(status().isOk())
				.andExpect(model().attribute("cakeList", equalTo(GET_CAKES_LIST)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertTwoCakes(content);

		assertNoError(content);

		cakeServiceOrderVerifier.verify(mockCakeService).getCakes();
	}

	@Test
	public void testPostCakeRequest() throws Exception {

		CakeServiceModel cakeServiceModel = new CakeServiceModel(SAVE_CAKE_LIST, null);

		when(mockCakeService.saveCake(REESES_DONUT)).thenReturn(cakeServiceModel);

		MvcResult result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", REESES_DONUT.getTitle()).param("description", REESES_DONUT.getDescription())
						.param("image", REESES_DONUT.getImage()))
				.andExpect(status().isOk()).andExpect(model().attribute("cakeList", equalTo(SAVE_CAKE_LIST)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertThreeCakes(content);

		assertNoError(content);

		cakeServiceOrderVerifier.verify(mockCakeService).saveCake(REESES_DONUT);
	}

	@Test
	public void testPostCakeTwiceRequest() throws Exception {

		CakeServiceModel cakeServiceModelOne = new CakeServiceModel(SAVE_CAKE_LIST, null);
		CakeServiceModel cakeServiceModelTwo = new CakeServiceModel(SAVE_CAKE_TWICE_LIST, null);

		when(mockCakeService.saveCake(REESES_DONUT)).thenReturn(cakeServiceModelOne);
		when(mockCakeService.saveCake(CHOCOLATE_CAKE)).thenReturn(cakeServiceModelTwo);

		MvcResult result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", REESES_DONUT.getTitle()).param("description", REESES_DONUT.getDescription())
						.param("image", REESES_DONUT.getImage()))
				.andExpect(status().isOk()).andExpect(model().attribute("cakeList", equalTo(SAVE_CAKE_LIST)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertThreeCakes(content);

		assertNoError(content);

		result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", CHOCOLATE_CAKE.getTitle()).param("description", CHOCOLATE_CAKE.getDescription())
						.param("image", CHOCOLATE_CAKE.getImage()))
				.andExpect(status().isOk()).andExpect(model().attribute("cakeList", equalTo(SAVE_CAKE_TWICE_LIST)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		content = result.getResponse().getContentAsString();

		assertFourCakes(content);

		assertNoError(content);

		cakeServiceOrderVerifier.verify(mockCakeService).saveCake(REESES_DONUT);
		cakeServiceOrderVerifier.verify(mockCakeService).saveCake(CHOCOLATE_CAKE);
	}

	@Test
	public void testPostSameCakeTwiceRequest() throws Exception {

		CakeServiceModel cakeServiceModelOne = new CakeServiceModel(SAVE_CAKE_LIST, null);
		CakeServiceModel cakeServiceModelTwo = new CakeServiceModel(SAVE_CAKE_LIST, DUPLICATE_TITLE_CAKE_SERVICE_ERROR);

		when(mockCakeService.saveCake(REESES_DONUT)).thenReturn(cakeServiceModelOne, cakeServiceModelTwo);

		MvcResult result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", REESES_DONUT.getTitle()).param("description", REESES_DONUT.getDescription())
						.param("image", REESES_DONUT.getImage()))
				.andExpect(status().isOk()).andExpect(model().attribute("cakeList", equalTo(SAVE_CAKE_LIST)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertThreeCakes(content);

		assertNoError(content);

		result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", REESES_DONUT.getTitle()).param("description", REESES_DONUT.getDescription())
						.param("image", REESES_DONUT.getImage()))
				.andExpect(status().isOk()).andExpect(model().attribute("cakeList", equalTo(SAVE_CAKE_LIST)))
				.andExpect(model().attribute("cakeServiceError", equalTo(DUPLICATE_TITLE_CAKE_SERVICE_ERROR)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		content = result.getResponse().getContentAsString();

		assertThreeCakes(content);

		assertDuplicateError(content);

		cakeServiceOrderVerifier.verify(mockCakeService, times(2)).saveCake(REESES_DONUT);
	}

	@Test
	public void testGetCakesErrorIsUnparseableJson() throws Exception {

		CakeServiceModel cakeServiceModel = new CakeServiceModel(new ArrayList<Cake>(),
				UNPARSEABLE_GET_CAKES_ERROR);

		when(mockCakeService.getCakes()).thenReturn(cakeServiceModel);

		MvcResult result = mockMvc.perform(get("/cakes")).andExpect(status().isOk())
				.andExpect(model().attribute("cakeList", equalTo(new ArrayList<Cake>())))
				.andExpect(model().attribute("cakeServiceError", equalTo(UNPARSEABLE_GET_CAKES_ERROR)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertNoCakes(content);

		assertReadCakesParseError(content);

		cakeServiceOrderVerifier.verify(mockCakeService).getCakes();
	}

	@Test
	public void testPostCakeErrorIsUnparseableJson() throws Exception {

		CakeServiceModel cakeServiceModel = new CakeServiceModel(GET_CAKES_LIST, UNPARSEABLE_SAVE_CAKE_ERROR);

		when(mockCakeService.saveCake(REESES_DONUT)).thenReturn(cakeServiceModel);

		MvcResult result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", REESES_DONUT.getTitle()).param("description", REESES_DONUT.getDescription())
						.param("image", REESES_DONUT.getImage()))
				.andExpect(status().isOk()).andExpect(model().attribute("cakeList", equalTo(GET_CAKES_LIST)))
				.andExpect(model().attribute("cakeServiceError", equalTo(UNPARSEABLE_SAVE_CAKE_ERROR)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertTwoCakes(content);

		assertSaveCakeParseError(content);

		cakeServiceOrderVerifier.verify(mockCakeService).saveCake(REESES_DONUT);
	}

	@Test
	public void testPostCakeAndGetCakesErrorsAreUnparseableJson() throws Exception {

		CakeServiceModel cakeServiceModel = new CakeServiceModel(new ArrayList<Cake>(),
				UNPARSEABLE_GET_CAKES_ERROR);

		when(mockCakeService.saveCake(REESES_DONUT)).thenReturn(cakeServiceModel);

		MvcResult result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", REESES_DONUT.getTitle()).param("description", REESES_DONUT.getDescription())
						.param("image", REESES_DONUT.getImage()))
				.andExpect(status().isOk()).andExpect(model().attribute("cakeList", equalTo(new ArrayList<Cake>())))
				.andExpect(model().attribute("cakeServiceError", equalTo(UNPARSEABLE_GET_CAKES_ERROR)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertNoCakes(content);

		assertReadCakesParseError(content);

		cakeServiceOrderVerifier.verify(mockCakeService).saveCake(REESES_DONUT);
	}

}
