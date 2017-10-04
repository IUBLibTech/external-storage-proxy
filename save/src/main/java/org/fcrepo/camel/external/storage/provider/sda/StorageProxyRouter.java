package org.fcrepo.camel.external.storage.provider.sda;

import static org.slf4j.LoggerFactory.getLogger;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.apache.camel.Exchange;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Processor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.apache.camel.ExchangePattern;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class StorageProxyRouter extends RouteBuilder {

	private static final Logger LOGGER = getLogger(StorageProxyRouter.class);
	//private String fileId;
	//private String restEndpoint;

	public void configure() throws Exception {


		from("direct:searchFile")
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
		.to("direct:getCache", "direct:getChecksum")
		.end()
		.log("searchFile done****${body}*******")
		.to("direct:output");


		from("direct:getCache")
		.process("{{sda_processor_bean}}")
                .to("jetty:http://{{sda_hostname}}:{{sda_port}}/{{sda_servicename}}?bridgeEndpoint=true&amp;throwExceptionOnFailure=false")
		.log("get cache body...${body}...done");

		from("direct:getChecksum")
		//.process(new DefaultProcessor()) 
		.processRef("{{sda_processor_bean}}")

                .to("jetty:http://{{sda_hostname}}:{{sda_port}}/{{sda_fixityservice}}?bridgeEndpoint=true&amp;throwExceptionOnFailure=false")
		.log("get checksum...${body}");


		
		from("direct:listFile")
		.log("list file body------${body}");


		from("direct:output")
		.log("output header...${headers}")
		.log("output body...:${body}")
		.process(new RestProcessor())
		.marshal().json(JsonLibrary.Jackson)
		.log("output body......${body}");

		from("direct:postJob")
		.process(new Processor() {
                  @Override
                  public void process(Exchange exchange) throws Exception{

                         JobBeanParam myBean = new JobBeanParam();
                         myBean = exchange.getIn().getBody(JobBeanParam.class);
                         String fileId = myBean.getFileId();
			 String type = myBean.getType();
			 exchange.getIn().setBody("{\"cache_file_name\":\""+fileId+"\",\"type\":\""+type+"\"}",String.class);
			 exchange.getIn().setHeader(Exchange.HTTP_PATH, "/" + fileId);
                         exchange.getIn().setHeader(type, String.class);
                  }
                 })
		.log("forwarding head... ${header.type}")
		.log("forwarding body...${body}")
		.recipientList(simple("direct:${header.type}"));

		from("direct:stage")
		.log("forwarding stage head...${headers}")
		.inOnly("direct:stagenoreturn")
		.to("direct:joboutput");

		from("direct:joboutput")
		.process(new JobProcessor())
		.marshal().json(JsonLibrary.Jackson)
		.log("return response...done");

		from("direct:stagenoreturn")
		.to("jetty:http://{{sda_hostname}}:{{sda_port}}/{{sda_servicename}}?bridgeEndpoint=true&amp;throwExceptionOnFailure=false&httpMethodRestrict=POST")
		.log("finish stage!!!");

		from("direct:unstage")
		.setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
		.to("jetty:http://{{sda_hostname}}:{{sda_port}}/{{sda_servicename}}?bridgeEndpoint=true&amp;throwExceptionOnFailure=false&httpMethodRestrict=DELETE")
		.to("direct:joboutput");

		from("direct:fixity")
		.log("fixity header ... ${headers}")
		.wireTap("direct:stagenoreturn")
		.wireTap("direct:checksum")
		.to("direct:joboutput");

		from("direct:checksum")
		.to("jetty:http://{{sda_hostname}}:{{sda_port}}/{{sda_fixityservice}}?bridgeEndpoint=true&amp;throwExceptionOnFailure=false&httpMethodRestrict=POST")
		.log("finish checksum!!!");

	}
}
