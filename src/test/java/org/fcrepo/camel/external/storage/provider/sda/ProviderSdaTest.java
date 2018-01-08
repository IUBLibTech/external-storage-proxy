package org.fcrepo.camel.external.storage.provider.sda;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
// import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.fcrepo.camel.external.storage.model.Job;
import org.fcrepo.camel.external.storage.repository.JobJpaRepository;
import org.fcrepo.camel.external.storage.service.DatabaseService;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import junit.framework.Assert;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:afterTests.sql")

/* To skip any failing test methods, annotate them with @Ignore 
 FYI avoid creating actual jobs that will be picked up by processor routes in main context.
  i.e. If stage requests are made, database entries will be acted upon by routes outside these tests
  and will fail because their resulting HTTP requests won't be handled by mocks as they are here.
*/        

public class ProviderSdaTest extends CamelTestSupport {

    private TestRestTemplate restTemplate;
    
    
    @Autowired
    JobJpaRepository jobs;
    
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8101);
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from("direct:startpoint").id("route1") //
                        .process(new Processor() {

                            @Override
                            public void process(Exchange exchng) throws Exception {
                               // Place List containing one job on exchange here
                                
                                DatabaseService dbService = new DatabaseService();
                                Job testJob = dbService.generateJob("testUri", "test", "test");
                                testJob.setStatus("test_started");
                                jobs.save(testJob);
                                Iterable<Job> testJobs = jobs.findByExternalUriEquals("testUri");
                                log.info("running test with Job: " + testJob.getExternalUri() + ":" + testJob.getStatus());
                                exchng.getOut().setBody(testJobs);
                            }
                        })
                        // Call JobService that will ping mocked SDA service and update job 
                        .process(new JobService());

                from("direct:sdaGetCache")
                 .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                 .to("jetty:http://localhost:8101/cacheManager.cgi/cache/testUri");        
            }
        };
    }


    
    @Ignore
    @Test
    public void sdaJobPendingTest() throws InterruptedException {
        stubFor(get(urlPathMatching("/cacheManager.cgi/.*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("\"status\": \"test_completed\"")));
        log.info("running test");
        // Call endpoint with a List containing one job
        template.requestBody("direct:startpoint", "<root><name>abc</name></root>");
        // Status for job in the database should now be test_completed
        // assertThat(jobs).element(0)
        //   .hasFieldOrPropertyWithValue("status", "test_completed");
    }
    
    @Test
    // @Ignore
    public void wireMockSampleTest() {
        stubFor(get(urlPathMatching("/cacheManager.cgi/.*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("\"status\": \"failed\"")));
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://localhost:8101/cacheManager.cgi/cache/foo");
        String stringResponse = "";
        try {
            HttpResponse httpResponse = httpClient.execute(request);
            stringResponse = convertHttpResponseToString(httpResponse);
            verify(getRequestedFor(urlEqualTo("/cacheManager.cgi/cache/foo")));
            Assert.assertEquals(200, httpResponse.getStatusLine().getStatusCode());
            Assert.assertEquals("application/json", httpResponse.getFirstHeader("Content-Type").getValue());
            Assert.assertEquals("\"status\": \"failed\"", stringResponse);
        } catch(IOException ie) {
            
        }
    }
    
    
    @Test
    @Ignore
    public void sdaJobStatusTest() {
        ResponseEntity<String> stageResponse = restTemplate.exchange("/sda/stage/foo",
                                                                    HttpMethod.POST, null, String.class);
        
        // Wait for the job_queue_processor route to consume/alter the db rows
        // assertThat(stageResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ResponseEntity<List<Job>> response = restTemplate.exchange("/jobs",
            HttpMethod.GET, null, new ParameterizedTypeReference<List<Job>>() {
            });
        List<Job> jobs = response.getBody();
        /* If the Camel JPA routes are working, the status of selected rows in the database 
           will eventually be changed from waiting to queued to pending. */
        // assertThat(jobs).element(0)
        //   .hasFieldOrPropertyWithValue("status", "pending");

    }
    
    private String convertHttpResponseToString(HttpResponse httpResponse) throws IOException {
        InputStream inputStream = httpResponse.getEntity().getContent();
        return convertInputStreamToString(inputStream);
    }
    
    private String convertInputStreamToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        String string = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return string;
    }
    
}