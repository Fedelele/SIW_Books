package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
//Name changed because "user" is a keyword in postgres
@Table(name = "users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotBlank(message = "Il nome è obbligatorio")
	private String name;
	@NotBlank(message = "Il cognome è obbligatorio")
	private String surname;
	@NotBlank(message = "L'indirizzo mail è obbligatorio")
	private String email;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private Set<Review> reviews = new HashSet<>();
	
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Set<Review> getReviews(){
		return this.reviews;
	}
	
	public void setReviews(Set<Review> reviews) {
		this.reviews = reviews;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime*result+((name == null) ? 0 : name.hashCode());
		result = prime*result+((surname == null) ? 0 : surname.hashCode());
		result = prime*result+((email == null) ? 0 : email.hashCode());	
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this==obj)
			return true;
		if(obj==null)
			return false;
		if(this.getClass()!=obj.getClass())
			return false;
		User o = (User) obj;
		if(name==null) {
			if(o.name!=null)
				return false;
		}else if(!name.equals(o.name))
			return false;
		if(surname==null) {
			if(o.surname!=null)
				return false;
		}else if(!surname.equals(o.surname))
			return false;
		if(email==null) {
			if(o.email!=null)
				return false;
		}else if(!email.equals(o.email))
			return false;
		return true;
	}

}
