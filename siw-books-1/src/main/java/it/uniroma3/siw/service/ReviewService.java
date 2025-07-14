package it.uniroma3.siw.service;

import java.util.List;
import java.util.Optional;

import it.uniroma3.siw.repository.BookRepository;
import it.uniroma3.siw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ReviewRepository;

@Service
public class ReviewService {
	
	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private UserRepository userRepository;

	public List<Review> getReviewsByBook(Book book){
		return reviewRepository.findByBook(book);
	}

	public List<Review> getReviewsByUser(User user){
		return reviewRepository.findByUser(user);
	}

	public List<Review> findAllReviews() {
		return this.reviewRepository.findAll();
	}

	@Transactional
	public Review saveReview (Review review) {
		User managedUser = userRepository.findById(review.getUser().getId()).orElseThrow(() -> new IllegalArgumentException("User not found!"));
		Book managedBook = bookRepository.findById(review.getBook().getId()).orElseThrow(() -> new IllegalArgumentException("Book not found!"));
		review.setUser(managedUser);
		review.setBook(managedBook);
		Review savedReview = reviewRepository.save(review);
		updateBookAverageRating(savedReview.getBook());
		return savedReview;
	}

	public Optional<Review> findReviewByUserAndBook(User user, Book book) {
		return reviewRepository.findByUserAndBook(user, book);
	}

	public boolean hasUserReviewedBook(User user, Book book) {
		return findReviewByUserAndBook(user, book).isPresent();
	}

	@Transactional
	public void deleteReview(Long id) {
		//Finds the review, if exists then ->
		this.reviewRepository.findById(id).ifPresent(review -> {
			Book book = review.getBook();
			reviewRepository.deleteById(id);
			updateBookAverageRating(book);
		});
	}

	@Transactional
	public void delete(Review review) {reviewRepository.delete(review);}

	@Transactional
	public void deleteAllReviewsByBook(Book book) {
		reviewRepository.deleteByBook(book);
	}

	public Optional<Review> findById(Long id) { return reviewRepository.findById(id);}

	private void updateBookAverageRating(Book book){
		List<Review> reviews = reviewRepository.findByBook(book);
		if(reviews.isEmpty()){
			book.setAverageRating(0.0);
		} else{
			double average = reviews.stream()
									.mapToInt(Review::getRating)
									.average()
									.orElse(0.0);
			book.setAverageRating(Math.round(average * 10.0) / 10.0);
		}
		bookRepository.save(book);
	}
}
