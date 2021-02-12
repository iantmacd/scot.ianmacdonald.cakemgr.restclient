package scot.ianmacdonald.cakemgr.restclient;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import scot.ianmacdonald.cakemgr.restclient.controller.CakeManagerClientController;
import scot.ianmacdonald.cakemgr.restclient.security.SecurityConfig;

@SpringBootTest
class CakeManagerClientApplicationTest {
	
	@Autowired
	private RestTemplate cakeServiceRestTemplate;

	@Autowired
	private CakeManagerClientController controller;
	
	@Autowired
	private SecurityConfig securityConfig;

	@Test
	void contextLoads() {
		assertThat(cakeServiceRestTemplate).isNotNull();
		assertThat(controller).isNotNull();
		assertThat(securityConfig).isNotNull();
	}

}
