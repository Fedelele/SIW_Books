package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.CredentialsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.model.Author;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class AuthorController {

	//To log errors when saving an entity fails
	private static final Logger log = LoggerFactory.getLogger(AuthorController.class);

	@Autowired
	private CredentialsService  credentialsService;

	@Autowired
	private AuthorService authorService;

	@GetMapping(value="/admin/formNewAuthor")
	public String formNewAuthor(Model model) {
		model.addAttribute("author", new Author());
		return "admin/formNewAuthor.html";
	}

	//For inserting a new author + pic management
	@PostMapping("admin/author/new")
	public String authorNew(@Valid @ModelAttribute Author author,
							BindingResult bindingResult,
							@RequestParam("image") MultipartFile image,
							Model model, RedirectAttributes redirectAttributes) {
		if(bindingResult.hasErrors()){
			return "/admin/formNewAuthor.html";
		}

		/*
		 *Before using the author id I need to save it in the DB
		 * authorService.save(author);
		 *Now the id is available for use
		 */

		try{
			String fileName = author.getName() + author.getId() + '.' + image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.') + 1);
			Path uploadPath = Paths.get("C:/Users/wufed/Desktop/uploads-siw-books/author-photo/");
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			Path filePath = uploadPath.resolve(fileName);
			image.transferTo(filePath.toFile());
			author.setImageUrl(String.format("/author-photo/%s", fileName));
			authorService.save(author);

		} catch (IOException e) {
			log.error("Errore nel salvataggio del file", e);
			model.addAttribute("error", "Errore durante il caricamento dell'immagine. Riprova.");
			return "/admin/formNewAuthor.html";
		}
		redirectAttributes.addFlashAttribute("success", "Autore aggiunto con successo");
		return "redirect:/author/all";
	}

	@GetMapping("/admin/formUpdateAuthor/{id}")
	public String formUpdateAuthor(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes){
		Author author = authorService.findById(id).orElse(null);
		if(author == null) {
			redirectAttributes.addFlashAttribute("error", "Autore non trovato");
			return "redirect:/author/all";
		}
		model.addAttribute("author", author);
		return "admin/formNewAuthor.html";
	}

	@PostMapping("/admin/updateAuthor/{id}")
	public String updateAuthor(@PathVariable Long id,
							   @Valid @ModelAttribute Author author,
							   BindingResult bindingResult,
							   RedirectAttributes redirectAttributes, Model model) {
		if (bindingResult.hasErrors()) {
			return "admin/formNewAuthor.html";
		}
		try{
			authorService.updateAuthor(id, author);
			redirectAttributes.addFlashAttribute("success", "Autore aggiornato con successo");
			return "redirect:/author/all";
		} catch (IllegalArgumentException e){
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/author/all";
	}

	@GetMapping("/author/details/{id}")
	public String author(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes){
		Author author = this.authorService.findById(id).orElse(null);
		if(author == null){
			redirectAttributes.addFlashAttribute("error", "Autore non trovato");
			return "redirect:/author/all";
		}

		model.addAttribute("author", author);
		model.addAttribute("books", author.getAuthorsOf());

		Credentials loggedCredentials = credentialsService.getLoggedCredentials();
		model.addAttribute("isLoggedIn", loggedCredentials != null);
		return "author";
	}

	@GetMapping("/author/all")
	public String getAuthors(Model model) {
		Credentials loggedCredentials = credentialsService.getLoggedCredentials();
		if(loggedCredentials == null){
			model.addAttribute("isLoggedIn", false);
			model.addAttribute("role", "NOROLE");
		} else {
			model.addAttribute("isLoggedIn", true);
			model.addAttribute("role", loggedCredentials.getRole());
		}
		model.addAttribute("authors", this.authorService.findAll());
		return "authors.html";
	}

	@PostMapping("/author/delete/{id}")
	public String deleteAuthor(@PathVariable Long id, Model model) {
		Author author = this.authorService.findById(id).orElse(null);
		if(author == null){
			model.addAttribute("error", "Author not found");
			return "error";
		}
		this.authorService.deleteAuthor(author);
		return "redirect:/author/all";
	}
}
