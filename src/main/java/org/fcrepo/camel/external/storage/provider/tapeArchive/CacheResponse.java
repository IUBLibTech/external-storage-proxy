package org.fcrepo.camel.external.storage.provider.tapeArchive;

/**
 *  *cache response template for get cache file information 
 *   */

import com.fasterxml.jackson.annotation.JsonProperty;

public class CacheResponse{
	private String name = null;
	private String status = null;
	private String url = null;
	private boolean fixity_available = false;
	private String fixity_date = null;
	private String checksum = null;

	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@JsonProperty(value = "fixity_available")
	public boolean getFixityAvailable() {
		return fixity_available;
	}
	public void setFixityAvailable(boolean fixity_available) {
		this.fixity_available = fixity_available;
	}
	@JsonProperty(value = "fixity_date")
	public String getFixityDate() {
		return fixity_date;
	}
	public void setFixityDate(String fixity_date) {
		this.fixity_date = fixity_date;
	}

	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String toString() {
		return "url: "+ this.url + "\n" + "name: " + this.name + "\n";
	}


}
