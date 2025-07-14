package it.uniroma3.siw.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.model.Author;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface AuthorRepository extends JpaRepository<Author, Long> {

	@Query("SELECT a FROM Author a WHERE a NOT IN (SELECT b.authors FROM Book b WHERE b.id = :bookId)")
    Iterable<Author> findAuthorsNotInBook(@Param("bookId") Long id);

//	boolean existsByNameAndSurname(String name, String surname);
//
//	List<Author> findBySurnameContainingIgnoreCase(String lastName);
//	List<Author> findByNationality(String nationality);
//	List<Author> findByNameAndSurname(String firstName, String lastName);

	List<Author> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String nameKeyword, String surnameKeyword);

	//Method to find authors with most books published
	@Query("SELECT a FROM Author a JOIN a.booksWritten b GROUP BY a ORDER BY COUNT(b) DESC")
	List<Author> findAuthorsWithMostBooks(Pageable pageable);

	@Override
	@EntityGraph(attributePaths = {"booksWritten"})
	List<Author> findAll();

	@EntityGraph(attributePaths = {"booksWritten"})
	Optional<Author> findWithBooksById(Long id);

}
