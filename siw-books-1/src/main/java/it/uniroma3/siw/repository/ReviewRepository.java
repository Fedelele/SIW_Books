package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {


	//finds all reviews for a book
	List<Review> findByBook(Book book);
	//finds all reviews written by a certain user
	List<Review> findByUser(User user);
	//finds a review written by User for a specific Book (a user can only write one review per book)
	//Useful to check if a user has already reviewed a book, to avoid doubles
	Optional<Review> findByUserAndBook(@Param("user") User user,@Param("book") Book book);
	//Deletes all reviews of a book -> used for administrator role only
	void deleteByBook(Book book);

}
