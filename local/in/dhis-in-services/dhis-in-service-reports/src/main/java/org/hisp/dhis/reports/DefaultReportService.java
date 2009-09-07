package org.hisp.dhis.reports;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datamart.DataMartStore;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.system.util.MathUtils;
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

    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementCategoryOptionComboService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryOptionComboService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }
    
    private DataMartStore dataMartStore;
    
    public void setDataMartStore( DataMartStore dataMartStore )
    {
        this.dataMartStore = dataMartStore;
    }


    // -------------------------------------------------------------------------
    // Report_in
    // -------------------------------------------------------------------------

    public int addReport( Report_in report )
    {
        return reportStore.addReport( report );
    }

    public void deleteReport( Report_in report )
    {
        reportStore.deleteReport( report );
    }

    public void updateReport( Report_in report )
    {
        reportStore.updateReport( report );
    }

    public Collection<Report_in> getAllReports()
    {
        return reportStore.getAllReports();
    }

    public Report_in getReport( int id )
    {
        return reportStore.getReport( id );
    }

    public Report_in getReportByName( String name )
    {
        return reportStore.getReportByName( name );
    }

    public Collection<Report_in> getReportBySource( Source source )
    {
        return reportStore.getReportBySource( source );
    }

    public Collection<Report_in> getReportsByPeriodAndReportType( PeriodType periodType, String reportType )
    {
        return reportStore.getReportsByPeriodAndReportType( periodType, reportType );
    }

    public Collection<Report_in> getReportsByPeriodType( PeriodType periodType )
    {
        return reportStore.getReportsByPeriodType( periodType );
    }

    public Collection<Report_in> getReportsByReportType( String reportType )
    {
        return reportStore.getReportsByReportType( reportType );
    }
    
    public Collection<Report_in> getReportsByPeriodSourceAndReportType( PeriodType periodType, Source source, String reportType )
    {
        return reportStore.getReportsByPeriodSourceAndReportType( periodType, source, reportType );
    }

    
    public List<Report_inDesign> getReportDesign( Report_in report )
    {
        List<Report_inDesign> deCodes = new ArrayList<Report_inDesign>();
        String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        
        String path = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + report.getXmlTemplateName();
        
        if( path == null )
        {
            System.out.println("DHIS2_HOME is not set");
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
                int rowno =  new Integer( deCodeElement.getAttribute( "rowno" ) );
                int colno = new Integer( deCodeElement.getAttribute( "colno" ) );
                int rowMerge = new Integer( deCodeElement.getAttribute( "rowmerge" ) );
                int colMerge = new Integer( deCodeElement.getAttribute( "colmerge" ) );
                
                Report_inDesign reportDesign = new Report_inDesign( stype, ptype, sheetno, rowno, colno, rowMerge, colMerge, expression );
                
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
    public Period getPreviousPeriod(Date startDate, Date endDate)
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

    
    public String getResultDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit, String aggCB )
    {
        try
        {
            int deFlag1 = 0;
            int deFlag2 = 0;
            
            List<Period> periodList = new ArrayList<Period>(periodStore.getIntersectingPeriods( startDate, endDate ));
            
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

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
                    //double aggregatedValue = aggregationService.getAggregatedDataValue( dataElement, optionCombo, startDate, endDate, organisationUnit );
                    
                    double aggregatedValue = 0.0;
                    for(Period p : periodList)
                    {
                        double tempAggVal = dataMartStore.getAggregatedValue( dataElement, optionCombo, p, organisationUnit );                                    

                        if( tempAggVal != DataMartStore.NO_VALUES_REGISTERED )
                        {
                            aggregatedValue += tempAggVal;
                        }
                    }    
                    
                    replaceString = String.valueOf( aggregatedValue );

                    deFlag2 = 1;                                                                                                                               
                }
                else
                {
                    deFlag1 = 1;
                                           
                    for( Period p : periodList )
                    {
                        DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, p, optionCombo );

                        if ( dataValue != null )
                        {                            
                            replaceString += dataValue.getValue();
                        }                        
                    }

                    if ( replaceString == null )
                        replaceString = "";
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
                    //if ( !(reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" )) )
                    //    resultValue = "" + (int) d;

                    // if ( resultValue.equalsIgnoreCase( "0" ) )
                    // {
                    // resultValue = "";
                    // }
                }

            }
            else
            {
                resultValue = buffer.toString();
            }

            if ( resultValue.equalsIgnoreCase( "" ) )
                resultValue = " ";

            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }
}
