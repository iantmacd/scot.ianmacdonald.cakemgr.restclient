package scot.ianmacdonald.cakemgr.restclient.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

import scot.ianmacdonald.cakemgr.restclient.util.CakeManagerClientTestUtils;

@ExtendWith(SpringExtension.class)
public class CakeServiceTest implements CakeManagerClientTestUtils {

	@TestConfiguration
	static class CakeServiceTestConfig {

		@Bean
		public CakeService cakeService() {
			return new CakeService();
		}
	}

	@Autowired
	private CakeService cakeService;

	@MockBean
	private RestTemplate cakeServiceRestTemplate;
	
	// static test data specific to this test
	private static InOrder restTemplateOrderVerifier = null;

	@BeforeAll
	public static void setUpStaticTestData(@Autowired RestTemplate cakeServiceRestTemplate) {

		restTemplateOrderVerifier = inOrder(cakeServiceRestTemplate);
	}

	@Test
	public void testGetCakes() {

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET, STRING_ENTITY,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(GET_CAKES_RESPONSE);

		CakeServiceModel getCakesModel = cakeService.getCakes();
		assertThat(getCakesModel.getCakes()).isEqualTo(GET_CAKES_LIST);
		assertThat(getCakesModel.getCakeServiceError()).isNull();

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET,
				STRING_ENTITY, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
	}

	@Test
	public void testSaveCake() {

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST, REESES_DONUT_ENTITY,
				Cake.class)).thenReturn(SAVE_CAKE_RESPONSE);

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET, STRING_ENTITY,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(SAVE_CAKE_GET_CAKES_RESPONSE);

		CakeServiceModel saveCakeModel = cakeService.saveCake(REESES_DONUT);
		assertThat(saveCakeModel.getCakes()).isEqualTo(SAVE_CAKE_LIST);
		assertThat(saveCakeModel.getCakeServiceError()).isNull();

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST,
				REESES_DONUT_ENTITY, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET,
				STRING_ENTITY, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});

	}

	@Test
	public void testSaveCakeTwice() {

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST, REESES_DONUT_ENTITY,
				Cake.class)).thenReturn(SAVE_CAKE_RESPONSE);
		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST, CHOCOLATE_CAKE_ENTITY,
				Cake.class)).thenReturn(SAVE_CAKE_TWICE_RESPONSE);
		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET, STRING_ENTITY,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(SAVE_CAKE_GET_CAKES_RESPONSE).thenReturn(SAVE_CAKE_GET_CAKES_TWICE_RESPONSE);

		CakeServiceModel saveCakeModel = cakeService.saveCake(REESES_DONUT);
		assertThat(saveCakeModel.getCakes()).isEqualTo(SAVE_CAKE_LIST);
		assertThat(saveCakeModel.getCakeServiceError()).isNull();
		CakeServiceModel saveCakeTwiceModel = cakeService.saveCake(CHOCOLATE_CAKE);
		assertThat(saveCakeTwiceModel.getCakes()).isEqualTo(SAVE_CAKE_TWICE_LIST);
		assertThat(saveCakeTwiceModel.getCakeServiceError()).isNull();

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
	public void testSaveSameCakeTwice() throws JsonProcessingException {

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST, REESES_DONUT_ENTITY,
				Cake.class)).thenReturn(SAVE_CAKE_RESPONSE).thenThrow(DUPLICATE_CAKE_EXCEPTION);

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET, STRING_ENTITY,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(SAVE_CAKE_GET_CAKES_RESPONSE).thenReturn(SAVE_CAKE_GET_CAKES_RESPONSE);

		CakeServiceModel saveCakeModel = cakeService.saveCake(REESES_DONUT);
		assertThat(saveCakeModel.getCakes()).isEqualTo(SAVE_CAKE_LIST);
		assertThat(saveCakeModel.getCakeServiceError()).isNull();
		saveCakeModel = cakeService.saveCake(REESES_DONUT);
		assertThat(saveCakeModel.getCakes()).isEqualTo(SAVE_CAKE_LIST);
		assertThat(saveCakeModel.getCakeServiceError()).isEqualTo(DUPLICATE_TITLE_CAKE_SERVICE_ERROR);

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
	public void testUnparseableExceptionFromGetCakes() {

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET, STRING_ENTITY,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenThrow(UNPARSEABLE_GET_CAKES_EXCEPTION);

		CakeServiceModel getCakesModel = cakeService.getCakes();
		assertThat(getCakesModel.getCakes()).isEmpty();
		assertThat(getCakesModel.getCakeServiceError()).isEqualTo(UNPARSEABLE_GET_CAKES_ERROR);

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET,
				STRING_ENTITY, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});

	}

	@Test
	public void testUnparseableExceptionFromSaveCake() {

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST, REESES_DONUT_ENTITY,
				Cake.class)).thenThrow(UNPARSEABLE_SAVE_CAKE_EXCEPTION);

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET, STRING_ENTITY,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(GET_CAKES_RESPONSE);

		CakeServiceModel getCakesModel = cakeService.saveCake(REESES_DONUT);
		assertThat(getCakesModel.getCakes()).isEqualTo(GET_CAKES_LIST);
		assertThat(getCakesModel.getCakeServiceError()).isEqualTo(UNPARSEABLE_SAVE_CAKE_ERROR);

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST,
				REESES_DONUT_ENTITY, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET,
				STRING_ENTITY, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});

	}

	@Test
	public void testUnparseableExceptionsFromSaveCakeThenGetCakes() {

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST, REESES_DONUT_ENTITY,
				Cake.class)).thenThrow(UNPARSEABLE_SAVE_CAKE_EXCEPTION);

		when(cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET, STRING_ENTITY,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenThrow(UNPARSEABLE_GET_CAKES_EXCEPTION);

		CakeServiceModel getCakesModel = cakeService.saveCake(REESES_DONUT);
		assertThat(getCakesModel.getCakes()).isEmpty();
		assertThat(getCakesModel.getCakeServiceError()).isEqualTo(UNPARSEABLE_GET_CAKES_ERROR);

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST,
				REESES_DONUT_ENTITY, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange(CAKE_MANAGER_WS_URL, HttpMethod.GET,
				STRING_ENTITY, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});

	}

}