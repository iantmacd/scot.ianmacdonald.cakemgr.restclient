package scot.ianmacdonald.cakemgr.restclient.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
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

import java.util.Collections;

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
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

@ExtendWith(SpringExtension.class)
public class CakeServiceTest {

	@TestConfiguration
	static class CakeServiceTestContextConfiguration {

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

		httpHeaders.setContentType(MediaTypes.HAL_JSON);
		httpHeaders.setAccept(Collections.singletonList(MediaTypes.HAL_JSON));
		restTemplateOrderVerifier = inOrder(cakeServiceRestTemplate);
	}

	@Test
	public void testGetCakes() {

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.GET, stringEntity,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(getCakesResponse);

		CakeServiceModel getCakesModel = cakeService.getCakes();
		assertThat(getCakesModel.getCakes()).isEqualTo(getCakesList);
		assertThat(getCakesModel.getCakeServiceError()).isNull();

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
	}

	@Test
	public void testSaveCake() {

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.POST, reesDonutEntity,
				Cake.class)).thenReturn(saveCakeResponse);

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.GET, stringEntity,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(saveCakeGetCakesResponse);

		CakeServiceModel saveCakeModel = cakeService.saveCake(reesesDonut);
		assertThat(saveCakeModel.getCakes()).isEqualTo(saveCakeList);
		assertThat(saveCakeModel.getCakeServiceError()).isNull();

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.POST,
				reesDonutEntity, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});

	}

	@Test
	public void testSaveCakeTwice() {

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.POST, reesDonutEntity,
				Cake.class)).thenReturn(saveCakeResponse);
		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.POST, chocolateCakeEntity,
				Cake.class)).thenReturn(saveCakeTwiceResponse);
		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.GET, stringEntity,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(saveCakeGetCakesResponse).thenReturn(saveCakeGetCakesTwiceResponse);

		CakeServiceModel saveCakeModel = cakeService.saveCake(reesesDonut);
		assertThat(saveCakeModel.getCakes()).isEqualTo(saveCakeList);
		assertThat(saveCakeModel.getCakeServiceError()).isNull();
		CakeServiceModel saveCakeTwiceModel = cakeService.saveCake(chocolateCake);
		assertThat(saveCakeTwiceModel.getCakes()).isEqualTo(saveCakeTwiceList);
		assertThat(saveCakeTwiceModel.getCakeServiceError()).isNull();

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
	public void testSaveSameCakeTwice() throws JsonProcessingException {

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.POST, reesDonutEntity,
				Cake.class)).thenReturn(saveCakeResponse).thenThrow(duplicateCakeException);

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.GET, stringEntity,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(saveCakeGetCakesResponse).thenReturn(saveCakeGetCakesResponse);

		CakeServiceModel saveCakeModel = cakeService.saveCake(reesesDonut);
		assertThat(saveCakeModel.getCakes()).isEqualTo(saveCakeList);
		assertThat(saveCakeModel.getCakeServiceError()).isNull();
		saveCakeModel = cakeService.saveCake(reesesDonut);
		assertThat(saveCakeModel.getCakes()).isEqualTo(saveCakeList);
		assertThat(saveCakeModel.getCakeServiceError()).isEqualTo(duplicateTitleCakeServiceError);

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
	public void testUnparseableExceptionFromGetCakes() {

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.GET, stringEntity,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenThrow(unparseableGetCakesException);

		CakeServiceModel getCakesModel = cakeService.getCakes();
		assertThat(getCakesModel.getCakes()).isEmpty();
		assertThat(getCakesModel.getCakeServiceError()).isEqualTo(unparseableGetCakesExceptionError);

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});

	}

	@Test
	public void testUnparseableExceptionFromSaveCake() {

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.POST, reesDonutEntity,
				Cake.class)).thenThrow(unparseableSaveCakeException);

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.GET, stringEntity,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(getCakesResponse);

		CakeServiceModel getCakesModel = cakeService.saveCake(reesesDonut);
		assertThat(getCakesModel.getCakes()).isEqualTo(getCakesList);
		assertThat(getCakesModel.getCakeServiceError()).isEqualTo(unparseableSaveCakeExceptionError);

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.POST,
				reesDonutEntity, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});

	}

	@Test
	public void testUnparseableExceptionsFromSaveCakeThenGetCakes() {

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.POST, reesDonutEntity,
				Cake.class)).thenThrow(unparseableSaveCakeException);

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.GET, stringEntity,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenThrow(unparseableGetCakesException);

		CakeServiceModel getCakesModel = cakeService.saveCake(reesesDonut);
		assertThat(getCakesModel.getCakes()).isEmpty();
		assertThat(getCakesModel.getCakeServiceError()).isEqualTo(unparseableGetCakesExceptionError);

		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.POST,
				reesDonutEntity, Cake.class);
		restTemplateOrderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});

	}

}