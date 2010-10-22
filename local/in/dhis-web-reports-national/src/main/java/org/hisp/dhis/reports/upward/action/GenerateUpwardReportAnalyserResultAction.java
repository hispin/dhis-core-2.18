package org.hisp.dhis.reports.upward.action;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.CellType;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.VerticalAlignment;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.apache.velocity.tools.generic.MathTool;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

public class GenerateUpwardReportAnalyserResultAction
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

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

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
    private String contentType;

    public String getContentType()
    {
    return contentType;
    }
     */
    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    /*
    private int bufferSize;

    public int getBufferSize()
    {
    return bufferSize;
    }
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

    private double tempNum = 0;

//    private List<Integer> totalOrgUnitsCountList;

    private String raFolderName;

    private List<OrganisationUnit> childOrgUnits;

    public List<OrganisationUnit> getChildOrgUnits()
    {
        return childOrgUnits;
    }

    int deFlag1;

    int deFlag2;

    int isAggregated = 0;
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {

        statementManager.initialise();
        deFlag2 = 0;
        deFlag1 = 0;
        // Initialization
        raFolderName = reportService.getRAFolderName();

        mathTool = new MathTool();
        services = new ArrayList<String>();
        slNos = new ArrayList<String>();
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();
//        totalOrgUnitsCountList = new ArrayList<Integer>();
        String deCodesXMLFileName = "";
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        monthFormat = new SimpleDateFormat( "MMMM" );
        simpleMonthFormat = new SimpleDateFormat( "MMM" );
        yearFormat = new SimpleDateFormat( "yyyy" );
        simpleYearFormat = new SimpleDateFormat( "yy" );
        
        Report_in selReportObj =  reportService.getReport( Integer.parseInt( reportList ) );
        
        deCodesXMLFileName = selReportObj.getXmlTemplateName();

        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        
        System.out.println(reportModelTB + " : " + reportFileNameTB + " : " + deCodesXMLFileName );
        
        System.out.println( "Report Generation Start Time is : \t" + new Date() );

        String parentUnit = "";

        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();

        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );

        if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-ORGUNIT" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
            Collections.sort( orgUnitList, new OrganisationUnitNameComparator() );
        }
        else if ( reportModelTB.equalsIgnoreCase( "STATIC" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList.add( orgUnit );
        }
        else if ( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
            Collections.sort( orgUnitList, new OrganisationUnitNameComparator() );
            orgUnitList.add( orgUnit );

            parentUnit = orgUnit.getName();
        }
        else if ( reportModelTB.equalsIgnoreCase( "STATIC-DATAELEMENTS" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList.add( orgUnit );
        }
        else if ( reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList.add( orgUnit );
        }
        else if ( reportModelTB.equalsIgnoreCase( "INDICATOR-AGAINST-PARENT" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
//            OrganisationUnit parent = orgUnit.getParent();
            orgUnitList = new ArrayList<OrganisationUnit>();

            Collections.sort( orgUnitList, new OrganisationUnitNameComparator() );

            orgUnitList.add( orgUnit );
        }
        else if ( reportModelTB.equalsIgnoreCase( "INDICATOR-AGAINST-SIBLINGS" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );

            orgUnitList = new ArrayList<OrganisationUnit>();

            orgUnitList.addAll( orgUnit.getChildren() );

            Collections.sort( orgUnitList, new OrganisationUnitNameComparator() );

            orgUnitList.add( 0, orgUnit );
        }
        else if ( reportModelTB.equalsIgnoreCase( "INDICATOR-FOR-FEEDBACK" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );

            orgUnitList = new ArrayList<OrganisationUnit>();

            orgUnitList.addAll( orgUnit.getChildren() );

            Collections.sort( orgUnitList, new OrganisationUnitNameComparator() );

            orgUnitList.add( 0, orgUnit );
        }

        selectedPeriod = periodService.getPeriod( availablePeriods );

        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );

        eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );

        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );

        // Getting DataValues
        dataValueList = new ArrayList<String>();
        List<String> deCodesList = getDECodes( deCodesXMLFileName );

        Iterator<OrganisationUnit> it = orgUnitList.iterator();
        int orgUnitCount = 0;
        int orgUnitGroupCount = 0;

        int rowCounter = 0;

        // ---------------------------------------------------------------------------------------------------
        // Feedback without orgunit START
        // This part is for generating feedback reports for orgunits without any
        // children
        // ---------------------------------------------------------------------------------------------------

        OrganisationUnit checkChildOrgunit = new OrganisationUnit();

        checkChildOrgunit = organisationUnitService.getOrganisationUnit( ouIDTB );

        childOrgUnits = new ArrayList<OrganisationUnit>();

        childOrgUnits.addAll( checkChildOrgunit.getChildren() );

        int children = 1;

        if ( reportModelTB.equalsIgnoreCase( "INDICATOR-FOR-FEEDBACK" ) && ( childOrgUnits == null || childOrgUnits.size() == 0 ) )
        {
            children = 0;
        }

        if ( children == 0 )
        {
            //int quarterPeriod = 0;

            OrganisationUnit currentOrgUnit = (OrganisationUnit) it.next();

            Iterator<String> it1 = deCodesList.iterator();
            int count1 = 0;

            while ( it1.hasNext() )
            {
                String deCodeString = (String) it1.next();

                String deType = (String) deCodeType.get( count1 );
               // String sType = (String) serviceType.get( count1 );
               // int count = 0;
               // double sum = 0.0;
               // int flag1 = 0;
                String tempStr = "";

                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                List<Calendar> calendarList = new ArrayList<Calendar>( reportService.getStartingEndingPeriods( deType, selectedPeriod ) );
                if ( calendarList == null || calendarList.isEmpty() )
                {
                    tempStartDate.setTime( selectedPeriod.getStartDate() );
                    tempEndDate.setTime( selectedPeriod.getEndDate() );
                    return SUCCESS;
                } else
                {
                    tempStartDate = calendarList.get( 0 );
                    tempEndDate = calendarList.get( 1 );
                }

                if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = "";

                } else
                {
                    if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                    {
                        tempStr = currentOrgUnit.getName();

                    } else
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                        {
                            OrganisationUnit orgUnitP = new OrganisationUnit();

                            orgUnitP = currentOrgUnit.getParent();

                            tempStr = orgUnitP.getName();

                        } else
                        {
                            if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                            {
                                OrganisationUnit orgUnitP = new OrganisationUnit();

                                OrganisationUnit orgUnitPP = new OrganisationUnit();

                                orgUnitP = currentOrgUnit.getParent();

                                orgUnitPP = orgUnitP.getParent();

                                tempStr = orgUnitPP.getName();

                            } else
                            {
                                if ( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                                {
                                    OrganisationUnit orgUnitP = new OrganisationUnit();

                                    OrganisationUnit orgUnitPP = new OrganisationUnit();

                                    OrganisationUnit orgUnitPPP = new OrganisationUnit();

                                    orgUnitP = currentOrgUnit.getParent();

                                    orgUnitPP = orgUnitP.getParent();

                                    orgUnitPPP = orgUnitPP.getParent();

                                    tempStr = orgUnitPPP.getName();

                                } else
                                {
                                    if ( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) )
                                    {
                                        tempStr = monthFormat.format( sDate );

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

                                            if ( ( startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth.equalsIgnoreCase( "March" ) ) )
                                            {
                                                tempQuarterYear.roll( Calendar.YEAR, -1 );

                                                sDateTemp = tempQuarterYear.getTime();

                                            }

                                            startYear = yearFormat.format( sDateTemp );

                                            tempQuarterYear.setTime( eDateTemp );

                                            if ( !( startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth.equalsIgnoreCase( "March" ) ) )
                                            {
                                                tempQuarterYear.roll( Calendar.YEAR, 1 );

                                                eDateTemp = tempQuarterYear.getTime();

                                            }
                                            endYear = yearFormat.format( eDateTemp );

                                            tempStr = startYear + " - " + endYear;

                                        } else
                                        {
                                            tempStr = "";
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                WritableCellFormat wCellformat = new WritableCellFormat();
                wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                wCellformat.setWrap( true );
                wCellformat.setAlignment( Alignment.CENTRE );

                int tempRowNo = rowList.get( count1 );
                int tempColNo = colList.get( count1 );
                int sheetNo = sheetList.get( count1 ) + orgUnitGroupCount;
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );

                if ( tempStr == null || tempStr.equals( " " ) )
                {
                    sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );
                }
                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );

                count1++;
            }

        }

        // ---------------------------------------------------------------------------------------------------
        // Feedback without orgunit END
        // ---------------------------------------------------------------------------------------------------

        // ---------------------------------------------------------------------------------------------------
        // All other reports START
        // ---------------------------------------------------------------------------------------------------

        while ( it.hasNext() && children != 0 )
        {

           // int quarterPeriod = 0;

            OrganisationUnit currentOrgUnit = (OrganisationUnit) it.next();

            Iterator<String> it1 = deCodesList.iterator();
            int count1 = 0;
            while ( it1.hasNext() )
            {
                String deCodeString = (String) it1.next();

                String deType = (String) deCodeType.get( count1 );
                String sType = (String) serviceType.get( count1 );
                //int count = 0;
               // double sum = 0.0;
                //int flag1 = 0;
                String tempStr = "";

                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                List<Calendar> calendarList = new ArrayList<Calendar>( reportService.getStartingEndingPeriods( deType, selectedPeriod ) );
                if ( calendarList == null || calendarList.isEmpty() )
                {
                    tempStartDate.setTime( selectedPeriod.getStartDate() );
                    tempEndDate.setTime( selectedPeriod.getEndDate() );
                    return SUCCESS;
                } 
                else
                {
                    tempStartDate = calendarList.get( 0 );
                    tempEndDate = calendarList.get( 1 );
                }

                if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = currentOrgUnit.getName();

                } 
                else
                {
                    if ( deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) )
                    {
                        tempStr = parentUnit;
                    } else
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                        {
                            tempStr = currentOrgUnit.getParent().getName();

                        } else
                        {
                            if ( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                            {
                                OrganisationUnit orgUnitP = new OrganisationUnit();

                                orgUnitP = currentOrgUnit.getParent();

                                tempStr = orgUnitP.getParent().getName();

                            } else
                            {
                                if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                                {
                                    OrganisationUnit orgUnitP = new OrganisationUnit();

                                    OrganisationUnit orgUnitPP = new OrganisationUnit();

                                    orgUnitP = currentOrgUnit.getParent();

                                    orgUnitPP = orgUnitP.getParent();

                                    tempStr = orgUnitPP.getParent().getName();

                                } else
                                {
                                    if ( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                                    {
                                        OrganisationUnit orgUnitP = new OrganisationUnit();

                                        OrganisationUnit orgUnitPP = new OrganisationUnit();

                                        OrganisationUnit orgUnitPPP = new OrganisationUnit();

                                        orgUnitP = currentOrgUnit.getParent();

                                        orgUnitPP = orgUnitP.getParent();

                                        orgUnitPPP = orgUnitPP.getParent();

                                        tempStr = orgUnitPPP.getParent().getName();

                                    } else
                                    {
                                        if ( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) )
                                        {
                                            tempStr = simpleDateFormat.format( sDate );

                                        } else
                                        {
                                            if ( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) )
                                            {
                                                tempStr = monthFormat.format( sDate );

                                            } else
                                            {
                                                if ( deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) )
                                                {
                                                    tempStr = simpleMonthFormat.format( sDate );

                                                } else
                                                {
                                                    if ( deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) )
                                                    {
                                                        tempStr = simpleMonthFormat.format( eDate );

                                                    } else
                                                    {
                                                        if ( deCodeString.equalsIgnoreCase( "MONTH-START" ) )
                                                        {
                                                            tempStr = monthFormat.format( sDate );

                                                        } else
                                                        {
                                                            if ( deCodeString.equalsIgnoreCase( "MONTH-END" ) )
                                                            {
                                                                tempStr = monthFormat.format( eDate );

                                                            } else
                                                            {
                                                                if ( deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) )
                                                                {
                                                                    tempStr = String.valueOf( tempStartDate.get( Calendar.WEEK_OF_MONTH ) );

                                                                } else
                                                                {
                                                                    if ( deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) )
                                                                    {
                                                                        String startMonth = "";

                                                                        startMonth = monthFormat.format( sDate );

                                                                        if ( startMonth.equalsIgnoreCase( "April" ) )
                                                                        {
                                                                            tempStr = "Quarter I";
                                                                        } else
                                                                        {
                                                                            if ( startMonth.equalsIgnoreCase( "July" ) )
                                                                            {
                                                                                tempStr = "Quarter II";
                                                                            } else
                                                                            {
                                                                                if ( startMonth.equalsIgnoreCase( "October" ) )
                                                                                {
                                                                                    tempStr = "Quarter III";
                                                                                } 
                                                                                else
                                                                                {
                                                                                    tempStr = "Quarter IV";

                                                                                    //quarterPeriod = 1;

                                                                                }
                                                                            }
                                                                        }
                                                                    } else
                                                                    {
                                                                        if ( deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" ) )
                                                                        {
                                                                            String startMonth = "";

                                                                            startMonth = monthFormat.format( sDate );

                                                                            if ( startMonth.equalsIgnoreCase( "April" ) )
                                                                            {
                                                                                tempStr = "Q1";
                                                                            } else
                                                                            {
                                                                                if ( startMonth.equalsIgnoreCase( "July" ) )
                                                                                {
                                                                                    tempStr = "Q2";
                                                                                } else
                                                                                {
                                                                                    if ( startMonth.equalsIgnoreCase( "October" ) )
                                                                                    {
                                                                                        tempStr = "Q3";
                                                                                    } else
                                                                                    {
                                                                                        tempStr = "Q4";

                                                                                        //quarterPeriod = 1;

                                                                                    }
                                                                                }
                                                                            }
                                                                        } else
                                                                        {
                                                                            if ( deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" ) )
                                                                            {
                                                                                String startMonth = "";

                                                                                startMonth = monthFormat.format( sDate );

                                                                                if ( startMonth.equalsIgnoreCase( "April" ) )
                                                                                {
                                                                                    tempStr = "Apr - Jun";
                                                                                } else
                                                                                {
                                                                                    if ( startMonth.equalsIgnoreCase( "July" ) )
                                                                                    {
                                                                                        tempStr = "Jul - Sep";
                                                                                    } else
                                                                                    {
                                                                                        if ( startMonth.equalsIgnoreCase( "October" ) )
                                                                                        {
                                                                                            tempStr = "Oct - Dec";
                                                                                        } else
                                                                                        {
                                                                                            tempStr = "Jan - Mar";

                                                                                           // quarterPeriod = 1;

                                                                                        }
                                                                                    }
                                                                                }
                                                                            } else
                                                                            {
                                                                                if ( deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" ) )
                                                                                {
                                                                                    String startMonth = "";

                                                                                    startMonth = monthFormat.format( sDate );

                                                                                    if ( startMonth.equalsIgnoreCase( "April" ) )
                                                                                    {
                                                                                        tempStr = "April - June";
                                                                                    } else
                                                                                    {
                                                                                        if ( startMonth.equalsIgnoreCase( "July" ) )
                                                                                        {
                                                                                            tempStr = "July - September";
                                                                                        } else
                                                                                        {
                                                                                            if ( startMonth.equalsIgnoreCase( "October" ) )
                                                                                            {
                                                                                                tempStr = "October - December";
                                                                                            } else
                                                                                            {
                                                                                                tempStr = "January - March";

                                                                                               // quarterPeriod = 1;

                                                                                            }
                                                                                        }
                                                                                    }
                                                                                } else
                                                                                {
                                                                                    if ( deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" ) )
                                                                                    {
                                                                                        String startMonth = "";

                                                                                        startMonth = monthFormat.format( sDate );

                                                                                        if ( startMonth.equalsIgnoreCase( "April" ) )
                                                                                        {
                                                                                            tempStr = "Apr";
                                                                                        } else
                                                                                        {
                                                                                            if ( startMonth.equalsIgnoreCase( "July" ) )
                                                                                            {
                                                                                                tempStr = "Jul";
                                                                                            } else
                                                                                            {
                                                                                                if ( startMonth.equalsIgnoreCase( "October" ) )
                                                                                                {
                                                                                                    tempStr = "Oct";
                                                                                                } else
                                                                                                {
                                                                                                    tempStr = "Jan";

                                                                                                   // quarterPeriod = 1;

                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    } else
                                                                                    {
                                                                                        if ( deCodeString.equalsIgnoreCase( "QUARTER-START" ) )
                                                                                        {
                                                                                            String startMonth = "";

                                                                                            startMonth = monthFormat.format( sDate );

                                                                                            if ( startMonth.equalsIgnoreCase( "April" ) )
                                                                                            {
                                                                                                tempStr = "April";
                                                                                            } else
                                                                                            {
                                                                                                if ( startMonth.equalsIgnoreCase( "July" ) )
                                                                                                {
                                                                                                    tempStr = "July";
                                                                                                } else
                                                                                                {
                                                                                                    if ( startMonth.equalsIgnoreCase( "October" ) )
                                                                                                    {
                                                                                                        tempStr = "October";
                                                                                                    } else
                                                                                                    {
                                                                                                        tempStr = "January";

                                                                                                       // quarterPeriod = 1;

                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        } else
                                                                                        {
                                                                                            if ( deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" ) )
                                                                                            {
                                                                                                String endMonth = "";

                                                                                                endMonth = monthFormat.format( eDate );

                                                                                                if ( endMonth.equalsIgnoreCase( "June" ) )
                                                                                                {
                                                                                                    tempStr = "Jun";
                                                                                                } else
                                                                                                {
                                                                                                    if ( endMonth.equalsIgnoreCase( "September" ) )
                                                                                                    {
                                                                                                        tempStr = "Sep";
                                                                                                    } else
                                                                                                    {
                                                                                                        if ( endMonth.equalsIgnoreCase( "December" ) )
                                                                                                        {
                                                                                                            tempStr = "Dec";
                                                                                                        } else
                                                                                                        {
                                                                                                            tempStr = "Mar";

                                                                                                           // quarterPeriod = 1;

                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            } else
                                                                                            {
                                                                                                if ( deCodeString.equalsIgnoreCase( "QUARTER-END" ) )
                                                                                                {
                                                                                                    String endMonth = "";

                                                                                                    endMonth = monthFormat.format( eDate );

                                                                                                    if ( endMonth.equalsIgnoreCase( "June" ) )
                                                                                                    {
                                                                                                        tempStr = "June";
                                                                                                    } else
                                                                                                    {
                                                                                                        if ( endMonth.equalsIgnoreCase( "September" ) )
                                                                                                        {
                                                                                                            tempStr = "September";
                                                                                                        } else
                                                                                                        {
                                                                                                            if ( endMonth.equalsIgnoreCase( "December" ) )
                                                                                                            {
                                                                                                                tempStr = "December";
                                                                                                            } else
                                                                                                            {
                                                                                                                tempStr = "March";

                                                                                                               // quarterPeriod = 1;

                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                } else
                                                                                                {
                                                                                                    if ( deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) )
                                                                                                    {
                                                                                                        sDateTemp = sDate;

                                                                                                        Calendar tempQuarterYear = Calendar.getInstance();

                                                                                                        tempQuarterYear.setTime( sDateTemp );

                                                                                                        String startMonth = "";

                                                                                                        startMonth = monthFormat.format( sDateTemp );

                                                                                                        periodType = selectedPeriod.getPeriodType();

                                                                                                        if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                                                                                                        {
                                                                                                            sDateTemp = sDate;
                                                                                                        } else
                                                                                                        {
                                                                                                            if ( ( startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth.equalsIgnoreCase( "March" ) ) && periodType.getName().equalsIgnoreCase( "Quarterly" ) )
                                                                                                            {
                                                                                                                tempQuarterYear.roll( Calendar.YEAR, -1 );

                                                                                                                sDateTemp = tempQuarterYear.getTime();

                                                                                                            }
                                                                                                        }

                                                                                                        tempStr = yearFormat.format( sDateTemp );
                                                                                                    } else
                                                                                                    {
                                                                                                        if ( deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" ) )
                                                                                                        {
                                                                                                            sDateTemp = sDate;

                                                                                                            Calendar tempQuarterYear = Calendar.getInstance();

                                                                                                            tempQuarterYear.setTime( sDateTemp );

                                                                                                            String startMonth = "";

                                                                                                            startMonth = monthFormat.format( sDateTemp );

                                                                                                            periodType = selectedPeriod.getPeriodType();

                                                                                                            if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                                                                                                            {
                                                                                                                sDateTemp = sDate;
                                                                                                            } else
                                                                                                            {
                                                                                                                if ( ( startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth.equalsIgnoreCase( "March" ) ) && periodType.getName().equalsIgnoreCase( "Quarterly" ) )
                                                                                                                {
                                                                                                                    tempQuarterYear.roll( Calendar.YEAR, -1 );

                                                                                                                    sDateTemp = tempQuarterYear.getTime();

                                                                                                                }
                                                                                                            }

                                                                                                            tempStr = simpleYearFormat.format( sDateTemp );
                                                                                                        } else
                                                                                                        {
                                                                                                            if ( deCodeString.equalsIgnoreCase( "YEAR-END" ) )
                                                                                                            {

                                                                                                                sDateTemp = sDate;

                                                                                                                Calendar tempQuarterYear = Calendar.getInstance();

                                                                                                                tempQuarterYear.setTime( sDate );

                                                                                                                sDate = tempQuarterYear.getTime();

                                                                                                                String startMonth = "";

                                                                                                                startMonth = monthFormat.format( sDateTemp );

                                                                                                                periodType = selectedPeriod.getPeriodType();

                                                                                                                if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                                                                                                                {
                                                                                                                    tempQuarterYear.roll( Calendar.YEAR, 1 );

                                                                                                                    sDateTemp = tempQuarterYear.getTime();

                                                                                                                }

                                                                                                                if ( !( startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth.equalsIgnoreCase( "March" ) ) )
                                                                                                                {
                                                                                                                    tempQuarterYear.roll( Calendar.YEAR, 1 );

                                                                                                                    sDateTemp = tempQuarterYear.getTime();

                                                                                                                }

                                                                                                                tempStr = yearFormat.format( sDateTemp );
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

                                                                                                                } else
                                                                                                                {
                                                                                                                    if ( deCodeString.equalsIgnoreCase( "SLNO" ) )
                                                                                                                    {
                                                                                                                        tempStr = "" + ( orgUnitCount + 1 );
                                                                                                                    } else
                                                                                                                    {
                                                                                                                        if ( deCodeString.equalsIgnoreCase( "NA" ) )
                                                                                                                        {
                                                                                                                            tempStr = " ";
                                                                                                                        } else
                                                                                                                        {
                                                                                                                            rowCounter += 1;

                                                                                                                            if ( sType.equalsIgnoreCase( "dataelement" ) )
                                                                                                                            {
                                                                                                                                if ( aggCB == null )
                                                                                                                                {
                                                                                                                                   // tempStr = getIndividualResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                                                                                                                                	 tempStr = reportService.getIndividualResultDataValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                                                                                                                                } else
                                                                                                                                {
                                                                                                                                    //tempStr = getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                                                                                                                                	tempStr = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                                                                                                                                }

                                                                                                                                if ( deFlag2 == 1 )
                                                                                                                                {
                                                                                                                                    try{
                                                                                                                                    //tempNum = Integer.parseInt( tempStr );
                                                                                                                                        tempNum = Double.parseDouble( tempStr );
                                                                                                                                    }
                                                                                                                                    catch(Exception ex){
                                                                                                                                        tempNum = 0;
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            } else
                                                                                                                            {
                                                                                                                                if ( sType.equalsIgnoreCase( "indicator-parent" ) )
                                                                                                                                {
                                                                                                                                    if ( aggCB == null )
                                                                                                                                    {
                                                                                                                                        //tempStr = getIndividualResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit.getParent() );
                                                                                                                                    	tempStr = reportService.getIndividualResultIndicatorValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit.getParent() );
                                                                                                                                    } else
                                                                                                                                    {
                                                                                                                                        //tempStr = getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit.getParent() );
                                                                                                                                    	tempStr = reportService.getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit.getParent() );
                                                                                                                                    }

                                                                                                                                    if ( deFlag2 == 1 )
                                                                                                                                    {
                                                                                                                                        try{
                                                                                                                                            //tempNum = Integer.parseInt( tempStr );
                                                                                                                                            tempNum = Double.parseDouble( tempStr );
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
                                                                                                                                            //tempStr = getBooleanDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                                                                                                                                        	tempStr = reportService.getBooleanDataValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB);
                                                                                                                                        } else
                                                                                                                                        {
                                                                                                                                            //tempStr = getBooleanDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                                                                                                                                        	tempStr = reportService.getBooleanDataValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB);
                                                                                                                                        }
                                                                                                                                    } else
                                                                                                                                    {
                                                                                                                                        if ( aggCB == null )
                                                                                                                                        {
                                                                                                                                            //tempStr = getIndividualResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                                                                                                                                        	tempStr = reportService.getIndividualResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                                                                                                                                        } else
                                                                                                                                        {
                                                                                                                                            //tempStr = getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                                                                                                                                        	tempStr = reportService.getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                                                                                                                                        }
                                                                                                                                        if ( deFlag2 == 1 )
                                                                                                                                        {
                                                                                                                                            try{
                                                                                                                                                //tempNum = Integer.parseInt( tempStr );
                                                                                                                                                tempNum = Double.parseDouble( tempStr );
                                                                                                                                                }
                                                                                                                                                catch(Exception ex){
                                                                                                                                                    tempNum = 0;
                                                                                                                                                }

                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                int tempRowNo = rowList.get( count1 );
                int tempColNo = colList.get( count1 );
                int sheetNo = sheetList.get( count1 ) + orgUnitGroupCount;
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                if ( tempStr == null || tempStr.equals( " " ) )
                {
                    tempColNo += orgUnitCount;

                    WritableCellFormat wCellformat = new WritableCellFormat();
                    wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                    wCellformat.setWrap( true );
                    wCellformat.setAlignment( Alignment.CENTRE );

                    sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );
                } else
                {

                    if ( reportModelTB.equalsIgnoreCase( "STATIC" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                        {
                        } else
                        {
                            if ( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                            {
                            } else
                            {
                                if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                                {
                                } else
                                {
                                    if ( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                                    {
                                    } else
                                    {
                                        if ( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) || deCodeString.equalsIgnoreCase( "MONTH-START" ) || deCodeString.equalsIgnoreCase( "MONTH-END" ) || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" ) || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-START" ) || deCodeString.equalsIgnoreCase( "QUARTER-END" ) || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" ) || deCodeString.equalsIgnoreCase( "YEAR-END" ) || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                                        {
                                        } else
                                        {
                                            // tempColNo +=
                                            // orgUnitCount;
                                        }
                                    }
                                }
                            }
                        }

                        WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );

                        CellFormat cellFormat = cell.getCellFormat();
                        WritableCellFormat wCellformat = new WritableCellFormat();
                        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                        wCellformat.setWrap( true );
                        wCellformat.setAlignment( Alignment.CENTRE );

                        if ( cell.getType() == CellType.LABEL )
                        {
                            Label l = (Label) cell;
                            l.setString( tempStr );
                            l.setCellFormat( cellFormat );
                        } else
                        {
                            if ( deFlag2 == 1 )
                            {
                                sheet0.addCell( new Number( tempColNo, tempRowNo, tempNum, wCellformat ) );
                            } else
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        }
                    }

                    if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-ORGUNIT" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) || deCodeString.equalsIgnoreCase( "FACILITYPP" ) || deCodeString.equalsIgnoreCase( "FACILITYPPP" ) || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                        {
                        } else
                        {
                            if ( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) || deCodeString.equalsIgnoreCase( "MONTH-START" ) || deCodeString.equalsIgnoreCase( "MONTH-END" ) || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" ) || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-START" ) || deCodeString.equalsIgnoreCase( "QUARTER-END" ) || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" ) || deCodeString.equalsIgnoreCase( "YEAR-END" ) || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                            {
                            } else
                            {
                                tempColNo += orgUnitCount;
                            }
                        }

                        WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );

                        CellFormat cellFormat = cell.getCellFormat();
                        WritableCellFormat wCellformat = new WritableCellFormat();

                        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                        wCellformat.setAlignment( Alignment.CENTRE );
                        wCellformat.setWrap( true );

                        if ( cell.getType() == CellType.LABEL )
                        {
                            Label l = (Label) cell;
                            l.setString( tempStr );
                            l.setCellFormat( cellFormat );
                        } else
                        {
                            if ( deFlag2 == 1 )
                            {
                                sheet0.addCell( new Number( tempColNo, tempRowNo, tempNum, wCellformat ) );
                            } else
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }

                        }
                    }

                    if ( reportModelTB.equalsIgnoreCase( "STATIC-DATAELEMENTS" ) || reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) || deCodeString.equalsIgnoreCase( "FACILITY" ) )
                        {
                        }
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                        {
                        } else
                        {
                            if ( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                            {
                            } else
                            {
                                if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                                {
                                } else
                                {
                                    if ( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                                    {
                                    } else
                                    {
                                        if ( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) || deCodeString.equalsIgnoreCase( "MONTH-START" ) || deCodeString.equalsIgnoreCase( "MONTH-END" ) || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" ) || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-START" ) || deCodeString.equalsIgnoreCase( "QUARTER-END" ) || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" ) || deCodeString.equalsIgnoreCase( "YEAR-END" ) || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                                        {
                                        }
                                    }
                                }
                            }
                        }

                        WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );

                        CellFormat cellFormat = cell.getCellFormat();
                        WritableCellFormat wCellformat = new WritableCellFormat();
                        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                        wCellformat.setWrap( true );
                        wCellformat.setAlignment( Alignment.CENTRE );

                        if ( cell.getType() == CellType.LABEL )
                        {
                            Label l = (Label) cell;
                            l.setString( tempStr );
                            l.setCellFormat( cellFormat );
                        } else
                        {
                            if ( deFlag2 == 1 )
                            {
                                sheet0.addCell( new Number( tempColNo, tempRowNo, tempNum, wCellformat ) );
                            } else
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        }
                    }

                    if ( reportModelTB.equalsIgnoreCase( "INDICATOR-AGAINST-PARENT" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                        {
                        } else
                        {
                            if ( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                            {
                            } else
                            {
                                if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                                {
                                } else
                                {
                                    if ( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                                    {
                                    } else
                                    {
                                        if ( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) || deCodeString.equalsIgnoreCase( "MONTH-START" ) || deCodeString.equalsIgnoreCase( "MONTH-END" ) || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" ) || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-START" ) || deCodeString.equalsIgnoreCase( "QUARTER-END" ) || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" ) || deCodeString.equalsIgnoreCase( "YEAR-END" ) || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                                        {
                                        } else
                                        {
                                            // tempColNo +=
                                            // (orgUnitCount * 2);
                                        }
                                    }
                                }
                            }
                        }

                        WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );

                        CellFormat cellFormat = cell.getCellFormat();
                        WritableCellFormat wCellformat = new WritableCellFormat();
                        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                        wCellformat.setAlignment( Alignment.CENTRE );

                        if ( cell.getType() == CellType.LABEL )
                        {
                            Label l = (Label) cell;
                            l.setString( tempStr );
                            l.setCellFormat( cellFormat );
                            System.out.println( "cell type = " + cell.getType() + " tempstr = " + tempStr );
                        } else
                        {
                            System.out.println( "deFlag2 = " + deFlag2 + " tempstr = " + tempStr );
                            if ( deFlag2 == 1 )
                            {
                                sheet0.addCell( new Number( tempColNo, tempRowNo, tempNum, wCellformat ) );
                            } else
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        }
                    }

                    if ( reportModelTB.equalsIgnoreCase( "INDICATOR-AGAINST-SIBLINGS" ) || reportModelTB.equalsIgnoreCase( "INDICATOR-FOR-FEEDBACK" ) )
                    {

                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                        {
                        } else
                        {
                            if ( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                            {
                            } else
                            {
                                if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                                {
                                } else
                                {
                                    if ( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                                    {
                                    } else
                                    {
                                        if ( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) || deCodeString.equalsIgnoreCase( "MONTH-START" ) || deCodeString.equalsIgnoreCase( "MONTH-END" ) || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" ) || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-START" ) || deCodeString.equalsIgnoreCase( "QUARTER-END" ) || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" ) || deCodeString.equalsIgnoreCase( "YEAR-END" ) || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                                        {
                                        } else
                                        {
                                            tempColNo += orgUnitCount;
                                        }
                                    }
                                }
                            }
                        }

                        WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );

                        CellFormat cellFormat = cell.getCellFormat();
                        WritableCellFormat wCellformat = new WritableCellFormat();

                        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                        wCellformat.setWrap( true );
                        wCellformat.setAlignment( Alignment.CENTRE );
                        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );

                        if ( cell.getType() == CellType.LABEL )
                        {
                            Label l = (Label) cell;
                            l.setString( tempStr );
                            l.setCellFormat( cellFormat );
                        } else
                        {
                            if ( deFlag2 == 1 )
                            {
                                sheet0.addCell( new Number( tempColNo, tempRowNo, tempNum, wCellformat ) );
                            } else
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        }
                    }
                    if ( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) || deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) )
                        {
                        } else
                        {
                            if ( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                            {
                            } else
                            {
                                if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                                {
                                } else
                                {
                                    if ( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                                    {
                                    } else
                                    {
                                        if ( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) || deCodeString.equalsIgnoreCase( "MONTH-START" ) || deCodeString.equalsIgnoreCase( "MONTH-END" ) || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" ) || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-START" ) || deCodeString.equalsIgnoreCase( "QUARTER-END" ) || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" ) || deCodeString.equalsIgnoreCase( "YEAR-END" ) || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                                        {
                                        } else
                                        {
                                            tempRowNo += orgUnitCount;
                                        }
                                    }
                                }
                            }
                        }

                        WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );

                        CellFormat cellFormat = cell.getCellFormat();
                        WritableCellFormat wCellformat = new WritableCellFormat();

                        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                        wCellformat.setWrap( true );
                        wCellformat.setAlignment( Alignment.CENTRE );
                        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );

                        if ( cell.getType() == CellType.LABEL )
                        {
                            Label l = (Label) cell;
                            l.setString( tempStr );
                            l.setCellFormat( cellFormat );
                            System.out.println( "cell type = " + cell.getType() + " tempstr = " + tempStr );
                        } else
                        {
                            System.out.println( "deFlag2 = " + deFlag2 + " tempstr = " + tempStr );
                            if ( deFlag2 == 1 )
                            {
                                sheet0.addCell( new Number( tempColNo, tempRowNo, tempNum, wCellformat ) );
                            } else
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        }
                    }

                    // }
                }
                count1++;
            }// inner while loop end
            orgUnitCount++;
        }// outer while loop end

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
        } 
        catch ( NullPointerException npe )
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
        } 
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ( ( x == null ) ? e : x ).printStackTrace();
        } 
        catch ( Throwable t )
        {
            t.printStackTrace();
        }
        return deCodes;
    }// getDECodes end

}
