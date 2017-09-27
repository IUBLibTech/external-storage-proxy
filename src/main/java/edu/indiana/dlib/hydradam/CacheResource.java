package edu.indiana.dlib.hydradam.storageproxy;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.MediaType;
import org.apache.camel.Exchange;
import org.apache.camel.Message;

public class CacheResource implements CacheResourceIntf {


    public String searchFile(CacheBeanParam cacheBeanParam) {
	//String fileId = cacheBeanParam.getFileId();
	return null;
    }

    public String  listFile( String  cacheId) {
	return cacheId;
    }
    
    public String listCaches() {
	/*Cache cache = new Cache();
	cache.setId("SDA");
	cache.setName("Scholarly Data Archiver");
	cache.setUrl("carbon.dlib.indiana.edu");
	return Response.status(Status.OK).entity("abc").build();
	*/
	return null;
    }

    public String postJob(JobBeanParam jobBeanParam) {

	return null;
    }

    public String checksum(String data) {
	return null;
    }

    public String getJobs() {
	return null;
    }


    public String  deleteJob( String  id) {
	return id;
    }
}

