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
- Clone or copy this repo into the Karaf deploy directory
- Place configured external_storage.cfg in the Karaf etc directory.
  - May need to restart Karaf for a new .cfg to be detected.  
- `log:tail` and `camel:route-list` to check for successful deployment
