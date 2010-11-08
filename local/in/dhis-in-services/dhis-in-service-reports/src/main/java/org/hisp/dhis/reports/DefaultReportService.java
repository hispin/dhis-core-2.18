package org.hisp.dhis.reports;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.system.util.MathUtils;
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
    private static final String NULL_REPLACEMENT = "0";
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
    
    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
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
    // added services 28/08/2010
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private DataElementCategoryService dataElementCategoryOptionComboService;
    
    public void setDataElementCategoryOptionComboService( DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
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
    // for Report Result Action input/otput
    // -------------------------------------------------------------------------

    
   // private String reportModelTB;

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

        if ( lltype.equalsIgnoreCase( "lllivebirth-l4DECodes.xml" ) || lltype.equalsIgnoreCase( "lllivebirth-l5DECodes.xml" ) || lltype.equalsIgnoreCase( "lllivebirth-l6DECodes.xml" ) )
        {
            dataElementid = 1020;
        }
        else if ( lltype.equalsIgnoreCase( "lldeath-l4DECodes.xml" ) || lltype.equalsIgnoreCase( "lldeath-l5DECodes.xml" ) || lltype.equalsIgnoreCase( "lldeath-l6DECodes.xml" ) )
        {
           dataElementid = 1027;
        }
        else if ( lltype.equalsIgnoreCase( "llmaternaldeath-l4DECodes.xml" ) || lltype.equalsIgnoreCase( "llmaternaldeath-l5DECodes.xml" ) || lltype.equalsIgnoreCase( "llmaternaldeath-l6DECodes.xml" ) )
        {
            dataElementid = 1032;
        }
        
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
        //System.out.println( lastDay.toString() );
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

    
    // -------------------------------------------------------------------------
    // Get Aggregated Result for dataelement expression 
    // -------------------------------------------------------------------------
    public String getResultDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit , String reportModelTB )
    {
        int deFlag1 = 0;
        int isAggregated = 0;

        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1, replaceString
                    .length() );

                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );

                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId );

                if ( dataElement == null || optionCombo == null )
                {
                    replaceString = "";
                    matcher.appendReplacement( buffer, replaceString );
                    continue;
                }
                if ( dataElement.getType().equalsIgnoreCase( "int" ) )
                {
                    Double aggregatedValue = aggregationService.getAggregatedDataValue( dataElement, optionCombo,
                        startDate, endDate, organisationUnit );
                    if ( aggregatedValue == null )
                    {
                        replaceString = NULL_REPLACEMENT;
                    }
                    else
                    {
                        replaceString = String.valueOf( aggregatedValue );

                        isAggregated = 1;
                    }

                }
                else
                {
                    deFlag1 = 1;
                    PeriodType dePeriodType = getDataElementPeriodType( dataElement );
                    List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType(
                        dePeriodType, startDate, endDate ) );
                    Period tempPeriod = new Period();
                    if ( periodList == null || periodList.isEmpty() )
                    {
                        replaceString = "";
                        matcher.appendReplacement( buffer, replaceString );
                        continue;
                    }
                    else
                    {
                        tempPeriod = (Period) periodList.get( 0 );
                    }

                    DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, tempPeriod,
                        optionCombo );

                    if ( dataValue != null && dataValue.getValue() != null )
                    {
                        replaceString = dataValue.getValue();
                    }
                    else
                    {
                        replaceString = "";
                    }

                    if ( replaceString == null )
                    {
                        replaceString = "";
                    }
                }
                matcher.appendReplacement( buffer, replaceString );

                resultValue = replaceString;
            }

            matcher.appendTail( buffer );

            if ( deFlag1 == 0 )
            {

                double d = 0.0;
                try
                {
                    d = MathUtils.calculateExpression( buffer.toString() );
                }
                catch ( Exception e )
                {
                    d = 0.0;
                    resultValue = "";
                }
                if ( d == -1 )
                {
                    d = 0.0;
                    resultValue = "";
                }
                else
                {
                    // This is to display financial data as it is like 2.1476838
                    resultValue = "" + d;

                    // These lines are to display financial data that do not
                    // have decimals
                    d = d * 10;
                    if ( d % 10 == 0 )
                    {
                        resultValue = "" + (int) d / 10;
                    }

                    d = d / 10;

                    // These line are to display non financial data that do not
                    // require decimals
                    if ( !(reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" )) )
                    {
                        resultValue = "" + (int) d;
                    }
                }

            }
            else
            {
                resultValue = buffer.toString();
            }

            if( isAggregated == 0 )
            {
                resultValue = " ";
            }

            if ( resultValue.equalsIgnoreCase( "" ) )
            {
                resultValue = " ";
            }

            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }


    // -------------------------------------------------------------------------
    // Get Individual Result for dataelement expression 
    // -------------------------------------------------------------------------
    public String getIndividualResultDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit , String reportModelTB )
    {
        int deFlag1 = 0;
       
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";
            boolean valueDoesNotExist = true;

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1, replaceString.length() );

                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );

                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId );

                if ( dataElement == null || optionCombo == null )
                {
                    replaceString = "";
                    matcher.appendReplacement( buffer, replaceString );
                    continue;
                }
                if ( dataElement.getType().equalsIgnoreCase( "int" ) )
                {

                    PeriodType dePeriodType = getDataElementPeriodType( dataElement );
                    List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType( dePeriodType, startDate, endDate ) );

                    if ( periodList == null || periodList.isEmpty() )
                    {
                        replaceString = "";
                        matcher.appendReplacement( buffer, replaceString );
                        continue;
                    }
                    else
                    {
                        double aggregatedValue = 0.0;
                        for ( Period tempPeriod : periodList )
                        {
                            DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement,
                                tempPeriod, optionCombo );

                            if ( dataValue != null )
                            {
                                aggregatedValue += Double.parseDouble( dataValue.getValue() );

                                valueDoesNotExist = false;
                            }
                        }
                        replaceString = String.valueOf( aggregatedValue );
                    }
                }
                else
                {
                    deFlag1 = 1;
                    PeriodType dePeriodType = getDataElementPeriodType( dataElement );
                    List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType( dePeriodType, startDate, endDate ) );
                    Period tempPeriod = new Period();
                    if ( periodList == null || periodList.isEmpty() )
                    {
                        replaceString = "";
                        matcher.appendReplacement( buffer, replaceString );
                        continue;
                    }
                    else
                    {
                        tempPeriod = (Period) periodList.get( 0 );
                    }

                    DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, tempPeriod, optionCombo );

                    if ( dataValue != null )
                    {
                        replaceString = dataValue.getValue();
                        valueDoesNotExist = false;
                    }
                    else
                    {
                        replaceString = "";
                    }

                    if ( replaceString == null )
                    {
                        replaceString = "";
                    }
                }
                matcher.appendReplacement( buffer, replaceString );

                resultValue = replaceString;
            }

            matcher.appendTail( buffer );

            if ( deFlag1 == 0 )
            {
                double d = 0.0;
                try
                {
                    d = MathUtils.calculateExpression( buffer.toString() );
                }
                catch ( Exception e )
                {
                    d = 0.0;

                    resultValue = "";
                }
                if ( d == -1 )
                {
                    d = 0.0;

                    resultValue = "";
                }
                else
                {
                    // This is to display financial data as it is like 2.1476838
                    resultValue = "" + d;

                    // These lines are to display financial data that do not
                    // have decimals
                    d = d * 10;

                    if ( d % 10 == 0 )
                    {
                        resultValue = "" + (int) d / 10;
                    }

                    d = d / 10;

                    // These line are to display non financial data that do not
                    // require decimals
                    if ( !(reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" )) )
                    {
                        resultValue = "" + (int) d;
                    }
                }
            }
            else
            {
                resultValue = buffer.toString();
            }

            if ( valueDoesNotExist )
            {
                resultValue = " ";
            }

            if ( resultValue.equalsIgnoreCase( "" ) )
            {
                resultValue = " ";
            }

            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }
    
     // functoin getBooleanDataValue stsrt
    
    public String getBooleanDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit , String reportModelTB )
    {
        int deFlag1 = 0;
        int deFlag2 = 0;
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1, replaceString.length() );

                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );

                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId );

                if ( dataElement == null || optionCombo == null )
                {
                    replaceString = "";
                    matcher.appendReplacement( buffer, replaceString );
                    continue;
                }

                if ( dataElement.getType().equalsIgnoreCase( "bool" ) )
                {
                    deFlag1 = 1;
                    deFlag2 = 0;
                    PeriodType dePeriodType = getDataElementPeriodType( dataElement );
                    List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType(
                        dePeriodType, startDate, endDate ) );
                    Period tempPeriod = new Period();
                    if ( periodList == null || periodList.isEmpty() )
                    {
                        replaceString = "";
                        matcher.appendReplacement( buffer, replaceString );
                        continue;
                    }
                    else
                    {
                        tempPeriod = (Period) periodList.get( 0 );
                    }

                    DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, tempPeriod, optionCombo );

                    if ( dataValue != null )
                    {
                        // Works for both text and boolean data types

                        if ( dataValue.getValue().equalsIgnoreCase( "true" ) )
                        {
                            replaceString = "Yes";
                        }
                        else if ( dataValue.getValue().equalsIgnoreCase( "false" ) )
                        {
                            replaceString = "No";
                        }
                        else
                        {
                            replaceString = dataValue.getValue();
                        }
                    }
                    else
                    {
                        replaceString = "";
                    }
                }
                else
                {
                    Double aggregatedValue = aggregationService.getAggregatedDataValue( dataElement, optionCombo,
                        startDate, endDate, organisationUnit );
                    if ( aggregatedValue == null )
                    {
                        replaceString = NULL_REPLACEMENT;
                    }
                    else
                    {
                        replaceString = String.valueOf( aggregatedValue );

                        deFlag2 = 1;
                    }
                }
                matcher.appendReplacement( buffer, replaceString );
            }

            matcher.appendTail( buffer );

            String resultValue = "";
            if ( deFlag1 == 0 )
            {
                double d = 0.0;
                try
                {
                    d = MathUtils.calculateExpression( buffer.toString() );
                }
                catch ( Exception e )
                {
                    d = 0.0;
                }
                if ( d == -1 )
                {
                    d = 0.0;
                }
                else
                {
                    d = Math.round( d * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                    resultValue = "" + (int) d;
                }

                if ( deFlag2 == 0 )
                {
                    resultValue = " ";
                }
            }
            else
            {
                deFlag2 = 0;
                resultValue = buffer.toString();
            }
            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }
    
 // functoin getBooleanDataValue end
    
    //function getStartingEndingPeriods starts
    
    public List<Calendar> getStartingEndingPeriods( String deType , Period selectedPeriod)
    {

        List<Calendar> calendarList = new ArrayList<Calendar>();

        Calendar tempStartDate = Calendar.getInstance();
        Calendar tempEndDate = Calendar.getInstance();

        Period previousPeriod = new Period();
        previousPeriod = getPreviousPeriod( selectedPeriod );

        if ( deType.equalsIgnoreCase( "ccmcy" ) )
        {
            tempStartDate.setTime( selectedPeriod.getStartDate() );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            tempEndDate.setTime( selectedPeriod.getEndDate() );
            // System.out.println("CCMCY : "+ String.valueOf(
            // tempStartDate.getTime()) +" ------ "+String.valueOf(
            // tempEndDate.getTime()));
        } 
        else if ( deType.equalsIgnoreCase( "cpmcy" ) )
            {
                tempStartDate.setTime( previousPeriod.getStartDate() );
                if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
                {
                    tempStartDate.roll( Calendar.YEAR, -1 );
                }
                tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
                tempEndDate.setTime( previousPeriod.getEndDate() );
            } 
        else if ( deType.equalsIgnoreCase( "cmpy" ) )
                {
                    tempStartDate.setTime( selectedPeriod.getStartDate() );
                    tempEndDate.setTime( selectedPeriod.getEndDate() );

                    tempStartDate.roll( Calendar.YEAR, -1 );
                    tempEndDate.roll( Calendar.YEAR, -1 );
                } 
        else if ( deType.equalsIgnoreCase( "ccmpy" ) )
                    {
                        tempStartDate.setTime( selectedPeriod.getStartDate() );
                        tempEndDate.setTime( selectedPeriod.getEndDate() );

                        tempStartDate.roll( Calendar.YEAR, -1 );
                        tempEndDate.roll( Calendar.YEAR, -1 );

                        if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
                        {
                            tempStartDate.roll( Calendar.YEAR, -1 );
                        }
                        tempStartDate.set( Calendar.MONTH, Calendar.APRIL );

                    } else if ( deType.equalsIgnoreCase( "pmcy" ) )
                        {
                            tempStartDate.setTime( previousPeriod.getStartDate() );
                            tempEndDate.setTime( previousPeriod.getEndDate() );
                        } else
                        {
                            tempStartDate.setTime( selectedPeriod.getStartDate() );
                            tempEndDate.setTime( selectedPeriod.getEndDate() );
                        }

        // System.out.print(deType+" -- ");
        calendarList.add( tempStartDate );
        calendarList.add( tempEndDate );

        return calendarList;
    }
  //function getStartingEndingPeriods end
    
    //function getPreviousPeriod starts
    
    public Period getPreviousPeriod( Period selectedPeriod )
    {
        Period period = new Period();
        Calendar tempDate = Calendar.getInstance();
        tempDate.setTime( selectedPeriod.getStartDate() );
        if ( tempDate.get( Calendar.MONTH ) == Calendar.JANUARY )
        {
            tempDate.set( Calendar.MONTH, Calendar.DECEMBER );
            tempDate.roll( Calendar.YEAR, -1 );

        } else
        {
            tempDate.roll( Calendar.MONTH, -1 );
        }
        PeriodType periodType = getPeriodTypeObject( "monthly" );
        period = getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ), periodType );

        return period;
    }
    
  //function getPreviousPeriod end
    
    public String getResultIndicatorValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit )
        {
    		int deFlag1 = 0;
    		int deFlag2 = 0;
    		
            try
            {
                Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

                Matcher matcher = pattern.matcher( formula );
                StringBuffer buffer = new StringBuffer();

                while ( matcher.find() )
                {
                    String replaceString = matcher.group();

                    replaceString = replaceString.replaceAll( "[\\[\\]]", "" );

                    replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                    int indicatorId = Integer.parseInt( replaceString );

                    Indicator indicator = indicatorService.getIndicator( indicatorId );

                    if ( indicator == null )
                    {
                        replaceString = "";
                        matcher.appendReplacement( buffer, replaceString );
                        continue;
                    }

                    Double aggregatedValue = aggregationService.getAggregatedIndicatorValue( indicator, startDate, endDate,
                        organisationUnit );

                    if ( aggregatedValue == null )
                    {
                        replaceString = NULL_REPLACEMENT;
                    } else
                    {
                        replaceString = String.valueOf( aggregatedValue );
                        deFlag2 = 1;
                    }
                    matcher.appendReplacement( buffer, replaceString );
                }

                matcher.appendTail( buffer );

                String resultValue = "";
                if ( deFlag1 == 0 )
                {
                    double d = 0.0;
                    try
                    {
                        d = MathUtils.calculateExpression( buffer.toString() );
                    } catch ( Exception e )
                    {
                        d = 0.0;
                    }
                    if ( d == -1 )
                    {
                        d = 0.0;
                    } else
                    {
                        d = Math.round( d * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        resultValue = "" + d;
                    }

                    if ( deFlag2 == 0 )
                    {
                        resultValue = " ";
                    }
                } else
                {
                    resultValue = buffer.toString();
                    deFlag2 = 0;
                }
                return resultValue;
            } catch ( NumberFormatException ex )
            {
                throw new RuntimeException( "Illegal DataElement id", ex );
            }
        }
    public String getIndividualResultIndicatorValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit )
        {
    		int deFlag1 = 0;
    		int deFlag2 = 0;
            try
            {
                Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

                Matcher matcher = pattern.matcher( formula );
                StringBuffer buffer = new StringBuffer();

                while ( matcher.find() )
                {
                    String replaceString = matcher.group();

                    replaceString = replaceString.replaceAll( "[\\[\\]]", "" );

                    replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                    int indicatorId = Integer.parseInt( replaceString );

                    Indicator indicator = indicatorService.getIndicator( indicatorId );

                    if ( indicator == null )
                    {
                        replaceString = "";
                        matcher.appendReplacement( buffer, replaceString );
                        continue;
                    }

                    String numeratorExp = indicator.getNumerator();
                    String denominatorExp = indicator.getDenominator();
                    int indicatorFactor = indicator.getIndicatorType().getFactor();
                    String reportModelTB = null;
					String numeratorVal = getIndividualResultDataValue( numeratorExp, startDate, endDate, organisationUnit, reportModelTB  );
                    String denominatorVal = getIndividualResultDataValue( denominatorExp, startDate, endDate, organisationUnit, reportModelTB );

                    double numeratorValue;
                    try
                    {
                        numeratorValue = Double.parseDouble( numeratorVal );
                    } catch ( Exception e )
                    {
                        numeratorValue = 0.0;
                    }

                    double denominatorValue;
                    try
                    {
                        denominatorValue = Double.parseDouble( denominatorVal );
                    } catch ( Exception e )
                    {
                        denominatorValue = 1.0;
                    }

                    double aggregatedValue;
                    try
                    {
                        aggregatedValue = ( numeratorValue / denominatorValue ) * indicatorFactor;
                    } 
                    catch ( Exception e )
                    {
                        System.out.println( "Exception while calculating Indicator value for Indicaotr " + indicator.getName() );
                        aggregatedValue = 0.0;
                    }

                    replaceString = String.valueOf( aggregatedValue );
                    deFlag2 = 1;

                    matcher.appendReplacement( buffer, replaceString );
                }

                matcher.appendTail( buffer );

                String resultValue = "";
                if ( deFlag1 == 0 )
                {
                    double d = 0.0;
                    try
                    {
                        d = MathUtils.calculateExpression( buffer.toString() );
                    } catch ( Exception e )
                    {
                        d = 0.0;
                    }
                    if ( d == -1 )
                    {
                        d = 0.0;
                    } else
                    {
                        d = Math.round( d * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        resultValue = "" + d;
                    }

                    if ( deFlag2 == 0 )
                    {
                        resultValue = " ";
                    }
                } else
                {
                    deFlag2 = 0;
                    resultValue = buffer.toString();
                }
                return resultValue;
            } catch ( NumberFormatException ex )
            {
                throw new RuntimeException( "Illegal DataElement id", ex );
            }
        }
    
    // -------------------------------------------------------------------------
    // Get Aggregated Result for dataelement expression 
    // -------------------------------------------------------------------------

    public List<Report_inDesign> getReportDesign( String fileName )
    {
        List<Report_inDesign> reportDesignList = new ArrayList<Report_inDesign>();
        
        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue()
            + File.separator + fileName;
        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue() + File.separator + fileName;
            }
        }
        catch ( NullPointerException npe )
        {
            System.out.println("DHIS2_HOME not set");
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                System.out.println( "There is no DECodes related XML file in the ra folder" );
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
                
                Report_inDesign report_inDesign = new Report_inDesign( stype, ptype, sheetno, rowno, colno, expression );
                reportDesignList.add( report_inDesign );
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
        return reportDesignList;
    }
    
}
