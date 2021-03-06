package scot.ianmacdonald.cakemgr.restclient.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import scot.ianmacdonald.cakemgr.restclient.model.Cake;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(OrderAnnotation.class)
class CakeManagerClientFunctionalTest {

	private static final String CAKE_WEBAPP_BASE_URL = "http://localhost:8080";

	private static Cake lemonCheeseCake = new Cake("Lemon cheesecake", "A cheesecake made of lemon",
			"https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg");

	private static Cake victoriaSponge = new Cake("victoria sponge", "sponge with jam",
			"http://www.bbcgoodfood.com/sites/bbcgoodfood.com/files/recipe_images/recipe-image-legacy-id--1001468_10.jpg");

	private static Cake carrotCake = new Cake("Carrot cake", "Bugs bunnys favourite",
			"http://www.villageinn.com/i/pies/profile/carrotcake_main1.jpg");

	private static Cake bananaCake = new Cake("Banana cake", "Donkey kongs favourite",
			"http://ukcdn.ar-cdn.com/recipes/xlarge/ff22df7f-dbcd-4a09-81f7-9c1d8395d936.jpg");

	private static Cake birthdayCake = new Cake("Birthday cake", "a yearly treat",
			"http://cornandco.com/wp-content/uploads/2014/05/birthday-cake-popcorn.jpg");

	private static Cake reesesDonut = new Cake("Reeses Donut", "Chocolate peanut butter goodness",
			"https://www.gannett-cdn.com/presto/2019/08/06/USAT/951746ac-9fcc-4a45-a439-300b72421984-Krispy_Kreme_Reeses_Lovers_Original_Filled_Doughnuts_Key_Visual_2.jpg");

	private static List<Cake> defaultCakeList = new ArrayList<>(
			Arrays.asList(lemonCheeseCake, victoriaSponge, carrotCake, bananaCake, birthdayCake));

	private static List<Cake> addedCakeList = new ArrayList<>(
			Arrays.asList(lemonCheeseCake, victoriaSponge, carrotCake, bananaCake, birthdayCake, reesesDonut));

	private WebClient webClient;

	@BeforeEach
	void setup(WebApplicationContext context) {
		
		webClient = MockMvcWebClientBuilder.webAppContextSetup(context).build();
	}

	@Test
	@Order(1)
	public void testBaseUrlDirectsToCakesContext() throws Exception {

		final HtmlPage page = webClient.getPage(CAKE_WEBAPP_BASE_URL);

		testIsDefaultContextPage(page);
	}

	@Test
	@Order(2)
	public void testRootPathDirectsToCakesContext() throws Exception {

		final HtmlPage page = webClient.getPage(CAKE_WEBAPP_BASE_URL + "/");
		testIsDefaultContextPage(page);
	}

	@Test
	@Order(3)
	public void testAllPathsDirectToCakesContext() throws Exception {

		final HtmlPage pageOne = webClient.getPage(CAKE_WEBAPP_BASE_URL + "/simple");
		testIsDefaultContextPage(pageOne);

		final HtmlPage pageTwo = webClient.getPage(CAKE_WEBAPP_BASE_URL + "/any/random/path");
		testIsDefaultContextPage(pageTwo);

		final HtmlPage pageThree = webClient.getPage(CAKE_WEBAPP_BASE_URL + "/the/path/less/travelled");
		testIsDefaultContextPage(pageThree);
	}

	@Test
	@Order(4)
	public void testCakesPathDirectsToCakesContext() throws Exception {

		final HtmlPage page = webClient.getPage(CAKE_WEBAPP_BASE_URL + "/cakes");
		testIsDefaultContextPage(page);
	}
	
	@Test
	@Order(5)
	public void testPostCakeFormTitleInvalid() throws Exception {

		HtmlPage page = submitCakeForm("", reesesDonut.getDescription(), reesesDonut.getImage());

		testCakeTableContent(page, defaultCakeList);

		testCakeFormTableContent(page, "", true, reesesDonut.getDescription(), false, reesesDonut.getImage(), false);

		testServerErrorTableContent(page, false);
	}
	
	@Test
	@Order(6)
	public void testPostCakeFormDescriptionInvalid() throws Exception {

		HtmlPage page = submitCakeForm(reesesDonut.getTitle(), "", reesesDonut.getImage());

		testCakeTableContent(page, defaultCakeList);

		testCakeFormTableContent(page, reesesDonut.getTitle(), false, "", true, reesesDonut.getImage(), false);

		testServerErrorTableContent(page, false);
	}
	
	@Test
	@Order(7)
	public void testPostCakeFormImageInvalid() throws Exception {

		HtmlPage page = submitCakeForm(reesesDonut.getTitle(), reesesDonut.getDescription(), "");

		testCakeTableContent(page, defaultCakeList);

		testCakeFormTableContent(page, reesesDonut.getTitle(), false, reesesDonut.getDescription(), false, "", true);

		testServerErrorTableContent(page, false);
	}
	
	@Test
	@Order(8)
	public void testPostCakeFormAllFieldsInvalid() throws Exception {

		HtmlPage page = submitCakeForm("", "", "");

		testCakeTableContent(page, defaultCakeList);

		testCakeFormTableContent(page, "", true, "", true, "", true);

		testServerErrorTableContent(page, false);
	}

	@Test
	@Order(9)
	public void testPostCakeForm() throws Exception {

		HtmlPage page = submitCakeForm(reesesDonut.getTitle(), reesesDonut.getDescription(), reesesDonut.getImage());

		testCakeTableContent(page, addedCakeList);

		testCakeFormTableContent(page, "", false, "", false, "", false);

		testServerErrorTableContent(page, false);
	}

