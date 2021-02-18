package scot.ianmacdonald.cakemgr.restclient.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.inOrder;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import scot.ianmacdonald.cakemgr.restclient.model.Cake;
import scot.ianmacdonald.cakemgr.restclient.util.CakeManagerClientTestUtils;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class CakeManagerClientIntegrationTest implements CakeManagerClientTestUtils {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RestTemplate cakeServiceRestTemplate;

	// static test data specific to this test
	private static InOrder restTemplateOrderVerifier = null;

	@BeforeAll
	public static void setUpStaticTestData(@Autowired RestTemplate cakeServiceRestTemplate) {

		restTemplateOrderVerifier = inOrder(cakeServiceRestTemplate);
	}

	@Test
	public void testGetCakesRequest() throws Exception {

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET, STRING_ENTITY,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(GET_CAKES_RESPONSE);
		
		MvcResult result = mockMvc.perform(get("/cakes")).andExpect(status().isOk())
				.andExpect(model().attribute("cakeList", equalTo(GET_CAKES_LIST)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertTwoCakes(content);
		
		assertNoError(content);

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET,
				STRING_ENTITY, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
	}
	
	@Test
	public void testPostCakeRequest() throws Exception {

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST, REESES_DONUT_ENTITY,
				Cake.class)).thenReturn(SAVE_CAKE_RESPONSE);

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET, STRING_ENTITY,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(SAVE_CAKE_GET_CAKES_RESPONSE);

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

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST,
				REESES_DONUT_ENTITY, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET,
				STRING_ENTITY, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
	}

	@Test
	public void testPostCakeTwiceRequest() throws Exception {

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST, REESES_DONUT_ENTITY,
				Cake.class)).thenReturn(SAVE_CAKE_RESPONSE);
		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST, CHOCOLATE_CAKE_ENTITY,
				Cake.class)).thenReturn(SAVE_CAKE_TWICE_RESPONSE);
		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET, STRING_ENTITY,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(SAVE_CAKE_GET_CAKES_RESPONSE).thenReturn(SAVE_CAKE_GET_CAKES_TWICE_RESPONSE);

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

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST,
				REESES_DONUT_ENTITY, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET,
				STRING_ENTITY, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST,
				CHOCOLATE_CAKE_ENTITY, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET,
				STRING_ENTITY, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});

	}

	@Test
	public void testPostSameCakeTwiceRequest() throws Exception {

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST, REESES_DONUT_ENTITY,
				Cake.class)).thenReturn(SAVE_CAKE_RESPONSE).thenThrow(DUPLICATE_CAKE_EXCEPTION);

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET, STRING_ENTITY,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(SAVE_CAKE_GET_CAKES_RESPONSE).thenReturn(SAVE_CAKE_GET_CAKES_RESPONSE);

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

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST,
				REESES_DONUT_ENTITY, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET,
				STRING_ENTITY, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST,
				REESES_DONUT_ENTITY, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET,
				STRING_ENTITY, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});

	}

	@Test
	public void testGetCakesErrorIsUnparseableJson() throws Exception {

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET, STRING_ENTITY,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenThrow(UNPARSEABLE_GET_CAKES_EXCEPTION);

		MvcResult result = mockMvc.perform(get("/cakes")).andExpect(status().isOk())
				.andExpect(model().attribute("cakeList", equalTo(new ArrayList<Cake>())))
				.andExpect(model().attribute("cakeServiceError", equalTo(UNPARSEABLE_GET_CAKES_ERROR)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake()))).andReturn();

		String content = result.getResponse().getContentAsString();

		assertNoCakes(content);

		assertReadCakesParseError(content);

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET,
				STRING_ENTITY, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
	}

	@Test
	public void testPostCakeErrorIsUnparseableJson() throws Exception {

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST, REESES_DONUT_ENTITY,
				Cake.class)).thenThrow(UNPARSEABLE_SAVE_CAKE_EXCEPTION);

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET, STRING_ENTITY,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(GET_CAKES_RESPONSE);

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

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST,
				REESES_DONUT_ENTITY, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET,
				STRING_ENTITY, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
	}
	
	@Test
	public void testPostCakeAndGetCakesErrorsAreUnparseableJson() throws Exception {

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST, REESES_DONUT_ENTITY,
				Cake.class)).thenThrow(UNPARSEABLE_SAVE_CAKE_EXCEPTION);

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET, STRING_ENTITY,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenThrow(UNPARSEABLE_GET_CAKES_EXCEPTION);

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

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST,
				REESES_DONUT_ENTITY, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET,
				STRING_ENTITY, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
	}

}
