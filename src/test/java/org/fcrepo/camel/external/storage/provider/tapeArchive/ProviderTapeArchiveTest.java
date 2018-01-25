package org.fcrepo.camel.external.storage.provider.tapeArchive;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.assertj.core.api.Assertions;
import org.fcrepo.camel.external.storage.model.Job;
import org.fcrepo.camel.external.storage.repository.JobJpaRepository;
import org.fcrepo.camel.external.storage.service.DatabaseService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.junit.WireMockRule;


//import static org.assertj.core.api.Assertions.assertThat;  // main one

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:afterTests.sql")

/* To skip any failing test methods, annotate them with @Ignore 
 FYI avoid creating actual jobs that will be picked up by processor routes in main context.
  i.e. If stage requests are made, database entries will be acted upon by routes outside these tests
  and will fail because their resulting HTTP requests won't be handled by mocks as they are here.
*/        

public class ProviderTapeArchiveTest extends CamelTestSupport {

    @Autowired
    JobJpaRepository jobs;
    
    @Autowired
    JobService jobService;
    
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8101);
    
    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("jobService", jobService);
        return registry;
    }
    
    Iterable<Job> testJobs = null;
    
    @Before
    public void executedBeforeEach() {
        DatabaseService dbService = new DatabaseService();
        Job testJob = dbService.generateJob("testUri", "test", "test");
        testJob.setStatus("test_started");
        jobs.save(testJob);
        testJobs = jobs.findByExternalUriEquals("testUri");
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                
                from("direct:startpoint").id("route1") //
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchng) throws Exception {
                                Iterable<Job> body = exchng.getIn().getBody(Iterable.class);
                                exchng.getOut().setBody(body);
                            }
                        })
                        .to("bean:jobService");

                from("direct:tapeArchiveGetCache")
                        .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                        // See tests below how this call gets mocked with Wiremock stubFor
                        .to("jetty:http://localhost:8101/cacheManager.cgi/cache/testUri");        
            }
        };
    }

    //@Ignore
    @Test
    public void tapeArchiveJobPendingSuccessTest() throws InterruptedException {
        // This WireMock stub will intercept the jetty HTTP request from within JobService
        stubFor(get(urlPathMatching("/cacheManager.cgi/.*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\": \"0\", \"message\": \"job_completed\", \"url\": \"NA\"}")));
        
        
        // Call the test endpoint with a List containing one job
        template.requestBody("direct:startpoint", testJobs);
        // Fields for the job should have changed after being processed by JobService
        testJobs = jobs.findByExternalUriEquals("testUri");
        Job myJob = testJobs.iterator().next();
        Assertions.assertThat(myJob)
           .hasFieldOrPropertyWithValue("status", "success");
        Assertions.assertThat(myJob)
        .hasFieldOrPropertyWithValue("vendorMsg", "job_completed");
    }
    
    //@Ignore
    @Test
    public void tapeArchiveJobPendingFailedTest() throws InterruptedException {
        // This WireMock stub will intercept the jetty HTTP request from within JobService
        stubFor(get(urlPathMatching("/cacheManager.cgi/.*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\": \"3\", \"message\": \"job_failed\", \"url\": \"NA\"}")));
        
        // Call the test endpoint with a List containing one job
        template.requestBody("direct:startpoint", testJobs);
        // Fields for the job should have changed after being processed by JobService
        testJobs = jobs.findByExternalUriEquals("testUri");
        Job myJob = testJobs.iterator().next();
        Assertions.assertThat(myJob)
           .hasFieldOrPropertyWithValue("status", "failed");
        Assertions.assertThat(myJob)
        .hasFieldOrPropertyWithValue("vendorMsg", "job_failed");
    }

    

    
}