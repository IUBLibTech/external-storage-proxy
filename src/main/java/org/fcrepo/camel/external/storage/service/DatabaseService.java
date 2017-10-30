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
}
