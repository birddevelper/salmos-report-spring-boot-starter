package io.github.birddevelper.salmos;

import io.github.birddevelper.salmos.setting.HtmlReportTemplate;
import io.github.birddevelper.salmos.setting.SummaryType;
import io.github.birddevelper.salmos.db.JdbcQueryExcuter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;


@Getter
@Setter
@Accessors(fluent = false, chain = true)
/**
 *  A class that makes HTML table from records retrieved by given sql query.
 *
 * @param dataSource jdbc data source that usually is configured in application.properties and can be autowired anywhere in application.
 * @param sqlQuery SQL query to retrieve records from database
 * @param title Optional Table title that will be shown on top of table
 * @param rightToLeft Indicates the table direction, default value is false
 * @param rowIndexVisible Indicates whether the table have index column or not, default value is false
 * @param tableCssClass Optional table css class name
 * @param oddRowCssClass Optional odd rows class name
 */
public class HtmlReportMaker extends ReportMaker {

    boolean rowIndexVisible;
    boolean rightToLeft;
    String oddRowCssClass;
    String evenRowCssClass;
    String titleBarCssClass;
    String headerRowCssClass;
    String footerRowCssClass;
    String tableCssClass;

    String oddRowCssStyle;
    String evenRowCssStyle;
    String titleBarCssStyle;
    String headerRowCssStyle;
    String footerRowCssStyle;
    String tableCssStyle;

    String title;
    String rowIndexHeader;



    public HtmlReportMaker(ObjectFactory objectFactory) {
        summaryColumns = new HashMap<String, SummaryType>();
        this.objectFactory = objectFactory;
        this.rowIndexVisible = false;
        this.rightToLeft = false;
        this.tableCssClass = "";
        this.oddRowCssClass = "";
        this.evenRowCssClass = "";
        this.titleBarCssClass = "";
        this.headerRowCssClass = "";
        this.footerRowCssClass = "";
        this.tableCssStyle = "";
        this.oddRowCssStyle = "";
        this.evenRowCssStyle = "";
        this.titleBarCssStyle = "";
        this.headerRowCssStyle = "";
        this.footerRowCssStyle = "";
        this.rowIndexHeader = "";
        this.title="";
        this.summaryCommaSeperatedNumbers = false;
    }


    public HtmlReportMaker(DataSource  datasource) {
        summaryColumns = new HashMap<String, SummaryType>();
        jdbcQueryExcuter = new JdbcQueryExcuter(datasource);
        this.rowIndexVisible = false;
        this.rightToLeft = false;
        this.tableCssClass = "";
        this.oddRowCssClass = "";
        this.evenRowCssClass = "";
        this.titleBarCssClass = "";
        this.headerRowCssClass = "";
        this.rowIndexHeader = "";
        this.footerRowCssClass = "";
        this.tableCssStyle = "";
        this.oddRowCssStyle = "";
        this.evenRowCssStyle = "";
        this.titleBarCssStyle = "";
        this.headerRowCssStyle = "";
        this.footerRowCssStyle = "";



        this.title="";
        this.summaryCommaSeperatedNumbers = false;
    }

    public void setTemplate(HtmlReportTemplate template){
        this.rowIndexVisible = template.isRowIndexVisible();
        this.tableCssClass = template.getTableCssClass();
        this.rightToLeft = template.isRightToLeft();
        this.oddRowCssClass = template.getOddRowCssClass();
        this.evenRowCssClass = template.getEvenRowCssClass();
        this.titleBarCssClass = template.getTitleBarCssClass();
        this.headerRowCssClass = template.getHeaderRowCssClass();
        this.rowIndexHeader = template.getRowIndexHeader();
        this.footerRowCssClass = template.getFooterRowCssClass();

        // setting embedded style attribute
        this.tableCssStyle = template.getTableCssStyle();
        this.oddRowCssStyle = template.getOddRowCssStyle();
        this.evenRowCssStyle = template.getEvenRowCssStyle();
        this.titleBarCssStyle = template.getTitleBarCssStyle();
        this.headerRowCssStyle = template.getHeaderRowCssStyle();
        this.footerRowCssStyle = template.getFooterRowCssStyle();


    }


    public void addSummaryColumn(String columnName, SummaryType summaryType){
        this.summaryColumns.put(columnName,summaryType);
    }

    public void removeSummaryColumn(String columnName){
        this.summaryColumns.remove(columnName);
    }




    public String generate(){
            return generateHTML();
        }

    @Override
    public File generateFile(String filePathName) throws IOException {
        File file = new File(filePathName);
        FileOutputStream outputStream = new FileOutputStream(file);
        String fileContent =  generateHTML();
        outputStream.write(fileContent.getBytes(Charset.forName("UTF-8")));
        return file;
    }

