package edu.indiana.dlib.hydradam.storageproxy;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;


public class DefaultProcessor implements Processor{
	//@Override	
	public void process(Exchange exchange) throws Exception{

                         CacheBeanParam myBean = new CacheBeanParam();
                         myBean = exchange.getIn().getBody(CacheBeanParam.class);
                         String fileId = myBean.getFileId();
                         exchange.getIn().setHeader(Exchange.HTTP_PATH, "/" + fileId);

                          exchange.getIn().setBody("");

                  }
}
