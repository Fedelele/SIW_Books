package it.uniroma3.siw.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

	//We will be using the variable uploadDir to build paths
	@Value("${file.upload-dir}")
	private String uploadDir;


	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/books-covers/**")
				.addResourceLocations("file:" + uploadDir + "books-covers/");
		registry.addResourceHandler("/author-photo/**")
				.addResourceLocations("file:" + uploadDir + "author-photo/");
	}
}
