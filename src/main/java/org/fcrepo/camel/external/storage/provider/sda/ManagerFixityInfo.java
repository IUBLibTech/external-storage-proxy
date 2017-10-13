package org.fcrepo.camel.external.storage.provider.sda;

import java.lang.*;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import com.fasterxml.jackson.annotation.JsonProperty;


@Produces("application/json")
public class ManagerFixityInfo{
	@JsonProperty("error")
	private Integer error;
	@JsonProperty("message")
	private String message;
	@JsonProperty("checksum")
	private String checksum = null;
	@JsonProperty("checktime")
	private long checktime;

	public Integer getError() {
		return error;
	}
	public void setError(Integer error) {
		this.error = error;
	}
	
	public long getChecktime() {
		return checktime;
	}
	public void setUrl(long checktime) {
		this.checktime = checktime;
	}
	
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}


	public String getMessage() {
		return message;
	}
	public void  setMessage(String message) {
		this.message = message;
	}
	
	public String toString() {
		return "message: " + this.message +"\t" +this.error+ "\n";
	}


}
