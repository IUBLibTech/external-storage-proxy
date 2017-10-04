package org.fcrepo.camel.external.storage.provider.sda;

import java.lang.*;
import javax.ws.rs.Produces;
import com.fasterxml.jackson.annotation.JsonProperty;

@Produces("application/json")
public class JobResponse{
	private String cachename = null;
	private String filename = null;
	private String type = null;

	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@JsonProperty(value = "cache_name")
	public String getCacheName() {
		return cachename;
	}
	public void setCacheName(String cachename) {
		this.cachename = cachename;
	}

	@JsonProperty(value = "cache_file_name")
	public String getFileName() {
		return filename;
	}
	public void setFileName(String filename) {
		this.filename = filename;
	}
	
	public String toString() {
		return "filename: "+ this.filename + "\n" + "cachename: " + this.cachename + "\n";
	}


}
