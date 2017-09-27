package edu.indiana.dlib.hydradam.storageproxy;
/*
*/
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;


public class JobProcessor implements Processor{
	private static final int BAD_REQUEST = 400;
	//@Override	
	public void process(final Exchange exchange) throws Exception {
		final Message in = exchange.getIn();
		final String contentType = in.getHeader(Exchange.CONTENT_TYPE, "", String.class);
		final String body = in.getBody(String.class);
		JobResponse jobResponse = new JobResponse();

		String path = in.getHeader(Exchange.HTTP_URI, String.class);
		jobResponse.setCacheName(in.getHeader("cacheId", String.class));
		jobResponse.setFileName(in.getHeader("fileId", String.class));
		jobResponse.setType(in.getHeader("type", String.class));
		exchange.getOut().setBody(jobResponse);
		exchange.getOut().setHeaders(exchange.getIn().getHeaders());
	}
}
