Target Technical Assessment - MyRetail RESTful Service
======================================================
The goal for this exercise is to create an end-to-end Proof-of-Concept for a products API which will aggregate product data from multiple sources and return it as JSON to the caller.  


Technologies Used
=================
* Java 1.8  
* Groovy 2.4.7
* Spring Boot 1.3.5  
* Gradle 2.14  
* Spock 1.0-groovy-2.4  
* MongoDB 3.3  
* Docker 1.12.0-rc2-beta17  
* Docker-Compose 1.8.0-rc1  
* Git 2.9.0  
* IntelliJ  


Setup
=====
Start docker  

In Terminal Window 1  
`$ cd <workspace>`  
`$ git clone https://github.com/jrtitko/Target.git`  
`$ cd <workspace>/Target/myRetail`  
Optionally build locally:  
* `$ gradle clean build -x test` - tests wont work until Mongo docker is built  
* `$ gradle eclipse` -- or -- `$ gradle idea` - to build IDE specific files  
* `$ docker build -t jrtitko/myretail .`  

`$ docker-compose up`  
Note: This terminal window will display the logs. To run in background, use: docker-compose up -d  
Note: Control-C should shut down the instances or use docker stop if running in background  

In Terminal Window 2  
`$ docker ps` 	=> To confirm docker containers started  
`$ docker exec -it myretail_mongodb_1 bash`  => myretail_mongodb_1 is named automatically so could be different  
`# mongo`  
`> use myRetail`  
`> db.price.find()`  => should be 5 records for demonstration purposes


JUnit Tests
===========
Due to the nature of this project (small proof of concept), some of the JUnit tests are actually integration tests which requires a MongoDB instance to be available.

In A Terminal Window  
If the Setup (above) has been done, use:  
  `$ docker start myretail_mongodb_1`  => myretail_mongodb_1 is named automatically so could be different  
Otherwise, use:  
  `$ docker run -d -p 27017:27017 mongo --storageEngine wiredTiger`  
  Note: Use docker stop to shut down the instance after the test  

`$ cd <workspace>/Target/myRetail`  
`$ gradle clean check`  
 

Demonstration
=============
Use the Advanced REST Client (ARC) in google to hit the RESTful service.  

Make sure the docker images are running by using:  
`$ docker-compose up`  

Demonstrate GET request
-----------------------
URL: http://localhost:8080/myRetail/products/13860428  
Select request type: GET  
Click button: SEND  
JSON Response returned  
`{  
	"id": 13860428  
	"name": "The Big Lebowski [Blu-ray]"  
	"current_price": {  
		"id": 13860428  
		"value": 13.49  
		"currency_code": "USD"  
	}  
}`  

Demonstrate POST request
------------------------
URL: http://localhost:8080/myRetail/products/13860428  
Select request type: POST  
Select application type: application/json  
Set raw payload to:  
`{  
	"id": 13860428,  
	"value": "29.99",  
	"currency_code": "USD"  
}`  
Click button: SEND  
No JSON response returned  

Confirm update by repeating GET request above and checking price  

Demonstrate GET Request on unknown product
------------------------------------------
URL: http://localhost:8080/myRetail/products/99999999  
Select request type: GET  
Click button: SEND  
JSON Response returned  
`{  
	"id": 99999999  
}`  

Demonstrate POST request on different product
---------------------------------------------
URL: http://localhost:8080/myRetail/products/99999999  
Select request type: POST  
Select application type: application/json  
Set raw payload to:  
`{  
	"id": 13860428,  
	"value": "39.99",  
	"currency_code": "USD"  
}`  
Click button: SEND  
Status: 500 Internal Server Error  
JSON response returned  
`{
	"timestamp": 1467555025459  
	"status": 500  
	"error": "Internal Server Error"  
	"exception": "java.lang.RuntimeException"  
	"message": "A price update request for id 99999999 attempted to update the price for id 13860428"  
	"path": "/myRetail/products/99999999"  
}`  

Confirm no price saved on 99999999 by repeating GET request for item 99999999 and checking price  
Confirm no update on 13860428 by repeating GET request for item 13860428 and checking price (29.99)  

Demonstrate GET Request on product with no pricing
--------------------------------------------------
URL: http://localhost:8080/myRetail/products/16752456  
Select request type: GET  
Click button: SEND  
JSON Response returned  
`{  
	"id": 16752456  
	"name": "Lego&reg; Super Heroes The Tumbler 76023"  
}`  

List of known product ids
-------------------------
13860428, 15117729, 16483589, 16696652, 16752456 (no pricing info), 15643793  


Productionize Application
=========================
* Front end security (depending on who end users will be and who can change prices)  
* Add mechanism to detect being spammed (time between requests, x requests per 1 minute per IP, etc)  
* Secure the Mongo database.  i.e. Vault  
* API versioning using URL names: http://localhost:8080/myRetail/v1/products/13860428  
* Based on the volume and frequency of changing data, possibly add an expiring caching mechanism for data from the external product api.  
  * Is there a cost per transaction to the external system?  
* Addressing the situation if multiple items can come back on a single product Id  
* Performance of request/response  
