# Public 24 
Web service for privat24 public API integration.

Provides a webhook for API.AI agent.

Server and web demo: https://public24.herokuapp.com/ 

Staging server:  https://public24-staging.herokuapp.com/

Current features in this version (0.2):

- "Current Exchange Rate" - returns current exchange rate. Is available for cash and non-cash transactions (non-cash by default). Request example: __Current cash exchange for USD*__. 

- "Exchange Rate History" - returns exchange rate history for inputted data. Request example: __CAD* for 03.06.2016__.

- "Infrastructure Location" - returns locations of PrivatBank infrastructure with Google Maps links. Request example: __7 ATMs at Kharkiv, Heroiv Pratsi__. Device type (ATM or self-service terminal) and city name are required. Address and limit are optional.

> *If the currency isn't specified returns all existing currencies.
