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
    
    public Job generateJob(String file_uri, String service) {
        Job job = new Job();
        job.setExternal_uri(file_uri);
        job.setService(service);
        job.setStatus("waiting");
        return job;
    }
}
