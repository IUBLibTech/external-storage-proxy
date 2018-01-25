package org.fcrepo.camel.external.storage.provider.tapeArchive;

import java.lang.*;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import com.fasterxml.jackson.annotation.JsonProperty;


@Produces("application/json")
public class ManagerInfo{
	@JsonProperty("error")
	private Integer error;
	@JsonProperty("message")
	private String message;
	@JsonProperty("url")
	private String url;
	
	public Integer getError() {
		return error;
	}
	public void setError(Integer error) {
		this.error = error;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}


	public String getMessage() {
		return message;
	}
	public void  setMessage(String message) {
		this.message = message;
	}
	
	public String toString() {
		return "url: "+ this.url + "\n" + "message: " + this.message + "\t" + this.error+"\n";
	}


}
