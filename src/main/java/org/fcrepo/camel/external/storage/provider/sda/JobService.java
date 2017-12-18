package org.fcrepo.camel.external.storage.provider.sda;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.fcrepo.camel.external.storage.model.Job;
import org.fcrepo.camel.external.storage.repository.JobJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JobService implements Processor {

    @Autowired
    JobJpaRepository jobs;
    
    public void process(final Exchange exchange) throws Exception {
        
        ProducerTemplate template = exchange.getContext().createProducerTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        
        System.out.println("Inside JobService processing pending SDA jobs...");
        List<Job> sda_jobs = exchange.getIn().getBody(List.class);
        if (sda_jobs != null && !sda_jobs.isEmpty()){
            for (Job job : sda_jobs) {
                // TODO Have to conditionally handle fixity
                String result = template.requestBodyAndHeader("direct:sdaGetCache", "mybody", "external_uri", 
                                                              job.getExternalUri(), String.class);
                
                Map<String, Object> map = objectMapper.readValue(result, new TypeReference<Map<String,Object>>(){});
                
                String message = map.get("message").toString();
                int errorCode = Integer.valueOf(map.get("error").toString());

                if (errorCode == 0) {
                    String url = map.get("url").toString();
                    job.setStatus("success");
                    job.setResult(url);
                }
                else {
                    job.setStatus("failed");
                }
                job.setVendorMsg(message);
                
                System.out.println("Saving Job for " + job.getExternalUri() + " with status " + job.getStatus());
                jobs.save(job);
            }
        }
        
        template.stop();
    }
    

    // TODO Conditional logic to handle either stage or fixity could be handled in a method
    public Job updateJob(Job job, String action, String result) {
        job.setStatus("success");
        job.setResult(result);
        return job;
    }
}