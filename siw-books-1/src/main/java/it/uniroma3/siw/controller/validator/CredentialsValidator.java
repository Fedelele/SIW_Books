package it.uniroma3.siw.controller.validator;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CredentialsValidator implements Validator {

    @Autowired
    private CredentialsService credentialsService;

	@Override
	public boolean supports(Class<?> aClass) {
		return Credentials.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
        Credentials credentials = (Credentials) o;
        String username = credentials.getUsername();

        //Check if username is null, empty or it already exists
        if(username == null || credentials.getUsername().trim().isEmpty()) {
            errors.rejectValue("username", "required", "Username obbligatorio");
        } else if(credentialsService.existsByUsername(username)) {
            errors.rejectValue("username", "credentials.duplicate", "Username già esistente");
        }

        //Check if password is null
        if(credentials.getPassword() == null || credentials.getPassword().trim().isEmpty()) {
            errors.rejectValue("password", "required", "Il campo è obbligatorio");
        }
	}

}
