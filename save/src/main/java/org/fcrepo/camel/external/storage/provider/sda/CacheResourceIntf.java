package org.fcrepo.camel.external.storage.provider.sda;

/**
 * Hello world!
 */

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.jws.WebService;
import org.apache.camel.Exchange;
import org.apache.camel.Message;

@Path("/")
public interface CacheResourceIntf {


    @GET
    @Path("/caches/{cacheId}/files/{fileId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String searchFile(@BeanParam CacheBeanParam cacheBeanParam);
    
    @GET
    @Path("/caches/{cacheId}/files")
    @Produces(MediaType.APPLICATION_JSON)
    public String listFile(@PathParam("cacheId") String cacheId);


    @GET
    @Path("/caches")
    @Produces(MediaType.APPLICATION_JSON)
    public String listCaches();


    @POST
    @Path("/jobs/{cacheId}/{fileId}/{type}{fixity : (/fixity)?}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String postJob(@BeanParam JobBeanParam jobBeanParam);

    @GET
    @Path("/jobs")
    @Produces(MediaType.APPLICATION_JSON)
    public String getJobs();
    
    
    @DELETE
    @Path("/jobs/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteJob(@PathParam("id") String id);
   
    @POST
    @Path("/checksum")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String checksum(String data);
}
