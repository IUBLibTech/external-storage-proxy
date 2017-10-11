package org.fcrepo.camel.external.storage.provider.sda;

import java.util.HashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.fcrepo.camel.external.storage.common.CommonResponse;

public class ResponseProcessor implements Processor {
    public void process(final Exchange exchange) throws Exception {
        final Message in = exchange.getIn();
        final CacheResponse body = in.getBody(CacheResponse.class);
        CommonResponse common = new CommonResponse();

        if (body != null){
            
            HashMap<String, String> stage = new HashMap<String, String>();
            HashMap<String, String> fixity = new HashMap<String, String>();
            common.setExternal_uri(body.getName());
            common.setService("sda");
            if (body.getStatus() != null) {
                stage.put("status", body.getStatus());
                stage.put("result", body.getUrl());
                common.setStage(stage);
            }
            if (body.getFixityAvailable()) {
                fixity.put("status", "success");
                fixity.put("result", body.getChecksum());
                fixity.put("created_at", body.getFixityDate());
                fixity.put("updated_at", body.getFixityDate());
                common.setFixity(fixity);
            }
            exchange.getOut().setBody(common);
        }
    }
}
