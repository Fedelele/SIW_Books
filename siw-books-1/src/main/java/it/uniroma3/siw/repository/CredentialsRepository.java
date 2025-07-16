package it.uniroma3.siw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.model.Credentials;

public interface CredentialsRepository extends JpaRepository<Credentials, Long> {

	public Optional<Credentials> findByUsername(String username);

	boolean existsByUsername(String username);

	//findById should already be implemented on CrudRepository
}
