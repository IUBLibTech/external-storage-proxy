package org.fcrepo.camel.external.storage.common;

import java.util.HashMap;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.fcrepo.camel.external.storage.model.Job;

public class CommonResponseProcessor implements Processor {
    
    public void process(final Exchange exchange) throws Exception {
        final Message in = exchange.getIn();
        final List<Job> jobs = in.getBody(List.class);
        final String externalUri = exchange.getIn().getHeader("external_uri", String.class);
        final String service = exchange.getIn().getHeader("service", String.class);
        CommonResponse finalResponse = buildResponse(jobs, externalUri, service);
        exchange.getOut().setBody(finalResponse);
    }
    
    private CommonResponse buildResponse(List<Job> jobs, String externalUri, String service) {
        // TODO Refactor to make sure the latest Job ojbects are selected for known action types
        //  Currently the last ones will be written to the hash maps, which are probably newest but not certainly
        CommonResponse common = new CommonResponse();
        common.setExternalUri(externalUri);
        common.setService(service);
        if (jobs != null && !jobs.isEmpty()){
            HashMap<String, String> stage = new HashMap<String, String>();
            HashMap<String, String> fixity = new HashMap<String, String>();
            for (Job job : jobs) {
                if (job.getType().equals("stage")) {
                    stage.put("status", job.getStatus());
                    stage.put("result", job.getResult());
                    stage.put("vendor_message", job.getVendorMsg());
                    common.setStage(stage);
                }
                if (job.getType().equals("fixity")) {
                    stage.put("status", job.getStatus());
                    stage.put("result", job.getResult());
                    stage.put("vendor_message", job.getVendorMsg());
                    common.setFixity(fixity);
                }
            }
        }
        return common;
    }
}
