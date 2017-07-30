package com.voidaspect.public24;

import ai.api.GsonFactory;
import lombok.val;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

@SpringBootApplication
public class Public24Application {

	public static void main(String[] args) {
		SpringApplication.run(Public24Application.class, args);
	}

	@Bean
	public GsonHttpMessageConverter gsonHttpMessageConverter() {
	    val converter = new GsonHttpMessageConverter();
	    converter.setGson(GsonFactory.getDefaultFactory().getGson());
	    return converter;
    }

}
