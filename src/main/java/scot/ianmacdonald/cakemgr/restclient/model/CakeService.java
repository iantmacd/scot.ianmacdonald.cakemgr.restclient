package scot.ianmacdonald.cakemgr.restclient.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CakeService {
	
	private static final String CAKE_MANAGER_WS_URL = "http://localhost:8081/cakes";

	private static ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	private RestTemplate cakeServiceRestTemplate;
	
	public CakeServiceModel getCakes() {
		
		return getModelForView(null);
	}
	
	public CakeServiceModel saveCake(final Cake cake) {
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaTypes.HAL_JSON);
		httpHeaders.setAccept(Collections.singletonList(MediaTypes.HAL_JSON));
		HttpEntity<Cake> entity = new HttpEntity<Cake>(cake, httpHeaders);

		CakeServiceError cakeServiceError = null;

		try {
			cakeServiceRestTemplate.exchange(CAKE_MANAGER_WS_URL, HttpMethod.POST, entity, Cake.class);
		} catch (HttpClientErrorException ex) {
			String jsonErrorBody = ex.getResponseBodyAsString();
			try {
				cakeServiceError = objectMapper.readValue(jsonErrorBody, CakeServiceError.class);
			} catch (IOException ioe) {
				cakeServiceError = new CakeServiceError(HttpStatus.INTERNAL_SERVER_ERROR,
						"Unable to parse error from server while attempting to save Cake object", ioe);
			}

		}
		
		return getModelForView(cakeServiceError);
	}
	
	private CakeServiceModel getModelForView(CakeServiceError cakeServiceError) {
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaTypes.HAL_JSON);
		httpHeaders.setAccept(Collections.singletonList(MediaTypes.HAL_JSON));
		HttpEntity<String> entity = new HttpEntity<String>("", httpHeaders);
		
		ResponseEntity<CollectionModel<Cake>> cakeResponse = null;

		try {
			cakeResponse = cakeServiceRestTemplate.exchange(
					CAKE_MANAGER_WS_URL, HttpMethod.GET, entity,
					new ParameterizedTypeReference<CollectionModel<Cake>>() {
					});
		} catch (HttpClientErrorException ex) {
			String jsonErrorBody = ex.getResponseBodyAsString();
			try {
				cakeServiceError = objectMapper.readValue(jsonErrorBody, CakeServiceError.class);
			} catch (IOException ioe) {
				cakeServiceError = new CakeServiceError(HttpStatus.INTERNAL_SERVER_ERROR,
						"Unable to parse error from server while attempting to read Cake objects", ioe);
			}

		}

		List<Cake> cakeList = new ArrayList<>();
		if (cakeResponse != null) {
			cakeList = cakeResponse.getBody().getContent().stream().collect(Collectors.toList());
		}
		
		return new CakeServiceModel(cakeList, cakeServiceError);
	}

}
