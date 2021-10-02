What is SalmosReport
======
[![Maven Central](https://img.shields.io/maven-central/v/io.github.birddevelper/salmos-report-spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.birddevelper%22%20AND%20a:%22salmos-report-spring-boot-starter%22)

SalmosReport is a spring boot plugin hat helps you turn your SQL query into a beautiful html table,xml document or any other format you wish with very specific features.

### Getting started


Current Version of the this plugin is 1.0.0

Gradle :
```shell
implementation 'io.github.birddevelper:salmos-report-spring-boot-starter:1.0.0'
```


Maven :
```shell
<dependency>
  <groupId>io.github.birddevelper</groupId>
  <artifactId>salmos-report-spring-boot-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Usage

There exist 3 classes in this plugin to make amazing things for you.

* HtmlReportMaker : A class that generates HTML table from records retrieved by given sql query.
* XmlReportMaker : A class that generates XML document from records retrieved by given sql query.
* GeneralReportMaker : This class can generate output in any structure and format.
