package org.fcrepo.camel.external.storage.provider.sda;
import java.util.HashMap;

/*
*/
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.fcrepo.camel.external.storage.common.CommonResponse;


public class JobProcessor implements Processor{
	private static final int BAD_REQUEST = 400;
	//@Override	
	public void process(final Exchange exchange) throws Exception {
		final Message in = exchange.getIn();
		final String contentType = in.getHeader(Exchange.CONTENT_TYPE, "", String.class);
		final String body = in.getBody(String.class);
		JobResponse jobResponse = new JobResponse();
		CommonResponse common = new CommonResponse(); 

		String path = in.getHeader(Exchange.HTTP_URI, String.class);
		jobResponse.setCacheName(in.getHeader("service", String.class));
		jobResponse.setFileName(in.getHeader("external_uri", String.class));
		jobResponse.setType(in.getHeader("action", String.class));
		
                common.setExternalUri(jobResponse.getFileName());
                common.setService(jobResponse.getCacheName());
                if (jobResponse.getType().equals("stage")) {
                    HashMap<String, String> stage = new HashMap<String, String>();
                    stage.put("status", "pending");
                    common.setStage(stage);
                }
                else if (jobResponse.getType().equals("fixity")) {
                    HashMap<String, String> fixity = new HashMap<String, String>();
                    fixity.put("status", "pending");
                    common.setFixity(fixity);
                }
		
		exchange.getOut().setBody(common);
		exchange.getOut().setHeaders(exchange.getIn().getHeaders());
	}
}
