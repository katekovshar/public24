package com.voidaspect.public24;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Backend service for API.AI chat bot which integrates with PrivatBank public API
 * <br>Provides webhook which fulfills bot's responses.
 * <hr>List of user intents supported in current version:
 * <ul>
 * <li>"Current Exchange Rate" - returns current exchange rate. Is available for cash and non-cash transactions (non-cash by default). Request example: Current cash exchange for USD*.
 * <li>"Exchange Rate History" - returns exchange rate history for inputted data. Request example: CAD* for 23/5/2001.
 * <li>"Infrastructure Location" - returns locations of PrivatBank infrastructure with Google Maps links. Request example: 7 ATMs at Kharkiv, Heroiv Pratsi. Device type (ATM or self-service terminal) and city name are required. Address and limit are optional.</li>
 * </ul>
 * <hr>* If the currency isn't specified returns all existing currencies.
 */
@SpringBootApplication
@EnableConfigurationProperties
public class Public24Application {

	/**
	 * Application entry point.
     *
	 * @param args command line args
     * @see SpringApplication#run(Object, String...)
     * @see SpringBootApplication
	 */
	public static void main(String[] args) {
		SpringApplication.run(Public24Application.class, args);
	}

}