    public File generatePDF(String filePathName,String[] fonts, String baseUri, boolean printable, boolean interactable) throws IOException {
        Document doc = Jsoup.parse(generateHTML(),baseUri);
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
            String fileContent =  doc.html();
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
        Document doc = Jsoup.parse(generateHTML(),baseUri);
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
            String fileContent =  doc.html();
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



    private String generateHTML(){
        List<Map<String,Object>> rows=null;

        if(this.objectFactory!=null){
                if(objectFactory.getListOfObjects()!=null )
                    rows = objectFactory.getListOfObjects();
                else
                    //Throw exception if object factory is passed to generator and object list is not loaded.
                    throw new IllegalArgumentException("Please load objects list in objectFactory before making report");
        }
        else if(this.sqlQuery!=null && this.sqlQuery.length()>0) {
            //working with query
            rows = jdbcQueryExcuter.getResultList(this.sqlQuery);
        }
        else {
            //if neither Objects List Nor Sql query is given to report generator, Throw exception
            throw new IllegalArgumentException("Neither Objects List Nor Sql query is given to report maker.");
        }




        String table="<table dir='"  + (this.rightToLeft ? "rtl" : "ltr" ) + "'  class='"+ this.tableCssClass +"'   style=\""+ this.tableCssStyle + "\">";
        boolean gotColumnName = false;
        Integer NumberOfcolumns = 0;
        String headerofTable = (!this.title.equals("") ? "<tr><th colspan= ::colspan class='" + this.titleBarCssClass + "'  style=\"" + this.titleBarCssStyle + "\"  > " + this.title + " </th></tr>" : "") + "<tr class='"+ this.headerRowCssClass + "'  style=\""+ this.headerRowCssStyle+ "\" >" +   (this.rowIndexVisible ? "<th>" + this.rowIndexHeader + " </th>" : "" );
        String bodyOfTable = "";
        String footerOfTable = "";
        String[] columnsNames = null;
        HashMap<String,Double> summaryValue = new HashMap<>();
        int Index=0;
        for(String column : this.summaryColumns.keySet())
            summaryValue.put(column,0.0);


        for(Map<String,Object> row:rows){
            Index++;



            /// Get the Columns name from first row
            if(!gotColumnName) {
                Set<String> columns  =  row.keySet();
                Iterator<String> column = columns.iterator();
                NumberOfcolumns = columns.size() ;

                while (column.hasNext()) {

                    String colName = column.next();
                    //System.out.println(colName);
                    headerofTable+= String.format("<th > %s %s", colName ,"</th>") ;
                }

                headerofTable+="</tr>";
                gotColumnName = true;

                columnsNames = new String[NumberOfcolumns];
                columns.toArray(columnsNames);

                headerofTable = headerofTable.replace("::colspan",  String.valueOf(NumberOfcolumns + (this.rowIndexVisible ? 1:0 )) );
                table += headerofTable ;
            }

            /// Getting data and making row
            String singleRow ="<tr class='" + (Index % 2 == 0 ? this.evenRowCssClass : this.oddRowCssClass)  + "'  style=\"" + (Index % 2 == 0 ? this.evenRowCssStyle : this.oddRowCssStyle)  + "\"   >" + (this.rowIndexVisible ? "<td>"+ String.valueOf(Index) + "</td>": "");

            for(int i=0; i< NumberOfcolumns; i++ ){

                String ColumnName= columnsNames[i];
                Object RawData = row.get(ColumnName);
                String data = String.valueOf(RawData);
                String dataType = RawData.getClass().getSimpleName();
                singleRow += String.format("<td > %s %s", data ,"</td>") ;

                /// accumulate in summary
                if(summaryValue.containsKey(ColumnName) && RawData!= null){


                    switch(this.summaryColumns.get(ColumnName))
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

            bodyOfTable+= singleRow + "</tr>";


        }

        table+=bodyOfTable;



        if(this.summaryColumns.size()>0) {
            footerOfTable = "<tr class='"+ this.footerRowCssClass +"'  style=\""+ this.footerRowCssStyle +"\"  >"+   (this.rowIndexVisible ? "<td> </td>" : "" );
            for (String column : columnsNames) {
                if(summaryValue.containsKey(column))
                    footerOfTable += "<th>" + roundOff( summaryValue.get(column), this.summaryDecimalPrecision, this.summaryCommaSeperatedNumbers  ) + "</th>";
                else
                    footerOfTable += "<th> - </th>";
            }
        }
        footerOfTable += "</tr>";
        table+= footerOfTable + "</table>";

        return table;
    }




}
