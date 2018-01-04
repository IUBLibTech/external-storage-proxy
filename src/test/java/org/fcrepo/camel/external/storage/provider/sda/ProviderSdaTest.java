package org.fcrepo.camel.external.storage.provider.sda;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;

import org.fcrepo.camel.external.storage.model.Job;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.matchers.JUnitMatchers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:afterTests.sql")

// To skip any failing test methods, annotate them with @Ignore 

public class ProviderSdaTest {

    @Autowired
    private TestRestTemplate restTemplate;
    
    // TODO Figure out how to use the following to mock SDA calls
    // private RestTemplate remoteRestTemplate = new RestTemplate();
    // private MockRestServiceServer server = MockRestServiceServer.bindTo(remoteRestTemplate).build();

    
    @Before
    public void setUp() {
            // Do any mock setup for service responses here.
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
        assertThat(jobs).element(0)
           .hasFieldOrPropertyWithValue("status", "pending");

    }
    
}