package scot.ianmacdonald.cakemgr.restclient.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import scot.ianmacdonald.cakemgr.restclient.model.Cake;

@Controller
public class CakeManagerClientController {

	private static final Logger log = LoggerFactory.getLogger(CakeManagerClientController.class);

	@Autowired
	private RestTemplate cakeServiceRestTemplate;

	@GetMapping("/cakes")
	public String getCakes(Model model) {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaTypes.HAL_JSON);
		httpHeaders.setAccept(Collections.singletonList(MediaTypes.HAL_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(null, httpHeaders);

		ResponseEntity<CollectionModel<Cake>> cakeResponse = cakeServiceRestTemplate.exchange(
				"http://localhost:8080/cakes", HttpMethod.GET, entity,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});

		List<Cake> cakeList = cakeResponse.getBody().getContent().stream().collect(Collectors.toList());

		model.addAttribute("cakeList", cakeList);
		model.addAttribute("cakeForm", new Cake()); 

		return "cakes";
	}

	@PostMapping("/cakes")
	public String createCake(@ModelAttribute Cake cakeForm, Model model) {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaTypes.HAL_JSON);
		httpHeaders.setAccept(Collections.singletonList(MediaTypes.HAL_JSON));
		HttpEntity<Cake> entity = new HttpEntity<Cake>(cakeForm, httpHeaders);

		ResponseEntity<Cake> createdCake = cakeServiceRestTemplate.exchange("http://localhost:8080/cakes",
				HttpMethod.POST, entity, Cake.class);

		log.info("Cake returned from REST service: " + createdCake);

		ResponseEntity<CollectionModel<Cake>> cakeResponse = cakeServiceRestTemplate.exchange(
				"http://localhost:8080/cakes", HttpMethod.GET, null,
				new ParameterizedTypeReference<CollectionModel<Cake>>() {
				});

		List<Cake> cakeList = cakeResponse.getBody().getContent().stream().collect(Collectors.toList());

		model.addAttribute("cakeList", cakeList);
		model.addAttribute("cakeForm", new Cake()); 

		return "cakes";
	}

}