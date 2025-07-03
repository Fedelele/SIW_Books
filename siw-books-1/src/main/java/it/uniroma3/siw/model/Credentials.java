package it.uniroma3.siw.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Credentials {


	public static final String USER_ROLE = "USER";
	public static final String ADMIN_ROLE = "ADMIN";

	
	/*
	public static final String ROLE_DEFAULT = "ROLE_DEFAULT";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	 */
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true, nullable = false)
	private String username;
	private String password;

	@Column(name = "user_role")
	private String role;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id")
	private User user;
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}

	public boolean isUser(){
		return USER_ROLE.equals(role);
	}

	public boolean isAdmin(){
		return ADMIN_ROLE.equals(role);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, username, password, role, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if(this.getClass() != obj.getClass())
			return false;
		Credentials o = (Credentials)obj;
		return Objects.equals(username, o.username) && Objects.equals(password, o.password) && Objects.equals(role, o.role)
				&& Objects.equals(user, o.user);
	}
}
