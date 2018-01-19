package org.fcrepo.camel.external.storage.provider.tapeArchive;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.fcrepo.camel.external.storage.model.Job;

public class StorageProxyRouter extends RouteBuilder {

	public void configure() throws Exception {


		from("direct:tapeArchiveSearchFile")
		.multicast().aggregationStrategy(new AggregationStrategy() {
			@Override
			public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
				String newBody = newExchange.getIn().getBody(String.class);

				if (oldExchange == null) {
			            return newExchange;
        			}
				String oldBody = oldExchange.getIn().getBody(String.class);
				newExchange.getIn().setBody(oldBody + "+" + newBody);
				return newExchange;

			}
		})
		.log("multicast==========")
		.to("direct:log_request")
		//.log("Headers Java DSL: ${headers}")
		.to("direct:tapeArchiveGetCache", "direct:tapeArchiveGetChecksum")
		.end()
		.log("searchFile done****${body}*******")
		.to("direct:tapeArchiveOutput");


		from("direct:tapeArchiveGetCache")
		.process("{{tapeArchive_processor_bean}}")
		.setHeader(Exchange.HTTP_METHOD, constant("GET"))
		.to("direct:log_request")
		.to("jetty:http://{{tapeArchive_hostname}}:{{tapeArchive_port}}/{{tapeArchive_servicename}}?bridgeEndpoint=true&throwExceptionOnFailure=false")
		.log("get cache body...${body}...done");

		from("direct:tapeArchiveGetChecksum")
		//.process(new DefaultProcessor()) 
		.processRef("{{tapeArchive_processor_bean}}")

                .to("jetty:http://{{tapeArchive_hostname}}:{{tapeArchive_port}}/{{tapeArchive_fixityservice}}?bridgeEndpoint=true&throwExceptionOnFailure=false")
		.log("get checksum...${body}");


		
		from("direct:tapeArchiveListFile")
		.log("list file body------${body}");


		from("direct:tapeArchiveOutput")
		.log("output header...${headers}")
		.log("output body...:${body}")
		.process(new RestProcessor())
		.log("output body......${body}");

		from("direct:tapeArchivePostJob")
		.process(new Processor() {
                  @Override
                  public void process(Exchange exchange) throws Exception{

                         Job jobBean = new Job();
                         jobBean = exchange.getIn().getBody(Job.class);
                         String fileId = jobBean.getExternalUri();
                         //String fileId = exchange.getIn().getHeader("external_uri", String.class);
                         String type = jobBean.getType();
                         //String type = exchange.getIn().getHeader("action", String.class);
                         exchange.getIn().setBody("{\"cache_file_name\":\""+fileId+"\",\"type\":\""+type+"\"}",String.class);
                         exchange.getIn().setHeader(Exchange.HTTP_PATH, "/" + fileId);
                         exchange.getIn().setHeader(type, String.class);
                  }
                 })
		.log("forwarding head... ${header.type}")
		.log("forwarding body...${body}")
		.recipientList(simple("direct:tapeArchive_stage")); // TODO this has to conditionally handle fixity as an action

		from("direct:tapeArchive_stage")
		.log("forwarding stage head...${headers}")
		.inOnly("direct:tapeArchive_stagenoreturn");

		from("direct:tapeArchive_joboutput")
		.log("return response...done");

		from("direct:tapeArchive_stagenoreturn")
		.to("jetty:http://{{tapeArchive_hostname}}:{{tapeArchive_port}}/{{tapeArchive_servicename}}?bridgeEndpoint=true&throwExceptionOnFailure=true&httpMethodRestrict=POST")
		.log("finish stage!!!");

		from("direct:tapeArchive_unstage")
		.setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
		.to("jetty:http://{{tapeArchive_hostname}}:{{tapeArchive_port}}/{{tapeArchive_servicename}}?bridgeEndpoint=true&throwExceptionOnFailure=false&httpMethodRestrict=DELETE");

		from("direct:tapeArchive_fixity")
		.log("fixity header ... ${headers}")
		.wireTap("direct:tapeArchive_stagenoreturn")
		.wireTap("direct:tapeArchive_checksum");

		from("direct:tapeArchive_checksum")
		.to("jetty:http://{{tapeArchive_hostname}}:{{tapeArchive_port}}/{{tapeArchive_fixityservice}}?bridgeEndpoint=true&throwExceptionOnFailure=false&httpMethodRestrict=POST")
		.log("finish checksum!!!");

	}
}
