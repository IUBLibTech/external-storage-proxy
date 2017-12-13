package org.fcrepo.camel.external.storage;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import io.hawt.config.ConfigFacade;
import io.hawt.springboot.HawtPlugin;
import io.hawt.web.AuthenticationFilter;

@SpringBootApplication
@ImportResource("classpath:external-storage-proxy.xml")
public class Application {

	public static void main(String[] args) {
	    System.setProperty(AuthenticationFilter.HAWTIO_AUTHENTICATION_ENABLED, "false");
            SpringApplication.run(Application.class, args);
	}
	
	@Bean
	public ServletRegistrationBean camelServletRegistrationBean() {
	    ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/*");
	    registration.setName("CamelServlet");
	    return registration;
	}
	
	@Component
	class RestApi extends RouteBuilder {

	    @Override
	    public void configure() {
	        restConfiguration()
	        .component("servlet")
	        .bindingMode(RestBindingMode.json)
	        .dataFormatProperty("prettyPrint", "true")
	        .apiContextPath("/api-doc")
	        .apiProperty("api.title", "External Storage Proxy API").apiProperty("api.version", "v1.0.0.beta1")
	        .apiProperty("cors", "true");
	    }
	}
	
	/**
	* Loading a plugin to deliver for the console.
	*/
	@Bean
	public HawtPlugin samplePlugin() {
	   return new HawtPlugin("sample-plugin",
	        "/hawtio/plugins",
	        "",
	        new String[] { "sample-plugin/js/sample-plugin.js" });
	}

	/**
	 * Set things up to be in offline mode.
	*/
	@Bean
	public ConfigFacade configFacade() {
	    System.setProperty("hawtio.offline", "true");
	    return ConfigFacade.getSingleton();
	}
}
