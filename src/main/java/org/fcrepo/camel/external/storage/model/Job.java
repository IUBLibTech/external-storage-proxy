package org.fcrepo.camel.external.storage.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.camel.component.jpa.Consumed;

@Entity
@Table(name = "jobs")
@NamedQuery(name = "readyJobs", query = "select j from Job j where j.status = 'waiting'")
public class Job {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String fname;  // TODO Why do we need this field from whiteboard sketches?
    private String resource_uri; // The resource uri for fname in a repository like Fedora
    private String external_uri; // The uri to the fname in the storage service
    private String service;
    private String status;
    private Date created_at;
    private Date updated_at;
    private String result;
    private String type;
    private String vendor_msg;
    
    public Long getId() {
        return id;
    }
    
    public String getFname() {
        return fname;
    }
    public void setFname(String fname) {
        this.fname = fname;
    }
    public String getResource_uri() {
        return resource_uri;
    }
    public void setResource_uri(String resource_uri) {
        this.resource_uri = resource_uri;
    }
    public String getExternal_uri() {
        return external_uri;
    }
    public void setExternal_uri(String external_uri) {
        this.external_uri = external_uri;
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
    public Date getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
    public Date getUpdated_at() {
        return updated_at;
    }
    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
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

    public String getVendor_msg() {
        return vendor_msg;
    }
    public void setVendor_msg(String vendor_msg) {
        this.vendor_msg = vendor_msg;
    }
    
    @Consumed
    public void afterConsume()
    {
        setStatus("queued");
    }
}
