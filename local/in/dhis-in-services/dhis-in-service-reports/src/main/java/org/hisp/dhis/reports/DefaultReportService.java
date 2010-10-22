package org.hisp.dhis.reports;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.source.Source;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DefaultReportService
    implements ReportService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ReportStore reportStore;

    public void setReportStore( ReportStore reportStore )
    {
        this.reportStore = reportStore;
    }

    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

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
    // Report_in
    // -------------------------------------------------------------------------

    @Transactional
    public int addReport( Report_in report )
    {
        return reportStore.addReport( report );
    }

    @Transactional
    public void deleteReport( Report_in report )
    {
        reportStore.deleteReport( report );
    }

    @Transactional
    public void updateReport( Report_in report )
    {
        reportStore.updateReport( report );
    }

    @Transactional
    public Collection<Report_in> getAllReports()
    {
        return reportStore.getAllReports();
    }

    @Transactional
    public Report_in getReport( int id )
    {
        return reportStore.getReport( id );
    }

    @Transactional
    public Report_in getReportByName( String name )
    {
        return reportStore.getReportByName( name );
    }

    @Transactional
    public Collection<Report_in> getReportBySource( Source source )
    {
        return reportStore.getReportBySource( source );
    }

    @Transactional
    public Collection<Report_in> getReportsByPeriodAndReportType( PeriodType periodType, String reportType )
    {
        return reportStore.getReportsByPeriodAndReportType( periodType, reportType );
    }

    @Transactional
    public Collection<Report_in> getReportsByPeriodType( PeriodType periodType )
    {
        return reportStore.getReportsByPeriodType( periodType );
    }

    @Transactional
    public Collection<Report_in> getReportsByReportType( String reportType )
    {
        return reportStore.getReportsByReportType( reportType );
    }

    @Transactional
    public Collection<Report_in> getReportsByPeriodSourceAndReportType( PeriodType periodType, Source source,
        String reportType )
    {
        return reportStore.getReportsByPeriodSourceAndReportType( periodType, source, reportType );
    }

    // -------------------------------------------------------------------------
    // Support Methods Defination
    // -------------------------------------------------------------------------

    public String getRAFolderName()
    {
        String raFolderName = "ra_national";

        try
        {
            raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        }
        catch ( Exception e )
        {
            System.out.println( "Exception : " + e.getMessage() );
            return null;
        }

        return raFolderName;
    }

    public List<Integer> getLinelistingRecordNos( OrganisationUnit organisationUnit, Period period, String lltype )
    {
        List<Integer> recordNosList = new ArrayList<Integer>();

        String query = "";

        int dataElementid = 1020;

        if ( lltype.equalsIgnoreCase( "lldeath-l4DECodes.xml" )
            || lltype.equalsIgnoreCase( "lllivebirth-l5DECodes.xml" )
            || lltype.equalsIgnoreCase( "lllivebirth-l6DECodes.xml" ) )
            dataElementid = 1020;
        else if ( lltype.equalsIgnoreCase( "lldeath-l4DECodes.xml" )
            || lltype.equalsIgnoreCase( "lldeath-l5DECodes.xml" ) || lltype.equalsIgnoreCase( "lldeath-l6DECodes.xml" ) )
            dataElementid = 1027;
        else if ( lltype.equalsIgnoreCase( "llmaternaldeath-l4DECodes.xml" )
            || lltype.equalsIgnoreCase( "llmaternaldeath-l5DECodes.xml" )
            || lltype.equalsIgnoreCase( "llmaternaldeath-l6DECodes.xml" ) )
            dataElementid = 1032;

        try
        {
            query = "SELECT recordno FROM lldatavalue WHERE dataelementid = " + dataElementid + " AND periodid = "
                + period.getId() + " AND sourceid = " + organisationUnit.getId();

            SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );

            while ( rs1.next() )
            {
                recordNosList.add( rs1.getInt( 1 ) );
            }

            Collections.sort( recordNosList );
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
        }

        return recordNosList;
    }

    public List<Report_inDesign> getReportDesign( Report_in report )
    {
        List<Report_inDesign> deCodes = new ArrayList<Report_inDesign>();
        String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER )
            .getValue();

        String path = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + report.getXmlTemplateName();

        if ( path == null )
        {
            System.out.println( "DHIS2_HOME is not set" );
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                System.out.println( "DECodes related XML file not found" );
                return null;
            }

            NodeList listOfDECodes = doc.getElementsByTagName( "de-code" );
            int totalDEcodes = listOfDECodes.getLength();

            for ( int s = 0; s < totalDEcodes; s++ )
            {
                Element deCodeElement = (Element) listOfDECodes.item( s );
                NodeList textDECodeList = deCodeElement.getChildNodes();

                String expression = ((Node) textDECodeList.item( 0 )).getNodeValue().trim();
                String stype = deCodeElement.getAttribute( "stype" );
                String ptype = deCodeElement.getAttribute( "type" );
                int sheetno = new Integer( deCodeElement.getAttribute( "sheetno" ) );
                int rowno = new Integer( deCodeElement.getAttribute( "rowno" ) );
                int colno = new Integer( deCodeElement.getAttribute( "colno" ) );
                int rowMerge = new Integer( deCodeElement.getAttribute( "rowmerge" ) );
                int colMerge = new Integer( deCodeElement.getAttribute( "colmerge" ) );

                Report_inDesign reportDesign = new Report_inDesign( stype, ptype, sheetno, rowno, colno, rowMerge,
                    colMerge, expression );

                deCodes.add( reportDesign );

            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }
        return deCodes;
    }// getDECodes end

    /*
     * Returns Previous Month's Period object For ex:- selected period is
     * Aug-2007 it returns the period object corresponding July-2007
     */
    public Period getPreviousPeriod( Date startDate, Date endDate )
    {
        Period period = new Period();
        Calendar tempDate = Calendar.getInstance();
        tempDate.setTime( startDate );
        if ( tempDate.get( Calendar.MONTH ) == Calendar.JANUARY )
        {
            tempDate.set( Calendar.MONTH, Calendar.DECEMBER );
            tempDate.roll( Calendar.YEAR, -1 );
        }
        else
        {
            tempDate.roll( Calendar.MONTH, -1 );
        }
        PeriodType periodType = new MonthlyPeriodType();
        period = getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ), periodType );

        return period;
    }

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
        newPeriod = periodService.getPeriod( firstDay, lastDay, periodType );
        return newPeriod;
    }

    public List<Calendar> getStartingEndingPeriods( String deType, Date startDate, Date endDate )
    {
        List<Calendar> calendarList = new ArrayList<Calendar>();

        Calendar tempStartDate = Calendar.getInstance();
        Calendar tempEndDate = Calendar.getInstance();

        Period previousPeriod = new Period();
        previousPeriod = getPreviousPeriod( startDate, endDate );

        if ( deType.equalsIgnoreCase( ReportDesignPeriodType.RDPT_CCMCY ) )
        {
            tempStartDate.setTime( startDate );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            tempEndDate.setTime( endDate );
        }
        else if ( deType.equalsIgnoreCase( ReportDesignPeriodType.RDPT_CPMCY ) )
        {
            tempStartDate.setTime( previousPeriod.getStartDate() );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            tempEndDate.setTime( previousPeriod.getEndDate() );
        }
        else if ( deType.equalsIgnoreCase( ReportDesignPeriodType.RDPT_CMPY ) )
        {
            tempStartDate.setTime( startDate );
            tempEndDate.setTime( endDate );

            tempStartDate.roll( Calendar.YEAR, -1 );
            tempEndDate.roll( Calendar.YEAR, -1 );
        }
        else if ( deType.equalsIgnoreCase( ReportDesignPeriodType.RDPT_CCMPY ) )
        {
            tempStartDate.setTime( startDate );
            tempEndDate.setTime( endDate );

            tempStartDate.roll( Calendar.YEAR, -1 );
            tempEndDate.roll( Calendar.YEAR, -1 );

            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
        }
        else if ( deType.equalsIgnoreCase( ReportDesignPeriodType.RDPT_PMCY ) )
        {
            tempStartDate.setTime( previousPeriod.getStartDate() );
            tempEndDate.setTime( previousPeriod.getEndDate() );
        }
        else
        {
            tempStartDate.setTime( startDate );
            tempEndDate.setTime( endDate );
        }

        // System.out.print(deType+" -- ");
        calendarList.add( tempStartDate );
        calendarList.add( tempEndDate );

        return calendarList;
    }
  
    public List<Period> getMonthlyPeriods( Date start, Date end )
    {
        List<Period> periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( start, end ) );
        PeriodType monthlyPeriodType = getPeriodTypeObject( "monthly" );

        List<Period> monthlyPeriodList = new ArrayList<Period>();
        Iterator<Period> it = periodList.iterator();
        while ( it.hasNext() )
        {
            Period period = (Period) it.next();
            if ( period.getPeriodType().getId() == monthlyPeriodType.getId() )
            {
                monthlyPeriodList.add( period );
            }
        }
        return monthlyPeriodList;
    }

    /*
     * Returns the PeriodType Object based on the Period Type Name For ex:- if
     * we pass name as Monthly then it returns the PeriodType Object for Monthly
     * PeriodType If there is no such PeriodType returns null
     */
    public PeriodType getPeriodTypeObject( String periodTypeName )
    {
        Collection<PeriodType> periodTypes = periodService.getAllPeriodTypes();
        PeriodType periodType = null;
        Iterator<PeriodType> iter = periodTypes.iterator();
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
    public List<OrganisationUnit> getAllChildren( OrganisationUnit selecteOU )
    {
        List<OrganisationUnit> ouList = new ArrayList<OrganisationUnit>();
        Iterator<OrganisationUnit> it = selecteOU.getChildren().iterator();
        while ( it.hasNext() )
        {
            OrganisationUnit orgU = (OrganisationUnit) it.next();
            ouList.add( orgU );
        }
        return ouList;
    }

    /*
     * Returns the PeriodType Object for selected DataElement, If no PeriodType
     * is found then by default returns Monthly Period type
     */
    public PeriodType getDataElementPeriodType( DataElement de )
    {
        List<DataSet> dataSetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        Iterator<DataSet> it = dataSetList.iterator();
        while ( it.hasNext() )
        {
            DataSet ds = (DataSet) it.next();
            List<DataElement> dataElementList = new ArrayList<DataElement>( ds.getDataElements() );
            if ( dataElementList.contains( de ) )
            {
                return ds.getPeriodType();
            }
        }

        return null;

    } // getDataElementPeriodType end

}
