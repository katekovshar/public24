# Public 24 
Web service for privat24 public API integration.

Provides a webhook for API.AI agent.

Server and web demo: https://public24.herokuapp.com/ 

Current features in this version:

- "Current Exchange Rate" - returns current exchange rate. Is available for cash and non-cash transactions (non-cash by default). Request example: __Current cash exchange for USD*__. 

- "Exchange Rate History" - returns exchange rate history for inputted data. Request example: __CAD* for 03.06.2016__.

> *If the currency isn't specified returns all existing currencies.
