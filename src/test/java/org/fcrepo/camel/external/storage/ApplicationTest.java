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
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Ignore
public class ApplicationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void jobsTest() {
        ResponseEntity<List<Job>> response = restTemplate.exchange("//external-storage-proxy/jobs",
            HttpMethod.GET, null, new ParameterizedTypeReference<List<Job>>() {
            });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Job> jobs = response.getBody();
        assertThat(jobs).hasSize(2);
        assertThat(jobs).element(0)
            .hasFieldOrPropertyWithValue("external_uri", "myFile")
            .hasFieldOrPropertyWithValue("type", "stage");
        assertThat(jobs).element(1)
            .hasFieldOrPropertyWithValue("external_uri", "myFile")
            .hasFieldOrPropertyWithValue("type","fixity");
    }
}