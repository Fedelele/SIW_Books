package it.uniroma3.siw.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import it.uniroma3.siw.controller.validator.BookValidator;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BookController {
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private AuthorService authorService;

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private ReviewService reviewService;


	//Validator needed to check any duplicates when adding a new book
	@Autowired
	private BookValidator bookValidator;

	@GetMapping(value="/admin/formNewBook")
	public String formNewBook(Model model) {
		model.addAttribute("book", new Book());
		model.addAttribute("authors", this.authorService.findAll());
		return "admin/formNewBook.html";
	}

	@PostMapping("/admin/book/new")
	public String newBook(@Valid @ModelAttribute Book book,
						  @RequestParam(value = "authors[]", required = false) List<Long> authorIds,
						  BindingResult bindingResult, @ModelAttribute("image") MultipartFile image, Model model) {
		this.bookValidator.validate(book, bindingResult);

		if(bindingResult.hasErrors()){
			model.addAttribute("authors", authorService.findAll());
			return "admin/formNewBook.html";
		}

		List<Author> authors = authorService.findByIds(authorIds);
		book.setAuthors(authors);

		try{
			String fileName = book.getTitle() + book.getAuthors().get(0).getName()
					+ '.' + image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".")+1);
			Path uploadPath = Paths.get("C:/Users/wufed/Desktop/uploads-siw-books/books-covers/");

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			Path filePath = uploadPath.resolve(fileName);
			image.transferTo(filePath.toFile());
			book.setImageUrl(String.format("/books-covers/%s", fileName));
			for(Author a : book.getAuthors())
				a.getAuthorsOf().add(book);
			bookService.save(book);
		}catch(IOException e) {
			e.printStackTrace();
			bindingResult.rejectValue("book.image.upload.failed", "Impossibile salvare l'immagine");
			model.addAttribute("authors", authorService.findAll());
			return "admin/formNewBook.html";
		}
		return "redirect:/book/all";
	}

	//By returning formNewBook, it should be already filled
	@GetMapping(value="/admin/formUpdateBook/{id}")
	public String formUpdateBook(@PathVariable Long id, Model model) {
		Book book = bookService.findById(id).orElseThrow(() -> new IllegalArgumentException("Libro non trovato"));
		model.addAttribute("book", book);
		model.addAttribute("authors", this.authorService.findAll());
		return "admin/formNewBook.html";
	}

	@PostMapping("/admin/updateBook/{id}")
	public String updateBook(@PathVariable Long id,
							 @Valid @ModelAttribute("book") Book book,
							 BindingResult bindingResult,
							 @RequestParam(value = "authors[]", required = false) List<Long> authorIds,
							 RedirectAttributes redirectAttributes, Model model){

		this.bookValidator.validate(book, bindingResult);

		if (bindingResult.hasErrors()) {
			model.addAttribute("authors", this.authorService.findAll());
			//Returns to the form with the errors
			return "admin/formNewBook.html";
		}

		try {
			bookService.updateBook(id, book, authorIds);
			redirectAttributes.addFlashAttribute("success", "Libro aggiornato con successo!");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/book/all";
	}

	@GetMapping("/book/details/{id}")
	public String book(@PathVariable Long id, Model model) {
		Book book = this.bookService.findById(id).orElse(null);
		if (book == null) {
			model.addAttribute("error", "Book not found");
			return "error";
		}

		//Logged user credentials check
		Credentials loggedCredentials = credentialsService.getLoggedCredentials();
		if (loggedCredentials == null) {
			model.addAttribute("isLoggedIn", false);
			model.addAttribute("book", book);
			model.addAttribute("hasReviewed", false);
			model.addAttribute("userReview", null);
			model.addAttribute("reviews", book.getReviews());
			model.addAttribute("role", "NOROLE");
			return "book";
		}
		User user = loggedCredentials.getUser();
		model.addAttribute("isLoggedIn", true);

		boolean hasReviewed = false;
		if(!user.getReviews().isEmpty()){
			hasReviewed = reviewService.hasUserReviewedBook(user, book);
		}

		//If the user has already reviewed a book, it isolates it from the list of reviews
		List<Review> reviews = book.getReviews();
		if(hasReviewed){
			Review userReview = reviewService.findReviewByUserAndBook(user, book);
			model.addAttribute("userReview", userReview);
			reviews.remove(userReview);
			model.addAttribute("reviews", reviews);
		}else{
			model.addAttribute("userReview", null);
			model.addAttribute("reviews", reviews);
		}

		model.addAttribute("book", book);
		model.addAttribute("hasReviewed", hasReviewed);

		return "book";
	}
	
	@GetMapping("/book/all")
	public String getBooks(Model model) {		
		Credentials loggedCredentials = credentialsService.getLoggedCredentials();
		if(loggedCredentials == null){
			model.addAttribute("isLoggedIn", false);
			model.addAttribute("role", "NOROLE");
		}else{
			model.addAttribute("isLoggedIn", true);
			model.addAttribute("role", loggedCredentials.getRole());
		}
		model.addAttribute("books", this.bookService.findAll());
		return "books.html";
	}

	@PostMapping("/book/delete/{id}")
	public String deleteBook(@PathVariable Long id, Model model) {
		Book book = this.bookService.findById(id).orElse(null);
		if(book == null) {
			model.addAttribute("error", "Book not found");
			return "error";
		}
		this.bookService.deleteBook(book);
		return "redirect:/book/all";
	}
}
