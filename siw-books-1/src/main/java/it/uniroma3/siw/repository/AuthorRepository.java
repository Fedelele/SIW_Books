package it.uniroma3.siw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.model.Author;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {

	public Iterable<Author> findAuthorsNotInBook(@Param("bookId") Long id);

	boolean existsByNameAndSurname(String name, String surname);

	List<Author> findByLastNameContainingIgnoreCase(String lastName);
	List<Author> findByNationality(String nationality);
	List<Author> findByFirstNameAndLastName(String firstName, String lastName);
}
