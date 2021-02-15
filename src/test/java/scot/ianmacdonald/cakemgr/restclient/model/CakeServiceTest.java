package scot.ianmacdonald.cakemgr.restclient.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	// static test data
	private static final HttpHeaders httpHeaders = new HttpHeaders();
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
	private final CollectionModel<Cake> getCakesCollectionModel = CollectionModel.of(getCakesList);
	private final CollectionModel<Cake> saveCakeCollectionModel = CollectionModel.of(saveCakeList);
	private final CollectionModel<Cake> saveCakeTwiceCollectionModel = CollectionModel.of(saveCakeTwiceList);
	private final ResponseEntity<CollectionModel<Cake>> getCakesResponse = new ResponseEntity<CollectionModel<Cake>>(
			getCakesCollectionModel, HttpStatus.OK);
	private final ResponseEntity<CollectionModel<Cake>> saveCakeGetCakesResponse = new ResponseEntity<CollectionModel<Cake>>(
			saveCakeCollectionModel, HttpStatus.OK);
	private final ResponseEntity<CollectionModel<Cake>> saveCakeGetCakesTwiceResponse = new ResponseEntity<CollectionModel<Cake>>(
			saveCakeTwiceCollectionModel, HttpStatus.OK);
	private final ResponseEntity<Cake> saveCakeResponse = new ResponseEntity<>(reesesDonut, HttpStatus.OK);
	private final ResponseEntity<Cake> saveCakeTwiceResponse = new ResponseEntity<>(reesesDonut, HttpStatus.OK);
	private final HttpEntity<String> stringEntity = new HttpEntity<String>("", httpHeaders);
	private final HttpEntity<Cake> reesDonutEntity = new HttpEntity<Cake>(reesesDonut, httpHeaders);
	private final HttpEntity<Cake> chocolateCakeEntity = new HttpEntity<Cake>(chocolateCake, httpHeaders);
	private final CakeServiceError unparseableGetCakesExceptionError = new CakeServiceError(
			HttpStatus.INTERNAL_SERVER_ERROR, "Unable to parse error from server while attempting to read Cake objects",
			new Throwable(
					"Unrecognized token 'unparseable': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n"
							+ " at [Source: (String)\"unparseable\"; line: 1, column: 12]"));
	private final HttpClientErrorException unparseableGetCakesException = new HttpClientErrorException(
			HttpStatus.INTERNAL_SERVER_ERROR, null, "unparseable".getBytes(), null);
	private final CakeServiceError unparseableSaveCakeExceptionError = new CakeServiceError(
			HttpStatus.INTERNAL_SERVER_ERROR, "Unable to parse error from server while attempting to save Cake object",
			new Throwable(
					"Unrecognized token 'unparseable': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n"
							+ " at [Source: (String)\"unparseable\"; line: 1, column: 12]"));
	private final HttpClientErrorException unparseableSaveCakeException = new HttpClientErrorException(
			HttpStatus.INTERNAL_SERVER_ERROR, null, "unparseable".getBytes(), null);

	@BeforeAll
	public static void setUpStaticTestData(@Autowired RestTemplate cakeServiceRestTemplate) {

		httpHeaders.setContentType(MediaTypes.HAL_JSON);
		httpHeaders.setAccept(Collections.singletonList(MediaTypes.HAL_JSON));
		orderVerifier = inOrder(cakeServiceRestTemplate);
	}

	@Test
	public void testGetCakes() {

		when(cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.GET, stringEntity,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				})).thenReturn(getCakesResponse);

		CakeServiceModel getCakesModel = cakeService.getCakes();
		assertThat(getCakesModel.getCakes()).isEqualTo(getCakesList);
		assertThat(getCakesModel.getCakeServiceError()).isNull();

		orderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
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

		orderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.POST,
				reesDonutEntity, Cake.class);
		orderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
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
				})).thenReturn(saveCakeGetCakesResponse).thenReturn(saveCakeGetCakesTwiceResponse);;

		CakeServiceModel saveCakeModel = cakeService.saveCake(reesesDonut);
		assertThat(saveCakeModel.getCakes()).isEqualTo(saveCakeList);
		assertThat(saveCakeModel.getCakeServiceError()).isNull();
		CakeServiceModel saveCakeTwiceModel = cakeService.saveCake(chocolateCake);
		assertThat(saveCakeTwiceModel.getCakes()).isEqualTo(saveCakeTwiceList);
		assertThat(saveCakeTwiceModel.getCakeServiceError()).isNull();

		orderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.POST,
				reesDonutEntity, Cake.class);
		orderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
		orderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.POST,
				chocolateCakeEntity, Cake.class);
		orderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});

	}

	@Test
	public void testSaveSameCakeTwice() throws JsonProcessingException {

		CakeServiceError duplicateTitleCakeServiceError = new CakeServiceError(HttpStatus.FORBIDDEN,
				"It is forbidden to create a Cake with a duplicate title", new Throwable(
						"could not execute statement; SQL [n/a]; constraint [\"PUBLIC.UK_O5VGXH55G2VXMKU8W39A88WH0_INDEX_1 ON PUBLIC.CAKE(TITLE) VALUES 11\"; SQL statement: insert into cake (description, image, title, id) values (?, ?, ?, ?) [23505-200]]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement"));
		byte[] exceptionResponseBody = new ObjectMapper().writeValueAsBytes(duplicateTitleCakeServiceError);
		HttpClientErrorException duplicateCakeException = new HttpClientErrorException(HttpStatus.FORBIDDEN, null,
				exceptionResponseBody, null);

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

		orderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.POST,
				reesDonutEntity, Cake.class);
		orderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});
		orderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.POST,
				reesDonutEntity, Cake.class);
		orderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
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

		orderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
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

		orderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.POST,
				reesDonutEntity, Cake.class);
		orderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
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

		orderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.POST,
				reesDonutEntity, Cake.class);
		orderVerifier.verify(cakeServiceRestTemplate).exchange("http://localhost:8080/cakes", HttpMethod.GET,
				stringEntity, new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});

	}

}