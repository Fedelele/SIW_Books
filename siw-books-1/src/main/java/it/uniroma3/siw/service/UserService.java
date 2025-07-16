package it.uniroma3.siw.service;

import java.util.List;
import it.uniroma3.siw.model.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	CredentialsService credentialsService;

	/**
	 * The method saves a User in the Database
	 * @param user the User to save
	 * @throws DataIntegrityViolationException if a User with the same username as the passed one already exists in the DB
	 */
	@Transactional
	public void save(User user) {
		this.userRepository.save(user);
	}

	//This method retrieves the User based on the credentials
	public User getLoggedUser() {
		Credentials credentials = credentialsService.getLoggedCredentials();
		return credentials.getUser();
	}

	//This method retrieves all Users from the DB
	@Transactional
	public List<User> getAllUser(){
		return this.userRepository.findAll();
	}
	
}
