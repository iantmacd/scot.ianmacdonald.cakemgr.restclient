package scot.ianmacdonald.cakemgr.restclient.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import scot.ianmacdonald.cakemgr.restclient.model.Cake;
import scot.ianmacdonald.cakemgr.restclient.model.CakeService;
import scot.ianmacdonald.cakemgr.restclient.model.CakeServiceModel;

@Controller
public class CakeManagerClientController {

	@Autowired
	private CakeService cakeService;

	@RequestMapping("**")
	public RedirectView redirectToCakesView() {

		return new RedirectView("/cakes");
	}

	@GetMapping("/cakes")
	public String getCakes(Model model) {

		final CakeServiceModel cakeServiceModel = cakeService.getCakes();
		return prepareModelForView(model, cakeServiceModel, new Cake());
	}

	@PostMapping("/cakes")
	public String saveCake(@Valid @ModelAttribute("cakeForm") Cake cakeForm, BindingResult bindingResult, Model model) {
		
		CakeServiceModel cakeServiceModel = null;
		
		if (bindingResult.hasErrors()) {
			cakeServiceModel = cakeService.getCakes();
			return prepareModelForView(model, cakeServiceModel, cakeForm);
		} else {
			cakeServiceModel = cakeService.saveCake(cakeForm);
			return prepareModelForView(model, cakeServiceModel, new Cake());
		}

	}

	private String prepareModelForView(Model model, CakeServiceModel cakeServiceModel, Cake cakeForm) {

		model.addAttribute("cakeList", cakeServiceModel.getCakes());
		model.addAttribute("cakeServiceError", cakeServiceModel.getCakeServiceError());
		model.addAttribute("cakeForm", cakeForm);

		return "cakes";
	}

}