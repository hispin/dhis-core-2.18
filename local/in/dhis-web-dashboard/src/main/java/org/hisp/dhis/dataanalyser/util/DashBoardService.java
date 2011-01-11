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

import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;

public class DashBoardService
{
    private final String OPTIONCOMBO = "optioncombo";
    
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
    
    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
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

    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With Children Wise start ( this method is called when view by -> periodWise and group not selected )
    // -------------------------------------------------------------------------
        
 
    public DataElementChartResult generateDataElementChartDataWithChildrenWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,String  periodTypeLB ,List<DataElement> dataElementList, String deSelection, List<DataElementCategoryOptionCombo> decocList, OrganisationUnit selectedOrgUnit , String aggDataCB ) throws Exception
    {
       System.out.println( "inside Dashboard Service generateChartDataWithChildrenWise " );
        
       DataElementChartResult dataElementChartResult;
       
       List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
       childOrgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren());
       
       
       String[] series = new String[dataElementList.size()];
       String[] categories = new String[childOrgUnitList.size()];
       Double[][] data = new Double[dataElementList.size()][childOrgUnitList.size()];
       String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName();
       
      // String chartTitle = "OrganisationUnit : " + orgUnit.getShortName();
       String xAxis_Title = "Facilities";
       String yAxis_Title = "Value";
    
      // System.out.println("\n\n +++ \n decoc : " + decocList);
       
       int serviceCount = 0;     
     
       for( DataElement dataElement : dataElementList )
       {
           DataElementCategoryOptionCombo decoc;
          
           if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
           
          // if( dataElement.isMultiDimensional() )               
           {
               decoc = decocList.get( serviceCount );
                   
               series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
               //yseriesList.add( dataElement.getName() + " : " + decoc.getName() );
           }
           else
           {
               decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
               series[serviceCount] = dataElement.getName();
               
               //yseriesList.add( dataElement.getName() );
           }
           
           int childCount = 0;
           for( OrganisationUnit orgChild : childOrgUnitList )
           {
               categories[childCount] = orgChild.getName();
               
               Double aggDataValue = 0.0;

               int periodCount = 0;
               for( Date startDate : selStartPeriodList )
               {
                   Date endDate = selEndPeriodList.get( periodCount );
                   PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                   //Collection<Period> periods = periodService.getPeriodsBetweenDates( periodType, startDate, endDate );
                   Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
                   
                   System.out.println( periods.size() + ":" + periodType + ":" + startDate + ":" +  endDate );
                   
                  // for( Period period : periods )
                  // {
                      // System.out.println( dataElement + ":" + decoc + ":" +period.getStartDate() + ":" +  period.getEndDate()+ ":" + orgChild + ":" + aggDataCB );
                       
                      // if( aggDataCB != null )
                       int aggChecked = Integer.parseInt( aggDataCB );
                   
                       if( aggChecked == 1 )
                       {
                           //System.out.println( "inside aggDataCB check  : " );
                           Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc, startDate, endDate, orgChild );
                           //System.out.println( dataElement + ":" + decoc + ":" +period.getStartDate() + ":" +  period.getEndDate()+ ":" + orgChild );
                          
                           if( tempAggDataValue != null ) aggDataValue += tempAggDataValue;
                           //System.out.println( "Agg data value after zero assign is aggDataCB check  : " + aggDataValue );
                       }
                       else
                       {
                           for( Period period : periods )
                           {
                               //System.out.println( "inside aggDataCB not check  : " );
                               DataValue dataValue = dataValueService.getDataValue( orgChild, dataElement, period, decoc );
                               
                               try
                               {
                                   aggDataValue += Double.parseDouble( dataValue.getValue() );
                               }
                               catch( Exception e )
                               {
                                   
                               }
                               
                           }
                         
                           //System.out.println( "Agg data value after zero assign is when aggDataCB not check  : " + aggDataValue );
                       }
                  // }
                   periodCount++;
               }
 
               data[serviceCount][childCount] = aggDataValue;
               
               if( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
               {
                  if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                  {
                      data[serviceCount][childCount] = Math.round( data[serviceCount][childCount] * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                  }
                  else
                  {
                      data[serviceCount][childCount] = Math.round( data[serviceCount][childCount] * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                  }
               }
               childCount++;
           }
           
           serviceCount++;          
       }
    
       dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title, yAxis_Title );
       return dataElementChartResult;
    }
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With Children Wise end
    // -------------------------------------------------------------------------
        
    
    
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With groupMember Wise start ( this method is called when view by -> periodWise and group  selected )
    // -------------------------------------------------------------------------
        
 
    public DataElementChartResult generateDataElementChartDataWithGroupMemberWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,String  periodTypeLB ,List<DataElement> dataElementList, String deSelection, List<DataElementCategoryOptionCombo> decocList, OrganisationUnit selectedOrgUnit , OrganisationUnitGroup selectedOrgUnitGroup , String aggDataCB ) throws Exception
    {
        System.out.println( "inside Dashboard Service generateChartDataWithGroupMemberWise " );
        
        DataElementChartResult dataElementChartResult;
        
        //OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup(  selectedOrgUnitGroup  );
        
        List<OrganisationUnit> selectedOUGroupMemberList = new ArrayList<OrganisationUnit>( selectedOrgUnitGroup.getMembers() );
       
        List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
        childOrgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
       
        selectedOUGroupMemberList.retainAll( childOrgUnitList );
        
       
        String[] series = new String[dataElementList.size()];
        String[] categories = new String[selectedOUGroupMemberList.size()];
        Double[][] data = new Double[dataElementList.size()][selectedOUGroupMemberList.size()];
        //String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName();
        String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName()+ "(" + selectedOrgUnitGroup.getName() +  ")";
       
      // String chartTitle = "OrganisationUnit : " + orgUnit.getShortName();
        String xAxis_Title = "Facilities";
        String yAxis_Title = "Value";
    
        //System.out.println("size of children : " +childOrgUnitList.size() + ", Size og GroupMember : " + selectedOUGroupMemberList.size()+ ", size of CommomGroupMember : " + selectedOUGroupMemberList.size());
       
       int serviceCount = 0;     
     
       for( DataElement dataElement : dataElementList )
       {
           DataElementCategoryOptionCombo decoc;
          
           if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
           
          // if( dataElement.isMultiDimensional() )               
           {
               decoc = decocList.get( serviceCount );
                   
               series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
               //yseriesList.add( dataElement.getName() + " : " + decoc.getName() );
           }
           else
           {
               decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
               series[serviceCount] = dataElement.getName();
               
               //yseriesList.add( dataElement.getName() );
           }
           
           int GroupMemberCount = 0;
           for( OrganisationUnit orgUnit : selectedOUGroupMemberList )
           {
               categories[GroupMemberCount] = orgUnit.getName();
               
               Double aggDataValue = 0.0;

               int periodCount = 0;
               for( Date startDate : selStartPeriodList )
               {
                   Date endDate = selEndPeriodList.get( periodCount );
                   //PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                   //Collection<Period> periods = periodService.getPeriodsBetweenDates( periodType, startDate, endDate );
                   Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
                   
                   //System.out.println( periods.size() + ":" + periodType + ":" + startDate + ":" +  endDate );
                   
                  // for( Period period : periods )
                  // {
                      // System.out.println( dataElement + ":" + decoc + ":" +period.getStartDate() + ":" +  period.getEndDate()+ ":" + orgChild + ":" + aggDataCB );
                       
                       int aggChecked = Integer.parseInt( aggDataCB );
                   
                       if( aggChecked == 1 )
                       {
                           //System.out.println( "inside aggDataCB check  : " );
                           Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc, startDate, endDate, orgUnit );
                           //System.out.println( dataElement + ":" + decoc + ":" +period.getStartDate() + ":" +  period.getEndDate()+ ":" + orgChild );
                          
                           if( tempAggDataValue != null ) aggDataValue += tempAggDataValue;
                           //System.out.println( "Agg data value after zero assign is aggDataCB check  : " + aggDataValue );
                       }
                       else
                       {
                           for( Period period : periods )
                           {
                               //System.out.println( "inside aggDataCB not check  : " );
                               DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period, decoc );
                               
                               try
                               {
                                   aggDataValue += Double.parseDouble( dataValue.getValue() );
                               }
                               catch( Exception e )
                               {
                                   
                               }
                               
                           }
                         
                           //System.out.println( "Agg data value after zero assign is when aggDataCB not check  : " + aggDataValue );
                       }
                  // }
                   periodCount++;
               }
 
               data[serviceCount][GroupMemberCount] = aggDataValue;
               
               if( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
               {
                  if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                  {
                      data[serviceCount][GroupMemberCount] = Math.round( data[serviceCount][GroupMemberCount] * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                  }
                  else
                  {
                      data[serviceCount][GroupMemberCount] = Math.round( data[serviceCount][GroupMemberCount] * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                  }
               }
               GroupMemberCount++;
           }
           
           serviceCount++;          
       }
    
       dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title, yAxis_Title );
       return dataElementChartResult;
    }
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With groupMember Wise end
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data only Period Wise start ( this method is called when view by ->Selected + children and  Group not selected,and view by -> children and group selected )
    // -------------------------------------------------------------------------
    
    
    public DataElementChartResult generateDataElementChartDataWithPeriodWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,List<String> periodNames,String  periodTypeLB ,List<DataElement> dataElementList, String deSelection, List<DataElementCategoryOptionCombo> decocList, OrganisationUnit selectedOrgUnit , String aggDataCB ) throws Exception
    {
       DataElementChartResult dataElementChartResult;
       
       System.out.println( "inside Dashboard Service generate Chart Data With Period Wise " );
       
       String[] series = new String[dataElementList.size()];
       String[] categories = new String[selStartPeriodList.size()];
       Double[][] data = new Double[dataElementList.size()][selStartPeriodList.size()];
       String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName();
       String xAxis_Title = "Time Line";
       String yAxis_Title = "Value";
    
      // System.out.println("\n\n +++ \n decoc : " + decocList);
       
       int serviceCount = 0;     
     
     
       for( DataElement dataElement : dataElementList )
       {
           DataElementCategoryOptionCombo decoc;
           if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
           {
               decoc = decocList.get( serviceCount );
                   
               series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
               //yseriesList.add( dataElement.getName() + " : " + decoc.getName() );
           }
           else
           {
               decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
               series[serviceCount] = dataElement.getName();
               //System.out.println( "selectedStatus  : " + selectedStatus );
               //yseriesList.add( dataElement.getName() );
           }
           
           int periodCount = 0;
           for( Date startDate : selStartPeriodList )
           {
               Date endDate = selEndPeriodList.get( periodCount );
               
               categories[periodCount] = periodNames.get( periodCount );
               //PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
               
               Double aggDataValue = 0.0;
               int aggChecked = Integer.parseInt( aggDataCB );
               
               if( aggChecked == 1 )
               {
                   //System.out.println( "inside aggDataCB check  : " );
                   aggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc, startDate, endDate, selectedOrgUnit );
                   //System.out.println( "start Date is   : " + startDate + " , End date is : " + endDate );
                   //System.out.println( "Agg data value before is  : " + aggDataValue );
                   if(aggDataValue == null ) aggDataValue = 0.0;
                   //System.out.println( "Agg data value after zero assign is  : " + aggDataValue );
                   //System.out.println( "Agg data value after zero assign is aggDataCB check  : " + aggDataValue );
               }
               else
               {
                   //System.out.println( "inside aggDataCB not check  : " );
                 //  PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                  // Collection<Period> periods = periodService.getPeriodsBetweenDates( periodType, startDate, endDate );
                   Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
                   
                  // System.out.println( periods.size() + ":"  + startDate + ":" +  endDate );
                   for( Period period : periods )
                   {
                       DataValue dataValue = dataValueService.getDataValue( selectedOrgUnit, dataElement, period, decoc );
                      
                      // String values = orgUnit.getId() + ":"+ dataElement.getId() + ":"+ decoc.getId() + ":" + period.getId();
                      // selectedValues.add(values);
                       
                      // System.out.println( "selectedValues  : " + selectedValues );
                       
                       try
                       {
                           aggDataValue += Double.parseDouble( dataValue.getValue() );
                       }
                       catch( Exception e )
                       {
                           
                       }
                      // System.out.println( "Agg data value after zero assign is when aggDataCB not check  : " + aggDataValue );
                   }
               }
               
               data[serviceCount][periodCount] = aggDataValue;
               
               if( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
               {
                  if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                  {
                      data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount] * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                  }
                  else
                  {
                      data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount] * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                  }
               }
               periodCount++;
           }
           
           serviceCount++;          
       }
       
       dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title, yAxis_Title );
       return dataElementChartResult;
    }
    
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data only Period Wise end
    // -------------------------------------------------------------------------
    
    
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data OrgGroup Period Wise start
    // -------------------------------------------------------------------------
       
    public DataElementChartResult generateDataElementChartDataWithGroupToPeriodWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,List<String> periodNames,String  periodTypeLB ,List<DataElement> dataElementList, String deSelection, List<DataElementCategoryOptionCombo> decocList, OrganisationUnit selectedOrgUnit , OrganisationUnitGroup selectedOrgUnitGroup , String aggDataCB ) throws Exception
    {
       DataElementChartResult dataElementChartResult;
       
       List<OrganisationUnit> selectedOUGroupMemberList = new ArrayList<OrganisationUnit>( selectedOrgUnitGroup.getMembers() );
       
       List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
       childOrgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
      
       selectedOUGroupMemberList.retainAll( childOrgUnitList );

       String[] series = new String[dataElementList.size()];
       String[] categories = new String[selStartPeriodList.size()];
       Double[][] data = new Double[dataElementList.size()][selStartPeriodList.size()];
       String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName()+ "( Group - " + selectedOrgUnitGroup.getName() +  " )";
       String xAxis_Title = "Time Line";
       String yAxis_Title = "Value";
       
       
       int serviceCount = 0;     
     
       for( DataElement dataElement : dataElementList )
       {
           DataElementCategoryOptionCombo decoc;
          
           if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
           
          // if( dataElement.isMultiDimensional() )               
           {
               decoc = decocList.get( serviceCount );
                   
               series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
               //yseriesList.add( dataElement.getName() + " : " + decoc.getName() );
           }
           else
           {
               decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
               series[serviceCount] = dataElement.getName();
               
               //yseriesList.add( dataElement.getName() );
           }
           int periodCount = 0;
           for( Date startDate : selStartPeriodList )
           {
               Date endDate = selEndPeriodList.get( periodCount );
               categories[periodCount] = periodNames.get( periodCount );
               Double aggDataValue = 0.0;
               Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
                  
               int orgGroupCount = 0;
                       
               for( OrganisationUnit orgUnit : selectedOUGroupMemberList )
               {
                   int aggChecked = Integer.parseInt( aggDataCB );
                   if( aggChecked == 1 )
                   {
                       Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc, startDate, endDate, orgUnit );
                      // System.out.println( "Agg data value before is  : " + aggDataValue );
                      
                       if(tempAggDataValue != null ) aggDataValue = tempAggDataValue;
                      // System.out.println( "Agg data value after zero assign is  : " + aggDataValue );
                   }
                   else
                   {
                       for( Period period : periods )
                       {
                           DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period, decoc );
                           
                           try
                           {
                               aggDataValue += Double.parseDouble( dataValue.getValue() );
                           }
                           catch( Exception e )
                           {
                               
                           }
                       }
                      
                   }
                   orgGroupCount++;
           }
   
           data[serviceCount][periodCount] = aggDataValue;
               
           if( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
           {
               if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
               {
                   data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount] * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
               }
               else
               {
                   data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount] * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
               }
               }
               periodCount++;    
           }
           
           serviceCount++;          
       }
    
       dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title, yAxis_Title );
       
      return dataElementChartResult;
    
   }
   
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data OrgGroup Period Wise end 
    // -------------------------------------------------------------------------
    
    
    // --------------------------------------------------------
    // for Indicators DrillDown Supportive method   ndicator Wise
    //---------------------------------------------------------
   
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With Children Wise start ( this method is called when view by -> periodWise and group not selected ) --ndicator Wise
    // -------------------------------------------------------------------------
        
    
    public IndicatorChartResult generateIndicatorChartDataWithChildrenWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList, String  periodTypeLB, List<Indicator> indicatorList, OrganisationUnit selectedOrgUnit , String aggDataCB ) throws Exception
    {
        System.out.println( "inside Dashboard Service generate Chart Data With Children Wise " );
        
        IndicatorChartResult indicatorChartResult;
        
        List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
        childOrgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren());
        
        String[] series = new String[indicatorList.size()];
        String[] categories = new String[childOrgUnitList.size()];

        Double[][] numDataArray = new Double[indicatorList.size()][childOrgUnitList.size()];
        Double[][] denumDataArray = new Double[indicatorList.size()][childOrgUnitList.size()];
        Double[][] data = new Double[indicatorList.size()][childOrgUnitList.size()];

        String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName();

        String xAxis_Title = "Facilities";
        String yAxis_Title = "Value";

        int serviceCount = 0;

        for ( Indicator indicator : indicatorList )
        {
            series[serviceCount] = indicator.getName();
            //yseriesList.add( indicator );

            //numeratorDEList.add( indicator.getNumeratorDescription() );
           // denominatorDEList.add( indicator.getDenominatorDescription() );

            int childCount = 0;
            for ( OrganisationUnit orgChild : childOrgUnitList )
            {

                categories[childCount] = orgChild.getName();

                Double aggIndicatorValue = 0.0;
                Double aggIndicatorNumValue = 0.0;
                Double aggIndicatorDenumValue = 0.0;
                int periodCount = 0;
                for ( Date startDate : selStartPeriodList )
                {
                    Date endDate = selEndPeriodList.get( periodCount );

                   // if ( aggDataCB != null )
                   // {
                    int aggChecked = Integer.parseInt( aggDataCB );
                        
                    if( aggChecked == 1 )
                    {
                        Double tempAggIndicatorNumValue = aggregationService.getAggregatedNumeratorValue( indicator, startDate, endDate, orgChild );
                        Double tempAggIndicatorDenumValue = aggregationService.getAggregatedDenominatorValue( indicator, startDate, endDate, orgChild );

                        if ( tempAggIndicatorNumValue != null )
                        {
                            aggIndicatorNumValue += tempAggIndicatorNumValue;

                        }
                        if ( tempAggIndicatorDenumValue != null )
                        {
                            aggIndicatorDenumValue += tempAggIndicatorDenumValue;

                        }

                    }
                    else
                    {
                        Double tempAggIndicatorNumValue = 0.0;
                        String tempStr = reportservice.getIndividualResultDataValue( indicator.getNumerator(), startDate, endDate, orgChild, "" );
                        try
                        {
                            tempAggIndicatorNumValue = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            tempAggIndicatorNumValue = 0.0;
                        }
                        aggIndicatorNumValue += tempAggIndicatorNumValue;

                        Double tempAggIndicatorDenumValue = 0.0;

                        tempStr = reportservice.getIndividualResultDataValue( indicator.getDenominator(), startDate, endDate, orgChild, "" );
                        try
                        {
                            tempAggIndicatorDenumValue = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            tempAggIndicatorDenumValue = 0.0;
                        }
                        aggIndicatorDenumValue += tempAggIndicatorDenumValue;

                    }

                    periodCount++;
                }
                try
                {
                    // aggIndicatorValue = ( aggIndicatorNumValue /
                    // aggIndicatorDenumValue )*
                    // indicator.getIndicatorType().getFactor();
                    if ( aggIndicatorDenumValue == 0 )
                    {
                        aggIndicatorValue = 0.0;
                    }
                    else
                    {
                        aggIndicatorValue = (aggIndicatorNumValue / aggIndicatorDenumValue) * indicator.getIndicatorType().getFactor();
                    }
                }
                catch ( Exception e )
                {
                    aggIndicatorValue = 0.0;
                }
                // rounding indicator value ,Numenetor,denumenetor
                data[serviceCount][childCount] = aggIndicatorValue;
                data[serviceCount][childCount] = Math.round( data[serviceCount][childCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );

                numDataArray[serviceCount][childCount] = aggIndicatorNumValue;
                numDataArray[serviceCount][childCount] = Math.round( numDataArray[serviceCount][childCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                
                denumDataArray[serviceCount][childCount] = aggIndicatorDenumValue;
                denumDataArray[serviceCount][childCount] = Math.round( denumDataArray[serviceCount][childCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                // data[serviceCount][childCount] = aggDataValue;
                childCount++;
            }

            serviceCount++;
        }

        indicatorChartResult = new IndicatorChartResult( series, categories, data, numDataArray, denumDataArray,chartTitle, xAxis_Title, yAxis_Title );
        return indicatorChartResult;
    }

    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With Children Wise start ( this method is called when view by -> periodWise and group not selected ) End --ndicator Wise
    // -------------------------------------------------------------------------
        
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With groupMember Wise start ( this method is called when view by -> periodWise and group  selected ) --- indicator Wise
    // -------------------------------------------------------------------------
        
 
    public IndicatorChartResult generateIndicatorChartDataWithGroupMemberWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,String  periodTypeLB ,List<Indicator> indicatorList, OrganisationUnit selectedOrgUnit , OrganisationUnitGroup selectedOrgUnitGroup , String aggDataCB ) throws Exception
    {
        System.out.println( " inside Dashboard Service generate Indicator Chart Data With Group Member Wise " );
        
        IndicatorChartResult indicatorChartResult;
        
        //OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup(  selectedOrgUnitGroup  );
        
        List<OrganisationUnit> selectedOUGroupMemberList = new ArrayList<OrganisationUnit>( selectedOrgUnitGroup.getMembers() );
       
        List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
        childOrgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
       
        selectedOUGroupMemberList.retainAll( childOrgUnitList );
        
       
        String[] series = new String[indicatorList.size()];
        String[] categories = new String[selectedOUGroupMemberList.size()];

        Double[][] numDataArray = new Double[indicatorList.size()][selectedOUGroupMemberList.size()];
        Double[][] denumDataArray = new Double[indicatorList.size()][selectedOUGroupMemberList.size()];
        Double[][] data = new Double[indicatorList.size()][selectedOUGroupMemberList.size()];
        //String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName();
        String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName()+ "( Group - " + selectedOrgUnitGroup.getName() +  ")";
       
      // String chartTitle = "OrganisationUnit : " + orgUnit.getShortName();
        String xAxis_Title = "Facilities";
        String yAxis_Title = "Value";
    
        //System.out.println("size of children : " +childOrgUnitList.size() + ", Size og GroupMember : " + selectedOUGroupMemberList.size()+ ", size of CommomGroupMember : " + selectedOUGroupMemberList.size());
       
        int serviceCount = 0;

        for ( Indicator indicator : indicatorList )
        {
            series[serviceCount] = indicator.getName();

            int childCount = 0;
            for ( OrganisationUnit orgChild : selectedOUGroupMemberList )
            {

                categories[childCount] = orgChild.getName();

                Double aggIndicatorValue = 0.0;
                Double aggIndicatorNumValue = 0.0;
                Double aggIndicatorDenumValue = 0.0;
                int periodCount = 0;
                for ( Date startDate : selStartPeriodList )
                {
                    Date endDate = selEndPeriodList.get( periodCount );

                    int aggChecked = Integer.parseInt( aggDataCB );
                    
                    if( aggChecked == 1 )
                    {

                        Double tempAggIndicatorNumValue = aggregationService.getAggregatedNumeratorValue( indicator,
                            startDate, endDate, orgChild );
                        Double tempAggIndicatorDenumValue = aggregationService.getAggregatedDenominatorValue(
                            indicator, startDate, endDate, orgChild );

                        if ( tempAggIndicatorNumValue != null )
                        {
                            aggIndicatorNumValue += tempAggIndicatorNumValue;

                        }
                        if ( tempAggIndicatorDenumValue != null )
                        {
                            aggIndicatorDenumValue += tempAggIndicatorDenumValue;

                        }

                    }
                    else
                    {
                        Double tempAggIndicatorNumValue = 0.0;
                        String tempStr = reportservice.getIndividualResultDataValue( indicator.getNumerator(),
                            startDate, endDate, orgChild, "" );
                        try
                        {
                            tempAggIndicatorNumValue = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            tempAggIndicatorNumValue = 0.0;
                        }
                        aggIndicatorNumValue += tempAggIndicatorNumValue;

                        Double tempAggIndicatorDenumValue = 0.0;

                        tempStr = reportservice.getIndividualResultDataValue( indicator.getDenominator(), startDate,
                            endDate, orgChild, "" );
                        try
                        {
                            tempAggIndicatorDenumValue = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            tempAggIndicatorDenumValue = 0.0;
                        }
                        aggIndicatorDenumValue += tempAggIndicatorDenumValue;

                    }

                    periodCount++;
                }
                try
                {
                    // aggIndicatorValue = ( aggIndicatorNumValue /
                    // aggIndicatorDenumValue )*
                    // indicator.getIndicatorType().getFactor();
                    if ( aggIndicatorDenumValue == 0 )
                    {
                        aggIndicatorValue = 0.0;
                    }
                    else
                    {
                        aggIndicatorValue = (aggIndicatorNumValue / aggIndicatorDenumValue)
                            * indicator.getIndicatorType().getFactor();
                    }
                }
                catch ( Exception e )
                {
                    aggIndicatorValue = 0.0;
                }
                // rounding indicator value ,Numenetor,denumenetor
                data[serviceCount][childCount] = aggIndicatorValue;
                data[serviceCount][childCount] = Math.round( data[serviceCount][childCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );

                numDataArray[serviceCount][childCount] = aggIndicatorNumValue;
                numDataArray[serviceCount][childCount] = Math.round( numDataArray[serviceCount][childCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                
                denumDataArray[serviceCount][childCount] = aggIndicatorDenumValue;
                denumDataArray[serviceCount][childCount] = Math.round( denumDataArray[serviceCount][childCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                // data[serviceCount][childCount] = aggDataValue;
                childCount++;
            }

            serviceCount++;
        }

        indicatorChartResult = new IndicatorChartResult( series, categories, data, numDataArray, denumDataArray,chartTitle, xAxis_Title, yAxis_Title );
        return indicatorChartResult;
    }
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With groupMember Wise start ( this method is called when view by -> periodWise and group  selected ) --- indicator Wise
    // ------ end
    
    // -------------------------------------------------------------------------
    // for Indicator
    // Methods for getting Chart Data only Period Wise start ( this method is called when view by ->Selected + children and  Group not selected,and view by -> children and group selected )
    // -------------------------------------------------------------------------
    
    
    public IndicatorChartResult generateIndicatorChartDataWithPeriodWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,List<String> periodNames,String  periodTypeLB ,List<Indicator> indicatorList,  OrganisationUnit selectedOrgUnit , String aggDataCB ) throws Exception
    {
       System.out.println( "inside Dashboard Service generate Chart Data With Period Wise " );
       
       IndicatorChartResult indicatorChartResult;

       String[] series = new String[indicatorList.size()];
       String[] categories = new String[selStartPeriodList.size()];
       Double[][] data = new Double[indicatorList.size()][selStartPeriodList.size()];

       Double[][] numDataArray = new Double[indicatorList.size()][selStartPeriodList.size()];
       Double[][] denumDataArray = new Double[indicatorList.size()][selStartPeriodList.size()];

       // Map<Integer, List<Double>> numData = new HashMap<Integer, List<Double>>();
       // Map<Integer, List<Double>> denumData = new HashMap<Integer, List<Double>>();

       String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName();
       String xAxis_Title = "Time Line";
       String yAxis_Title = "Value";

       int serviceCount = 0;
       for ( Indicator indicator : indicatorList )
       {
           series[serviceCount] = indicator.getName();

           // List<Double> numeratorValueList = new ArrayList<Double>();
           // List<Double> denumeratorValueList = new ArrayList<Double>();

           int periodCount = 0;
           for ( Date startDate : selStartPeriodList )
           {
               Date endDate = selEndPeriodList.get( periodCount );
              // String drillDownPeriodName = periodNames.get( periodCount );

               categories[periodCount] = periodNames.get( periodCount );

               Double aggIndicatorValue = 0.0;
               Double aggIndicatorNumValue = 0.0;
               Double aggIndicatorDenumValue = 0.0;
               
               int aggChecked = Integer.parseInt( aggDataCB );
               
               if( aggChecked == 1 )
               {
                   aggIndicatorValue = aggregationService.getAggregatedIndicatorValue( indicator, startDate, endDate, selectedOrgUnit );

                   aggIndicatorNumValue = aggregationService.getAggregatedNumeratorValue( indicator, startDate, endDate, selectedOrgUnit );
                   aggIndicatorDenumValue = aggregationService.getAggregatedDenominatorValue( indicator, startDate, endDate, selectedOrgUnit );

                   if ( aggIndicatorValue == null ) aggIndicatorValue = 0.0;

               }
               else
               {
                   aggIndicatorValue = getIndividualIndicatorValue( indicator, selectedOrgUnit, startDate, endDate );

                  // System.out.println( " \nIndicator Numerator value  : " + indicator.getNumerator()
                     //  + ", Start Date :- " + startDate + ", End Date :- " + endDate + ", Org Unit :- " + orgUnit );

                   String tempStr = reportservice.getIndividualResultDataValue( indicator.getNumerator(), startDate, endDate, selectedOrgUnit, "" );
                  // System.out.println( " \nIndicatorNumerator valu is " + tempStr );

                   try
                   {
                       aggIndicatorNumValue = Double.parseDouble( tempStr );
                   }
                   catch ( Exception e )
                   {
                       aggIndicatorNumValue = 0.0;
                   }

                   tempStr = reportservice.getIndividualResultDataValue( indicator.getDenominator(), startDate, endDate, selectedOrgUnit, "" );

                   try
                   {
                       aggIndicatorDenumValue = Double.parseDouble( tempStr );
                   }
                   catch ( Exception e )
                   {
                       aggIndicatorDenumValue = 0.0;
                   }

               }
               // rounding indicator value ,Numenetor,denumenetor
               data[serviceCount][periodCount] = aggIndicatorValue;
               data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );

               numDataArray[serviceCount][periodCount] = aggIndicatorNumValue;
               numDataArray[serviceCount][periodCount] = Math.round( numDataArray[serviceCount][periodCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
               
               denumDataArray[serviceCount][periodCount] = aggIndicatorDenumValue;
               denumDataArray[serviceCount][periodCount] = Math.round( denumDataArray[serviceCount][periodCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
               // numeratorValueList.add( aggIndicatorNumValue );
               // denumeratorValueList.add( aggIndicatorDenumValue );

               periodCount++;
           }

           // numData.put( serviceCount, numeratorValueList );
           // denumData.put( serviceCount, denumeratorValueList );

           serviceCount++;
       }

       indicatorChartResult = new IndicatorChartResult( series, categories, data, numDataArray, denumDataArray,chartTitle, xAxis_Title, yAxis_Title );
       return indicatorChartResult;

    }
    
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data only Period Wise end
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data OrgGroup Period Wise start - IndicatorWise
    // -------------------------------------------------------------------------
    
    public IndicatorChartResult generateIndicatorChartDataWithGroupToPeriodWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,List<String> periodNames,String  periodTypeLB ,List<Indicator> indicatorList, OrganisationUnit selectedOrgUnit , OrganisationUnitGroup selectedOrgUnitGroup , String aggDataCB )
        throws Exception
    {
        IndicatorChartResult indicatorChartResult;

        List<OrganisationUnit> selectedOUGroupMemberList = new ArrayList<OrganisationUnit>( selectedOrgUnitGroup.getMembers() );
        
        List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
        childOrgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
       
        selectedOUGroupMemberList.retainAll( childOrgUnitList );
        
        String[] series = new String[indicatorList.size()];
        String[] categories = new String[selStartPeriodList.size()];

        Double[][] numDataArray = new Double[indicatorList.size()][selStartPeriodList.size()];
        Double[][] denumDataArray = new Double[indicatorList.size()][selStartPeriodList.size()];
        Double[][] data = new Double[indicatorList.size()][selStartPeriodList.size()];

        String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName() + "( Group - " + selectedOrgUnitGroup.getName() + " )";
        String xAxis_Title = "Time Line";
        String yAxis_Title = "Value";

        int serviceCount = 0;

        for ( Indicator indicator : indicatorList )
        {
            series[serviceCount] = indicator.getName();

            Double aggIndicatorValue = 0.0;
            Double aggIndicatorNumValue = 0.0;
            Double aggIndicatorDenumValue = 0.0;

            int periodCount = 0;
            for ( Date startDate : selStartPeriodList )
            {
                Date endDate = selEndPeriodList.get( periodCount );
                categories[periodCount] = periodNames.get( periodCount );
                
                int orgGroupCount = 0;

                for ( OrganisationUnit orgUnit : selectedOUGroupMemberList )
                {
                    int aggChecked = Integer.parseInt( aggDataCB );
                    
                    if( aggChecked == 1 )
                    {
                        Double tempAggIndicatorNumValue = aggregationService.getAggregatedNumeratorValue( indicator, startDate, endDate, orgUnit );
                        Double tempAggIndicatorDenumValue = aggregationService.getAggregatedDenominatorValue( indicator, startDate, endDate, orgUnit );

                        if ( tempAggIndicatorNumValue != null )
                        {
                            aggIndicatorNumValue += tempAggIndicatorNumValue;

                        }
                        if ( tempAggIndicatorDenumValue != null )
                        {
                            aggIndicatorDenumValue += tempAggIndicatorDenumValue;

                        }
                    }
                    else
                    {
                        Double tempAggIndicatorNumValue = 0.0;

                        String tempStr = reportservice.getIndividualResultDataValue( indicator.getNumerator(), startDate, endDate, orgUnit, "" );
                        try
                        {
                            tempAggIndicatorNumValue = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            tempAggIndicatorNumValue = 0.0;
                        }
                        aggIndicatorNumValue += tempAggIndicatorNumValue;

                        Double tempAggIndicatorDenumValue = 0.0;

                        // tempStr =
                        // reportService.getIndividualResultIndicatorValue(
                        // indicator.getDenominator(), startDate, endDate,
                        // orgUnit );
                        tempStr = reportservice.getIndividualResultDataValue( indicator.getDenominator(), startDate, endDate, orgUnit, "" );
                        try
                        {
                            tempAggIndicatorDenumValue = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            tempAggIndicatorDenumValue = 0.0;
                        }
                        aggIndicatorDenumValue += tempAggIndicatorDenumValue;

                    }
                    orgGroupCount++;
                }

                try
                {
                    // aggIndicatorValue = ( aggIndicatorNumValue /
                    // aggIndicatorDenumValue )*
                    // indicator.getIndicatorType().getFactor();
                    if ( aggIndicatorDenumValue == 0 )
                    {
                        aggIndicatorValue = 0.0;
                    }
                    else
                    {
                        aggIndicatorValue = (aggIndicatorNumValue / aggIndicatorDenumValue)
                            * indicator.getIndicatorType().getFactor();
                    }
                }
                catch ( Exception e )
                {
                    aggIndicatorValue = 0.0;
                }
                // rounding indicator value ,Numenetor,denumenetor
                data[serviceCount][periodCount] = aggIndicatorValue;
                data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );

                numDataArray[serviceCount][periodCount] = aggIndicatorNumValue;
                numDataArray[serviceCount][periodCount] = Math.round( numDataArray[serviceCount][periodCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                
                denumDataArray[serviceCount][periodCount] = aggIndicatorDenumValue;
                denumDataArray[serviceCount][periodCount] = Math.round( denumDataArray[serviceCount][periodCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                
                periodCount++;
            }

            serviceCount++;
        }

        indicatorChartResult = new IndicatorChartResult( series, categories, data, numDataArray, denumDataArray, chartTitle, xAxis_Title, yAxis_Title );
        return indicatorChartResult;

    }
    
    
    
} // class end
