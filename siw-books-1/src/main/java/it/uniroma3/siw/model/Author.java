package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.*;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.Cascade;
import org.springframework.format.annotation.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class Author {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotBlank(message = "Name is required")
	private String name;
	@NotBlank(message = "Surname is required")
	private String surname;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth;
	//Not always requested
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfDeath;
	@NotBlank(message = "Nationality is required")
	private String nationality;

	//Variables for the description and images of the author
	@Column(columnDefinition = "Text")
	private String bio;

	private String imageUrl;

	@ManyToMany(mappedBy = "authors", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private List<Book> booksWritten;
	
	//Constructor for Author to initialize the HashSet
	public Author() {
		this.booksWritten = new ArrayList<>();
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSurname() {
		return this.surname;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public LocalDate getDateOfBirth() {
		return this.dateOfBirth;
	}
	
	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public LocalDate getDateOfDeath() {
		return this.dateOfDeath;
	}
	
	public void setDateOfDeath(LocalDate dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}

	public String getNationality() {
		return this.nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getBio() {
		return this.bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getImageUrl() {
		return this.imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public List<Book> getAuthorsOf(){
		return booksWritten;
	}
	
	public void setAuthorsOf(List<Book> booksWritten) {
		this.booksWritten = booksWritten;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, name, surname, dateOfBirth, nationality);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this==obj)
			return true;
		if(obj==null)
			return false;
		if(this.getClass()!=obj.getClass())
			return false;
		Author o = (Author) obj;
		return Objects.equals(name, o.name) && Objects.equals(surname, o.surname) &&
				Objects.equals(dateOfBirth, o.dateOfBirth) && Objects.equals(nationality, o.nationality)
				&& Objects.equals(id, o.id);
	}
	
}
