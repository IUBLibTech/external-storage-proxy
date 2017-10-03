package org.fcrepo.camel.external.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.fcrepo.camel.external.storage.model.Job;
import org.fcrepo.camel.external.storage.repository.JobJpaRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations={"classpath:org/fcrepo/camel/external/storage/applicationTests-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class JobPersistenceTest {
	@Autowired
        private JobJpaRepository jobJpaRepository;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Test
	public void testJpaFind() {
	    List<Job> jobs = jobJpaRepository.findAll();
	    assertNotNull(jobs);
	}
	
	@Test
	@Transactional
	public void testSaveAndGetAndDelete() throws Exception {
		Job job = new Job();
		job.setFname("fictional/filename");
		job.setType("stage");
		job = jobJpaRepository.saveAndFlush(job);
		
		// clear the persistence context so we don't return the previously cached job object
		// this is a test only thing and normally doesn't need to be done in prod code
		entityManager.clear();

		// Job otherJob = jobJpaRepository.findOne(job.getId());
		Optional<Job> otherJob = jobJpaRepository.findById(job.getId());
		assertEquals("fictional/filename", otherJob.get().getFname());
		assertEquals("stage", otherJob.get().getType());
		
		jobJpaRepository.delete(otherJob.get());
	}

	@Test
	public void testFindBy() throws Exception {
		List<Job> jobs = jobJpaRepository.findByType("stage");
		assertEquals(4, jobs.size());
	}
}
