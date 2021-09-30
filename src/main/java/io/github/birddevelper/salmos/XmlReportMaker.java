package io.github.birddevelper.salmos;

import io.github.birddevelper.salmos.setting.SummaryType;
import io.github.birddevelper.salmos.setting.XmlReportElementType;
import io.github.birddevelper.salmos.db.JdbcQueryExcuter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Configurable;

import javax.sql.DataSource;
import java.util.*;
import java.util.regex.Pattern;

@Getter
@Setter
@Configurable
public class XmlReportMaker extends ReportMaker{


    //JdbcQueryExcuter jdbcQueryExcuter;
    //String sqlQuery;
    //boolean summaryCommaSeperatedNumbers;
    //int summaryDecimalPrecision = 0;
    String rootElementName = "root";
    String childElementName = "child";
    XmlReportElementType xmlReportElementType = XmlReportElementType.RecordColumnAsElementChild;
    String newLine = "\n";

    //@Getter(value= AccessLevel.NONE)
    //@Setter(value= AccessLevel.NONE)
    //private HashMap<String, SummaryType> summaryColumns;

    public XmlReportMaker(DataSource  datasource) {
        jdbcQueryExcuter = new JdbcQueryExcuter(datasource);
        summaryColumns = new HashMap<String, SummaryType>();

    }





    public String generate(){
        if(xmlReportElementType == XmlReportElementType.RecordColumnAsElementChild)
                return generateXMLRecordColumnAsElementChild( );
        else
                return generateXMLRecordColumnAsElementAttribute( );
        }






    private String generateXMLRecordColumnAsElementAttribute( ){


        List<Map<String,Object>> rows =  jdbcQueryExcuter.getResultList(this.sqlQuery);

        String xmlRoot = "<"+ rootElementName +"  ::sumAttr >" + this.newLine ;

        String bodyXml = "";

        boolean gotColumnName = false;
        String[] columnsNames = null;
        int NumberOfcolumns = 0;
        HashMap<String,Double> summaryValue = new HashMap<>();

        int Index=0;
        for(String column : summaryColumns.keySet())
            summaryValue.put(column,0.0);



        for(Map<String,Object> row:rows){
            Index++;

            /// Getting the Columns name from first row
            String childElement ="\t<"+ childElementName + " ::childAttr />"+this.newLine ;
            /// Getting data and making row
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

                Attributes += String.format("%s = \"%s\" ", ColumnName, data) ;

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

            childElement = childElement.replace("::childAttr",Attributes);

            bodyXml+= childElement;










        }
        String sumAttr="";
        if(summaryColumns.size()>0) {

            for (String column : columnsNames) {
                if(summaryValue.containsKey(column))
                    sumAttr +=  column + "=\""  + roundOff( summaryValue.get(column), this.summaryDecimalPrecision, this.summaryCommaSeperatedNumbers  ) + "\"  ";

            }
        }
        xmlRoot = xmlRoot.replace("::sumAttr",sumAttr);
        xmlRoot+= bodyXml;
        xmlRoot += "</"+ rootElementName +">" + this.newLine ;





        return xmlRoot;
    }







    private String generateXMLRecordColumnAsElementChild( ){


        List<Map<String,Object>> rows =  jdbcQueryExcuter.getResultList(this.sqlQuery);

        String xmlRoot = "<"+ rootElementName +"  ::sumAttr >" + this.newLine ;

        String bodyXml = "";

        boolean gotColumnName = false;
        String[] columnsNames = null;
        int NumberOfcolumns = 0;
        HashMap<String,Double> summaryValue = new HashMap<>();

        int Index=0;
        for(String column : summaryColumns.keySet())
            summaryValue.put(column,0.0);



        for(Map<String,Object> row:rows){
            Index++;

            /// Getting the Columns name from first row
            String childElement ="\t<"+ childElementName + " >"+this.newLine ;
            /// Getting data and making row
            if(!gotColumnName) {
                Set<String> columns  =  row.keySet();
                Iterator<String> column = columns.iterator();
                NumberOfcolumns = columns.size() ;
                gotColumnName = true;
                columnsNames = new String[NumberOfcolumns];
                columns.toArray(columnsNames);
            }

            for(int i=0; i< NumberOfcolumns; i++ ){

                String ColumnName= columnsNames[i];
                Object RawData = row.get(ColumnName);
                String data = String.valueOf(RawData);

                childElement += String.format("\t\t<%s>%s</%s>%s", ColumnName, data, ColumnName, this.newLine) ;

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

            childElement +="\t</"+ childElementName + ">" + this.newLine ;

            bodyXml+= childElement;










        }

        String sumAttr="";
        if(summaryColumns.size()>0) {

            for (String column : columnsNames) {
                if(summaryValue.containsKey(column))
                    sumAttr +=  column + "=\""  + roundOff( summaryValue.get(column), this.summaryDecimalPrecision, this.summaryCommaSeperatedNumbers  ) + "\"  ";

            }
        }
        xmlRoot = xmlRoot.replace("::sumAttr",sumAttr);
        xmlRoot+= bodyXml;
        xmlRoot += "</"+ rootElementName +">" + this.newLine ;


        return xmlRoot;
    }





}
