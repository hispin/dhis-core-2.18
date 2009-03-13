package org.hisp.dhis.reports.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;

public class ReportService 
{

    /* Dependencies */
    private PeriodStore periodStore;
    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }
    
    private DBConnection dbConnection;
    
    public void setDbConnection( DBConnection dbConnection )
    {
        this.dbConnection = dbConnection;
    }

    
    /* Services */    
    public List<Period> getMonthlyPeriods(Date start, Date end)
    {
        List<Period> periodList = new ArrayList<Period>(periodStore.getPeriodsBetweenDates( start, end ));
        PeriodType monthlyPeriodType = getPeriodTypeObject("monthly");
        
        List<Period> monthlyPeriodList = new ArrayList<Period>();
        Iterator it = periodList.iterator();
        while(it.hasNext())
        {
            Period period = (Period) it.next();
            if(period.getPeriodType().getId() == monthlyPeriodType.getId())
            {
                monthlyPeriodList.add( period );
            }
        }
        return monthlyPeriodList;
    }
    
    
    /*
     * Returns the Period Object of the given date
     * For ex:- if the month is 3, year is 2006 and periodType Object of type Monthly then
     * it returns the corresponding Period Object
     */
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
        System.out.println( lastDay.toString() );        
        Period newPeriod = new Period();
        newPeriod = periodStore.getPeriod( firstDay, lastDay, periodType );      
        return newPeriod;
    }
    
    
    /*
     * Returns the PeriodType Object based on the Period Type Name
     * For ex:- if we pass name as Monthly then it returns the PeriodType Object 
     * for Monthly PeriodType
     * If there is no such PeriodType returns null
     */
    public PeriodType getPeriodTypeObject(String periodTypeName)
    {        
        Collection periodTypes = periodStore.getAllPeriodTypes();
        PeriodType periodType = null;
        Iterator iter = periodTypes.iterator();
        while ( iter.hasNext() )
        {
            PeriodType tempPeriodType = (PeriodType) iter.next();
            if ( tempPeriodType.getName().toLowerCase().trim().equals( periodTypeName ) )
            {
                periodType = tempPeriodType;
                break;
            }
        }
        if ( periodType == null )
        {
            System.out.println( "No Such PeriodType" );
            return null;
        }        
        return periodType;
    }

    /*
     * Returns the child tree of the selected Orgunit
     */
    public List<OrganisationUnit> getAllChildren(OrganisationUnit selecteOU) {
        List<OrganisationUnit> ouList = new ArrayList<OrganisationUnit>();
        Iterator it = selecteOU.getChildren().iterator();
        while (it.hasNext()) {
                OrganisationUnit orgU = (OrganisationUnit) it.next();
                ouList.add(orgU);
        }
        return ouList;
    }
    
    public List<Integer> getLinelistingRecordNos(OrganisationUnit organisationUnit, Period period, String lltype )
    {
        List<Integer> recordNosList = new ArrayList<Integer>();
        
        Connection con = dbConnection.openConnection();

        Statement st = null;
        
        ResultSet rs1 = null;

        String query = "";
        
        int dataElementid = 1020;
        
        /*
        if( lltype.equalsIgnoreCase( "lllivebirth" ) )
            dataElementid = LLDataSets.LLB_CHILD_NAME;
        else if( lltype.equalsIgnoreCase( "lllivebirth" ) )
            dataElementid = LLDataSets.LLD_CHILD_NAME;
        else if( lltype.equalsIgnoreCase( "lllivebirth" ) )
            dataElementid = LLDataSets.LLMD_MOTHER_NAME;
        */

        if( lltype.equalsIgnoreCase( "lllivebirth" ) )
            dataElementid = 1020;
        else if( lltype.equalsIgnoreCase( "lldeath" ) )
            dataElementid = 1027;
        else if( lltype.equalsIgnoreCase( "llmaternaldeath" ) )
            dataElementid = 1032;

        try
        {
            st = con.createStatement();            
            
            query = "SELECT recordno FROM lldatavalue WHERE dataelementid = "+ dataElementid +" AND periodid = "+ period.getId() +" AND sourceid = "+organisationUnit.getId();
            rs1 = st.executeQuery( query );
            
            while(rs1.next())
            {
                recordNosList.add( rs1.getInt( 1 ) );
            }
            
            Collections.sort( recordNosList );
        }
        catch ( Exception e )
        {
            System.out.println("SQL Exception : "+e.getMessage());     
            return null;
        }
        finally
        {
            try
            {
                if(st != null) st.close();
                if(rs1 != null) rs1.close();
                
                if(con != null) con.close();
            }
            catch( Exception e )
            {
                System.out.println("SQL Exception : "+e.getMessage());
                return null;
            }
        }// finally block end

        return recordNosList;
    }
    

}
