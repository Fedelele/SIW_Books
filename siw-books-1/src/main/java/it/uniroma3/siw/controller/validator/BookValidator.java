package it.uniroma3.siw.controller.validator;

import org.springframework.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.service.BookService;

@Component
public class BookValidator implements Validator {
	
	@Autowired
	private BookService bookService;
	
	@Override
	public void validate(Object o, Errors errors) {
		Book book = (Book) o;
		
		if(book.getTitle() == null || book.getTitle().trim().isEmpty()) {
			errors.rejectValue("title", "required");
		}
		
		if(book.getYear() == null) {
			errors.rejectValue("year", "required");
		}
		
		//duplicates check
		if(book.getTitle() != null && book.getYear() != null &&
				bookService.existsByTitleAndYear(book.getTitle(), book.getYear())) {
			errors.reject("book.duplicate");
		}
		
	}
	
	@Override
	public boolean supports(Class<?> aClass) {
		return Book.class.equals(aClass);
	}

}
