package org.hisp.dhis.analytics.event.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.period.Cal;
import org.hisp.dhis.program.Program;

public class EventQueryPlanner
{
    private static final String TABLE_BASE_NAME = "analytics_event_";
    
    public static List<EventQueryParams> planQuery( EventQueryParams params )
    {
        return splitByPartition( params );
    }
    
    private static List<EventQueryParams> splitByPartition( EventQueryParams params )
    {
        List<EventQueryParams> list = new ArrayList<EventQueryParams>();
        
        Program program = params.getProgram();
        
        Date startDate = params.getStartDate();
        Date endDate = params.getEndDate();
        
        Date currentStartDate = startDate;
        Date currentEndDate = endDate;
        
        while ( true )
        {
            if ( year( currentStartDate ) < year( endDate ) ) // Spans multiple
            {
                // Set end date to max of current year
                
                currentEndDate = maxOfYear( currentStartDate ); 
                
                list.add( getQuery( params, currentStartDate, currentEndDate, program ) );
                
                // Set start date to start of next year
                
                currentStartDate = new Cal( ( year( currentStartDate ) + 1 ), 1, 1 ).time();                 
            }
            else
            {
                list.add( getQuery( params, currentStartDate, endDate, program ) );
                
                break;
            }
        }
        
        return list;
    }
    
    private static EventQueryParams getQuery( EventQueryParams params, Date startDate, Date endDate, Program program )
    {
        EventQueryParams query = new EventQueryParams( params );
        query.setStartDate( startDate );
        query.setEndDate( endDate );
        query.setTableName( TABLE_BASE_NAME + year( startDate ) + "_" + program.getUid() );
        return query;
    }
    
    private static int year( Date date )
    {
        return new Cal( date ).getYear();
    }
    
    private static Date maxOfYear( Date date )
    {
        return new Cal( year( date ), 12, 31 ).time();
    }
}
