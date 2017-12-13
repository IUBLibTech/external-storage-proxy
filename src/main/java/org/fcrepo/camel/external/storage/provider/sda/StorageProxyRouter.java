package org.fcrepo.camel.external.storage.provider.sda;

import static org.slf4j.LoggerFactory.getLogger;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.fcrepo.camel.external.storage.model.Job;
import org.slf4j.Logger;


public class StorageProxyRouter extends RouteBuilder {

	private static final Logger LOGGER = getLogger(StorageProxyRouter.class);
	//private String fileId;
	//private String restEndpoint;

	public void configure() throws Exception {


		from("direct:sdaSearchFile")
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
		.to("direct:sdaGetCache", "direct:sdaGetChecksum")
		.end()
		.log("searchFile done****${body}*******")
		.to("direct:sdaOutput");


		from("direct:sdaGetCache")
		.process("{{sda_processor_bean}}")
		.setHeader(Exchange.HTTP_METHOD, constant("GET"))
		.to("direct:log_request")
		.to("jetty:http://{{sda_hostname}}:{{sda_port}}/{{sda_servicename}}?bridgeEndpoint=true&amp;throwExceptionOnFailure=false")
		.log("get cache body...${body}...done");

		from("direct:sdaGetChecksum")
		//.process(new DefaultProcessor()) 
		.processRef("{{sda_processor_bean}}")

                .to("jetty:http://{{sda_hostname}}:{{sda_port}}/{{sda_fixityservice}}?bridgeEndpoint=true&amp;throwExceptionOnFailure=false")
		.log("get checksum...${body}");


		
		from("direct:sdaListFile")
		.log("list file body------${body}");


		from("direct:sdaOutput")
		.log("output header...${headers}")
		.log("output body...:${body}")
		.process(new RestProcessor())
		.log("output body......${body}");

		from("direct:sdaPostJob")
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
		.recipientList(simple("direct:sda_stage"));

		from("direct:sda_stage")
		.log("forwarding stage head...${headers}")
		.inOnly("direct:sda_stagenoreturn");
		//.to("direct:sda_joboutput");

		from("direct:sda_joboutput")
		.process(new JobProcessor())
		.log("return response...done");

		from("direct:sda_stagenoreturn")
		.to("jetty:http://{{sda_hostname}}:{{sda_port}}/{{sda_servicename}}?bridgeEndpoint=true&throwExceptionOnFailure=true&httpMethodRestrict=POST")
		.log("finish stage!!!");

		from("direct:sda_unstage")
		.setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
		.to("jetty:http://{{sda_hostname}}:{{sda_port}}/{{sda_servicename}}?bridgeEndpoint=true&throwExceptionOnFailure=false&httpMethodRestrict=DELETE")
		.to("direct:sda_joboutput");

		from("direct:sda_fixity")
		.log("fixity header ... ${headers}")
		.wireTap("direct:sda_stagenoreturn")
		.wireTap("direct:sda_checksum")
		.to("direct:sda_joboutput");

		from("direct:sda_checksum")
		.to("jetty:http://{{sda_hostname}}:{{sda_port}}/{{sda_fixityservice}}?bridgeEndpoint=true&amp;throwExceptionOnFailure=false&httpMethodRestrict=POST")
		.log("finish checksum!!!");

	}
}
