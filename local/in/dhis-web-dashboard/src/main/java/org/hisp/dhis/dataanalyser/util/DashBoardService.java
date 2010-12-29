package org.hisp.dhis.dataanalyser.util;

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

import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;

public class DashBoardService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private ReportService reportservice ;
    
    public void setReportservice( ReportService reportservice )
      {
          this.reportservice = reportservice;
      }

    /*
    private DBConnection dbConnection;

    public void setDbConnection( DBConnection dbConnection )
    {
        this.dbConnection = dbConnection;
    }
*/

    public List<Period> getMonthlyPeriods( Date start, Date end )
    {
        PeriodType monthlyPeriodType = PeriodType.getByNameIgnoreCase( "monthly" );

        List<Period> monthlyPeriodList = new ArrayList<Period>();
        for ( Period period : monthlyPeriodList )
        {
            if ( period.getPeriodType().getId() == monthlyPeriodType.getId() )
            {
                monthlyPeriodList.add( period );
            }
        }
        return monthlyPeriodList;
    }

    /*
     * public List<Period> getMonthlyPeriods(Date start, Date end) { PeriodType
     * monthlyPeriodType = getPeriodTypeObject("monthly"); Calendar cal =
     * Calendar.getInstance(); cal.setTime(start); Calendar cal1 =
     * Calendar.getInstance(); cal1.setTime(end); boolean januaryIsFirst =
     * false; if (cal.get(Calendar.MONTH) == Calendar.JANUARY) { januaryIsFirst
     * = true; } List<Period> periods = new ArrayList<Period>(); while
     * (cal.get(Calendar.MONTH) != cal1.get(Calendar.MONTH)) { Period period =
     * getPeriodByMonth(cal.get(Calendar.MONTH), cal .get(Calendar.YEAR),
     * monthlyPeriodType); if (period != null) { periods.add(period); }
     * cal.roll(Calendar.MONTH, true); if (!januaryIsFirst &&
     * cal.get(Calendar.MONTH) == 0) { cal.roll(Calendar.YEAR, true); } }
     * periods.add(getPeriodByMonth(cal1.get(Calendar.MONTH), cal1
     * .get(Calendar.YEAR), monthlyPeriodType)); return periods; }
     */

    /*
     * Returns the Period Object of the given date For ex:- if the month is 3,
     * year is 2006 and periodType Object of type Monthly then it returns the
     * corresponding Period Object
     */
    public Period getPeriodByMonth( int month, int year, PeriodType periodType )
    {
        int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        Calendar cal = Calendar.getInstance();
        cal.set( year, month, 1, 0, 0, 0 );
        Date firstDay = new Date( cal.getTimeInMillis() );

        if ( periodType.getName().equalsIgnoreCase( "Monthly" ) )
        {
            cal.set( year, month, 1, 0, 0, 0 );
            if ( year % 4 == 0 && month == 1 )
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] + 1 );
            }
            else
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] );
            }
        }
        else if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
        {
            cal.set( year, Calendar.DECEMBER, 31 );
        }

        Date lastDay = new Date( cal.getTimeInMillis() );

        Period newPeriod = periodService.getPeriod( firstDay, lastDay, periodType );

        return newPeriod;
    }

    /*
    public String getRootDataPath()
    {
        // Connection con = (new DBConnection()).openConnection();
        Connection con = dbConnection.openConnection();

        Statement st = null;

        ResultSet rs1 = null;

        String rootDataPath = null;

        String query = "";

        try
        {
            st = con.createStatement();

            query = "SELECT mvalue FROM maintenancein WHERE mkey LIKE '" + MaintenanceIN.KEY_ROOTDATAPATH + "'";
            rs1 = st.executeQuery( query );

            if ( rs1.next() )
            {
                rootDataPath = rs1.getString( 1 );
            }
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
            return null;
        }
        finally
        {
            try
            {
                if ( st != null )
                    st.close();
                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {
                System.out.println( "SQL Exception : " + e.getMessage() );
                return null;
            }
        }// finally block end

        return rootDataPath;
    }

    public String getMYSqlPath()
    {
        // Connection con = (new DBConnection()).openConnection();
        Connection con = dbConnection.openConnection();

        Statement st = null;

        ResultSet rs1 = null;

        String mysqlPath = null;

        String query = "";

        try
        {
            st = con.createStatement();

            query = "SELECT mvalue FROM maintenancein WHERE mkey LIKE '" + MaintenanceIN.KEY_MYSQLPATH + "'";
            rs1 = st.executeQuery( query );

            if ( rs1.next() )
            {
                mysqlPath = rs1.getString( 1 );
            }

        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
            return null;
        }
        finally
        {
            try
            {
                if ( st != null )
                    st.close();
                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {
                System.out.println( "SQL Exception : " + e.getMessage() );
                return null;
            }
        }// finally block end

        return mysqlPath;

    }

    public void setUserdefinedConfigurations( String dbPath, String dataPath )
    {
        // Connection con = (new DBConnection()).openConnection();
        Connection con = dbConnection.openConnection();

        Statement st = null;
        Statement st1 = null;
        Statement st2 = null;

        Statement st3 = null;
        Statement st4 = null;

        ResultSet rs1 = null;
        ResultSet rs2 = null;

        String mysqlKey = null;
        String mysqlPath = null;

        String rootDataKey = null;
        String rootDataPath = null;

        String query = "";

        try
        {
            st = con.createStatement();
            st1 = con.createStatement();

            query = "CREATE TABLE IF NOT EXISTS maintenancein ( mkey varchar(100) NOT NULL, mvalue varchar(400) default NULL, PRIMARY KEY  (mkey)  ) ENGINE=InnoDB DEFAULT CHARSET=utf8";
            st.executeUpdate( query );
            System.out.println( "Table Created (if not exists) " );

            // MYSQL PATH
            query = "SELECT mkey, mvalue FROM maintenancein WHERE mkey LIKE '" + MaintenanceIN.KEY_MYSQLPATH + "'";
            rs1 = st1.executeQuery( query );

            if ( rs1.next() )
            {
                mysqlKey = rs1.getString( 1 );
                mysqlPath = rs1.getString( 2 );
            }

            if ( mysqlKey == null )
            {
                mysqlKey = MaintenanceIN.KEY_MYSQLPATH;
                mysqlPath = dbPath;

                st2 = con.createStatement();
                query = "INSERT INTO maintenancein VALUES ('" + mysqlKey + "','" + mysqlPath + "')";

                st2.executeUpdate( query );
                System.out.println( " MySQL UserDefined Path added" );
            }
            else
            {
                mysqlPath = dbPath;

                st2 = con.createStatement();
                query = "UPDATE maintenancein SET mvalue = '" + mysqlPath + "' WHERE mkey LIKE '"
                    + MaintenanceIN.KEY_MYSQLPATH + "'";

                st2.executeUpdate( query );
                System.out.println( " MySQL UserDefined Path updated" );
            }

            // DATA PATH
            st3 = con.createStatement();

            query = "SELECT mkey, mvalue FROM maintenancein WHERE mkey LIKE '" + MaintenanceIN.KEY_ROOTDATAPATH + "'";
            rs2 = st3.executeQuery( query );

            if ( rs2.next() )
            {
                rootDataKey = rs2.getString( 1 );
                rootDataPath = rs2.getString( 2 );
            }

            if ( rootDataKey == null )
            {
                rootDataKey = MaintenanceIN.KEY_ROOTDATAPATH;
                rootDataPath = dataPath;

                st4 = con.createStatement();
                query = "INSERT INTO maintenancein VALUES ('" + rootDataKey + "','" + rootDataPath + "')";

                st4.executeUpdate( query );
                System.out.println( " RootData UserDefined Path added" );
            }
            else
            {
                rootDataPath = dataPath;

                st4 = con.createStatement();
                query = "UPDATE maintenancein SET mvalue = '" + rootDataPath + "' WHERE mkey LIKE '"
                    + MaintenanceIN.KEY_ROOTDATAPATH + "'";

                st4.executeUpdate( query );
                System.out.println( " RootData UserDefined Path updated" );
            }
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
        }
        finally
        {
            try
            {
                if ( st != null )
                    st.close();
                if ( st1 != null )
                    st1.close();
                if ( st2 != null )
                    st2.close();

                if ( st3 != null )
                    st3.close();
                if ( st4 != null )
                    st4.close();

                if ( rs1 != null )
                    rs1.close();
                if ( rs2 != null )
                    rs2.close();

                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {
                System.out.println( "SQL Exception : " + e.getMessage() );
            }
        }// finally block end
    }

    public String setMYSqlDefaultPath( String path )
    {
        // Connection con = (new DBConnection()).openConnection();
        Connection con = dbConnection.openConnection();

        Statement st = null;
        Statement st1 = null;
        Statement st2 = null;

        ResultSet rs1 = null;

        String mysqlKey = null;
        String mysqlPath = null;

        String query = "";

        try
        {
            st = con.createStatement();
            st1 = con.createStatement();

            query = "CREATE TABLE IF NOT EXISTS maintenancein ( mkey varchar(100) NOT NULL, mvalue varchar(400) default NULL, PRIMARY KEY  (mkey)  ) ENGINE=InnoDB DEFAULT CHARSET=utf8";
            st.executeUpdate( query );
            System.out.println( "Table Created (if not exists) " );

            query = "SELECT mkey, mvalue FROM maintenancein WHERE mkey LIKE '" + MaintenanceIN.KEY_MYSQLPATH + "'";
            rs1 = st1.executeQuery( query );

            if ( rs1.next() )
            {
                mysqlKey = rs1.getString( 1 );
                mysqlPath = rs1.getString( 2 );
            }

            if ( mysqlKey == null )
            {
                mysqlKey = MaintenanceIN.KEY_MYSQLPATH;
                mysqlPath = path;

                st2 = con.createStatement();
                query = "INSERT INTO maintenancein VALUES ('" + mysqlKey + "','" + mysqlPath + "')";

                st2.executeUpdate( query );
                System.out.println( " MySQL Default Path added" );
            }
            else if ( mysqlPath == null || mysqlPath.trim().equals( "" ) )
            {
                mysqlPath = path;

                st2 = con.createStatement();
                query = "UPDATE maintenancein SET mvalue = '" + mysqlPath + "' WHERE mkey LIKE '"
                    + MaintenanceIN.KEY_MYSQLPATH + "'";

                st2.executeUpdate( query );
                System.out.println( " MySQL Default Path updated" );
            }
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
            return null;
        }
        finally
        {
            try
            {
                if ( st != null )
                    st.close();
                if ( st1 != null )
                    st1.close();
                if ( st2 != null )
                    st2.close();
                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {
                System.out.println( "SQL Exception : " + e.getMessage() );
                return null;
            }
        }// finally block end

        return mysqlPath;
    }
*/
    public String createDataTable( String orgUnitInfo, String deInfo, String periodInfo, Connection con )
    {
        // Connection con = (new DBConnection()).openConnection();
        // Connection con = dbConnection.openConnection();

        Statement st1 = null;
        Statement st2 = null;

        String dataTableName = "data" + UUID.randomUUID().toString();
        dataTableName = dataTableName.replaceAll( "-", "" );

        String query = "DROP TABLE IF EXISTS " + dataTableName;

        try
        {
            st1 = con.createStatement();
            st2 = con.createStatement();

            st1.executeUpdate( query );

            System.out.println( "Table " + dataTableName + " dropped Successfully (if exists) " );

            query = "CREATE table " + dataTableName + " AS "
                + " SELECT sourceid,dataelementid,periodid,value FROM datavalue " + " WHERE dataelementid in ("
                + deInfo + ") AND " + " sourceid in (" + orgUnitInfo + ") AND " + " periodid in (" + periodInfo + ")";

            st2.executeUpdate( query );

            System.out.println( "Table " + dataTableName + " created Successfully" );
        } // try block end
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
            return null;
        }
        finally
        {
            try
            {
                if ( st1 != null )
                    st1.close();
                if ( st2 != null )
                    st2.close();
                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {
                System.out.println( "SQL Exception : " + e.getMessage() );
                return null;
            }
        }// finally block end

        return dataTableName;
    }

    /*
    public String createDataTableForComments( String orgUnitInfo, String deInfo, String periodInfo )
    {
        // Connection con = (new DBConnection()).openConnection();
        Connection con = dbConnection.openConnection();

        Statement st1 = null;
        Statement st2 = null;

        String dataTableName = "data" + UUID.randomUUID().toString();
        dataTableName = dataTableName.replaceAll( "-", "" );

        String query = "DROP TABLE IF EXISTS " + dataTableName;

        try
        {
            st1 = con.createStatement();
            st2 = con.createStatement();

            st1.executeUpdate( query );

            System.out.println( "TABLE NAME : " + dataTableName );
            System.out.println( "Table dropped Successfully (if exists) " );

            query = "CREATE table " + dataTableName + " AS "
                + " SELECT sourceid,dataelementid,periodid,value,comment FROM datavalue " + " WHERE dataelementid in ("
                + deInfo + ") AND " + " sourceid in (" + orgUnitInfo + ") AND " + " periodid in (" + periodInfo
                + ") AND " + " commnet IS NOT NULL";

            st2.executeUpdate( query );

            System.out.println( "Table created Successfully" );
        } // try block end
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
            return null;
        }
        finally
        {
            try
            {
                if ( st1 != null )
                    st1.close();
                if ( st2 != null )
                    st2.close();
                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {
                System.out.println( "SQL Exception : " + e.getMessage() );
                return null;
            }
        }// finally block end

        return dataTableName;
    }

    public void deleteDataTable( String dataTableName )
    {
        // Connection con = (new DBConnection()).openConnection();
        Connection con = dbConnection.openConnection();

        Statement st1 = null;

        String query = "DROP TABLE IF EXISTS " + dataTableName;

        try
        {
            st1 = con.createStatement();
            st1.executeUpdate( query );
            System.out.println( "Table " + dataTableName + " dropped Successfully" );
        } // try block end
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
        }
        finally
        {
            try
            {
                if ( st1 != null )
                    st1.close();
                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {
                System.out.println( "SQL Exception : " + e.getMessage() );
            }
        }// finally block end
    }
*/
    public List<String> getPeriodNamesByPeriodType( PeriodType periodType, Collection<Period> periods )
    {
        SimpleDateFormat simpleDateFormat1;

        SimpleDateFormat simpleDateFormat2;

        List<String> periodNameList = new ArrayList<String>();

        if ( periodType.getName().equalsIgnoreCase( "monthly" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "MMM-yyyy" );
            for ( Period p1 : periods )
            {
                periodNameList.add( simpleDateFormat1.format( p1.getStartDate() ) );
            }
        }
        else if ( periodType.getName().equalsIgnoreCase( "quarterly" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "MMM" );
            simpleDateFormat2 = new SimpleDateFormat( "MMM-yyyy" );

            for ( Period p1 : periods )
            {
                String tempPeriodName = simpleDateFormat1.format( p1.getStartDate() ) + " - "
                    + simpleDateFormat2.format( p1.getEndDate() );
                periodNameList.add( tempPeriodName );
            }
        }
        else if ( periodType.getName().equalsIgnoreCase( "yearly" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "yyyy" );
            int year;
            for ( Period p1 : periods )
            {
                year = Integer.parseInt( simpleDateFormat1.format( p1.getStartDate() ) ) + 1;
                periodNameList.add( simpleDateFormat1.format( p1.getStartDate() ) + "-" + year );
            }
        }
        else if( periodType.getName().equalsIgnoreCase( "daily" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "yyyy-MM-dd" );
            for ( Period p1 : periods )
            {
                String tempPeriodName = simpleDateFormat1.format( p1.getStartDate() );
                //String tempPeriodName = ""+p1.getStartDate();   
                periodNameList.add( tempPeriodName );
            }
        }
        else
        {
            simpleDateFormat1 = new SimpleDateFormat( "yyyy-MM-dd" );
            for ( Period p1 : periods )
            {
                String tempPeriodName = simpleDateFormat1.format( p1.getStartDate() ) + " - "
                    + simpleDateFormat1.format( p1.getEndDate() );
                periodNameList.add( tempPeriodName );
            }
        }

        return periodNameList;
    }
    
    public double getIndividualIndicatorValue( Indicator indicator, OrganisationUnit orgunit, Date startDate, Date endDate ) 
    {

        String numeratorExp = indicator.getNumerator();
        String denominatorExp = indicator.getDenominator();
        int indicatorFactor = indicator.getIndicatorType().getFactor();
        String reportModelTB = "";
        String numeratorVal = reportservice.getIndividualResultDataValue( numeratorExp, startDate, endDate, orgunit, reportModelTB  );
        String denominatorVal = reportservice.getIndividualResultDataValue( denominatorExp, startDate, endDate, orgunit, reportModelTB );

        double numeratorValue;
        try
        {
            numeratorValue = Double.parseDouble( numeratorVal );
        } 
        catch ( Exception e )
        {
            numeratorValue = 0.0;
        }

        double denominatorValue;
        try
        {
            denominatorValue = Double.parseDouble( denominatorVal );
        } 
        catch ( Exception e )
        {
            denominatorValue = 1.0;
        }

        double aggregatedValue;
        try
        {
            //aggregatedValue = ( numeratorValue / denominatorValue ) * indicatorFactor;
            if( denominatorValue == 0 )
            {
                aggregatedValue = 0.0;
            }
            else
            {
                aggregatedValue = ( numeratorValue / denominatorValue ) * indicatorFactor;
            }
        } 
        catch ( Exception e )
        {
            System.out.println( "Exception while calculating Indicator value for Indicaotr " + indicator.getName() );
            aggregatedValue = 0.0;
        }
        
        return aggregatedValue;
    }

} // class end
