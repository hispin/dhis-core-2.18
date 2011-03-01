package org.hisp.dhis.reports.periodwiseprogress.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;

import com.opensymphony.xwork2.Action;

public class GeneratePeriodWiseProgressAnalyserResultAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }

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

    private List<OrganisationUnit> orgUnitList;

    private Period selectedPeriod;

    private SimpleDateFormat simpleDateFormat;

    private SimpleDateFormat monthFormat;

    private SimpleDateFormat yearFormat;

    private String reportFileNameTB;

    private String reportModelTB;

    private Date sDate;

    private Date eDate;

    private Date sDateTemp;

    private Date eDateTemp;

    private PeriodType periodType;

    private String raFolderName;

    Integer startMonth;

    Integer endMonth;

    private Map<Integer, Double> mapOfTotalValues;

    private int startRowNumber;

    private int totalColumnNumber;

    private int sheetNo = 0;

    private int tempColNo;

    private int tempRowNo;

    Map<String, String> months;
    Map<String, Integer> monthOrder;
    String[] monthArray;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        statementManager.initialise();
        
        // Initialization
        raFolderName = reportService.getRAFolderName();

        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        monthFormat = new SimpleDateFormat( "MMMM" );
        yearFormat = new SimpleDateFormat( "yyyy" );
        mapOfTotalValues = new HashMap<Integer, Double>();
        List<Integer> totalRowList = new ArrayList<Integer>();
        
        startMonth = 0;
        endMonth = 0;

        String deCodesXMLFileName = "";

        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        String parentUnit = "";

        if( reportModelTB.equalsIgnoreCase( "PROGRESSIVE-PERIOD" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList.add( orgUnit );
        }

        System.out.println( orgUnitList.get( 0 ).getName()+ " : " + selReportObj.getName()+" : Report Generation Start Time is : " + new Date() );

        months = new HashMap<String, String>();
        monthOrder = new HashMap<String, Integer>();
        monthArray = new String[12];

        init();
        
        selectedPeriod = periodService.getPeriod( availablePeriods );

        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );

        eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );

        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );

        Calendar tempStartMonth = Calendar.getInstance();
        Calendar tempEndMonth = Calendar.getInstance();
        tempStartMonth.setTime( selectedPeriod.getStartDate() );
        if( tempStartMonth.get( Calendar.MONTH ) == Calendar.JANUARY || tempStartMonth.get( Calendar.MONTH ) == Calendar.FEBRUARY || tempStartMonth.get( Calendar.MONTH ) == Calendar.MARCH )
        {
            tempStartMonth.set( Calendar.MONTH, Calendar.APRIL );
            tempStartMonth.roll( Calendar.YEAR, -1 );
        } 
        else
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

        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );
        
        // Getting DataValues
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );

        int rowCounter = 0;

        int monthCount = 0;

        String currentMonth = "";

        OrganisationUnit currentOrgUnit = orgUnitList.get( 0 );

        int currentMonthOrder = monthOrder.get( monthFormat.format( tempEndMonth.getTime() ) );

        while ( monthCount < currentMonthOrder )
        {
            currentMonth = monthArray[monthCount];
            int count1 = 0;

            Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
            while (  reportDesignIterator.hasNext() )
            {
                Report_inDesign reportDesign =  reportDesignIterator.next();
                String deCodeString = reportDesign.getExpression();

                String deType = reportDesign.getPtype();
                String sType = reportDesign.getStype();
                String tempStr = "";

                tempRowNo = reportDesign.getRowno();
                tempColNo = reportDesign.getColno();
                sheetNo = reportDesign.getSheetno();
                totalRowList.add( count1, tempRowNo );

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
                List<Calendar> calendarList = new ArrayList<Calendar>( reportService.getStartingEndingPeriods( deType, tempStart.getTime(), tempEnd.getTime() ) );
                if ( calendarList == null || calendarList.isEmpty() )
                {
                    tempStr = currentMonth;
                } 
                else
                {
                    tempStartDate = calendarList.get( 0 );
                    tempEndDate = calendarList.get( 1 );
                }

                if( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = currentOrgUnit.getName();
                } 
                else if( deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) )
                {
                    tempStr = parentUnit;
                } 
                else if ( deCodeString.equalsIgnoreCase( "MONTH-RANGE" ) )
                {
                    tempStr = startMonthName + " - " + endMonthName;
                } 
                else if ( deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
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
                    } 
                    else if ( ( startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth.equalsIgnoreCase( "March" ) ) )
                    {
                        tempQuarterYear.roll( Calendar.YEAR, -1 );

                        sDateTemp = tempQuarterYear.getTime();
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
                } 
                else if( deCodeString.equalsIgnoreCase( "PROGRESSIVE-PERIOD" ) )
                {
                    Period p = new Period();
    
                    p = periodService.getPeriod( tempStartDate.getTime(), tempEndDate.getTime(), periodService.getPeriodTypeByName( "Monthly" ) );
    
                    startRowNumber = tempRowNo;
    
                    if( p == null )
                    {
                        tempStr = currentMonth;
                    } 
                    else
                    {
                        tempStr = monthFormat.format( p.getStartDate() );
                    }
                } 
                else if( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                } 
                else
                {
                    rowCounter += 1;
                    
                    if( sType.equalsIgnoreCase( "dataelement" ) )
                    {
                        if( aggCB == null )
                        {
                            tempStr = reportService.getIndividualResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                        } 
                        else
                        {
                            tempStr = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                        }
                        
                        double totalRowValue = 0.0;
    
                        if( mapOfTotalValues.get( tempRowNo ) != null )
                        {
                            totalRowValue = mapOfTotalValues.get( tempRowNo );
    
                            try
                            {
                                totalRowValue += Double.parseDouble( tempStr );
                            }
                            catch( Exception e )
                            {
                                
                            }
                            
                            /*
                            if ( !( tempStr.equalsIgnoreCase( " " ) || tempStr.equalsIgnoreCase( "" ) ) )
                            {
                                totalRowValue += Integer.valueOf( tempStr );
                            }
                            */
    
                            mapOfTotalValues.put( tempRowNo, totalRowValue );
                        } 
                        else if( !( tempStr.equalsIgnoreCase( " " ) || tempStr.equalsIgnoreCase( "" ) ) )
                        {
                            try
                            {
                                totalRowValue += Double.parseDouble( tempStr );
                            }
                            catch( Exception e )
                            {
                                
                            }
                        }
                        mapOfTotalValues.put( tempRowNo, totalRowValue );
                    } 
                                        
                }
                
                if( tempStr == null || tempStr.equals( " " ) )
                {
                    tempColNo += monthCount;

                    WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                    WritableCellFormat wCellformat = new WritableCellFormat();

                    wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                    wCellformat.setWrap( true );
                    wCellformat.setAlignment( Alignment.CENTRE );

                    sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );
                } 
                else
                {
                    if( reportModelTB.equalsIgnoreCase( "PROGRESSIVE-PERIOD" ) )
                    {
                        if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "FACILITYP" ) || deCodeString.equalsIgnoreCase( "FACILITYPP" ) || deCodeString.equalsIgnoreCase( "FACILITYPPP" ) || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                        {                            
                        } 
                        else if( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) || deCodeString.equalsIgnoreCase( "MONTH-RANGE" ) || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) || deCodeString.equalsIgnoreCase( "MONTH-START" ) || deCodeString.equalsIgnoreCase( "MONTH-END" ) || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" ) || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-START" ) || deCodeString.equalsIgnoreCase( "QUARTER-END" ) || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" ) || deCodeString.equalsIgnoreCase( "YEAR-END" ) || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                        {
                        } 
                        else
                        {
                            tempColNo += monthCount;
                        }

                        WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                        WritableCellFormat wCellformat = new WritableCellFormat();
                        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                        wCellformat.setAlignment( Alignment.CENTRE );
                        wCellformat.setWrap( true );

                        try
                        {
                            try
                            {
                                sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), wCellformat ) );
                            }
                            catch( Exception e )
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        } 
                        catch( Exception e )
                        {
                            System.out.println( "Cannot write to Excel" );
                        }
                    }
                }
                count1++;
            }// inner while loop end

            tempStartMonth.roll( Calendar.MONTH, 1 );

            if( tempStartMonth.get( Calendar.MONTH ) == Calendar.JANUARY )
            {
                tempStartMonth.roll( Calendar.YEAR, 1 );
            }

            monthCount++;

        }// outer while loop end

        // ---------------------------------------------------------------------
        // Writing Total Values
        // ---------------------------------------------------------------------
        totalColumnNumber = tempColNo + 1;
        String valueToPrint = " ";

        Iterator<Integer> totalRowListIterator = totalRowList.iterator();
        while( totalRowListIterator.hasNext() )
        {
            Integer i = (Integer) totalRowListIterator.next();
            if( i < startRowNumber )
            {
                totalRowListIterator.remove();
            }
        }

        Iterator<Integer> rowIterator = totalRowList.iterator();
        while( rowIterator.hasNext() )
        {
            Integer currentRow = (Integer) rowIterator.next();
            double value = 0;

            if( mapOfTotalValues.containsKey( currentRow ) )
            {
                value = mapOfTotalValues.get( currentRow );
                valueToPrint = String.valueOf( value );
            }

            if( value == 0 )
            {
                valueToPrint = " ";
            }

            WritableSheet totalSheet = outputReportWorkbook.getSheet( sheetNo );
            WritableCellFormat totalCellformat = new WritableCellFormat();

            totalCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
            totalCellformat.setAlignment( Alignment.CENTRE );
            totalCellformat.setWrap( true );
            
            try
            {
                if( valueToPrint.trim().equals("") )
                {
                    totalSheet.addCell( new Label( totalColumnNumber, currentRow.intValue(), valueToPrint, totalCellformat ) );
                }
                else
                {
                    totalSheet.addCell( new Number( totalColumnNumber, currentRow.intValue(), Integer.parseInt(valueToPrint), totalCellformat ) );
                }
            } 
            catch( Exception e )
            {
                System.out.println( "Cannot write to Excel" );
            }

            WritableSheet totalCellSheet = outputReportWorkbook.getSheet( sheetNo );
            WritableCellFormat totalCellformat1 = new WritableCellFormat();

            totalCellformat1.setBorder( Border.ALL, BorderLineStyle.THIN );
            totalCellformat1.setAlignment( Alignment.CENTRE );
            totalCellformat1.setWrap( true );

            try
            {
                totalCellSheet.addCell( new Label( totalColumnNumber, startRowNumber, "Total", totalCellformat1 ) );
            } 
            catch( Exception e )
            {
                System.out.println( "Cannot write to Excel" );
            }
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + orgUnitList.get( 0 ).getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( selectedPeriod.getStartDate() ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( orgUnitList.get( 0 ).getName()+ " : " + selReportObj.getName()+" : Report Generation End Time is : " + new Date() );

        outputReportFile.deleteOnExit();

        statementManager.destroy();

        return SUCCESS;
    }
    
    private void init()
    {
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
    }
}

