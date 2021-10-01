package io.github.birddevelper.salmos;

import io.github.birddevelper.salmos.setting.HtmlReportTemplate;
import io.github.birddevelper.salmos.setting.SummaryType;
import io.github.birddevelper.salmos.db.JdbcQueryExcuter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.util.*;
import java.util.regex.Pattern;

@Getter
@Setter
/**
 *  A class that makes HTML table from records fetched by given sql query.
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
    String tableCssClass;
    boolean rightToLeft;
    String oddRowCssClass;
    String evenRowCssClass;
    String titleBarCssClass;
    String headerRowCssClass;
    String footerRowCssClass;
    String title;
    String rowIndexHeader;



    public HtmlReportMaker(DataSource  datasource) {
        summaryColumns = new HashMap<String, SummaryType>();
        jdbcQueryExcuter = new JdbcQueryExcuter(datasource);
        this.rowIndexVisible = false;
        this.tableCssClass = "";
        this.rightToLeft = false;
        this.oddRowCssClass = "";
        this.evenRowCssClass = "";
        this.titleBarCssClass = "";
        this.headerRowCssClass = "";
        this.rowIndexHeader = "";
        this.footerRowCssClass = "";
        this.title="";
        this.summaryCommaSeperatedNumbers = false;
        //jdbcQueryExcuter = new JDBCQueryExcuter();
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


    private String generateHTML(){


        List<Map<String,Object>> rows =  jdbcQueryExcuter.getResultList(this.sqlQuery);

        String table="<table dir='"  + (this.rightToLeft ? "rtl" : "ltr" ) + "'  class='"+ this.tableCssClass +"' >";

        boolean gotColumnName = false;
        Integer NumberOfcolumns = 0;
        String headerofTable = (!this.title.equals("") ? "<tr><th colspan= ::colspan class='" + this.titleBarCssClass + "' > " + this.title + " </th></tr>" : "") + "<tr class='"+ this.headerRowCssClass + "'>" +   (this.rowIndexVisible ? "<th>" + this.rowIndexHeader + " </th>" : "" );
        String bodyOfTable = "";
        String footerOfTable = "";
        String[] columnsNames = null;
        HashMap<String,Double> summaryValue = new HashMap<>();
        int Index=0;
        for(String column : this.summaryColumns.keySet())
            summaryValue.put(column,0.0);



        for(Map<String,Object> row:rows){
            Index++;

            //String tableRow = "<tr>" +   (rowIndex ? "<th> </th>" : "" );

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
            String singleRow ="<tr class='" + (Index % 2 == 0 ? this.evenRowCssClass : this.oddRowCssClass)  + "'>" + (this.rowIndexVisible ? "<td>"+ String.valueOf(Index) + "</td>": "");

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

        //System.out.println("table " + table);

        //double decimalPrecision = 10 ^ this.summaryDecimalPrecision;

        if(this.summaryColumns.size()>0) {
            footerOfTable = "<tr class='"+ this.footerRowCssClass +"' >"+   (this.rowIndexVisible ? "<td> </td>" : "" );
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


/*    private String roundOff(double val, int decimalPlace, boolean summaryCommaSeperatedNumbers)
    {
        return String.format("%"+(summaryCommaSeperatedNumbers ? ",": "")+"." + decimalPlace + "f", val);
    }



    private boolean isNumeric(String strNum) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }*/


}
