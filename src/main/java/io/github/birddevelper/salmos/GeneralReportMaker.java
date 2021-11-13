package io.github.birddevelper.salmos;

import io.github.birddevelper.salmos.db.JdbcQueryExcuter;
import io.github.birddevelper.salmos.setting.SummaryType;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class GeneralReportMaker extends ReportMaker {


    String templateBody ="";
    String templateHeader = "";
    String templateFooter = "";
    String itemSeparator ="";




    @Override
    public String generate() {
        return mixTemplateWithData();
    }

    @Override
    public File generateFile(String filePathName) throws IOException {
        File file = new File(filePathName);
        FileOutputStream outputStream = new FileOutputStream(file);
        String fileContent =  mixTemplateWithData();
        outputStream.write(fileContent.getBytes(Charset.forName("UTF-8")));
        return file;
    }

    public File generatePDF(String filePathName,String[] fonts, String baseUri, boolean printable, boolean interactable) throws IOException {
         Document doc = Jsoup.parse(mixTemplateWithData(),baseUri);
         doc.outputSettings().syntax(Document.OutputSettings.Syntax.html);
         File file = new File(filePathName);
         try(OutputStream outputStream = new FileOutputStream(file)){
             ITextRenderer renderer = new ITextRenderer();
             SharedContext sharedContext = renderer.getSharedContext();
             if(fonts!=null){
                 for(String font:fonts){
                     renderer.getFontResolver().addFont(font,true);
                 }
             }
             sharedContext.setPrint(printable);
             sharedContext.setInteractive(interactable);
             String fileContent =  mixTemplateWithData();
             renderer.setDocumentFromString(fileContent,baseUri);
             renderer.layout();
             renderer.createPDF(outputStream);
             return file;
         }
         catch(Exception e){
             System.out.println("generate PDF error : "+ e.getMessage());
             return null;

         }


    }

    public File generatePDF(String filePathName,String[] fonts, String baseUri) throws IOException {
        Document doc = Jsoup.parse(mixTemplateWithData(),baseUri);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.html);
        File file = new File(filePathName);
        try(OutputStream outputStream = new FileOutputStream(file)){
            ITextRenderer renderer = new ITextRenderer();
            SharedContext sharedContext = renderer.getSharedContext();
            if(fonts!=null){
                for(String font:fonts){
                    renderer.getFontResolver().addFont(font,true);
                }
            }
            sharedContext.setPrint(true);
            sharedContext.setInteractive(true);
            String fileContent =  mixTemplateWithData();
            renderer.setDocumentFromString(fileContent,baseUri);
            renderer.layout();
            renderer.createPDF(outputStream);
            return file;
        }
        catch(Exception e){
            System.out.println("generate PDF error : "+ e.getMessage());
            return null;
        }

    }



    public GeneralReportMaker(DataSource datasource) {
        summaryColumns = new HashMap<String, SummaryType>();
        jdbcQueryExcuter = new JdbcQueryExcuter(datasource);
    }

    public GeneralReportMaker(DataSource datasource, String sqlQuery, String templateBody) {
        jdbcQueryExcuter = new JdbcQueryExcuter(datasource);
        summaryColumns = new HashMap<String,SummaryType>();
        this.sqlQuery = sqlQuery;
        this.templateBody = templateBody;
    }

    public GeneralReportMaker(DataSource datasource, String sqlQuery, String templateBody, String templateHeader, String templateFooter) {
        jdbcQueryExcuter = new JdbcQueryExcuter(datasource);
        summaryColumns = new HashMap<String,SummaryType>();
        this.sqlQuery = sqlQuery;
        this.templateBody = templateBody;
    }

    public GeneralReportMaker(DataSource datasource, String sqlQuery, InputStream templateBodyInputStream) {
        jdbcQueryExcuter = new JdbcQueryExcuter(datasource);
        summaryColumns = new HashMap<String, SummaryType>();
        this.sqlQuery = sqlQuery;
        this.templateBody = getStringFromInputStream(templateBodyInputStream);

    }

    public GeneralReportMaker(DataSource datasource, String sqlQuery, InputStream templateBodyInputStream, InputStream templateHeaderInputStream, InputStream templateFooterInputStream  ) {
        jdbcQueryExcuter = new JdbcQueryExcuter(datasource);
        summaryColumns = new HashMap<String, SummaryType>();
        this.sqlQuery = sqlQuery;
        this.templateBody = getStringFromInputStream(templateBodyInputStream);
        this.templateHeader = getStringFromInputStream(templateHeaderInputStream);
        this.templateFooter = getStringFromInputStream(templateFooterInputStream);

    }

    public GeneralReportMaker(DataSource datasource, String sqlQuery, String templateFilePath, String itemSeparator) {
        jdbcQueryExcuter = new JdbcQueryExcuter(datasource);
        summaryColumns = new HashMap<String, SummaryType>();
        this.sqlQuery = sqlQuery;
        this.templateBody = getResourceFileAsString(templateFilePath);
        this.itemSeparator = itemSeparator;

    }

    public GeneralReportMaker(DataSource datasource, String sqlQuery, String templateBodyFilePath, String templateHeaderFilePath, String templateFooterFilePath, String itemSeparator) {
        jdbcQueryExcuter = new JdbcQueryExcuter(datasource);
        summaryColumns = new HashMap<String, SummaryType>();
        this.sqlQuery = sqlQuery;
        this.templateBody = getResourceFileAsString(templateBodyFilePath);
        this.templateHeader = getResourceFileAsString(templateHeaderFilePath);
        this.templateFooter = getResourceFileAsString(templateFooterFilePath);
        this.itemSeparator = itemSeparator;

    }



    public void loadTemplateBodyFromFile(String templateBodyFilePath){

        this.templateBody = getResourceFileAsString(templateBodyFilePath);
    }

    public void loadTemplateHeaderFromFile(String templateHeaderFilePath){

        this.templateHeader = getResourceFileAsString(templateHeaderFilePath);
    }

    public void loadTemplateFooterFromFile(String templateFooterFilePath){

        this.templateFooter = getResourceFileAsString(templateFooterFilePath);
    }



    private String mixTemplateWithData() {

        List<Map<String, Object>> rows = jdbcQueryExcuter.getResultList(this.sqlQuery);
        String output = "";
        int Index=0;
        boolean gotColumnName = false;
        int NumberOfcolumns = 0;
        String[] columnsNames = null;
        HashMap<String,Double> summaryValue = new HashMap<>();

        for(String column : summaryColumns.keySet())
            summaryValue.put(column,0.0);


        for(Map<String,Object> row:rows){
            String tempTemplate = this.templateBody;
            Index++;
            System.out.println("hi2");
            if(!gotColumnName) {
                Set<String> columns  =  row.keySet();
                Iterator<String> column = columns.iterator();
                NumberOfcolumns = columns.size() ;
                gotColumnName = true;
                columnsNames = new String[NumberOfcolumns];
                columns.toArray(columnsNames);
            }

            String Attributes = "";
            for(int i=0; i< NumberOfcolumns; i++ ){

                String ColumnName= columnsNames[i];
                Object RawData = row.get(ColumnName);
                String data = String.valueOf(RawData);
                tempTemplate = tempTemplate.replace("::"+ColumnName, data) ;

                if(summaryValue.containsKey(ColumnName) && RawData!= null){
                    switch(summaryColumns.get(ColumnName))
                    {
                        case SUM:
                            if(isNumeric(data.replace(",","")))
                                summaryValue.put(ColumnName, summaryValue.get(ColumnName)+ Double.parseDouble(data.replace(",","")));
                            break;
                        case AVERAGE:
                            if(isNumeric(data.replace(",","")))
                                summaryValue.put(ColumnName, (summaryValue.get(ColumnName) * (  Index -1 ) + Double.parseDouble(data.replace(",","")))/Index );
                            break;
                        case COUNT:
                            if( data!=null && data!="")
                                summaryValue.put(ColumnName, summaryValue.get(ColumnName)+ 1);
                            break;
                        default:
                            System.out.println("no match");
                    }


                }


            }

            output += tempTemplate + this.itemSeparator;


        }

        if(summaryColumns.size()>0) {

            for (String columnName : columnsNames) {
                if(summaryValue.containsKey(columnName)) {
                    this.templateHeader = this.templateHeader.replace("::" + columnName + "Summary", roundOff(summaryValue.get(columnName), this.summaryDecimalPrecision, this.summaryCommaSeperatedNumbers));
                    this.templateFooter = this.templateFooter.replace("::" + columnName + "Summary", roundOff(summaryValue.get(columnName), this.summaryDecimalPrecision, this.summaryCommaSeperatedNumbers));
                }
            }
        }

        return this.templateHeader + output+ this.templateFooter;
    }




    private String getStringFromInputStream(InputStream inputStream) {
        InputStream is = inputStream;
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return (String) reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } else {
            throw new RuntimeException("not valid stream");
        }
    }


    private String getResourceFileAsString(String fileName) {
        InputStream is = getResourceFileAsInputStream(fileName);
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return (String) reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } else {
            throw new RuntimeException("resource not found");
        }
    }

    private InputStream getResourceFileAsInputStream(String fileName) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }




}
