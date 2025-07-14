package it.uniroma3.siw.repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.model.Book;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {

	//-------PROVA PER VEDERE SE RISOLVE SAVE NEW BOOK--------
	@Override
	@EntityGraph(attributePaths = {"description", "authors"}) //Usa l'Entity graph per caricare la descrizione
	List<Book> findAll();
	//-------------------------------------------------------
	
	 List<Book> findByYear(int year);

	 List<Book> findByTitleContainingIgnoreCase(String keyword);

	 boolean existsByTitleAndYear(String title, int year);

	//Method to find books ordered based on their average rating
	@Query("SELECT r.book FROM Review r GROUP BY r.book ORDER BY AVG(r.rating) DESC")
	List<Book> findTopRatedBooks(Pageable pageable);

	//Method to find most recent books, ordered by release year
	List<Book> findByOrderByYearDesc(Pageable pageable);

	Optional<Book> findByTitleAndYear(String title, Integer year);


}
