package org.hisp.dhis.analytics.event.data;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.period.Cal;
import org.hisp.dhis.program.Program;

/**
 * @author Lars Helge Overland
 */
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
