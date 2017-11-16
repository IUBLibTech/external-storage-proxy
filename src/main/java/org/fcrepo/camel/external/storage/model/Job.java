package org.fcrepo.camel.external.storage.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.camel.component.jpa.Consumed;

@Entity
@Table(name = "jobs")
@NamedQueries({
    @NamedQuery(name = "readyJobs", query = "select j from Job j where j.status = 'waiting'"),
    @NamedQuery(name = "queuedJobs", query = "select j from Job j where j.status = 'queued'")    
})

public class Job {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String fname;  // TODO Why do we need this field from whiteboard sketches?
    private String resourceUri; // The resource uri for fname in a repository like Fedora
    private String externalUri; // The uri to the fname in the storage service
    private String service;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private String result;
    private String type;
    private String vendorMsg;
    
    public Long getId() {
        return id;
    }
    
    public String getFname() {
        return fname;
    }
    public void setFname(String fname) {
        this.fname = fname;
    }
    public String getResourceUri() {
        return resourceUri;
    }
    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }
    public String getExternalUri() {
        return externalUri;
    }
    public void setExternalUri(String externalUri) {
        this.externalUri = externalUri;
    }
    public String getService() {
        return service;
    }
    public void setService(String service) {
        this.service = service;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getVendorMsg() {
        return vendorMsg;
    }
    public void setVendorMsg(String vendorMsg) {
        this.vendorMsg = vendorMsg;
    }
    
    @Consumed
    public void afterConsume()
    {
        if (this.status.equals("waiting")) {
            setStatus("queued");
        } else if (this.status.equals("queued")) {
            setStatus("pending");
        }
    }
}
