package it.uniroma3.siw.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
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
	private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

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
						.requestMatchers(HttpMethod.GET, "/", "/index", "/home", "/login", "/register", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/author-photo/**", "/books-covers/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/book/**", "/author/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/register", "/login").permitAll()

						//Accessible to admin only
						.requestMatchers("/admin/**").hasAuthority("ADMIN")
						//Accessible only to users with user role
						.requestMatchers("/user/**").hasAnyAuthority("USER", "ADMIN")

						//Accessible only to authenticated users
						.requestMatchers("/profile/**").authenticated()
						//Any other request requires authentication
						.anyRequest().authenticated()
				)
				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						.permitAll()
						.successHandler(customAuthenticationSuccessHandler)
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
}
