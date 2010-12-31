package org.hisp.dhis.reports.ouwiseprogress.action;

import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.reports.ReportType;

import com.opensymphony.xwork2.Action;

public class GenerateOuWiseProgressReportFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    private String periodTypeName;
    
    public String getPeriodTypeName()
    {
        return periodTypeName;
    }

    private String reportTypeName;

    public String getReportTypeName()
    {
        return reportTypeName;
    }  

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        reportTypeName = ReportType.RT_ORGUNITWISEPROGRESS;
        
        periodTypeName = MonthlyPeriodType.NAME;
        
        return SUCCESS;
    }
}
