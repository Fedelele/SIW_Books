package it.uniroma3.siw.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/books-covers/**")
				.addResourceLocations("file:C:/Users/wufed/Desktop/uploads-siw-books/books-covers/");
		registry.addResourceHandler("/author-photo/**")
				.addResourceLocations("file:C:/Users/wufed/Desktop/uploads-siw-books/author-photo/");
	}
}
