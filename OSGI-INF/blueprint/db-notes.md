## Karaf console

```bash
feature:repo-add camel 2.19.2
feature:install camel
feature:install camel-sql
bundle:install -s mvn:commons-pool/commons-pool/1.6
bundle:install -s mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.commons-dbcp/1.4_3
feature:install pax-jdbc-mysql
feature:install camel-jetty
```
