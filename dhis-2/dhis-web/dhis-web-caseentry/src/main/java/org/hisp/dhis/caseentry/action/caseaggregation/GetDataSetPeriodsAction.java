/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

package org.hisp.dhis.caseentry.action.caseaggregation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;

import com.opensymphony.xwork2.Action;

public class GetDataSetPeriodsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private List<Period> periods;

    public List<Period> getPeriods()
    {
        return periods;
    }

    private List<String> periodNameList;

    public List<String> getPeriodNameList()
    {
        return periodNameList;
    }

    private SimpleDateFormat simpleDateFormat1;

    private SimpleDateFormat simpleDateFormat2;
	
	private PeriodType periodType;
            
    public PeriodType getPeriodType()
    {
        return periodType;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute()
        throws Exception
    {
        periodNameList = new ArrayList<String>();
        DataSet dSet;
        dSet = dataSetService.getDataSet( id );
        periodType = dSet.getPeriodType();

        periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( periodType ) );

        Iterator<Period> periodIterator = periods.iterator();
        while( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();
            
            if ( p1.getStartDate().compareTo( new Date() ) > 0 )
            {
                periodIterator.remove( );
            }
            
        }
        
        Collections.sort( periods, new PeriodComparator() );

        if ( periodType.getName().equalsIgnoreCase( "monthly" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "MMM-yyyy" );
            for ( Period p1 : periods )
            {
                //if ( p1.getStartDate().compareTo( new Date() ) <= 0 )
                    periodNameList.add( simpleDateFormat1.format( p1.getStartDate() ) );

            }

        }
        else if ( periodType.getName().equalsIgnoreCase( "quarterly" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "MMM" );
            simpleDateFormat2 = new SimpleDateFormat( "MMM-yyyy" );

            for ( Period p1 : periods )
            {
                //if ( p1.getStartDate().compareTo( new Date() ) <= 0 )
                {
                    String tempPeriodName = simpleDateFormat1.format( p1.getStartDate() ) + " - "
                        + simpleDateFormat2.format( p1.getEndDate() );
                    periodNameList.add( tempPeriodName );
                }
            }
        }
        /*
         * else if(periodType.getName().equalsIgnoreCase("yearly")) {
         * simpleDateFormat1 = new SimpleDateFormat( "yyyy" ); for(Period p1 :
         * periods) { periodNameList.add(
         * simpleDateFormat1.format(p1.getStartDate() ) ); } }
         */

        else if ( periodType.getName().equalsIgnoreCase( "yearly" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "yyyy" );
            int year;
            for ( Period p1 : periods )
            {
                //if ( p1.getStartDate().compareTo( new Date() ) <= 0 )
                {
                    year = Integer.parseInt( simpleDateFormat1.format( p1.getStartDate() ) ) + 1;
                    periodNameList.add( simpleDateFormat1.format( p1.getStartDate() ) + "-" + year );
                }
            }
        }
        else
        {
            simpleDateFormat1 = new SimpleDateFormat( "yyyy-mm-dd" );
            for ( Period p1 : periods )
            {
                //if ( p1.getStartDate().compareTo( new Date() ) <= 0 )
                {
                    String tempPeriodName = simpleDateFormat1.format( p1.getStartDate() ) + " - "
                        + simpleDateFormat1.format( p1.getEndDate() );
                    periodNameList.add( tempPeriodName );
                }
            }
        }

        return SUCCESS;
    }

}
