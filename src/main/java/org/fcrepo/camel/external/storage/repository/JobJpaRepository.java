package org.fcrepo.camel.external.storage.repository;

import java.util.List;

import org.fcrepo.camel.external.storage.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobJpaRepository extends JpaRepository<Job, Long> {
    List<Job> findByFname(String fname);
    List<Job> findByType(String type);

}
