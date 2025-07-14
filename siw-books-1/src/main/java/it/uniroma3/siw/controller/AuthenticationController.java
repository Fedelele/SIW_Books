package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.dto.RegistrationForm;
import it.uniroma3.siw.model.*;
import it.uniroma3.siw.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import java.util.List;


@Controller
public class AuthenticationController {

	@Autowired
	private CredentialsService credentialsService;
	
	@Autowired 
	private UserService userService;

	@Autowired
	private BookService bookService;

	@Autowired
	private AuthorService authorService;

	//Needed for the profile page
	@Autowired
	private ReviewService reviewService;

//Version 1
//	@GetMapping("/")
//	public String homeRedirectedByRole() {
//		Credentials credentials = credentialsService.getLoggedCredentials();
//
//		//if the user has role "ADMIN", send him to the admin page
//		if (credentials != null && Credentials.ADMIN_ROLE.equals(credentials.getRole())) {
//			return "redirect:/admin/home";
//		}
//		//else send him to the default page
//		return "redirect:/home";
//	}

	//Version 2
	@GetMapping("/")
	public String index(){
		return "redirect:/home";
	}

	@GetMapping(value="/home")
	public String home(Model model){
		//Loads top 3 books
		List<Book> bestSellers = this.bookService.findTopRatedBooks(4);
		model.addAttribute("bestSellers", bestSellers);

		//Loads top 3 authors
		List<Author> topAuthors = this.authorService.findAuthorsWithMostBooks(4);
		model.addAttribute("topAuthors", topAuthors);

		//Loads the 3 latest books published
		List<Book> newReleases = this.bookService.findMostRecentBooks(5);
		model.addAttribute("newReleases", newReleases);

		//User management for view's header
		Credentials credentials = credentialsService.getLoggedCredentials();
		if(credentials != null){
			model.addAttribute("user", credentials.getUser());
			model.addAttribute("isLoggedIn", true);
		}else{
			model.addAttribute("isLoggedIn", false);
		}
		return "home.html";
	}

	@GetMapping(value="/login")
	public String showLoginForm() {
		return "formLogin.html";
	}

	//Version 1
//	@GetMapping(value="/register")
//	public String showRegisterForm(Model model) {
//		model.addAttribute("user", new User());
//		model.addAttribute("credentials", new Credentials());
//		return "formRegister.html";
//	}
//
//	@PostMapping("/register")
//	public String registerUser(@ModelAttribute User user,
//							   BindingResult userBindingResult, @ModelAttribute Credentials credentials,
//							   BindingResult credentialsBindingResult,
//							   Model model) {
//		//Validation to check if already exists a user with the same username
//		this.credentialsValidator.validate(credentials, credentialsBindingResult);
//
//		//If both user and credential are valid, memorize them in the DB
//		if(!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
//			this.userService.save(user);
//			credentials.setUser(user);
//			credentialsService.saveCredentials(credentials);
//			model.addAttribute("user", user);
//			return "registrationSuccessful.html";
//		}
//		return "formRegister.html";
//	}

	//Version 2
	@GetMapping(value="/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("registrationForm", new RegistrationForm());
		return "formRegister.html";
	}

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute RegistrationForm registrationForm, BindingResult bindingResult, Model model) {
		if (credentialsService.existsByUsername(registrationForm.getUsername())) {
			bindingResult.rejectValue("username", "credentials.duplicate", "Username already taken");
		}

		if(bindingResult.hasErrors()){
			return "formRegister.html";
		}

		User user = new User();
		user.setName(registrationForm.getName());
		user.setSurname(registrationForm.getSurname());
		user.setEmail(registrationForm.getEmail());
		userService.save(user);

		Credentials credentials = new Credentials();
		credentials.setUsername(registrationForm.getUsername());
		credentials.setPassword(registrationForm.getPassword());
		credentials.setUser(user);
		credentialsService.saveCredentials(credentials);

		model.addAttribute("user", user);
		return "registrationSuccessful.html";
	}

	@GetMapping("/profile")
	public String showProfile(Model model){
		Credentials credentials = credentialsService.getLoggedCredentials();
		if(credentials == null){
			return "redirect:/login";
		}

		User user = credentials.getUser();
		List<Review> reviews = reviewService.getReviewsByUser(user);

		model.addAttribute("user", user);
		model.addAttribute("credentials", credentials);
		model.addAttribute("reviews", reviews);
		return "profile.html";
	}
}
