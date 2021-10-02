package io.github.birddevelper.salmos;

import io.github.birddevelper.salmos.db.JdbcQueryExcuter;
import io.github.birddevelper.salmos.setting.SummaryType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.regex.Pattern;

@Getter
@Setter
public abstract class ReportMaker {

    protected boolean summaryCommaSeperatedNumbers = false;
    protected int summaryDecimalPrecision = 0;
    protected String sqlQuery;

    @Getter(value= AccessLevel.NONE)
    @Setter(value= AccessLevel.NONE)
    protected HashMap<String, SummaryType> summaryColumns;

    @Getter(value= AccessLevel.NONE)
    @Setter(value= AccessLevel.NONE)
    protected JdbcQueryExcuter jdbcQueryExcuter;


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

}