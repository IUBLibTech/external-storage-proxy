package org.fcrepo.camel.external.storage.provider.sda;

import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.fcrepo.camel.external.storage.common.CommonResponse;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class RestProcessor implements Processor{
	//@Override	
	public void process(final Exchange exchange) throws Exception {
		final Message in = exchange.getIn();
		final String contentType = in.getHeader(Exchange.CONTENT_TYPE, "", String.class);
		final String body = in.getBody(String.class);
		CacheResponse cacheResponse = new CacheResponse();
		ObjectMapper mapper = new ObjectMapper();

		String path = in.getHeader(Exchange.HTTP_URI, String.class);
		cacheResponse.setName( path.substring(path.lastIndexOf('/')+1));
		ManagerInfo msg = new ManagerInfo();
		ManagerFixityInfo fmsg = new ManagerFixityInfo();

		if (body != null && !body.isEmpty()){
                   CommonResponse common = new CommonResponse(); 
		   try {
			ArrayList<String> items = new ArrayList(Arrays.asList(body.split("\\+")));

			for(int i=0;i<items.size();i++) {
			 	if (items.get(i).matches(".*url.*")) {
					msg = mapper.readValue(items.get(i), ManagerInfo.class);

				}
				else if (items.get(i).matches(".*checksum.*")) {
				  fmsg = mapper.readValue(items.get(i), ManagerFixityInfo.class);
				}
			
			}

			System.out.println("msg :" + msg.toString());
			System.out.println("fmsg :" + fmsg.toString());

			//combine the two messages and transform to the response message
			if (msg.getError() == 0) {

                            if (msg.getUrl()!=null) {
                                cacheResponse.setUrl(exchange.getContext().resolvePropertyPlaceholders("{{sda_hostname}}") +":" + exchange.getContext().resolvePropertyPlaceholders("{{sda_port}}") + msg.getUrl());
				if (fmsg.getError() == 0) {
				    if (!fmsg.equals(null)) {
					cacheResponse.setFixityAvailable(true);
                                        cacheResponse.setChecksum(fmsg.getChecksum());
					//return time unit is second, need to change to millisecond
                                        long val = fmsg.getChecktime() * 1000;
                                        Date date=new Date(val);
                                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                                        cacheResponse.setFixityDate(df.format(date));
					cacheResponse.setStatus("staged");
				    }
				}
				else if (fmsg.getError() == -666) {
				    	cacheResponse.setStatus("calculating checksum");
					cacheResponse.setChecksum(null);
				    
			        }
				else {
				    cacheResponse.setStatus("staged");
				    cacheResponse.setFixityAvailable(false);
                                         cacheResponse.setChecksum(null);
				}
					
                            }
                            else {
                                    cacheResponse.setStatus("staging");
                                    cacheResponse.setUrl(null);
                            }
                        }
                        else {
                                    cacheResponse.setStatus(null);
                                    cacheResponse.setUrl(null);
                        }
			
			
			HashMap<String, String> stage = new HashMap<String, String>();
		        HashMap<String, String> fixity = new HashMap<String, String>();
		        common.setExternal_uri(cacheResponse.getName());
		        common.setService("sda");
		        if (cacheResponse.getStatus() != null) {
		            stage.put("status", cacheResponse.getStatus());
		            stage.put("result", cacheResponse.getUrl());
		            common.setStage(stage);
		        }
		        if (cacheResponse.getFixityAvailable()) {
		            fixity.put("status", "success");
		            fixity.put("result", cacheResponse.getChecksum());
		            fixity.put("created_at", cacheResponse.getFixityDate());
		            fixity.put("updated_at", cacheResponse.getFixityDate());
		            common.setFixity(fixity);
		        }
			exchange.getOut().setBody(common);
			}catch (JsonProcessingException e) {
				exchange.getOut().setBody(common);
                        }


		}
		
	}
}
