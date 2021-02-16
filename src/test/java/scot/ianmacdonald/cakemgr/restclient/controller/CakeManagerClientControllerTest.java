package scot.ianmacdonald.cakemgr.restclient.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.assertDuplicateError;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.assertFourCakes;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.assertNoCakes;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.assertNoError;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.assertReadCakesParseError;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.assertSaveCakeParseError;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.assertThreeCakes;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.assertTwoCakes;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.chocolateCake;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.duplicateTitleCakeServiceError;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.getCakesList;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.reesesDonut;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.saveCakeList;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.saveCakeTwiceList;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.unparseableGetCakesExceptionError;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.unparseableSaveCakeExceptionError;

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

@WebMvcTest(CakeManagerClientController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CakeManagerClientControllerTest {

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

		CakeServiceModel cakeServiceModel = new CakeServiceModel(getCakesList, null);

		when(mockCakeService.getCakes()).thenReturn(cakeServiceModel);

		MvcResult result = mockMvc.perform(get("/cakes")).andExpect(status().isOk())
				.andExpect(model().attribute("cakeList", equalTo(getCakesList)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertTwoCakes(content);

		assertNoError(content);

		cakeServiceOrderVerifier.verify(mockCakeService).getCakes();
	}

	@Test
	public void testPostCakeRequest() throws Exception {

		CakeServiceModel cakeServiceModel = new CakeServiceModel(saveCakeList, null);

		when(mockCakeService.saveCake(reesesDonut)).thenReturn(cakeServiceModel);

		MvcResult result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", reesesDonut.getTitle()).param("description", reesesDonut.getDescription())
						.param("image", reesesDonut.getImage()))
				.andExpect(status().isOk()).andExpect(model().attribute("cakeList", equalTo(saveCakeList)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertThreeCakes(content);

		assertNoError(content);

		cakeServiceOrderVerifier.verify(mockCakeService).saveCake(reesesDonut);
	}

	@Test
	public void testPostCakeTwiceRequest() throws Exception {

		CakeServiceModel cakeServiceModelOne = new CakeServiceModel(saveCakeList, null);
		CakeServiceModel cakeServiceModelTwo = new CakeServiceModel(saveCakeTwiceList, null);

		when(mockCakeService.saveCake(reesesDonut)).thenReturn(cakeServiceModelOne);
		when(mockCakeService.saveCake(chocolateCake)).thenReturn(cakeServiceModelTwo);

		MvcResult result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", reesesDonut.getTitle()).param("description", reesesDonut.getDescription())
						.param("image", reesesDonut.getImage()))
				.andExpect(status().isOk()).andExpect(model().attribute("cakeList", equalTo(saveCakeList)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertThreeCakes(content);

		assertNoError(content);

		result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", chocolateCake.getTitle()).param("description", chocolateCake.getDescription())
						.param("image", chocolateCake.getImage()))
				.andExpect(status().isOk()).andExpect(model().attribute("cakeList", equalTo(saveCakeTwiceList)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		content = result.getResponse().getContentAsString();

		assertFourCakes(content);

		assertNoError(content);

		cakeServiceOrderVerifier.verify(mockCakeService).saveCake(reesesDonut);
		cakeServiceOrderVerifier.verify(mockCakeService).saveCake(chocolateCake);

	}

	@Test
	public void testPostSameCakeTwiceRequest() throws Exception {

		CakeServiceModel cakeServiceModelOne = new CakeServiceModel(saveCakeList, null);
		CakeServiceModel cakeServiceModelTwo = new CakeServiceModel(saveCakeList, duplicateTitleCakeServiceError);

		when(mockCakeService.saveCake(reesesDonut)).thenReturn(cakeServiceModelOne, cakeServiceModelTwo);

		MvcResult result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", reesesDonut.getTitle()).param("description", reesesDonut.getDescription())
						.param("image", reesesDonut.getImage()))
				.andExpect(status().isOk()).andExpect(model().attribute("cakeList", equalTo(saveCakeList)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertThreeCakes(content);

		assertNoError(content);

		result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", reesesDonut.getTitle()).param("description", reesesDonut.getDescription())
						.param("image", reesesDonut.getImage()))
				.andExpect(status().isOk()).andExpect(model().attribute("cakeList", equalTo(saveCakeList)))
				.andExpect(model().attribute("cakeServiceError", equalTo(duplicateTitleCakeServiceError)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		content = result.getResponse().getContentAsString();

		assertThreeCakes(content);

		assertDuplicateError(content);

		cakeServiceOrderVerifier.verify(mockCakeService, times(2)).saveCake(reesesDonut);

	}

	@Test
	public void testGetCakesErrorIsUnparseableJson() throws Exception {

		CakeServiceModel cakeServiceModel = new CakeServiceModel(new ArrayList<Cake>(),
				unparseableGetCakesExceptionError);

		when(mockCakeService.getCakes()).thenReturn(cakeServiceModel);

		MvcResult result = mockMvc.perform(get("/cakes")).andExpect(status().isOk())
				.andExpect(model().attribute("cakeList", equalTo(new ArrayList<Cake>())))
				.andExpect(model().attribute("cakeServiceError", equalTo(unparseableGetCakesExceptionError)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertNoCakes(content);

		assertReadCakesParseError(content);

		cakeServiceOrderVerifier.verify(mockCakeService).getCakes();
	}

	@Test
	public void testPostCakeErrorIsUnparseableJson() throws Exception {

		CakeServiceModel cakeServiceModel = new CakeServiceModel(getCakesList, unparseableSaveCakeExceptionError);

		when(mockCakeService.saveCake(reesesDonut)).thenReturn(cakeServiceModel);

		MvcResult result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", reesesDonut.getTitle()).param("description", reesesDonut.getDescription())
						.param("image", reesesDonut.getImage()))
				.andExpect(status().isOk()).andExpect(model().attribute("cakeList", equalTo(getCakesList)))
				.andExpect(model().attribute("cakeServiceError", equalTo(unparseableSaveCakeExceptionError)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertTwoCakes(content);

		assertSaveCakeParseError(content);

		cakeServiceOrderVerifier.verify(mockCakeService).saveCake(reesesDonut);
	}

	@Test
	public void testPostCakeAndGetCakesErrorsAreUnparseableJson() throws Exception {

		CakeServiceModel cakeServiceModel = new CakeServiceModel(new ArrayList<Cake>(),
				unparseableGetCakesExceptionError);

		when(mockCakeService.saveCake(reesesDonut)).thenReturn(cakeServiceModel);

		MvcResult result = mockMvc
				.perform(post("/cakes").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", reesesDonut.getTitle()).param("description", reesesDonut.getDescription())
						.param("image", reesesDonut.getImage()))
				.andExpect(status().isOk()).andExpect(model().attribute("cakeList", equalTo(new ArrayList<Cake>())))
				.andExpect(model().attribute("cakeServiceError", equalTo(unparseableGetCakesExceptionError)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertNoCakes(content);

		assertReadCakesParseError(content);

		cakeServiceOrderVerifier.verify(mockCakeService).saveCake(reesesDonut);
	}

}
