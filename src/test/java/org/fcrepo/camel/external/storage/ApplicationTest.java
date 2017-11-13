package org.fcrepo.camel.external.storage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.fcrepo.camel.external.storage.model.Job;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:beforeTests.sql")
@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:afterTests.sql")

// To skip any failing test methods, annotate them with @Ignore 

public class ApplicationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getJobsTest() {
        ResponseEntity<List<Job>> response = restTemplate.exchange("/jobs",
            HttpMethod.GET, null, new ParameterizedTypeReference<List<Job>>() {
            });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Job> jobs = response.getBody();
        assertThat(jobs).hasSize(2);
        assertThat(jobs).element(0)
            .hasFieldOrPropertyWithValue("externalUri", "myFile")
            .hasFieldOrPropertyWithValue("type", "stage");
        assertThat(jobs).element(1)
            .hasFieldOrPropertyWithValue("externalUri", "myFile")
            .hasFieldOrPropertyWithValue("type","fixity");
    }
    
    @Test
    // @Ignore
     public void jobQueueProcessorTest() {
        // Wait for the job_queue_processor route to consume/alter the db rows
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ResponseEntity<List<Job>> response = restTemplate.exchange("/jobs",
            HttpMethod.GET, null, new ParameterizedTypeReference<List<Job>>() {
            });
        List<Job> jobs = response.getBody();
        /* If the Camel JPA route is working, the status of selected rows in the database 
           will be changed. */
        assertThat(jobs).element(0)
            .hasFieldOrPropertyWithValue("status", "queued");
    }
    
    @Test
    // @Ignore
    public void postJobTest() {
        ResponseEntity<String> stageResponse = restTemplate.exchange("/dummyService/stage/unstagedFile",
            HttpMethod.POST, null, String.class);
        assertThat(stageResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<List<Job>> response = restTemplate.exchange("/jobs",
            HttpMethod.GET, null, new ParameterizedTypeReference<List<Job>>() {});
        List<Job> jobs = response.getBody();
        assertThat(jobs).hasSize(3);
        assertThat(jobs).element(2)
            .hasFieldOrPropertyWithValue("externalUri", "unstagedFile");
    }
    
    @Test
    //@Ignore
    public void statusByFileUriTest() {
        ResponseEntity<String> stageResponse = restTemplate.exchange("/dummyService/stage/unstagedFile",
           HttpMethod.POST, null, String.class);
        ResponseEntity<List<Job>> response = restTemplate.exchange("/dummyService/status/unstagedFile",
           HttpMethod.GET, null, new ParameterizedTypeReference<List<Job>>() {});
        List<Job> jobs = response.getBody();
        assertThat(jobs).hasSize(1);
        assertThat(jobs).element(0)
        .hasFieldOrPropertyWithValue("externalUri", "unstagedFile");
    }
}