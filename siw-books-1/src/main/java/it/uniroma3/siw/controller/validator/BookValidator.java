package it.uniroma3.siw.controller.validator;

import it.uniroma3.siw.repository.BookRepository;
import org.springframework.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.service.BookService;

import java.util.Optional;

@Component
public class BookValidator implements Validator {
	
	@Autowired
	private BookService bookService;
    @Autowired
    private BookRepository bookRepository;

	@Override
	public void validate(Object o, Errors errors) {
		Book book = (Book) o;
		String title = book.getTitle();
		Integer year = book.getYear();

		//Checks if there is another book with the same title and year
		if(title != null && !title.trim().isEmpty() && year != null){
			Optional<Book> existingBookOpt = bookRepository.findByTitleAndYear(title, year);

			if(existingBookOpt.isPresent() && (book.getId() == null || !existingBookOpt.get().getId().equals(book.getId()))){
				errors.reject("duplicateBook", "It already exists a book with this title and year");
			}
		}

//		if(book.getTitle() == null || book.getTitle().trim().isEmpty()) {
//			errors.rejectValue("title", "required");
//		}
//
//		if(book.getYear() == null) {
//			errors.rejectValue("year", "required");
//		}
//
//		//duplicates check
//		if(title != null && !title.isEmpty() && year != null){
//			Book existingBook = bookRepository.findByTitleAndYear(title, year).orElse(null);
//			if(existingBook != null && !existingBook.getId().equals(book.getId())){
//				errors.rejectValue("book.duplicate", "It already exists a book with this title and year");
//			}
//		}
//		if(book.getTitle() != null && book.getYear() != null &&
//				bookService.existsByTitleAndYear(book.getTitle(), book.getYear())) {
//			errors.reject("book.duplicate");
//		}
		
	}
	
	@Override
	public boolean supports(Class<?> aClass) {
		return Book.class.equals(aClass);
	}

}
