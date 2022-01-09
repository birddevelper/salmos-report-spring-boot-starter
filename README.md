What is SalmosReport
======
[![Maven Central](https://img.shields.io/maven-central/v/io.github.birddevelper/salmos-report-spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.birddevelper%22%20AND%20a:%22salmos-report-spring-boot-starter%22)

SalmosReport is a spring boot library that helps to make beautiful html table, xml document, pdf document or any other format you wish from SQL query or Arrays and Lists. Until now, it can produce Html, xml, pdf document and custom format report .

![Salmos Report](https://raw.githubusercontent.com/birddevelper/salmos-report-spring-boot-starter/master/salmosReport_SQL_Hibernate.jpg)


### Features

* Read data from List of Objects and Entities
* Read data from database by sql query
* Aggregation functions (Sum, Average, Count)
* Generate HTML Report
* Generate XML document
* Generate PDF document
* Generate custom structure report
* Generate TEXT file from report
* Css Classes for HTML report
* Embedded Css Style for HTML report

### Getting started


Current Version of the  plugin is 2.1.0

Gradle :
```shell
implementation 'io.github.birddevelper:salmos-report-spring-boot-starter:2.1.0'
```


Maven :
```shell
<dependency>
  <groupId>io.github.birddevelper</groupId>
  <artifactId>salmos-report-spring-boot-starter</artifactId>
  <version>2.1.0</version>
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

        
        return grm.generate();  // return String containing the produced report


    }
}
```

### Generate PDF report 
```java
import io.github.birddevelper.salmos.HtmlReportMaker;
import io.github.birddevelper.salmos.setting.HtmlReportTemplate;
import io.github.birddevelper.salmos.setting.SummaryType;


@Service
public class ReportService {

    @Autowired
    DataSource dataSource;

    public File generatePDFReport() {

        // Creating instance of HtmlReportMaker
        HtmlReportMaker hrm = new HtmlReportMaker(dataSource);
        // specify columns of data that must be summarized in table footer row
        hrm.addSummaryColumn("Age", SummaryType.AVERAGE);
        hrm.addSummaryColumn("Salary", SummaryType.SUM);
        
        
        //  summary section numbers decimal point setting
        hrm.setSummaryDecimalPrecision(1);
        
        //  summary section numbers seperated by comma 
        hrm.setSummaryCommaSeperatedNumbers(true);
        
        // show  row's index
        hrm.isRowIndexVisible(true);
        
        //sql query to retrieve data rows
        String sql = "select fullname as 'Name', age as 'Age', salary as 'Salary'   from chamber limit 0,10";
        // set the query retrieving data from database
        hrm.setSqlQuery(sql);
        String[] fonts = {"fonts/ArialBold.ttf", "fonts/MyOtherFont.ttf"}; // path to fonts that you want embed in pdf document 
        String baseUri = "the base uri";
        
        return hrm.generatePDF("D:/mypdf.pdf",fonts,baseUri);

      

    }
}
```




#### Generate from list of objects ( for example : hibernate output )

```java
import io.github.birddevelper.salmos.XmlReportMaker;
import io.github.birddevelper.salmos.setting.SummaryType;
import io.github.birddevelper.salmos.setting.XmlReportElementType;
import lombok.Getter;
import lombok.Setter;
@Service
public class ReportService {

    @Getter
    @Setter
    public class Student {
        private String name;
        private int age;
        private Date birthDate;
        private List<String> skills;
        
    }
    
    @AutoWired
    StudentRepository studentRepository;

    public String generateHTMLReport() {

        
        List<Student> studentList = studentRepository.findAll();

        
        // Mapping the class fields to report columns (here we want only name and age, the reset of entity fields will be ignored)
        Map<String,String> fieldMap = new HashMap<>();
        fieldMap.put("name", "Full Name");
        fieldMap.put("age", "Age");
        
        // buidling instance of ObjectFactory class
        ObjectFactory objectFactory = new ObjectFactory();
        
        // setting mapping fields
        objectFactory.setReportFields(fieldMap);
        // setting entity lists
        objectFactory.loadObjects(studentList);
        
        // building instance of HtmlReportMaker with ObjectFactory as input parameter
        HtmlReportMaker htmlReportMaker = new HtmlReportMaker(objectFactory);
        
        // generate report
        return htmlReportMaker.generate();
    }
}
```






[Read More at Medium.com ](https://medium.com/javarevisited/with-salmos-report-in-spring-boot-generate-reports-in-few-lines-of-code-b5212486b921)



### Change Logs :

2.1.0 :
- Embedded css style attribute for HTML report

2.0.0 :
- Generate reports from list of objects (for example: list of entities retrieved by hibernate)

1.2.0 :
- Export to PDF and TEXT files added. 

1.1.0 :

- GeneralReportMaker class added.
- Bugs fixed.

1.0.0 : 
- First release.
- Generate Reports from Sql Query
- Generate HTML and XML reports



Project Contributors : [Mostafa Shaeri](https://m-shaeri.ir/blog/with-salmos-report-generate-reports-in-any-format-you-need/)
