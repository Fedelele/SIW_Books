package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
	
	public List<Book> findByYear(int year);

	public List<Book> findByTitleContainingIgnoreCase(String title);
	
	public boolean existsByTitleAndYear(String title, int year);
}
