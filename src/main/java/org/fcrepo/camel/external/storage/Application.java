package org.fcrepo.camel.external.storage;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

@SpringBootApplication
@ImportResource("classpath:external-storage-proxy.xml")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Component
	    class RestApi extends RouteBuilder {

	        @Override
	        public void configure() {
	            restConfiguration()
	                .contextPath("/").apiContextPath("/api-doc")
	                    .apiProperty("api.title", "Camel REST API")
	                    .apiProperty("api.version", "1.0")
	                    .apiProperty("cors", "true")
	                    .apiContextRouteId("doc-api")
	                .bindingMode(RestBindingMode.json);

	   /* TODO Translate this REST XML DSL into Java DSL        
	    * 
	    *     <restConfiguration component="jetty" bindingMode="json" port="9091"/>
      <componentProperty key="foo" value="123"/>
      <dataFormatProperty key="prettyPrint" value="true"/>
    </restConfiguration>

    <rest path="/">
      <get uri="/{service}/status/{external_uri}">
        <to uri="direct:status_header"/>
      </get>
      
      <post uri="/{service}/stage/{external_uri}">
        <to uri="direct:stage_header"/>
      </post>
    </rest>

	    * 
	    * rest("/books").description("Books REST service")
	                .get("/").description("The list of all the books")
	                    .route().routeId("books-api")
	                    .bean(Database.class, "findBooks")
	                    .endRest()
	                .get("order/{id}").description("Details of an order by id")
	                    .route().routeId("order-api")
	                    .bean(Database.class, "findOrder(${header.id})");
*/	        }
	}
}
