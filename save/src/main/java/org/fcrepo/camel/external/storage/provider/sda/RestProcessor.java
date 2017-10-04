package org.fcrepo.camel.external.storage.provider.sda;

import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

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
			
			exchange.getOut().setBody(cacheResponse);
			}catch (JsonProcessingException e) {
				exchange.getOut().setBody(cacheResponse);
                        }


		}
		
	}
}
