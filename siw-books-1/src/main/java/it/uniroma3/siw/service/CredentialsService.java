package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;

@Service
public class CredentialsService {
	
	@Autowired
	protected CredentialsRepository credentialsRepository;
	
	@Autowired
	protected PasswordEncoder passwordEncoder;

	public Credentials findByUsername(String username) {
		return credentialsRepository.findByUsername(username).orElse(null);
	}

//	public Credentials findById(Long id) {
//		return credentialsRepository.findById(id).orElse(null);
//	}
	
	@Transactional 
	public Credentials saveCredentials(Credentials credentials) {
		credentials.setRole(Credentials.USER_ROLE);
		credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
		return this.credentialsRepository.save(credentials);
	}

	public Credentials getLoggedCredentials() {
		try{
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String currentUsername = authentication.getName();
			return findByUsername(currentUsername);
		} catch (Exception e){
			return null;
		}
	}

	//Method for the credentials validator to check if there are any duplicates
	public boolean existsByUsername(String username) {
		return credentialsRepository.existsByUsername(username);
	}
}
