## Versions

Apache Maven 3.3.9
Karaf 4.1.2
Camel 2.19.2
MySQL 14.14

## Setup

### MySQL
- Install MySQL
- Log into mysql console
- Create database
```
create database external_storage_proxy;
```

- Use database (if not already set))
```
use external_storage_proxy;
```

- Create tables
```
create table jobs (
ID int NOT NULL AUTO_INCREMENT PRIMARY KEY,
EXTERNAL_URI VARCHAR(255) NOT NULL,
FEDORA_URI VARCHAR(255) NOT NULL,
STAGED int(2) UNSIGNED ,
STAGED_LOCATION varchar(255),
SERVICE varchar(47)
);

create table events (
ID int NOT NULL AUTO_INCREMENT PRIMARY KEY,
TYPE varchar(47) NOT NULL,
ACTION varchar(47) NOT NULL,
VENDOR_MESSAGE text,
EVENT_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
JOB integer REFERENCES jobs
);
```

### Karaf
### Mock S3

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
