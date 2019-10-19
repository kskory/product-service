# Demo Project

### To run
* clone the repo
* `cd products-service`
* `mvn clean package`
* `docker-compose up -d` (it will start a my sql db) 
* `java -jar target/products-service-0.0.1-SNAPSHOT.jar`
* open http://localhost:8080/swagger-ui.html in a web browser