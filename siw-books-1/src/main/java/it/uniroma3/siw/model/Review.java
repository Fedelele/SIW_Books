package it.uniroma3.siw.model;

import java.util.Objects;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"}))
public class Review {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String title;
	private int rating;

	private String text;

	//To assure that every review is linked to a user and a book
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "book_id")
	private Book book;

	public Long getId() {
	        return id;
	    }

		public void setId(Long id) {
	        this.id = id;
	    }

	    public String getTitle() {
	        return title;
	    }

	    public void setTitle(String title) {
	        this.title = title;
	    }

	    public int getRating() {
	        return rating;
	    }

	    public void setRating(int rating) {
	        this.rating = rating;
	    }

	    public String getText() {
	        return text;
	    }

	    public void setText(String text) {
	        this.text = text;
	    }

	    public User getUser() {
	        return user;
	    }

	    public void setUser(User user) {
	        this.user = user;
	    }

	    public Book getBook() {
	        return book;
	    }

	    public void setBook(Book book) {
	        this.book = book;
	    }
	    
	    @Override
	    public int hashCode() {
	    	return Objects.hash(user, book);
	    }
	    
	    @Override
	    public boolean equals(Object obj) {
	    	if(this==obj)
	    		return true;
	    	if(obj==null)
	    		return false;
	    	if(this.getClass()!=obj.getClass())
	    		return false;
	    	Review o = (Review) obj;
	    	return Objects.equals(user, o.user) && Objects.equals(book, o.book);
	    }
}
