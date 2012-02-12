package org.hisp.dhis.reporting.datamart.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.scheduling.DataMartTask;
import org.hisp.dhis.system.scheduling.Scheduler;
import org.hisp.dhis.system.util.DateUtils;

import com.opensymphony.xwork2.Action;

public class StartExportAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private Scheduler scheduler;
    
    public void setScheduler( Scheduler scheduler )
    {
        this.scheduler = scheduler;
    }

    private DataMartTask dataMartTask;

    public void setDataMartTask( DataMartTask dataMartTask )
    {
        this.dataMartTask = dataMartTask;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Set<String> periodType = new HashSet<String>();
    
    public void setPeriodType( Set<String> periodType )
    {
        this.periodType = periodType;
    }

    private String startDate;
    
    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        Date start = DateUtils.getMediumDate( startDate );
        Date end = DateUtils.getMediumDate( endDate );
        
        List<Period> periods = new ArrayList<Period>();
        
        for ( String type : periodType )
        {
            CalendarPeriodType periodType = (CalendarPeriodType) PeriodType.getPeriodTypeByName( type );
            
            periods.addAll( periodType.generatePeriods( start, end ) );
        }
        
        if ( periods.size() > 0 )
        {
            dataMartTask.setPeriods( periods );
        
            scheduler.executeTask( dataMartTask );
        }
        
        return SUCCESS;
    }
}
