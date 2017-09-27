package edu.indiana.dlib.hydradam.storageproxy;
/*
*/
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;


public class JobProcessor implements Processor{
	    public void process(Exchange exchange) throws Exception{

                         CacheBeanParam myBean = new CacheBeanParam();
                         myBean = exchange.getIn().getBody(CacheBeanParam.class);
                         String fileId = myBean.getFileId();
                         exchange.getIn().setHeader(Exchange.HTTP_PATH, "/" + fileId);

                          exchange.getIn().setBody("");

                  }

}
