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
public class HtmlReportMaker {


    JdbcQueryExcuter jdbcQueryExcuter;


    boolean rowIndexVisible;
    @Getter(value=AccessLevel.NONE)
    @Setter(value= AccessLevel.NONE)
    private HashMap<String, SummaryType> summaryColumns;
    String tableCssClass;
    boolean rightToLeft;
    String oddRowCssClass;
    String evenRowCssClass;
    String titleBarCssClass;
    String headerRowCssClass;
    String footerRowCssClass;
    String title;
    String rowIndexHeader;
    String sqlQuery;
    boolean summaryCommaSeperatedNumbers;
    int summaryDecimalPrecision = 0;


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

       return generateHTML(sqlQuery,rowIndexVisible, summaryColumns,  tableCssClass,  rightToLeft,  oddRowCssClass,  evenRowCssClass,
               titleBarCssClass,  headerRowCssClass, title,  rowIndexHeader, footerRowCssClass );

        }


    private String generateHTML(String sqlQuery , boolean rowIndexVisible, HashMap<String, SummaryType>  summaryColumns, String tableCssClass, boolean rightToLeft,
                                String oddRowCssClass, String evenRowCssClass, String titleBarCssClass, String headerRowCssClass, String title, String rowIndexHeader, String footerRowCssClass ){


        List<Map<String,Object>> rows =  jdbcQueryExcuter.getResultList(sqlQuery);

        String table="<table dir='"  + (rightToLeft ? "rtl" : "ltr" ) + "'  class='"+ tableCssClass +"' >";

        boolean gotColumnName = false;
        Integer NumberOfcolumns = 0;
        String headerofTable = (!title.equals("") ? "<tr><th colspan= ::colspan class='" + titleBarCssClass + "' > " + title + " </th></tr>" : "") + "<tr class='"+ headerRowCssClass + "'>" +   (rowIndexVisible ? "<th>" + rowIndexHeader + " </th>" : "" );
        String bodyOfTable = "";
        String footerOfTable = "";
        String[] columnsNames = null;
        HashMap<String,Double> summaryValue = new HashMap<>();
        int Index=0;
        for(String column : summaryColumns.keySet())
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
                    System.out.println(colName);
                    headerofTable+= String.format("<th > %s %s", colName ,"</th>") ;
                }

                headerofTable+="</tr>";
                gotColumnName = true;

                columnsNames = new String[NumberOfcolumns];
                columns.toArray(columnsNames);

                headerofTable = headerofTable.replace("::colspan",  String.valueOf(NumberOfcolumns + (rowIndexVisible ? 1:0 )) );
                table += headerofTable ;
            }

            /// Getting data and making row
            String singleRow ="<tr class='" + (Index % 2 == 0 ? evenRowCssClass : oddRowCssClass)  + "'>" + (rowIndexVisible ? "<td>"+ String.valueOf(Index) + "</td>": "");

            for(int i=0; i< NumberOfcolumns; i++ ){

                String ColumnName= columnsNames[i];
                Object RawData = row.get(ColumnName);
                String data = String.valueOf(RawData);
                String dataType = RawData.getClass().getSimpleName();
                singleRow += String.format("<td > %s %s", data ,"</td>") ;

                /// accumulate in summary
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

            bodyOfTable+= singleRow + "</tr>";



        }

        table+=bodyOfTable;

        //System.out.println("table " + table);

        //double decimalPrecision = 10 ^ this.summaryDecimalPrecision;

        if(summaryColumns.size()>0) {
            footerOfTable = "<tr class='"+ footerRowCssClass +"' >"+   (rowIndexVisible ? "<td> </td>" : "" );
            for (String column : columnsNames) {
                if(summaryValue.containsKey(column))
                    footerOfTable += "<th>" + roundOffTo2DecPlaces( summaryValue.get(column), this.summaryDecimalPrecision, this.summaryCommaSeperatedNumbers  ) + "</th>";
                else
                    footerOfTable += "<th> - </th>";
            }
        }
        footerOfTable += "</tr>";
        table+= footerOfTable + "</table>";

        return table;
    }


    private String roundOffTo2DecPlaces(double val, int decimalPlace, boolean summaryCommaSeperatedNumbers)
    {
        return String.format("%"+(summaryCommaSeperatedNumbers ? ",": "")+"." + decimalPlace + "f", val);
    }



    private boolean isNumeric(String strNum) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }


}
