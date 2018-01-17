
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

## Using


### Test endpoints to validate available endpoints
Accessing the built-in API docs endpoint at [http://localhost:8080/api-doc](http://localhost:8080/api-doc) should look something like:  

<details><summary>Click to expand</summary><p>

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

</p></details>

### Running a mock storage service

Included in the source is the [standalone version of the WireMock](http://wiremock.org/docs/running-standalone/) server, which is very useful in stubbing out mocked responses of storage services to alleviate runtime dependencies.  This is obviously necessary for testing, but it is also handy for developing and exercising provider plugins, such as the included reference implementation.

To start the WireMock server in a separate terminal window:

```
cd src/main/resources/wiremock
java -jar ./wiremock-standalone-2.13.0.jar # Will start by default on 8080
```
As seen in the [documentation](http://wiremock.org/docs/running-standalone/), stubbed responses in WireMock can be added via the addition of formatted JSON files describing the incoming request and the desired response. These files must simply exist in the mapping directory as described in the documentation.  There is a [sample response mapping](src/main/resources/wiremock/mappings/tape_service_response.json) included in the source which will provide the following response once WireMock is started:  

```
curl localhost:8101/tapeArchiveService/cache/some_media_file_01_access.mp4
{
 "url":"/download_cache/some_media_file_01_access.mp4","error":0,"message":"file is present"
}
```

Any number of mappings can be added using formatted JSON, and the documentation also describes how to get mappings into the server via [Record and Playback](http://wiremock.org/docs/record-playback/).