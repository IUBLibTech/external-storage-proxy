package org.fcrepo.camel.external.storage.provider.sda;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class DefaultProcessor implements Processor{
    //@Override	
    public void process(Exchange exchange) throws Exception{

        String fileId = exchange.getIn().getHeader("external_uri", String.class);
        exchange.getIn().setHeader(Exchange.HTTP_PATH, "/" + fileId);
        exchange.getIn().setBody("");

    }
}
