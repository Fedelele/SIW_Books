package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.repository.BookRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {
	
	@Autowired
	private BookRepository bookRepository;

    @Autowired
    private ReviewService reviewService;

	@Autowired
	private AuthorService authorService;


	@Transactional
	public Optional<Book> findById(Long id) {
		return this.bookRepository.findById(id);
	}

	@Transactional
	public Iterable<Book> findAll(){
		return bookRepository.findAll();
	}

	@Transactional
	public void save(Book book) {
		bookRepository.save(book);
	}
	
	public Iterable<Book> findByYear(int year){
		return bookRepository.findByYear(year);
	}
	
	public boolean existsByTitleAndYear(String title, Integer year) {
		return bookRepository.existsByTitleAndYear(title, year);
	}

	@Transactional
	public List<Book> searchBookByTitle(String keyword){
        List<Book> allBooks = new ArrayList<>(this.bookRepository.findAll());

		if(keyword == null || keyword.isEmpty()){
			return allBooks;
		}

		String lowerKeyword = keyword.toLowerCase();
		return allBooks.stream()
				.filter(book -> book.getTitle().toLowerCase().contains(lowerKeyword))
				.collect(Collectors.toList());
	}

	@Transactional
	public void deleteBook(Book book) {
		//For coherence, I should also delete the book from every author's "bookWritten" set
		for (Author author : book.getAuthors()) {
			author.getAuthorsOf().remove(book);
		}
		reviewService.deleteAllReviewsByBook(book);
		this.bookRepository.delete(book);
	}

	//Method to update the details of a book
	@Transactional
	public Book updateBook(Long bookId, Book updatedData, List<Long> authorIds) {
		Book bookToUpdate = this.bookRepository.findById(bookId)
				.orElseThrow(() -> new IllegalArgumentException("Libro non trovato con id: " + bookId));

		bookToUpdate.setTitle(updatedData.getTitle());
		bookToUpdate.setYear(updatedData.getYear());
		bookToUpdate.setDescription(updatedData.getDescription());
		// Authors management
		bookToUpdate.getAuthors().clear(); // Removes all existing authors
		if (authorIds != null && !authorIds.isEmpty()) {
			List<Author> newAuthors = authorService.findByIds(authorIds);
			for (Author author : newAuthors) {
				bookToUpdate.getAuthors().add(author);
			}
		}
		return this.bookRepository.save(bookToUpdate);
	}
}
