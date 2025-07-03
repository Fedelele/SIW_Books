package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.validator.CredentialsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;


@Controller
public class AuthenticationController {

	@Autowired
	private CredentialsService credentialsService;
	
	@Autowired 
	private UserService userService;

	@Autowired
	private CredentialsValidator credentialsValidator;

	@GetMapping("/")
	public String homeRedirectedByRole() {
		Credentials credentials = credentialsService.getLoggedCredentials();

		//if the user has role "ADMIN", send him to the admin page
		if (credentials != null && Credentials.ADMIN_ROLE.equals(credentials.getRole())) {
			return "redirect:/admin/home";
		}
		//else send him to the default page
		return "home";
	}

	@GetMapping(value="/login")
	public String showLoginForm() {
		return "formLogin.html";
	}
	
	@GetMapping(value="/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		return "formRegister.html";
	}

	@PostMapping("/register")
	public String registerUser(@ModelAttribute User user,
							   BindingResult userBindingResult, @ModelAttribute Credentials credentials,
							   BindingResult credentialsBindingResult,
							   Model model) {
		//Validation to check if already exists a user with the same username
		this.credentialsValidator.validate(credentials, credentialsBindingResult);

		//If both user and credential are valid, memorize them in the DB
		if(!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
			this.userService.save(user);
			credentials.setUser(user);
			credentialsService.saveCredentials(credentials);
			model.addAttribute("user", user);
			return "registrationSuccessful.html";
		}
		return "formRegister.html";
	}
}
