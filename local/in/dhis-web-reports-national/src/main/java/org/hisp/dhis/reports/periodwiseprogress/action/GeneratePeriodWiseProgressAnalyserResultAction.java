package org.hisp.dhis.reports.periodwiseprogress.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.apache.velocity.tools.generic.MathTool;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.system.util.MathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

public class GeneratePeriodWiseProgressAnalyserResultAction
    implements Action
{
    private static final String NULL_REPLACEMENT = "0";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private DataElementCategoryService dataElementCategoryOptionComboService;
    
    public void setDataElementCategoryOptionComboService( DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    /*
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
*/
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
/*
    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }
*/
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    /*
     * private String contentType;
     * 
     * public String getContentType() { return contentType; }
     */
    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    /*
     * private int bufferSize;
     * 
     * public int getBufferSize() { return bufferSize; }
     */
    private MathTool mathTool;

    public MathTool getMathTool()
    {
        return mathTool;
    }

    // private OrganisationUnit selectedOrgUnit;
    // public OrganisationUnit getSelectedOrgUnit()
    // {
    // return selectedOrgUnit;
    // }
    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    private Period selectedPeriod;

    public Period getSelectedPeriod()
    {
        return selectedPeriod;
    }

    private List<String> dataValueList;

    public List<String> getDataValueList()
    {
        return dataValueList;
    }

    private List<String> services;

    public List<String> getServices()
    {
        return services;
    }

    private List<String> slNos;

    public List<String> getSlNos()
    {
        return slNos;
    }

    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    private SimpleDateFormat monthFormat;

    public SimpleDateFormat getMonthFormat()
    {
        return monthFormat;
    }

    private SimpleDateFormat simpleMonthFormat;

    public SimpleDateFormat getSimpleMonthFormat()
    {
        return simpleMonthFormat;
    }

    private SimpleDateFormat yearFormat;

    public SimpleDateFormat getYearFormat()
    {
        return yearFormat;
    }

    private SimpleDateFormat simpleYearFormat;

    public SimpleDateFormat getSimpleYearFormat()
    {
        return simpleYearFormat;
    }

    private List<String> deCodeType;

    private List<String> serviceType;

    private String reportFileNameTB;

    public void setReportFileNameTB( String reportFileNameTB )
    {
        this.reportFileNameTB = reportFileNameTB;
    }

    private String reportModelTB;

    public void setReportModelTB( String reportModelTB )
    {
        this.reportModelTB = reportModelTB;
    }

    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }
/*
    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    private List<String> orgUnitListCB;

    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }
*/
    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private int availablePeriods;

    public void setAvailablePeriods( int availablePeriods )
    {
        this.availablePeriods = availablePeriods;
    }

    private String aggCB;

    public void setAggCB( String aggCB )
    {
        this.aggCB = aggCB;
    }

//    private Hashtable<String, String> serviceList;

    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    private Date sDate;

    private Date eDate;

    private Date sDateTemp;

    private Date eDateTemp;

    private PeriodType periodType;

    public PeriodType getPeriodType()
    {
        return periodType;
    }

    private List<Period> periods;

    public List<Period> getPeriods()
    {
        return periods;
    }

//    private List<Integer> totalOrgUnitsCountList;

    private String raFolderName;

    private List<OrganisationUnit> childOrgUnits;

    public List<OrganisationUnit> getChildOrgUnits()
    {
        return childOrgUnits;
    }

    Integer startMonth;

    Integer endMonth;

    private Map<String, String> months;

    public Map<String, String> getMonths()
    {
        return months;
    }

    private Map<String, Integer> monthOrder;

    public Map<String, Integer> getMonthOrder()
    {
        return monthOrder;
    }

    private Map<Integer, Integer> mapOfTotalValues;

    public void setMapOfTotalValues( Map<Integer, Integer> mapOfTotalValues )
    {
        this.mapOfTotalValues = mapOfTotalValues;
    }

    private int startRowNumber;

    public void setStartRowNumber( int startRowNumber )
    {
        this.startRowNumber = startRowNumber;
    }

    private int totalColumnNumber;

    public void setTotalColumnNumber( int totalColumnNumber )
    {
        this.totalColumnNumber = totalColumnNumber;
    }

    private int sheetNo;

    public void setSheetNo( int sheetNo )
    {
        this.sheetNo = sheetNo;
    }

    private int tempColNo;

    public void setTempColNo( int tempColNo )
    {
        this.tempColNo = tempColNo;
    }

    private int tempRowNo;

    public void setTempRowNo( int tempRowNo )
    {
        this.tempRowNo = tempRowNo;
    }

    int deFlag2;

    int deFlag1;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        deFlag2 = 0;
        deFlag1 = 0;
        statementManager.initialise();
        // Initialization
        raFolderName = reportService.getRAFolderName();

        mathTool = new MathTool();
        services = new ArrayList<String>();
        slNos = new ArrayList<String>();
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();
//      totalOrgUnitsCountList = new ArrayList<Integer>();
//      String deCodesXMLFileName = "";
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        monthFormat = new SimpleDateFormat( "MMMM" );
        simpleMonthFormat = new SimpleDateFormat( "MMM" );
        yearFormat = new SimpleDateFormat( "yyyy" );
        simpleYearFormat = new SimpleDateFormat( "yy" );
//      deCodesXMLFileName = reportList + "DECodes.xml";
        mapOfTotalValues = new HashMap<Integer, Integer>();
        int tempNum = 0;
        startMonth = 0;

        endMonth = 0;

        // Getting Report Details       
        String deCodesXMLFileName = "";

        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        
        System.out.println( reportModelTB + " : " + reportFileNameTB + " : " + deCodesXMLFileName + " : " + ouIDTB );
        System.out.println( "Report Generation Start Time is : \t" + new Date() );

        String parentUnit = "";

        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();

        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );

        if ( reportModelTB.equalsIgnoreCase( "PROGRESSIVE-PERIOD" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );

            System.out.println( "ORGUNIT IS : \t\t\t" + orgUnit.getName() );
            orgUnitList.add( orgUnit );

        }

        Map<String, String> months = new HashMap<String, String>();
        Map<String, Integer> monthOrder = new HashMap<String, Integer>();

        months.put( "April", "04" );
        months.put( "May", "05" );
        months.put( "June", "06" );
        months.put( "July", "07" );
        months.put( "August", "08" );
        months.put( "September", "09" );
        months.put( "October", "10" );
        months.put( "November", "11" );
        months.put( "December", "12" );
        months.put( "January", "01" );
        months.put( "February", "02" );
        months.put( "March", "03" );

        monthOrder.put( "April", 1 );
        monthOrder.put( "May", 2 );
        monthOrder.put( "June", 3 );
        monthOrder.put( "July", 4 );
        monthOrder.put( "August", 5 );
        monthOrder.put( "September", 6 );
        monthOrder.put( "October", 7 );
        monthOrder.put( "November", 8 );
        monthOrder.put( "December", 9 );
        monthOrder.put( "January", 10 );
        monthOrder.put( "February", 11 );
        monthOrder.put( "March", 12 );

        String[] monthArray = new String[12];

        monthArray[0] = "April";
        monthArray[1] = "May";
        monthArray[2] = "June";
        monthArray[3] = "July";
        monthArray[4] = "August";
        monthArray[5] = "September";
        monthArray[6] = "October";
        monthArray[7] = "November";
        monthArray[8] = "December";
        monthArray[9] = "January";
        monthArray[10] = "February";
        monthArray[11] = "March";

        selectedPeriod = periodService.getPeriod( availablePeriods );

        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );

        eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );

        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );

        Calendar tempStartMonth = Calendar.getInstance();
        Calendar tempEndMonth = Calendar.getInstance();
        tempStartMonth.setTime( selectedPeriod.getStartDate() );
        if ( tempStartMonth.get( Calendar.MONTH ) == Calendar.JANUARY || tempStartMonth.get( Calendar.MONTH ) == Calendar.FEBRUARY || tempStartMonth.get( Calendar.MONTH ) == Calendar.MARCH )
        {
            tempStartMonth.set( Calendar.MONTH, Calendar.APRIL );
            tempStartMonth.roll( Calendar.YEAR, -1 );

        } else
        {
            tempStartMonth.set( Calendar.MONTH, Calendar.APRIL );
        }

        String startMonthName = "";

        String endMonthName = "";

        startMonth = Calendar.MONTH;

        startMonthName = monthFormat.format( tempStartMonth.getTime() );

        tempEndMonth.setTime( selectedPeriod.getStartDate() );

        endMonth = Calendar.MONTH;

        endMonthName = monthFormat.format( tempEndMonth.getTime() );

        // Getting DataValues
        dataValueList = new ArrayList<String>();
        List<String> deCodesList = getDECodes( deCodesXMLFileName );

        Iterator<OrganisationUnit> it = orgUnitList.iterator();

        int rowCounter = 0;

        int monthCount = 0;

        // ---------------------------------------------------------------------------------------------------
        // All other reports START
        // ---------------------------------------------------------------------------------------------------

        String currentMonth = "";

        OrganisationUnit currentOrgUnit = new OrganisationUnit();

        while ( it.hasNext() )
        {
            currentOrgUnit = (OrganisationUnit) it.next();

        }

        int currentMonthOrder = monthOrder.get( monthFormat.format( tempEndMonth.getTime() ) );

        Boolean done = false;

        while ( monthCount < currentMonthOrder )
        {

            currentMonth = monthArray[monthCount];

            done = false;

            //int quarterPeriod = 0;

            Iterator<String> it1 = deCodesList.iterator();
            int count1 = 0;
            while ( it1.hasNext() && done == false )
            {

                String deCodeString = (String) it1.next();

                String deType = (String) deCodeType.get( count1 );
                String sType = (String) serviceType.get( count1 );
                //int count = 0;
                //double sum = 0.0;
               // int flag1 = 0;
                String tempStr = "";

                tempRowNo = rowList.get( count1 );
                tempColNo = colList.get( count1 );
                sheetNo = sheetList.get( count1 );
                Calendar tempStart = Calendar.getInstance();
                Calendar tempEnd = Calendar.getInstance();

                months.get( currentMonth );

                String tempS = "01-" + months.get( currentMonth ) + "-" + tempStartMonth.get( Calendar.YEAR );
                String tempE = String.valueOf( tempStartMonth.getActualMaximum( Calendar.DAY_OF_MONTH ) ) + "-" + months.get( currentMonth ) + "-" + tempStartMonth.get( Calendar.YEAR );

                DateFormat tempMonthFormat;

                tempMonthFormat = new SimpleDateFormat( "dd-MM-yy" );

                Date tempDate = new Date();

                tempStart.setTime( tempStartMonth.getTime() );

                tempDate = (Date) tempMonthFormat.parse( tempS );

                tempDate = (Date) tempMonthFormat.parse( tempE );

                tempEnd.setTime( tempDate );

                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                List<Calendar> calendarList = new ArrayList<Calendar>( getStartingEndingPeriods( deType, tempStart,
                    tempEnd ) );
                if ( calendarList == null || calendarList.isEmpty() )
                {

                    tempStr = currentMonth;

                } else
                {
                    tempStartDate = calendarList.get( 0 );
                    tempEndDate = calendarList.get( 1 );

                }

                if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = currentOrgUnit.getName();
                    deFlag2 = 0;
                } else
                {
                    if ( deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) )
                    {
                        tempStr = parentUnit;
                        deFlag2 = 0;
                    } else
                    {
                        if ( deCodeString.equalsIgnoreCase( "MONTH-RANGE" ) )
                        {
                            tempStr = startMonthName + " - " + endMonthName;
                            deFlag2 = 0;
                        } else
                        {
                            if ( deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                            {

                                sDateTemp = sDate;

                                eDateTemp = eDate;

                                Calendar tempQuarterYear = Calendar.getInstance();

                                String startYear = "";

                                String endYear = "";

                                String startMonth = "";

                                startMonth = monthFormat.format( sDateTemp );

                                periodType = selectedPeriod.getPeriodType();

                                tempQuarterYear.setTime( sDateTemp );

                                if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                                {
                                    sDateTemp = sDate;
                                } else
                                {
                                    if ( ( startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth.equalsIgnoreCase( "March" ) ) )
                                    {
                                        tempQuarterYear.roll( Calendar.YEAR, -1 );

                                        sDateTemp = tempQuarterYear.getTime();

                                    }
                                }

                                startYear = yearFormat.format( sDateTemp );

                                tempQuarterYear.setTime( eDateTemp );

                                if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                                {
                                    tempQuarterYear.roll( Calendar.YEAR, 1 );

                                    eDateTemp = tempQuarterYear.getTime();
                                }

                                if ( !( startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth.equalsIgnoreCase( "March" ) ) )
                                {
                                    tempQuarterYear.roll( Calendar.YEAR, 1 );

                                    eDateTemp = tempQuarterYear.getTime();

                                }
                                endYear = yearFormat.format( eDateTemp );

                                tempStr = startYear + " - " + endYear;
                                deFlag2 = 0;
                            } else
                            {
                                if ( deCodeString.equalsIgnoreCase( "PROGRESSIVE-PERIOD" ) )
                                {
                                    Period p = new Period();

                                    p = periodService.getPeriod( tempStartDate.getTime(), tempEndDate.getTime(), periodService.getPeriodTypeByName( "Monthly" ) );

                                    startRowNumber = tempRowNo;

                                    if ( p == null )
                                    {
                                        tempStr = currentMonth;

                                    } else
                                    {
                                        tempStr = monthFormat.format( p.getStartDate() );
                                    }
                                    deFlag2 = 0;
                                } else
                                {
                                    if ( deCodeString.equalsIgnoreCase( "NA" ) )
                                    {
                                        tempStr = " ";
                                        deFlag2 = 0;
                                    } else
                                    {
                                        rowCounter += 1;
                                        
                                        if ( sType.equalsIgnoreCase( "dataelement" ) )
                                        {
                                            if ( aggCB == null )
                                            {
                                                tempStr = getIndividualResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                                            } else
                                            {
                                                tempStr = getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(),
                                                    currentOrgUnit );
                                            }
                                            if ( deFlag2 == 1 )
                                            {

                                                try{
                                                    tempNum = Integer.parseInt( tempStr );
                                                    }
                                                    catch(Exception ex){
                                                        tempNum = 0;
                                                    }


                                            }
                                            int totalRowValue = 0;

                                            if ( mapOfTotalValues.get( tempRowNo ) != null )
                                            {

                                                totalRowValue = mapOfTotalValues.get( tempRowNo );

                                                if ( !( tempStr.equalsIgnoreCase( " " ) || tempStr.equalsIgnoreCase( "" ) ) )
                                                {
                                                    totalRowValue += Integer.valueOf( tempStr );
                                                }

                                                mapOfTotalValues.put( tempRowNo, totalRowValue );

                                            } else
                                            {

                                                if ( !( tempStr.equalsIgnoreCase( " " ) || tempStr.equalsIgnoreCase( "" ) ) )
                                                {
                                                    totalRowValue += Integer.valueOf( tempStr );
                                                }

                                                mapOfTotalValues.put( tempRowNo, totalRowValue );
                                            }
                                            //System.out.println("totalRowValue = "+totalRowValue);
                                        } else
                                        {
                                            if ( sType.equalsIgnoreCase( "indicator-parent" ) )
                                            {
                                                if ( aggCB == null )
                                                {
                                                    tempStr = getIndividualResultIndicatorValue( deCodeString, tempStartDate.getTime(),
                                                        tempEndDate.getTime(), currentOrgUnit.getParent() );
                                                } else
                                                {
                                                    tempStr = getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit.getParent() );
                                                }
                                                if ( deFlag2 == 1 )
                                                {
                                                    try{
                                                        tempNum = Integer.parseInt( tempStr );
                                                        }
                                                        catch(Exception ex){
                                                            tempNum = 0;
                                                        }

                                                }
                                            } else
                                            {
                                                if ( sType.equalsIgnoreCase( "dataelement-boolean" ) )
                                                {
                                                    if ( aggCB == null )
                                                    {
                                                        tempStr = getBooleanDataValue( deCodeString, tempStartDate.getTime(),
                                                            tempEndDate.getTime(), currentOrgUnit );
                                                    } else
                                                    {
                                                        tempStr = getBooleanDataValue( deCodeString, tempStartDate.getTime(),
                                                            tempEndDate.getTime(), currentOrgUnit );
                                                    }
                                                    deFlag2 = 0;
                                                } else
                                                {
                                                    if ( aggCB == null )
                                                    {
                                                        tempStr = getIndividualResultIndicatorValue( deCodeString, tempStartDate.getTime(),
                                                            tempEndDate.getTime(), currentOrgUnit );
                                                    } else
                                                    {
                                                        tempStr = getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                                                    }
                                                    if(deFlag2 == 1){
                                                        try{
                                                            tempNum = Integer.parseInt( tempStr );
                                                            }
                                                            catch(Exception ex){
                                                                tempNum = 0;
                                                            }

                                                    }
                                                    System.out.println("tempstr = "+tempStr);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if ( tempStr == null || tempStr.equals( " " ) )
                {
                    tempColNo += monthCount;

                    WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                    WritableCellFormat wCellformat = new WritableCellFormat();

                    wCellformat.setBorder( Border.ALL, BorderLineStyle.MEDIUM );
                    wCellformat.setWrap( true );
                    wCellformat.setAlignment( Alignment.CENTRE );

                    sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );

                } else
                {

                    if ( reportModelTB.equalsIgnoreCase( "PROGRESSIVE-PERIOD" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "FACILITYP" ) || deCodeString.equalsIgnoreCase( "FACILITYPP" ) || deCodeString.equalsIgnoreCase( "FACILITYPPP" ) || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                        {
                            
                        } else
                        {
                            if ( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) || deCodeString.equalsIgnoreCase( "MONTH-RANGE" ) || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) || deCodeString.equalsIgnoreCase( "MONTH-START" ) || deCodeString.equalsIgnoreCase( "MONTH-END" ) || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" ) || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-START" ) || deCodeString.equalsIgnoreCase( "QUARTER-END" ) || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" ) || deCodeString.equalsIgnoreCase( "YEAR-END" ) || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                            {
                            } else
                            {
                                tempColNo += monthCount;

                            }
                        }

                        WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                        WritableCellFormat wCellformat = new WritableCellFormat();

                       // WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );

                        wCellformat.setBorder( Border.ALL, BorderLineStyle.MEDIUM );
                        wCellformat.setAlignment( Alignment.CENTRE );
                        wCellformat.setWrap( true );

                        try
                        {

                            if ( deFlag2 == 1 )
                            {

                                sheet0.addCell( new Number( tempColNo, tempRowNo, tempNum, wCellformat ) );
                            } else
                            {

                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        } catch ( Exception e )
                        {
                            System.out.println( "Cannot write to Excel" );
                        }

                    }

                }

                count1++;

            }// inner while loop end

            tempStartMonth.roll( Calendar.MONTH, 1 );

            if ( tempStartMonth.get( Calendar.MONTH ) == Calendar.JANUARY )
            {
                tempStartMonth.roll( Calendar.YEAR, 1 );

            }

            monthCount++;

        }// outer while loop end

        //Writing total values begins
        totalColumnNumber = tempColNo + 1;

        List<Integer> totalRowList = new ArrayList<Integer>();

        totalRowList.addAll( rowList );

        Iterator<Integer> totalRowListIterator = totalRowList.iterator();

        String valueToPrint = " ";

        while ( totalRowListIterator.hasNext() )
        {
            Integer i = (Integer) totalRowListIterator.next();

            if ( i < startRowNumber )
            {
                totalRowListIterator.remove();
            }
        }

        Iterator<Integer> rowIterator = totalRowList.iterator();

        while ( rowIterator.hasNext() )
        {
            Integer currentRow = (Integer) rowIterator.next();

            int value = 0;

            if ( mapOfTotalValues.containsKey( currentRow ) )
            {
                value = mapOfTotalValues.get( currentRow );

                valueToPrint = String.valueOf( value );
            }

            if ( value == 0 )
            {
                valueToPrint = " ";
            }

            WritableSheet totalSheet = outputReportWorkbook.getSheet( sheetList.get( 0 ) );
            WritableCellFormat totalCellformat = new WritableCellFormat();

            //WritableCell cell = totalSheet.getWritableCell( totalColumnNumber, currentRow.intValue() );

            totalCellformat.setBorder( Border.ALL, BorderLineStyle.MEDIUM );
            totalCellformat.setAlignment( Alignment.CENTRE );
            totalCellformat.setWrap( true );
            
            try
            {
                if(valueToPrint.trim().equals("")){
                    
                    totalSheet.addCell( new Label( totalColumnNumber, currentRow.intValue(), valueToPrint,
                    totalCellformat ) );
                }
                else{
                    totalSheet.addCell( new Number( totalColumnNumber, currentRow.intValue(), Integer.parseInt(valueToPrint),
                    totalCellformat ) );
                }

            } catch ( Exception e )
            {
                System.out.println( "Cannot write to Excel" );
            }

            WritableSheet totalCellSheet = outputReportWorkbook.getSheet( sheetList.get( 0 ) );
            WritableCellFormat totalCellformat1 = new WritableCellFormat();

            //WritableCell totalCell = totalCellSheet.getWritableCell( totalColumnNumber, startRowNumber );

            totalCellformat1.setBorder( Border.ALL, BorderLineStyle.MEDIUM );
            totalCellformat1.setAlignment( Alignment.CENTRE );
            totalCellformat1.setWrap( true );

            try
            {

                totalCellSheet.addCell( new Label( totalColumnNumber, startRowNumber, "Total", totalCellformat1 ) );

            } catch ( Exception e )
            {
                System.out.println( "Cannot write to Excel" );
            }

            //Writing total values ends

        }

        /*
         * ActionContext ctx = ActionContext.getContext(); HttpServletResponse
         * res = (HttpServletResponse) ctx.get(
         * ServletActionContext.HTTP_RESPONSE );
         * 
         * res.setContentType("application/vnd.ms-excel");
         */

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + orgUnitList.get( 0 ).getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( selectedPeriod.getStartDate() ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( "Report Generation End Time is : \t" + new Date() );

        outputReportFile.deleteOnExit();

        statementManager.destroy();

        return SUCCESS;
    }

    public List<Calendar> getStartingEndingPeriods( String deType, Calendar tempStartDate, Calendar tempEndDate )
    {

        List<Calendar> calendarList = new ArrayList<Calendar>();

        Period previousPeriod = new Period();
        previousPeriod = getPreviousPeriod( tempStartDate.getTime() );

        if ( deType.equalsIgnoreCase( "ccmcy" ) )
        {
            // tempStartDate.setTime( selectedPeriod.getStartDate() );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            tempEndDate.setTime( selectedPeriod.getEndDate() );
            // System.out.println("CCMCY : "+ String.valueOf(
            // tempStartDate.getTime()) +" ------ "+String.valueOf(
            // tempEndDate.getTime()));
        } else
        {
            if ( deType.equalsIgnoreCase( "cpmcy" ) )
            {
                // tempStartDate.setTime( previousPeriod.getStartDate() );
                if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
                {
                    tempStartDate.roll( Calendar.YEAR, -1 );
                }
                tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
                tempEndDate.setTime( previousPeriod.getEndDate() );
            } else
            {
                if ( deType.equalsIgnoreCase( "cmpy" ) )
                {
                    // tempStartDate.setTime( selectedPeriod.getStartDate() );
                    tempEndDate.setTime( selectedPeriod.getEndDate() );

                    tempStartDate.roll( Calendar.YEAR, -1 );
                    tempEndDate.roll( Calendar.YEAR, -1 );
                } else
                {
                    if ( deType.equalsIgnoreCase( "ccmpy" ) )
                    {
                        // tempStartDate.setTime( selectedPeriod.getStartDate() );
                        tempEndDate.setTime( selectedPeriod.getEndDate() );

                        tempStartDate.roll( Calendar.YEAR, -1 );
                        tempEndDate.roll( Calendar.YEAR, -1 );

                        if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
                        {
                            tempStartDate.roll( Calendar.YEAR, -1 );
                        }
                        tempStartDate.set( Calendar.MONTH, Calendar.APRIL );

                    } else
                    {
                        if ( deType.equalsIgnoreCase( "pmcy" ) )
                        {
                            tempStartDate.setTime( previousPeriod.getStartDate() );
                            tempEndDate.setTime( previousPeriod.getEndDate() );

                        } else
                        {

                            tempStartDate.setTime( tempStartDate.getTime() );
                            tempEndDate.setTime( tempEndDate.getTime() );
                        }
                    }
                }
            }
        }

        // System.out.print(deType+" -- ");
        calendarList.add( tempStartDate );
        calendarList.add( tempEndDate );

        return calendarList;
    }

    public Period getPreviousPeriod( Date tempStartDate )
    {
        Period period = new Period();
        Calendar tempDate = Calendar.getInstance();
        // tempDate.setTime( selectedPeriod.getStartDate() );
        tempDate.setTime( tempStartDate );
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

    public Period getPeriodByMonth( int month, int year, PeriodType periodType )
    {
        int monthDays[] =
        {
            31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
        };

        Calendar cal = Calendar.getInstance();
        cal.set( year, month, 1, 0, 0, 0 );
        Date firstDay = new Date( cal.getTimeInMillis() );

        if ( periodType.getName().equals( "Monthly" ) )
        {
            cal.set( year, month, 1, 0, 0, 0 );
            if ( year % 4 == 0 )
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] + 1 );
            } else
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] );
            }
        } else
        {
            if ( periodType.getName().equals( "Yearly" ) )
            {
                cal.set( year, Calendar.DECEMBER, 31 );
            }
        }

        Date lastDay = new Date( cal.getTimeInMillis() );

        Period newPeriod = new Period();

        newPeriod.setStartDate( firstDay );
        newPeriod.setEndDate( lastDay );
        newPeriod.setPeriodType( periodType );

        return newPeriod;
    }

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

    public List<String> getDECodes( String fileName )
    {
        List<String> deCodes = new ArrayList<String>();
        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName + File.separator + fileName;
        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + raFolderName + File.separator + fileName;
            }
        } catch ( NullPointerException npe )
        {
            // do nothing, but we might be using this somewhere without
            // USER_HOME set, which will throw a NPE
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                // System.out.println( "There is no DECodes related XML file in
                // the user home" );
                return null;
            }

            NodeList listOfDECodes = doc.getElementsByTagName( "de-code" );
            int totalDEcodes = listOfDECodes.getLength();

            for ( int s = 0; s < totalDEcodes; s++ )
            {
                Element deCodeElement = (Element) listOfDECodes.item( s );
                NodeList textDECodeList = deCodeElement.getChildNodes();
                deCodes.add( ( (Node) textDECodeList.item( 0 ) ).getNodeValue().trim() );
                serviceType.add( deCodeElement.getAttribute( "stype" ) );
                deCodeType.add( deCodeElement.getAttribute( "type" ) );
                sheetList.add( new Integer( deCodeElement.getAttribute( "sheetno" ) ) );
                rowList.add( new Integer( deCodeElement.getAttribute( "rowno" ) ) );
                colList.add( new Integer( deCodeElement.getAttribute( "colno" ) ) );

            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
        } catch ( SAXException e )
        {
            Exception x = e.getException();
            ( ( x == null ) ? e : x ).printStackTrace();
        } catch ( Throwable t )
        {
            t.printStackTrace();
        }
        return deCodes;
    }// getDECodes end

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

    /**
     * Converts an expression on the form<br>
     * [34] + [23], where the numbers are IDs of DataElements, to the form<br>
     * 200 + 450, where the numbers are the values of the DataValues registered
     * for the Period and source.
     * 
     * @return The generated expression
     */
    private String getResultDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit )
    {
        try
        {
            // System.out.println( "expression : " + formula + " ***** " +
            // String.valueOf( startDate ) + " **** "
            // + String.valueOf( endDate ) );
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
                    Double aggregatedValue = aggregationService.getAggregatedDataValue( dataElement, optionCombo,
                        startDate, endDate, organisationUnit );
                    if ( aggregatedValue == null )
                    {
                        replaceString = NULL_REPLACEMENT;
                        deFlag2 = 1;
                    } else
                    {
                        replaceString = String.valueOf( aggregatedValue );

                        deFlag2 = 1;
                    }
                } else
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
                    } else
                    {
                        tempPeriod = (Period) periodList.get( 0 );
                    }

                    DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, tempPeriod,
                        optionCombo );

                    if ( dataValue != null )
                    {
                        // Works for both text and boolean data types

                        replaceString = dataValue.getValue();
                    } else
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
                } catch ( Exception e )
                {
                    d = 0.0;
                    resultValue = "";
                }
                if ( d == -1 )
                {
                    d = 0.0;
                    resultValue = "";
                } else
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
                    if ( !( reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" ) ) )
                    {
                        resultValue = "" + (int) d;
                    }

                    // if ( resultValue.equalsIgnoreCase( "0" ) )
                    // {
                    // resultValue = "";
                    // }
                }

            } else
            {
                resultValue = buffer.toString();
            }

            if ( resultValue.equalsIgnoreCase( "" ) )
            {
                resultValue = " ";
            }

            return resultValue;
        } catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    private String getIndividualResultDataValue( String formula, Date startDate, Date endDate,
        OrganisationUnit organisationUnit )
    {
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
                    List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType(
                        dePeriodType, startDate, endDate ) );

                    if ( periodList == null || periodList.isEmpty() )
                    {
                        replaceString = "";
                        matcher.appendReplacement( buffer, replaceString );
                        continue;
                    } else
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

                        deFlag2 = 1;
                    }
                    deFlag2 = 1;
                } else
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
                    } else
                    {
                        tempPeriod = (Period) periodList.get( 0 );
                    }

                    DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, tempPeriod,
                        optionCombo );

                    if ( dataValue != null )
                    {
                        // Works for both text and boolean data types

                        replaceString = dataValue.getValue();
                        valueDoesNotExist = false;
                    } else
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
                } catch ( Exception e )
                {
                    d = 0.0;

                    resultValue = "";
                }
                if ( d == -1 )
                {
                    d = 0.0;

                    resultValue = "";
                } else
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
                    if ( !( reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" ) ) )
                    {
                        resultValue = "" + (int) d;
                    }

                    // if ( resultValue.equalsIgnoreCase( "0" ) )
                    // {
                    // resultValue = "";
                    // }
                }
            } else
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
        } catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    private String getBooleanDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit )
    {
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
                    } else
                    {
                        tempPeriod = (Period) periodList.get( 0 );
                    }

                    DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, tempPeriod,
                        optionCombo );

                    if ( dataValue != null )
                    {
                        // Works for both text and boolean data types

                        if ( dataValue.getValue().equalsIgnoreCase( "true" ) )
                        {
                            replaceString = "Yes";
                        } else
                        {
                            if ( dataValue.getValue().equalsIgnoreCase( "false" ) )
                            {
                                replaceString = "No";
                            } else
                            {
                                replaceString = dataValue.getValue();
                            }
                        }
                    } else
                    {
                        replaceString = "";
                    }

                } else
                {
                    Double aggregatedValue = aggregationService.getAggregatedDataValue( dataElement, optionCombo,
                        startDate, endDate, organisationUnit );
                    if ( aggregatedValue == null)
                    {
                        replaceString = NULL_REPLACEMENT;
                        deFlag2 = 1;
                    } else
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
                    resultValue = "" + (int) d;
                }

                if ( deFlag2 == 0 )
                {
                    resultValue = " ";
                }
            } else
            {
                resultValue = buffer.toString();
            }
            return resultValue;
        } catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    private String getResultIndicatorValue( String formula, Date startDate, Date endDate,
        OrganisationUnit organisationUnit )
    {
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
                    deFlag2 = 1;
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
                
            }
            return resultValue;
        } catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    private String getIndividualResultIndicatorValue( String formula, Date startDate, Date endDate,
        OrganisationUnit organisationUnit )
    {
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
                String numeratorVal = getIndividualResultDataValue( numeratorExp, startDate, endDate, organisationUnit );
                String denominatorVal = getIndividualResultDataValue( denominatorExp, startDate, endDate,
                    organisationUnit );

                double numeratorValue;
                try
                {
                    numeratorValue = Double.parseDouble( numeratorVal );
                } catch ( Exception e )
                {
                    System.out.println( "Exception while getting Numerator : " + numeratorExp + " for Indicaotr " + indicator.getName() );
                    numeratorValue = 0.0;
                }

                double denominatorValue;
                try
                {
                    denominatorValue = Double.parseDouble( denominatorVal );
                } catch ( Exception e )
                {
                    System.out.println( "Exception while getting Deniminator : " + denominatorExp + " for Indicaotr " + indicator.getName() );
                    denominatorValue = 1.0;
                }

                double aggregatedValue;
                try
                {
                    aggregatedValue = ( numeratorValue / denominatorValue ) * indicatorFactor;
                } catch ( Exception e )
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
                resultValue = buffer.toString();
            }
            return resultValue;
        } catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }
}

