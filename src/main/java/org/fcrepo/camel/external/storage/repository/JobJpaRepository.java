package org.fcrepo.camel.external.storage.repository;

import org.fcrepo.camel.external.storage.model.Job;
import org.springframework.data.repository.CrudRepository;

public interface JobJpaRepository extends CrudRepository<Job, Integer> {
}
