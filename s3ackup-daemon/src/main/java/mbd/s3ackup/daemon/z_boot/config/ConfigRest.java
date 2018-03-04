package mbd.s3ackup.daemon.z_boot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfigRest implements WebMvcConfigurer {

	/**
	 * Allow Cross site requests when running front end with 'npm start' whilst
	 * developing.
	 * 
	 * @return
	 */

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**").allowedOrigins("*");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/*/*").addResourceLocations("classpath:static/");
	}

}
