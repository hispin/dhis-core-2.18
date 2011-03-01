package org.hisp.dhis.dataanalyser.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

public class DashBoardHomePageAction
    implements Action
{
    
    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private CurrentUserService currentUserService;
    
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    
    // ---------------------------------------------------------------
    // Input & Output
    // ---------------------------------------------------------------

    private String resultString;
    
    public String getResultString()
    {
        return resultString;
    }

 
    public String execute()
        throws Exception
    {
        statementManager.initialise();
        
        //clearCache();
        
        resultString = "";
        //getIndicatorValues();
        
        statementManager.destroy();
        
        return SUCCESS;
    }
    
    private void clearCache()
    {
        try
        {
            String cacheFolderPath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "db"+ File.separator + "output";
   
            File dir = new File( cacheFolderPath );
            String[] files = dir.list();        
            for ( String file : files )
            {
                file = cacheFolderPath + File.separator + file;
                File tempFile = new File(file);
                tempFile.delete();
            }
            //System.out.println("Cache cleared successfully");
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }        
    }

    @SuppressWarnings("unused")
    private void getIndicatorValues()
    {
        // OrgUnit Info
        User curUser = currentUserService.getCurrentUser();
        Collection<OrganisationUnit> ouList = curUser.getOrganisationUnits();
        OrganisationUnit orgUnit;
        if( ouList == null || ouList.isEmpty() )
        {
            ouList = organisationUnitService.getOrganisationUnitsAtLevel( 1 );
            if( ouList == null || ouList.isEmpty() )
            {
                System.out.println(" There are no OrgUnits ");
                resultString =  "There are no OrgUnits";
                return;
            }
            else
            {
                orgUnit = ouList.iterator().next();
            }
        }
        else
        {
            orgUnit = ouList.iterator().next();
        }
        
        //Indicator Info
        Collection<Indicator> indicatorList = indicatorService.getAllIndicators();
        
        if( indicatorList == null || indicatorList.isEmpty() )
        {
            System.out.println(" There are no Indicators ");
            resultString =  "There are no Indicators";
            return;
        }
        
        //Period Info        
        Date sysDate = new Date();
        Period selPeriod = getPreviousPeriod( sysDate );
        
        if( selPeriod == null )
        {
            System.out.println(" There are no Period ");
            resultString =  "There are no Period";
            return;            
        }
        
        for( Indicator ind : indicatorList )
        {
            double aggVal = aggregationService.getAggregatedIndicatorValue( ind, selPeriod.getStartDate(), selPeriod.getEndDate(), orgUnit );
            
            if(aggVal == -1) aggVal = 0.0;
            
            aggVal = Math.round( aggVal * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
            
            if( aggVal > 0 )  resultString += "** " + ind.getName() + " ( " + aggVal + " ) ";
        }           
        
        if(resultString.trim().equals( "" ))
            resultString = "NONE";
        
        //System.out.println("RESULT : "+ resultString );
        
    }
    
    public Period getPreviousPeriod(Date sDate)
    {
        Period period = new Period();
        Calendar tempDate = Calendar.getInstance();
        tempDate.setTime( sDate );
        if ( tempDate.get( Calendar.MONTH ) == Calendar.JANUARY )
        {
            tempDate.set( Calendar.MONTH, Calendar.DECEMBER );
            tempDate.roll( Calendar.YEAR, -1 );
        }
        else
        {
            tempDate.roll( Calendar.MONTH, -1 );
        }
        PeriodType monthlyPeriodType = new MonthlyPeriodType();
        period = getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ),
            monthlyPeriodType );

        return period;
    }

    public Period getPeriodByMonth( int month, int year, PeriodType periodType )
    {
        int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        Calendar cal = Calendar.getInstance();
        cal.set( year, month, 1, 0, 0, 0 );
        Date firstDay = new Date( cal.getTimeInMillis() );

        if ( periodType.getName().equals( "Monthly" ) )
        {
            cal.set( year, month, 1, 0, 0, 0 );
            if ( year % 4 == 0 )
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] + 1 );
            }
            else
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] );
            }
        }
        else if ( periodType.getName().equals( "Yearly" ) )
        {
            cal.set( year, Calendar.DECEMBER, 31 );
        }        
        Date lastDay = new Date( cal.getTimeInMillis() );
        //System.out.println( lastDay.toString() );        
        Period newPeriod = new Period();
        newPeriod = periodService.getPeriod( firstDay, lastDay, periodType );      
        return newPeriod;
    }
}
