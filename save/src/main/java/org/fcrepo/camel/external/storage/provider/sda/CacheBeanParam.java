package org.fcrepo.camel.external.storage.provider.sda;

/**
 *  * Hello world!
 *   */
import javax.ws.rs.PathParam;
import javax.ws.rs.BeanParam;

public class CacheBeanParam {
	@PathParam("cacheId")
	private String cacheId;
	@PathParam("fileId")
	private String fileId;

	
	public String getCacheId() {
		return cacheId;
	}
	public void setCacheId(String cacheId) {
		this.cacheId = cacheId;
	}
	
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	public String toString() {
		return "{\"Cache\":\""+ this.cacheId + "\"," + "\"file\":\"" + this.fileId + "\"}";
	}


}
