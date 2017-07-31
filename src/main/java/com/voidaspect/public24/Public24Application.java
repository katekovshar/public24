package com.voidaspect.public24;

import com.google.gson.Gson;
import com.voidaspect.public24.config.gson.GsonFactory;
import com.voidaspect.public24.config.gson.LocalDateAdapter;
import lombok.val;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class Public24Application {

	public static void main(String[] args) {
		SpringApplication.run(Public24Application.class, args);
	}

	@Bean
	public GsonHttpMessageConverter gsonHttpMessageConverter() {
	    val converter = new GsonHttpMessageConverter();
	    converter.setGson(gson());
	    return converter;
    }

    @Bean
    public Gson gson() {
	    return GsonFactory.getDefaultFactory()
                .getGsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter(privat24DateFormat()))
                .create();
    }

    @Bean
    public DateTimeFormatter privat24DateFormat() {
	    return DateTimeFormatter.ofPattern("dd.MM.yyyy");
    }

}
