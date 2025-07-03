package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Book;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.repository.AuthorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthorService {
	
	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private BookService bookService;
	
	public Optional<Author> findById(Long id) {
		return authorRepository.findById(id);
	}
	
	public Iterable<Author> findAll(){
		return this.authorRepository.findAll();
	}
	
	public void save(Author author) {
		authorRepository.save(author);
	}

	//List of authors that are not associated with that book
	public Iterable<Author> findAuthorsNotInBook(Long bookId){
		return authorRepository.findAuthorsNotInBook(bookId);
	}

	//Looks for authors based off the letters contained in their names
	public List<Author> searchAuthorsByName(String keyword){
        List<Author> authors = this.authorRepository.findAll();
        if(keyword == null || keyword.isEmpty()){
			return authors;
		}
		String lowerKeyword = keyword.toLowerCase();
		return authors.stream()
				.filter(author -> author.getName().toLowerCase().contains(lowerKeyword))
				.collect(Collectors.toList());
	}

	public void removeAuthorFromBook(Author author, Book book){
		book.getAuthors().remove(author);
		//To maintain the bidirectionality we have to eliminate the book from the Set
		//associated with the author and the books written
		author.getAuthorsOf().remove(book);
		this.bookService.save(book);
	}

	//Method to delete the author from every book he's been mentioned
	@Transactional
	public void deleteAuthor(Author author){
		List<Book> booksToDelete = new ArrayList<>(author.getAuthorsOf());
		for(Book b : booksToDelete){
			removeAuthorFromBook(author, b);
		}
		this.authorRepository.delete(author);
	}

	//Method to find all authors selected by the admin to add to a new book
	public List<Author> findByIds(List<Long> ids){
		return this.authorRepository.findAllById(ids);
	}

	//Method to update the details of an author
	@Transactional
	public Author updateAuthor(Long authorId, Author updatedData) {
		Author authorToUpdate = this.authorRepository.findById(authorId)
				.orElseThrow(() -> new IllegalArgumentException("Autore non trovato con id " + authorId));
		authorToUpdate.setName(updatedData.getName());
		authorToUpdate.setSurname(updatedData.getSurname());
		authorToUpdate.setDateOfBirth(updatedData.getDateOfBirth());
		authorToUpdate.setDateOfDeath(updatedData.getDateOfDeath());
		authorToUpdate.setNationality(updatedData.getNationality());
		authorToUpdate.setBio(updatedData.getBio());
		return this.authorRepository.save(authorToUpdate);
	}
}
