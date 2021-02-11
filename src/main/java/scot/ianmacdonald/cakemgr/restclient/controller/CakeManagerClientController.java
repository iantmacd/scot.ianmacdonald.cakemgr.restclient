package scot.ianmacdonald.cakemgr.restclient.controller;

import java.io.IOException;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.databind.ObjectMapper;

import scot.ianmacdonald.cakemgr.restclient.model.Cake;
import scot.ianmacdonald.cakemgr.restclient.model.CakeServiceError;

@Controller
public class CakeManagerClientController {

	private static ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private RestTemplate cakeServiceRestTemplate;

	@GetMapping("**")
	public RedirectView redirectToCakesView() {

		return new RedirectView("/cakes");
	}

	@GetMapping("/cakes")
	public String getCakes(Model model) {

		return prepareModelForView(model, null);
	}

	@PostMapping("/cakes")
	public String createCake(@ModelAttribute Cake cakeForm, Model model) {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaTypes.HAL_JSON);
		httpHeaders.setAccept(Collections.singletonList(MediaTypes.HAL_JSON));
		HttpEntity<Cake> entity = new HttpEntity<Cake>(cakeForm, httpHeaders);

		CakeServiceError cakeServiceError = null;

		try {
			cakeServiceRestTemplate.exchange("http://localhost:8080/cakes", HttpMethod.POST, entity, Cake.class);
		} catch (HttpClientErrorException ex) {
			String jsonErrorBody = ex.getResponseBodyAsString();
			try {
				cakeServiceError = objectMapper.readValue(jsonErrorBody, CakeServiceError.class);
			} catch (IOException ioe) {
				cakeServiceError = new CakeServiceError(HttpStatus.INTERNAL_SERVER_ERROR,
						"Unable to parse error from server", ioe);
			}

		}

		return prepareModelForView(model, cakeServiceError);
	}

	private String prepareModelForView(Model model, CakeServiceError cakeServiceError) {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaTypes.HAL_JSON);
		httpHeaders.setAccept(Collections.singletonList(MediaTypes.HAL_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(null, httpHeaders);
		
		ResponseEntity<CollectionModel<Cake>> cakeResponse = null;

		try {
			cakeResponse = cakeServiceRestTemplate.exchange(
					"http://localhost:8080/cakes", HttpMethod.GET, entity,
					new ParameterizedTypeReference<CollectionModel<Cake>>() {
					});
		} catch (HttpClientErrorException ex) {
			String jsonErrorBody = ex.getResponseBodyAsString();
			try {
				cakeServiceError = objectMapper.readValue(jsonErrorBody, CakeServiceError.class);
			} catch (IOException ioe) {
				cakeServiceError = new CakeServiceError(HttpStatus.INTERNAL_SERVER_ERROR,
						"Unable to parse error from server", ioe);
			}

		}

		List<Cake> cakeList = cakeResponse.getBody().getContent().stream().collect(Collectors.toList());

		model.addAttribute("cakeList", cakeList);
		model.addAttribute("cakeForm", new Cake());
		model.addAttribute("cakeServiceError", cakeServiceError);

		return "cakes";
	}

}