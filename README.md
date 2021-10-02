What is SalmosReport
======
[![Maven Central](https://img.shields.io/maven-central/v/io.github.birddevelper/salmos-report-spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.birddevelper%22%20AND%20a:%22salmos-report-spring-boot-starter%22)

SalmosReport is a spring boot plugin that helps you turn your SQL query into a beautiful html table, xml document or any other format you wish with very specific features. Until now, it can produce Html, xml and custom format report (just as string, not file ).

### Getting started


Current Version of the this plugin is 1.1.0

Gradle :
```shell
implementation 'io.github.birddevelper:salmos-report-spring-boot-starter:1.1.0'
```


Maven :
```shell
<dependency>
  <groupId>io.github.birddevelper</groupId>
  <artifactId>salmos-report-spring-boot-starter</artifactId>
  <version>1.1.0</version>
</dependency>
```

### Usage

There exist 3 classes in this plugin to make amazing things for you.

* HtmlReportMaker : A class that generates HTML table from records retrieved by given sql query.
* XmlReportMaker : A class that generates XML document from records retrieved by given sql query.
* GeneralReportMaker : This class generates output in any given structure and format.

#### HtmlReportMaker Sample code

```java
import io.github.birddevelper.salmos.HtmlReportMaker;
import io.github.birddevelper.salmos.setting.HtmlReportTemplate;
import io.github.birddevelper.salmos.setting.SummaryType;

@Service
public class ReportService {

    @Autowired
    DataSource dataSource;

    public String generateReport() {

        // Creating instance of HtmlReportMaker
        HtmlReportMaker hrm = new HtmlReportMaker(dataSource);
        // specify which columns of data must be summarized in table footer row
        hrm.addSummaryColumn("Age", SummaryType.AVERAGE);
        hrm.addSummaryColumn("Salary", SummaryType.SUM);
        
        // template specifies the report table appearance
        HtmlReportTemplate myTemplate = new HtmlReportTemplate();
        myTemplate.setTableCssClass("tblReport");
        myTemplate.setEvenRowCssClass("myEvensRow");
        myTemplate.setOddRowCssClass("myOddsRow");
        myTemplate.setHeaderRowCssClass("myheader");
        myTemplate.setRightToLeft(true);
        myTemplate.setRowIndexHeader("#");
        myTemplate.setRowIndexVisible(true);
        
        hrm.setTemplate(myTemplate);
        
        //  summary section numbers decimal point setting
        hrm.setSummaryDecimalPrecision(1);
        
        //  summary section numbers seperated by comma 
        hrm.setSummaryCommaSeperatedNumbers(true);
        
        // show  row's index
        hrm.isRowIndexVisible(true);
        
        // set the query retrieving data from database
        hrm.setSqlQuery("select fullname as 'Name', age as 'Age', salary as 'Salary'   from chamber limit 0,10");
        
        return hrm.generate();

      

    }
}
```