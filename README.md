## External Storage Proxy using Camel

### Installation

- Download and extract Apache Karaf Container 4.1+
  - http://karaf.apache.org/
- Start Karaf `bin/karaf`
- Install camel features (update repo version if needed)
  - `feature:repo-add camel 2.19.2`
  - `feature:install camel-blueprint`
  - `feature:install camel-jetty`
  - `feature:install camel-jackson`
  - `feature:install camel-http4`
  - `feature:install camel-mustache`
  - `feature:install camel-aws`
  - `feature:install camel-cxf`
- Clone this git repo somewhere.
- Generate a JAR with `mvn package`    
- Copy into the Karaf `deploy` directory:
  - `OSGI-INF/blueprint/external-storage-proxy.xml`
  - The generated .jar from the `target` directory 
- Place configured `external_storage.cfg` in the Karaf `etc` directory.
  - May need to restart Karaf for new files to be detected.  
- `log:tail` and `camel:route-list` to check for successful deployment.
- `bundle:diag` to troubleshoot missing dependencies.
