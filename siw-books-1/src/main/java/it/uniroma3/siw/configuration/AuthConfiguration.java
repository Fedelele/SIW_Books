package it.uniroma3.siw.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class AuthConfiguration {
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)throws Exception{
		auth.jdbcAuthentication()
			.dataSource(dataSource)
			.authoritiesByUsernameQuery("SELECT username, user_role FROM credentials WHERE username=?")
			.usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
		return authenticationConfiguration.getAuthenticationManager();
	}



	@Bean
	protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(authorize -> authorize
						// Public permits: anyone can access
						.requestMatchers(HttpMethod.GET, "/", "/index", "/login", "/register", "/css/**", "/images/**", "/favicon.ico").permitAll()
						.requestMatchers(HttpMethod.GET, "/books/**", "/authors/**", "/book/all", "/author/all", "/book/details/**", "/author/details/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/register", "/login").permitAll()

						//Accessible only to users with user role
						.requestMatchers("/user/**").hasAuthority("USER")

						// Accessible only to admins
						.requestMatchers("/admin/**").hasAuthority("ADMIN")

						//Any other request requires authentication
						.anyRequest().authenticated()
				)
				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						.permitAll()
						//If successful redirects to the root; The controller decides where to redirect (admin or public)
						.defaultSuccessUrl("/", true)
						.failureUrl("/login?error=true")
				)
				.logout(logout -> logout
						// The url to send the POST request to for the logout
						.logoutUrl("/logout")
						// Url to redirect after logout
						.logoutSuccessUrl("/")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.clearAuthentication(true)
						.permitAll()
				);
		return http.build();
	}
//	@Bean
//	protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//	    http.csrf().disable().cors().disable().authorizeHttpRequests()
//
//	        // Accesso libero a home, login, registrazione, statici
//	        .requestMatchers(HttpMethod.GET, "/", "/index", "/login", "/register", "/css/**", "/images/**", "/favicon.ico").permitAll()
//	        .requestMatchers(HttpMethod.POST, "/register", "/login").permitAll()
//
//	        // Accesso pubblico alle risorse visibili anche agli utenti anonimi (es. libri, autori, recensioni)
//	        .requestMatchers(HttpMethod.GET, "/books/**", "/authors/**", "/rest/books/**").permitAll()
//
//	        // Accesso REST admin-only
//	        .requestMatchers("/admin/**", "/admin/rest/**").hasAuthority("ROLE_ADMIN")
//
//	        // Accesso utente registrato per recensioni e profilo
//	        .requestMatchers("/user/**").hasAuthority("ROLE_USER")
//
//	        // Tutte le altre richieste richiedono autenticazione
//	        .anyRequest().authenticated()
//
//	        // Form login
//	        .and().formLogin()
//	            .loginPage("/login")
//	            .permitAll()
//	            .defaultSuccessUrl("/success", true)
//	            .failureUrl("/login?error=true")
//
//	        // Logout
//	        .and().logout()
//	            .logoutUrl("/logout")
//	            .logoutSuccessUrl("/")
//	            .invalidateHttpSession(true)
//	            .deleteCookies("JSESSIONID")
//	            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//	            .clearAuthentication(true)
//	            .permitAll();
//
//	    return http.build();
//	}


}
