## Kalah game REST API

### Description

REST API implementation of the [Kalah game](https://en.wikipedia.org/wiki/Kalah)
. It is a 6 pit and 6 stone implementation. The API exposes two endpoints, one for creating the game and a second one to
perform the moves. The app is implemented in Java using Spring and MongoDB for storage. 
For an easier set up an embedded mongo instance is used, both for the app server and integration tests.

### Build & run

Server starts on port 8080 and runs the embedded mongo on port 27017. 

**Note:** remember to unblock the ports above before running, localhost mongo instance can be shutdown using this command:
```bash
mongo --eval "db.getSiblingDB('admin').shutdownServer()"
```

**Build the project**
```bash
mvn clean install
```
**Run the server**
```bash
mvn spring-boot:run
```


### Docs

Api documentation is available after server startup:

    http://localhost:8080/swagger-ui/
