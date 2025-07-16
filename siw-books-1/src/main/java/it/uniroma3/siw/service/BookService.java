package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	@Lazy
	private AuthorService authorService;


	@Transactional
	public Optional<Book> findById(Long id) {
		Optional<Book> optionalBook = this.bookRepository.findById(id);
        optionalBook.ifPresent(book -> book.getAuthors().size());
		return optionalBook;
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
		if(keyword == null || keyword.isEmpty()){
			return this.bookRepository.findAll();
		}
		return this.bookRepository.findByTitleContainingIgnoreCase(keyword);
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

		//ADD THIS LET ME SEE
		Optional<Book> duplicate = bookRepository.findByTitleAndYear(updatedData.getTitle(), updatedData.getYear());
		if(duplicate.isPresent() && !duplicate.get().getId().equals(bookId)){
			throw new IllegalArgumentException("A book with this title and year already exists");
		}
		bookToUpdate.setTitle(updatedData.getTitle());
		bookToUpdate.setYear(updatedData.getYear());
		bookToUpdate.setDescription(updatedData.getDescription());
		// Authors management
		for(Author author : new ArrayList<>(bookToUpdate.getAuthors())) {
			author.getAuthorsOf().remove(bookToUpdate);
		}
		bookToUpdate.getAuthors().clear();

		if (authorIds != null && !authorIds.isEmpty()) {
			List<Author> newAuthors = authorService.findByIds(authorIds);
			for (Author author : newAuthors) {
				bookToUpdate.getAuthors().add(author);
			}
		}
		return this.bookRepository.save(bookToUpdate);
	}

	//Method to find books ordered based on their average rating from highest to lowest
	@Transactional
	public List<Book> findTopRatedBooks(int limit) {
		Pageable pageable = PageRequest.of(0, limit);
		return this.bookRepository.findTopRatedBooks(pageable);
	}

	//Method to find most recent books, ordered by release year
	@Transactional
	public List<Book> findMostRecentBooks(int limit) {
		Pageable pageable = PageRequest.of(0, limit);
		return this.bookRepository.findByOrderByYearDesc(pageable);
	}
}
