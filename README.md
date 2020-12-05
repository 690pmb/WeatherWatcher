# WeatherWatcher

### How to run it in local:  

Docker command to run a MariaDB container:  
```bash
docker run -d --name maria -e MARIADB_ROOT_PASSWORD=weather -e MARIADB_DATABASE=weather -p 3306:3306 mariadb/server:10.3
```
To launch the application:  
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```