	@Test
	@Order(10)
	public void testPostCakeFormAgain() throws Exception {

		HtmlPage page = submitCakeForm(reesesDonut.getTitle(), reesesDonut.getDescription(), reesesDonut.getImage());

		testCakeTableContent(page, addedCakeList);

		testCakeFormTableContent(page, "", false, "", false, "", false);

		testServerErrorTableContent(page, true);
	}

	private void testIsDefaultContextPage(HtmlPage page) {

		testCakeTableContent(page, defaultCakeList);

		testCakeFormTableContent(page, "", false, "", false, "", false);

		testServerErrorTableContent(page, false);
	}
	
	private HtmlPage submitCakeForm(String title, String description, String image) throws IOException, MalformedURLException {

		final HtmlPage page = webClient.getPage(CAKE_WEBAPP_BASE_URL + "/cakes");
		final HtmlForm form = page.getFormByName("cakeForm");
		HtmlTextInput titleInput = form.getInputByName("title");
		titleInput.setValueAttribute(title);
		HtmlTextInput descriptionInput = form.getInputByName("description");
		descriptionInput.setValueAttribute(description);
		HtmlTextInput imageInput = form.getInputByName("image");
		imageInput.setValueAttribute(image);
		HtmlSubmitInput submit = form.getOneHtmlElementByAttribute("input", "type", "submit");
		HtmlPage addedCakePage = submit.click();
		return addedCakePage;
	}

	private void testCakeTableContent(HtmlPage page, List<Cake> cakeList) {

		assertEquals("Cake Manager: View and Create Cakes", page.getTitleText());
		assertEquals("/cakes", page.getUrl().getPath());

		HtmlTable cakeTable = page.getHtmlElementById("cakeTable");
		assertNotNull(cakeTable);

		int count = 0;
		for (final HtmlTableRow row : cakeTable.getRows()) {

			if (count == 0) {
				assertEquals("Title", row.getCell(0).asText());
				assertEquals("Description", row.getCell(1).asText());
				assertEquals("Image", row.getCell(2).asText());
			} else {
				assertEquals(cakeList.get(count - 1).getTitle(), row.getCell(0).asText());
				assertEquals(cakeList.get(count - 1).getDescription(), row.getCell(1).asText());
				HtmlTableCell linkCell = row.getCell(2);
				assertEquals(cakeList.get(count - 1).getTitle(), linkCell.asText());
				HtmlAnchor anchor = (HtmlAnchor) linkCell.getFirstElementChild();
				assertEquals(cakeList.get(count - 1).getImage(), anchor.getHrefAttribute());
			}
			count++;
		}

	}

	private void testCakeFormTableContent(HtmlPage page, String titleValue, boolean isTitleError, String descriptionValue,
			boolean isDescriptionError, String imageValue, boolean isImageError) {

		HtmlForm cakeForm = page.getFormByName("cakeForm");
		assertNotNull(cakeForm);

		HtmlTable cakeFormTable = (HtmlTable) cakeForm.getFirstElementChild();

		List<HtmlTableRow> tableRows = cakeFormTable.getRows();
		assertEquals("Title:", tableRows.get(0).getCell(0).asText());
		assertEquals("Description:", tableRows.get(1).getCell(0).asText());
		assertEquals("Image:", tableRows.get(2).getCell(0).asText());

		HtmlTextInput titleInput = (HtmlTextInput) tableRows.get(0).getCell(1).getFirstElementChild();
		assertEquals("title", titleInput.getId());
		assertEquals("title", titleInput.getNameAttribute());
		assertEquals(titleValue, titleInput.getValueAttribute());
		if (isTitleError) {
			assertEquals("size must be between 2 and 50", tableRows.get(0).getCell(2).asText());
		}

		HtmlTextInput descriptionInput = (HtmlTextInput) tableRows.get(1).getCell(1).getFirstElementChild();
		assertEquals("description", descriptionInput.getId());
		assertEquals("description", descriptionInput.getNameAttribute());
		assertEquals(descriptionValue, descriptionInput.getValueAttribute());
		if (isDescriptionError) {
			assertEquals("size must be between 5 and 100", tableRows.get(1).getCell(2).asText());
		}

		HtmlTextInput imageInput = (HtmlTextInput) tableRows.get(2).getCell(1).getFirstElementChild();
		assertEquals("image", imageInput.getId());
		assertEquals("image", imageInput.getNameAttribute());
		assertEquals(imageValue, imageInput.getValueAttribute());
		if (isImageError) {
			assertEquals("size must be between 10 and 300", tableRows.get(2).getCell(2).asText());
		}
		
	}

	private void testServerErrorTableContent(HtmlPage page, boolean isError) {

		if (isError) {
			HtmlTable errorTable = page.getHtmlElementById("errorTable");
			assertNotNull(errorTable);
			assertEquals("HTTP Status:", errorTable.getRow(0).getCell(0).asText());
			assertEquals("403 FORBIDDEN", errorTable.getRow(0).getCell(1).asText());
			assertEquals("Error Message:", errorTable.getRow(1).getCell(0).asText());
			assertEquals("It is forbidden to create a Cake with a duplicate title",
					errorTable.getRow(1).getCell(1).asText());
			assertEquals("Debug Message:", errorTable.getRow(2).getCell(0).asText());
			assertEquals(
					"could not execute statement; SQL [n/a]; constraint [\"PUBLIC.UK_O5VGXH55G2VXMKU8W39A88WH0_INDEX_1 ON PUBLIC.CAKE(TITLE) VALUES 6\"; SQL statement: insert into cake (description, image, title, id) values (?, ?, ?, ?) [23505-200]]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement",
					errorTable.getRow(2).getCell(1).asText());
		} else {
			assertThrows(ElementNotFoundException.class, () -> {
				page.getHtmlElementById("errorTable");
			});
		}
		
	}

}
