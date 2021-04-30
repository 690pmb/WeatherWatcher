[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=pmb:weather-watcher&metric=alert_status)](https://sonarcloud.io/dashboard?id=pmb:weather-watcher)
# WeatherWatcher

### How to run it in local:  

Docker command to run a MariaDB container:  
`docker run -d --name maria -e MARIADB_ROOT_PASSWORD=weather -e MARIADB_DATABASE=weather -p 3306:3306 mariadb/server:10.3`  
To launch the application:  
`mvn spring-boot:run -Dspring-boot.run.profiles=local`  

### Other commands:  
To connect to the database:  
`docker exec -it maria mysql --user root -pweather weather`  
To launch it in production:  
`java -Dserver.port=7878 -jar -Dspring.profiles.active=dev weather-watcher.jar`  
