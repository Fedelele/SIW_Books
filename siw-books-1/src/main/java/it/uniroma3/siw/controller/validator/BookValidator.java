package it.uniroma3.siw.controller.validator;

import it.uniroma3.siw.repository.BookRepository;
import org.springframework.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import it.uniroma3.siw.model.Book;

import java.util.Optional;

@Component
public class BookValidator implements Validator {

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

	}
	
	@Override
	public boolean supports(Class<?> aClass) {
		return Book.class.equals(aClass);
	}

}
