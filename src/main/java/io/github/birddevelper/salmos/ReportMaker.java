package io.github.birddevelper.salmos;

import io.github.birddevelper.salmos.db.JdbcQueryExcuter;
import io.github.birddevelper.salmos.setting.SummaryType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;

@Getter
@Setter
public abstract class ReportMaker {

    protected boolean summaryCommaSeperatedNumbers = false;
    protected int summaryDecimalPrecision = 0;

    @Setter(value= AccessLevel.NONE)
    protected String sqlQuery;

    protected ObjectFactory objectFactory;


    @Getter(value= AccessLevel.NONE)
    @Setter(value= AccessLevel.NONE)
    protected HashMap<String, SummaryType> summaryColumns;

    @Getter(value= AccessLevel.NONE)
    @Setter(value= AccessLevel.NONE)
    protected JdbcQueryExcuter jdbcQueryExcuter;


    public void setSqlQuery(String sqlQuery){
        if(objectFactory!=null)
            throw new IllegalStateException("You can not set sql query when you built your report maker with ObjectFactory.");
        this.sqlQuery = sqlQuery;
    }

    public void addSummaryColumn(String columnName, SummaryType summaryType){
        this.summaryColumns.put(columnName,summaryType);
    }

    public void removeSummaryColumn(String columnName){
        this.summaryColumns.remove(columnName);
    }

    protected String roundOff(double val, int decimalPlace, boolean summaryCommaSeperatedNumbers)
    {
        return String.format("%"+(summaryCommaSeperatedNumbers ? ",": "")+"." + decimalPlace + "f", val);
    }



    protected boolean isNumeric(String strNum) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    abstract public String generate();

    abstract public File generateFile(String filePathName) throws IOException;

}
