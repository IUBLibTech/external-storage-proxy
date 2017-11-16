package org.fcrepo.camel.external.storage.service;

import org.fcrepo.camel.external.storage.model.Job;
import org.fcrepo.camel.external.storage.repository.JobJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseService {

    @Autowired
    JobJpaRepository jobs;

    public Iterable<Job> findJobs() {
        return jobs.findAll();
    }
    
    public Iterable<Job> findJobsByFile(String external_uri) {
        return jobs.findByExternalUriEquals(external_uri);
    }
    
    public Job generateJob(String external_uri, String service, String type) {
        Job job = new Job();
        job.setExternalUri(external_uri);
        job.setService(service);
        job.setType(type);
        job.setStatus("waiting");
        return job;
    }
}
