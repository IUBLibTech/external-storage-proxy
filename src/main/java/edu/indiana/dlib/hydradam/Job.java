package edu.indiana.dlib.hydradam.storageproxy;

/**
 *  * Hello world!
 *   */
import java.lang.*;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import com.fasterxml.jackson.annotation.JsonProperty;


@Produces("application/json")
public class Job{
	@JsonProperty("type")
	private String type;
	@JsonProperty("storeName")
	private String storeName;
	@JsonProperty("filePath")
	private String filePath;
	@JsonProperty("description")
	private String description;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


	public String getDescription() {
		return description;
	}
	public void  setDescription(String description) {
		this.description = description;
	}
	
	public String toString() {
		return "type: "+ this.type + "\n" + "storeName: " + this.storeName + "\n";
	}


}
