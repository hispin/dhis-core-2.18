package org.hisp.dhis.reports.md.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.comparator.PeriodComparator;
import org.hisp.dhis.reports.ReportType;

import com.opensymphony.xwork2.Action;

public class MDReportFormAction implements Action
{
    //--------------------------------------------------------------------------
    //  Dependencies
    //--------------------------------------------------------------------------
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
    this.periodService = periodService;
    }

    //--------------------------------------------------------------------------
    //Input/Output
    //--------------------------------------------------------------------------
    
    private List<Period> periods;
    
    public List<Period> getPeriods()
    {
        return periods;
    }

    private String reportTypeName;
    
    public String getReportTypeName()
    {
        return reportTypeName;
    }
    
    private String periodTypeName;
    
    public String getPeriodTypeName()
    {
        return periodTypeName;
    }
    
    //--------------------------------------------------------------------------
    //  Action Implementation
    //--------------------------------------------------------------------------
    
    private SimpleDateFormat simpleDateFormat;
    
    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }   
    
    public String execute() throws Exception
    {
        reportTypeName = ReportType.RT_MD_REPORT;
        periodTypeName = MonthlyPeriodType.NAME;
        
        //period information
        periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( new MonthlyPeriodType() ) );
       
        Iterator<Period> periodIterator = periods.iterator();
        while ( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();
            if ( p1.getStartDate().compareTo( new Date() ) > 0 )
            {
                periodIterator.remove();
            }
            
            
        }
        simpleDateFormat = new SimpleDateFormat( "MMM-yy" );
        Collections.sort( periods, new PeriodComparator() );
        return SUCCESS;
    }
    
    
}
