What is SalmosReport
======
[![Maven Central](https://img.shields.io/maven-central/v/io.github.birddevelper/salmos-report-spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.birddevelper%22%20AND%20a:%22salmos-report-spring-boot-starter%22)

SalmosReport is a spring boot plugin that helps you turn your SQL query result into a beautiful html table, xml document or any other format you wish with very specific features. Until now, it can produce Html, xml and custom format report (just as string, not file ).

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
        // specify columns of data that must be summarized in table footer row
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




#### XmlReportMaker Sample Code

```java
import io.github.birddevelper.salmos.XmlReportMaker;
import io.github.birddevelper.salmos.setting.SummaryType;
import io.github.birddevelper.salmos.setting.XmlReportElementType;
@Service
public class ReportService {

    @Autowired
    DataSource dataSource;

    public String generateXMLReport() {
        // Creating instance of XmlReportMaker
        XmlReportMaker xrm = new XmlReportMaker(dataSource);

        // specify columns of data that must be summarized (they will put in root element as attribute) 
        xrm.addSummaryColumn("Age", SummaryType.AVERAGE);
        xrm.addSummaryColumn("Salary", SummaryType.SUM);

        // summary section numbers decimal point setting
        xrm.setSummaryDecimalPrecision(0);
        
        xrm.setRootElementName("Persons");
        xrm.setChildElementName("person");
        
        // this line set the generator to put row data in attributes of row element
        xrm.setXmlReportElementType(XmlReportElementType.RecordColumnAsElementAttribute);
        
        // set the query retrieving data from database
        xrm.setSqlQuery("select fullname as 'Name', age as 'Age', salary as 'Salary'   from chamber limit 0,10");
        
        return xrm.generate();
    }
}
```






#### GeneralReportMaker Sample code

```java
import org.log.carvan.utils.GeneralReportMaker;
import io.github.birddevelper.salmos.setting.SummaryType;

@Service
public class ReportService {

    @Autowired
    DataSource dataSource;

    public String generateUniversalReport() {

        GeneralReportMaker grm = new GeneralReportMaker(dataSource);
        // load template from file located in resources 
        grm.loadTemplateBodyFromFile("templates/salmosTemplates/template1.html");

        // set the query retrieving data from database
        grm.setSqlQuery("select fullname as 'Name', age as 'Age', salary as 'Salary'   from chamber limit 0,10");

        // specify columns of data that must be summarized 
        grm.addSummaryColumn("Age", SummaryType.AVERAGE);
        grm.addSummaryColumn("Salary", SummaryType.SUM);

        // set footer template with String (to have a column summary in footer, you should use ::[column name]Summary in template 
        grm.setTemplateFooter("<p ><b> CityCount >> ::AgeSummary ---- Capacity Average >> ::SalarySummary </b> </p>");

        
        return grm.generate();


    }
}
```

[Read More at Medium.com ](https://medium.com/javarevisited/with-salmos-report-in-spring-boot-generate-reports-in-few-lines-of-code-b5212486b921)



### Change Logs :

1.2.0 :
- Export to PDF and TEXT files added. 

1.1.0 :

- GeneralReportMaker class added.
- Bugs fixed.

1.0.0 : 
- First release.



Project Contributors : [Mostafa Shaeri](https://m-shaeri.ir/blog/with-salmos-report-generate-reports-in-any-format-you-need/)
