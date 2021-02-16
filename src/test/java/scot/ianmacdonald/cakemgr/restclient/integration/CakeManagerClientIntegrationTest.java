package scot.ianmacdonald.cakemgr.restclient.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.inOrder;
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
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.chocolateCakeEntity;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.duplicateCakeException;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.duplicateTitleCakeServiceError;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.getCakesList;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.getCakesResponse;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.httpHeaders;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.reesDonutEntity;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.reesesDonut;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.saveCakeGetCakesResponse;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.saveCakeGetCakesTwiceResponse;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.saveCakeList;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.saveCakeResponse;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.saveCakeTwiceList;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.saveCakeTwiceResponse;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.stringEntity;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.unparseableGetCakesException;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.unparseableGetCakesExceptionError;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.unparseableSaveCakeException;
import static scot.ianmacdonald.cakemgr.restclient.util.CakeServiceClientTestUtils.unparseableSaveCakeExceptionError;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import scot.ianmacdonald.cakemgr.restclient.model.Cake;
import scot.ianmacdonald.cakemgr.restclient.model.CakeService;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
public class CakeManagerClientIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@TestConfiguration
	static class CakeServiceTestContextConfiguration {

		@Bean
		public CakeService cakeService() {
			return new CakeService();
		}
	}

	@MockBean
	private RestTemplate cakeServiceRestTemplate;

	// static test data specific to this test
	private static InOrder restTemplateOrderVerifier = null;

	@BeforeAll
	public static void setUpStaticTestData(@Autowired RestTemplate cakeServiceRestTemplate) {

		httpHeaders.setContentType(MediaTypes.HAL_JSON);
		httpHeaders.setAccept(Collections.singletonList(MediaTypes.HAL_JSON));
		restTemplateOrderVerifier = inOrder(cakeServiceRestTemplate);
	}

	@Test
	public void testGetCakesRequest() throws Exception {

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.GET, stringEntity,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(getCakesResponse);
		
		MvcResult result = mockMvc.perform(get("/cakes")).andExpect(status().isOk())
				.andExpect(model().attribute("cakeList", equalTo(getCakesList)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertTwoCakes(content);
		
		assertNoError(content);

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
	}
	
	@Test
	public void testPostCakeRequest() throws Exception {

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.POST, reesDonutEntity,
				Cake.class)).thenReturn(saveCakeResponse);

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.GET, stringEntity,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(saveCakeGetCakesResponse);

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

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.POST,
				reesDonutEntity, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
	}

	@Test
	public void testPostCakeTwiceRequest() throws Exception {

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.POST, reesDonutEntity,
				Cake.class)).thenReturn(saveCakeResponse);
		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.POST, chocolateCakeEntity,
				Cake.class)).thenReturn(saveCakeTwiceResponse);
		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.GET, stringEntity,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(saveCakeGetCakesResponse).thenReturn(saveCakeGetCakesTwiceResponse);

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

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.POST,
				reesDonutEntity, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.POST,
				chocolateCakeEntity, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});

	}

	@Test
	public void testPostSameCakeTwiceRequest() throws Exception {

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.POST, reesDonutEntity,
				Cake.class)).thenReturn(saveCakeResponse).thenThrow(duplicateCakeException);

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.GET, stringEntity,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(saveCakeGetCakesResponse).thenReturn(saveCakeGetCakesResponse);

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

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.POST,
				reesDonutEntity, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.POST,
				reesDonutEntity, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});

	}

	@Test
	public void testGetCakesErrorIsUnparseableJson() throws Exception {

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.GET, stringEntity,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenThrow(unparseableGetCakesException);

		MvcResult result = mockMvc.perform(get("/cakes")).andExpect(status().isOk())
				.andExpect(model().attribute("cakeList", equalTo(new ArrayList<Cake>())))
				.andExpect(model().attribute("cakeServiceError", equalTo(unparseableGetCakesExceptionError)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertNoCakes(content);

		assertReadCakesParseError(content);

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
	}

	@Test
	public void testPostCakeErrorIsUnparseableJson() throws Exception {

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.POST, reesDonutEntity,
				Cake.class)).thenThrow(unparseableSaveCakeException);

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.GET, stringEntity,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(getCakesResponse);

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

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.POST,
				reesDonutEntity, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
	}
	
	@Test
	public void testPostCakeAndGetCakesErrorsAreUnparseableJson() throws Exception {

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.POST, reesDonutEntity,
				Cake.class)).thenThrow(unparseableSaveCakeException);

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.GET, stringEntity,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenThrow(unparseableGetCakesException);

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

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.POST,
				reesDonutEntity, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
	}

}
