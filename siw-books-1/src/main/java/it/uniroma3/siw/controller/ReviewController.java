package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.websocket.WebSocketMessageBrokerSecurityBeanDefinitionParser;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class ReviewController {

	private static final Logger log = getLogger(ReviewController.class);
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private BookService bookService;

	@Autowired
	private CredentialsService credentialsService;

	//CHECK
	@GetMapping("/user/formNewReview/{bookId}")
	public String showReviewForm(@PathVariable Long bookId, Model model, RedirectAttributes redirectAttributes) {
		Book book = this.bookService.findById(bookId).orElse(null);
		if(book == null){
			redirectAttributes.addFlashAttribute("error", "Book not found!");
			return "redirect:/book/all";
		}
		Credentials credentials = this.credentialsService.getLoggedCredentials();
		if (credentials == null) {
			return "redirect:/login";
		}

		User user = credentials.getUser();
		
		if(this.reviewService.hasUserReviewedBook(user, book)) {
			redirectAttributes.addFlashAttribute("error", "You have already reviewed this book.");
			return "redirect:/book/details/" + bookId;
		}
		
		model.addAttribute("book", book);
		model.addAttribute("review", new Review());
		return "user/formNewReview";
	}
	
	@PostMapping("/user/review/new/{bookId}")
	public String saveReview(@PathVariable Long bookId, @Valid @ModelAttribute Review review,
							   BindingResult bindingResult, Model model,
							   RedirectAttributes redirectAttributes) {
		Book book = this.bookService.findById(bookId).orElse(null);
		if(book == null){
			redirectAttributes.addFlashAttribute("error", "Book not found, sorry!");
			return "redirect:/book/all";
		}

		Credentials credentials = this.credentialsService.getLoggedCredentials();
		if (credentials == null) {
			return "redirect:/login";
		}

		User user = this.credentialsService.getLoggedCredentials().getUser();

        if (this.reviewService.hasUserReviewedBook(user, book)) {
			redirectAttributes.addFlashAttribute("error", "You have already reviewed this book.");
       		return "redirect:/book/details/" + bookId;
        }

		//If there are any validation errors -> returns to the form
		if(bindingResult.hasErrors()){
			model.addAttribute("book", book);
			return "user/formNewReview";
		}

        review.setBook(book);
        review.setUser(user);
        this.reviewService.saveReview(review);
		redirectAttributes.addFlashAttribute("success", "Reviews has been added successfully!");

        return "redirect:/book/details/" + bookId;
	}

	@GetMapping("/user/formEditReview/{id}")
	@Transactional
	public String editReviewForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {

		//Checking if the user is logged in
		Credentials credentials = credentialsService.getLoggedCredentials();
		if(credentials == null){
			redirectAttributes.addFlashAttribute("error", "You must be logged in to edit a review");
			return "redirect:/login";
		}
		Review review = this.reviewService.findById(id).orElse(null);
		User user = credentials.getUser();

		if(review == null || !review.getUser().equals(user)) {
			redirectAttributes.addFlashAttribute("error", "You can't edit this review, it's not yours!");
			return "redirect:/";
		}

		model.addAttribute("review", review);
		model.addAttribute("book", review.getBook());

		return "user/formEditReview";
	}

	@PostMapping("/user/review/edit/{id}")
	public String submitReviewEdit(@PathVariable Long id, @Valid @ModelAttribute Review editedReview, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		Review originalReview = this.reviewService.findById(id).orElse(null);
		User user = this.credentialsService.getLoggedCredentials().getUser();

		if(originalReview == null || !originalReview.getUser().equals(user)) {
			redirectAttributes.addFlashAttribute("error", "You can't edit this review! It's not yours");
			return "redirect:/";
		}

		if(bindingResult.hasErrors()) {
			model.addAttribute("book", originalReview.getBook());
			return "user/formEditReview";
		}
		//changes
		originalReview.setTitle(editedReview.getTitle());
		originalReview.setRating(editedReview.getRating());
		originalReview.setText(editedReview.getText());

		this.reviewService.saveReview(originalReview);
		redirectAttributes.addFlashAttribute("success", "Review changed successfully!");

		return "redirect:/book/details/" + originalReview.getBook().getId();
	}

	//Delete review -> ADMIN ONLY
	@PostMapping("/admin/reviews/delete/{id}")
	public String deleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		Review review = reviewService.findById(id).orElse(null);
		if(review == null) {
			redirectAttributes.addFlashAttribute("error", "Review not found");
			return "redirect:/home";
		}
		try{
			reviewService.deleteReview(id);
			redirectAttributes.addFlashAttribute("success", "Review deleted successfully!");
		} catch (Exception e) {
			log.error("Error during the elimination of the review with id: {}", id, e);
			redirectAttributes.addFlashAttribute("error", "It was impossible to execute the action, please try again");
		}
		return "redirect:/book/details/" + review.getBook().getId();
	}


	//Delete review -> ADMIN AND USER ONLY
//	@PostMapping("/admin/reviews/delete/{id}")
//	public String deleteReview(@PathVariable Long id, Model model) {
//		Review review = reviewService.findById(id).orElse(null);
//		if(review == null){
//			model.addAttribute("error", "Review not found");
//			return "error";
//		}
//
//		//This is useful is we let a user delete his own review
//		Credentials credentials = credentialsService.getLoggedCredentials();
//		if(credentials == null){
//			model.addAttribute("error", "You must be logged in to delete a review");
//			return "error";
//		}
//
//		User user = credentials.getUser();
//		if(!user.getReviews().contains(review) && !credentials.getRole().equals("ADMIN")){
//			model.addAttribute("error", "You can't delete this review");
//			return "error";
//		} else if(user.getReviews().contains(review)) {
//			//I think this is for users to delete their own reviews
//			user.getReviews().remove(review);
//		} else if (credentials.getRole().equals("ADMIN")) {
//			User temp = review.getUser();
//			temp.getReviews().remove(review);
//		}
//
//		List<Review> reviews = reviewService.findAllReviews();
//		reviews.remove(review);
//		reviewService.delete(review);
//
//		return "redirect:/book/details/" + review.getBook().getId();
//	}
}
