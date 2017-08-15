package com.voidaspect.public24;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Backend service for API.AI chat bot which integrates with PrivatBank public API
 * <br>Provides webhook which fulfills bot's responses.
 * <br>List of user intents supported in current version:
 * <ul>
 * <li>"Current Exchange Rate" - returns current exchange rate. Is available for cash and non-cash transactions (non-cash by default). Request example: Current cash exchange for USD*.
 * <li>"Exchange Rate History" - returns exchange rate history for inputted data. Request example: CAD* for 23/5/2001.
 * </ul>
 * <p>* If the currency isn't specified returns all existing currencies.
 */
@SpringBootApplication
@EnableConfigurationProperties
public class Public24Application {

	public static void main(String[] args) {
		SpringApplication.run(Public24Application.class, args);
	}

}
