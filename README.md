
## External Storage Proxy using Camel

A web service using Camel routes for asynchronous retrieval of large objects from storage services.  Provides a common API and responses for an asynchronous-aware client to prevent long blocking requests and instead provide acceptable user interactions.   


### Prerequisites
```
Java 1.8+
Maven 3.0+
```

### Installing

Configure `src/main/resources/application.properties` appropriately.

Run via Maven:

```
mvn package
mvn spring-boot:run
```

## Test

Accessing the built-in API docs endpoint at [http://localhost:8080/api-doc](http://localhost:8080/api-doc) should look something like:  

```
{
  "swagger" : "2.0",
  "info" : {
    "version" : "1.0.0",
    "title" : "User API"
  },
  "host" : "0.0.0.0",
  "schemes" : [ "http" ],
  "paths" : {
    "//{service}/stage/{external_uri}" : {
      "post" : {
        "operationId" : "route2",
        "parameters" : [ {
          "name" : "service",
          "in" : "path",
          "description" : "",
          "required" : true,
          "type" : "string"
        }, {
          "name" : "external_uri",
          "in" : "path",
          "description" : "",
          "required" : true,
          "type" : "string"
        } ],
        "x-camelContextId" : "ExternalStorageProxy",
        "x-routeId" : "route2"
      }
    },
    "//{service}/status/{external_uri}" : {
      "get" : {
        "operationId" : "route1",
        "parameters" : [ {
          "name" : "service",
          "in" : "path",
          "description" : "",
          "required" : true,
          "type" : "string"
        }, {
          "name" : "external_uri",
          "in" : "path",
          "description" : "",
          "required" : true,
          "type" : "string"
        } ],
        "x-camelContextId" : "ExternalStorageProxy",
        "x-routeId" : "route1"
      }
    }
  }
}
```
