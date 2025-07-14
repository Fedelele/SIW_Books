package it.uniroma3.siw.model;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//This is being implemented for REST controllers
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
// --- AGGIUNGI QUESTO BLOCCO PER VEDERE SE FUNZIONA ADD BOOK ---
@NamedEntityGraph(
		name = "Book.withDescription",
		attributeNodes = {
			@NamedAttributeNode("description")
		}
)
//-------------------------------------------------------------
public class Book {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotBlank(message = "Il titolo è obbligatorio")
	private String title;
	@NotNull(message = "L'anno è obbligatorio")
	private Integer year;

	//Variables for the description and images of the book
//	@Lob
	@Column(columnDefinition = "Text")
	private String description;
//	@Lob
	private String imageUrl;

	private double averageRating;

	@ManyToMany
	@JoinTable(
			name = "book_author",
			joinColumns = @JoinColumn(name = "book_id"),
			inverseJoinColumns = @JoinColumn(name = "author_id")
	)
	//Books have more than one author <-> every author wrote more books
	//NB. It can be only one
	private List<Author> authors = new ArrayList<>();
	
	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Review> reviews = new ArrayList<>();
	
	
	//Setters & Getters
	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Integer getYear() {
		return this.year;
	}
	
	public void setYear(Integer year) {
		this.year = year;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return this.imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public double getAverageRating(){
		return this.averageRating;
	}

	public void setAverageRating(double averageRating){
		this.averageRating = averageRating;
	}

	public List<Author> getAuthors(){
		return this.authors;
	}
	
	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}
	
	public List<Review> getReviews(){
		return reviews;
	}
	
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(title, year);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this==obj)
			return true;
		if(obj==null)
			return false;
		if(this.getClass()!=obj.getClass())
			return false;
		Book o = (Book) obj;
		return Objects.equals(title, o.title) && year.equals(o.year) && Objects.equals(id, o.id);
	}
}
