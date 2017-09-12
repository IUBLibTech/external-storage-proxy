== External Storage Proxy in Camel/Ruby

=== Installation

- Download and extract Apache Karaf 4.1+
- Start Karaf `bin/karaf`
- Install camel features (update repo version if needed)
  - `feature:repo-add camel 2.19.2`
  - `feature:install camel-blueprint`
  - `feature:install camel-script-jruby`
- Copy this directory into the Karaf deploy directory
- `log:tail` and `camel:route-list` to check for successful deployment
