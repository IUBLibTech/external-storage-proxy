package org.fcrepo.camel.external.storage.common;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CommonResponse {

    private String external_uri = null;
    private String service = null;
    private HashMap<String, String> stage = null;
    private HashMap<String, String> fixity = null;
    
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
    public HashMap getStage() {
         return stage;
    }
    public void setStage(HashMap<String, String> stage) {
        this.stage = stage;
    }
    public HashMap getFixity() {
         return fixity;
    }
    public void setFixity(HashMap<String, String> fixity) {
        this.fixity = fixity;
    }

}
