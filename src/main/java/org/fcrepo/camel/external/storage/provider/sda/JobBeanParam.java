package org.fcrepo.camel.external.storage.provider.sda;
/**
 *  * Hello world!
 *   */
import javax.ws.rs.PathParam;
import javax.ws.rs.BeanParam;

public class JobBeanParam {
	@PathParam("cacheId")
	private String cacheId;
	@PathParam("fileId")
	private String fileId;
	@PathParam("type")
	private String type;
	@PathParam("fixity")
	private String fixity;
	
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
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getFixity() {
		return fixity;
	}
	public void setFixity(String fixity) {
		this.fixity = fixity;
	}
	public String toString() {
		return "Cache: "+ this.cacheId + "\n" + "file: " + this.fileId + "\n";
	}


}
