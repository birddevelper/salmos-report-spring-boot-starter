package io.github.birddevelper.salmos.setting;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HtmlReportTemplate {

    public HtmlReportTemplate() {
        this.rowIndexVisible = false;
        this.tableCssClass = "";
        this.rightToLeft = false;
        this.oddRowCssClass = "";
        this.evenRowCssClass = "";
        titleBarCssClass = "";
        this.headerRowCssClass = "";
        this.rowIndexHeader = "";
        this.footerRowCssClass = "";
    }

    public HtmlReportTemplate(boolean RowIndexVisible, String tableCssClass, boolean rightToLeft,
                              String oddRowCssClass, String evenRowCssClass, String titleBarCssClass, String headerRowCssClass,
                              String rowIndexHeader, String footerRowCssClass) {
        this.rowIndexVisible = RowIndexVisible;
        this.tableCssClass = tableCssClass;
        this.rightToLeft = rightToLeft;
        this.oddRowCssClass = oddRowCssClass;
        this.evenRowCssClass = evenRowCssClass;
        this.titleBarCssClass = titleBarCssClass;
        this.headerRowCssClass = headerRowCssClass;
        this.rowIndexHeader = rowIndexHeader;
        this.footerRowCssClass = footerRowCssClass;
    }

    private boolean rowIndexVisible;
    private List<String> summaryColumns;
    private String tableCssClass;
    private boolean rightToLeft;
    private String oddRowCssClass;
    private String evenRowCssClass;
    private String titleBarCssClass;
    private String headerRowCssClass;
    private String rowIndexHeader;
    private String footerRowCssClass;
}
