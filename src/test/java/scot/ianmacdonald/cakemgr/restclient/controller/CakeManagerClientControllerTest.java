package scot.ianmacdonald.cakemgr.restclient.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
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
	private CakeService cakeService;

	@MockBean
	private RestTemplate cakeServiceRestTemplate;

	// instance test data
	private final Cake banoffeePie = new Cake("Banoffee Pie", "Is it banana or toffee?  Who cares? Its deelishuss!",
			"http://www.banoffepiepics.com");
	private final Cake lemonCheesecake = new Cake("Lemon Cheesecake", "Lemony creamy cheesey goodness",
			"http://www.lemoncheesecake.org");
	private final Cake reesesDonut = new Cake("Reeses Donut", "Peanut butter choclate heaven",
			"http://www.reesesdonut.scot");
	private final ArrayList<Cake> getCakesList = new ArrayList<>(Arrays.asList(banoffeePie, lemonCheesecake));
	private final ArrayList<Cake> saveCakeList = new ArrayList<>(
			Arrays.asList(banoffeePie, lemonCheesecake, reesesDonut));

	@Test
	public void testGetCakesRequest() throws Exception {

		CakeServiceModel cakeServiceModel = new CakeServiceModel(getCakesList, null);

		when(cakeService.getCakes()).thenReturn(cakeServiceModel);

		mockMvc.perform(get("/cakes")).andDo(print()).andExpect(status().isOk())
				.andExpect(model().attribute("cakeList", equalTo(getCakesList)))
				.andExpect(model().attribute("cakeServiceError", equalTo(null)))
				.andExpect(model().attribute("cakeForm", equalTo(new Cake())));
	}

}
