/*
 * Copyright (c) 2004-2009, University of Oslo
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
package org.hisp.dhis.reports.auto.action;

/**
 * @author Brajesh Murari
 * @version $Id$
 */

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
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

import jxl.CellType;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.VerticalAlignment;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.WritableCell;
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
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.util.ReportService;
import org.hisp.dhis.system.util.MathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Brajesh Murari
 * @version $Id$
 */
public class GenerateAutoReportAnalyserResultAction
    extends ActionSupport
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

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
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

    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
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

    private PeriodType selectedPeriodType;

    public PeriodType getSelectedPeriodType()
    {
        return selectedPeriodType;
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

    public int getAvailablePeriods()
    {
        return availablePeriods;
    }

    private String aggCB;

    public void setAggCB( String aggCB )
    {
        this.aggCB = aggCB;
    }

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

    private String periodTypeId;

    public void setPeriodTypeId( String periodTypeId )
    {
        this.periodTypeId = periodTypeId;
    }

    private int Id;

    public int getId()
    {
        return Id;
    }

    private List<Period> periods;

    public List<Period> getPeriods()
    {
        return periods;
    }

    private String raFolderName;

    private List<OrganisationUnit> childOrgUnits;

    public List<OrganisationUnit> getChildOrgUnits()
    {
        return childOrgUnits;
    }

    private List<String> periodNameList;

    public List<String> getPeriodNameList()
    {
        return periodNameList;
    }

    private String statusMessage;

    public String getStatusMessage()
    {
        return statusMessage;
    }

    public void setStatusMessage( String statusMessage )
    {
        this.statusMessage = statusMessage;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        statementManager.initialise();
        // Initialization
        raFolderName = reportService.getRAFolderName();

        mathTool = new MathTool();
        services = new ArrayList<String>();
        slNos = new ArrayList<String>();
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();
        new ArrayList<Integer>();
        String deCodesXMLFileName = "";
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        monthFormat = new SimpleDateFormat( "MMMM" );
        simpleMonthFormat = new SimpleDateFormat( "MMM" );
        yearFormat = new SimpleDateFormat( "yyyy" );
        simpleYearFormat = new SimpleDateFormat( "yy" );
        deCodesXMLFileName = reportList + "DECodes.xml";

        System.out.println( "Report Generation Start Time is : \t" + new Date() );
        statusMessage = "Report Generation is Started.";
        String parentUnit = "";

        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();

        if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-ORGUNIT" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
            Collections.sort( orgUnitList, new OrganisationUnitNameComparator() );
        }

        if ( reportModelTB.equalsIgnoreCase( "STATIC" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
            parentUnit = orgUnit.getName();
        }

        if ( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
            Collections.sort( orgUnitList, new OrganisationUnitNameComparator() );
            orgUnitList.add( orgUnit );
            parentUnit = orgUnit.getName();
        }

        if ( reportModelTB.equalsIgnoreCase( "STATIC-DATAELEMENTS" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
            parentUnit = orgUnit.getName();
        }

        if ( reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
            parentUnit = orgUnit.getName();
        }

        if ( reportModelTB.equalsIgnoreCase( "INDICATOR-AGAINST-PARENT" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            // OrganisationUnit parent = orgUnit.getParent();
            orgUnitList = new ArrayList<OrganisationUnit>();
            Collections.sort( orgUnitList, new OrganisationUnitNameComparator() );
            orgUnitList.add( orgUnit );

        }
        if ( reportModelTB.equalsIgnoreCase( "INDICATOR-AGAINST-SIBLINGS" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>();
            orgUnitList.addAll( orgUnit.getChildren() );
            Collections.sort( orgUnitList, new OrganisationUnitNameComparator() );
            orgUnitList.add( 0, orgUnit );
        }

        if ( reportModelTB.equalsIgnoreCase( "INDICATOR-FOR-FEEDBACK" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>();
            orgUnitList.addAll( orgUnit.getChildren() );
            Collections.sort( orgUnitList, new OrganisationUnitNameComparator() );
            orgUnitList.add( 0, orgUnit );
        }

        // Getting DataValues
        dataValueList = new ArrayList<String>();
        List<String> deCodesList = getDECodes( deCodesXMLFileName );

        int orgUnitCount = 0;
        int orgUnitGroupCount = 0;
        int rowCounter = 0;

        OrganisationUnit checkChildOrgunit = new OrganisationUnit();
        checkChildOrgunit = organisationUnitService.getOrganisationUnit( ouIDTB );
        childOrgUnits = new ArrayList<OrganisationUnit>();
        childOrgUnits.addAll( checkChildOrgunit.getChildren() );

        int children = 1;

        if ( reportModelTB.equalsIgnoreCase( "INDICATOR-FOR-FEEDBACK" )
            && (childOrgUnits == null || childOrgUnits.size() == 0) )
        {
            children = 0;
        }

        if ( orgUnitList == null )
        {
            statusMessage = "Selected Organisation Unit has No Children. Please select Orgnisatinunit With Childrens.";
            return SUCCESS;
        }

        // ---------------------------------------------------------------------------------------------------
        // All other reports START
        // ---------------------------------------------------------------------------------------------------

        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + "template" + File.separator + reportFileNameTB;

        Iterator<OrganisationUnit> it = orgUnitList.iterator();

        while ( it.hasNext() && children != 0 )
        {
            simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
            PeriodType periodType = periodService.getPeriodTypeByName( periodTypeId );

            Collection<Period> reportingPeriod = new ArrayList<Period>();
            reportingPeriod = periodService.getPeriodsBetweenDates( periodType, format.parseDate( startDate ),
                format.parseDate( endDate ) );

            Iterator<Period> itr = reportingPeriod.iterator();

            OrganisationUnit currentOrgUnit = it.next();

            while ( itr.hasNext() )
            {
                Period dateItrVal = itr.next();
                sDate = dateItrVal.getStartDate();
                eDate = dateItrVal.getEndDate();

                fileName = reportFileNameTB.replace( ".xls", "" );
                fileName += "_" + currentOrgUnit.getShortName() + "_";

                fileName += "_" + simpleDateFormat.format( sDate ) + "_";
                fileName += "_" + simpleDateFormat.format( eDate ) + ".xls";

                String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName
                    + File.separator + "output" + File.separator + fileName;

                Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

                WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ),
                    templateWorkbook );

                int quarterPeriod = 0;

                // OrganisationUnit currentOrgUnit = it.next();

                Iterator<String> it1 = deCodesList.iterator();
                int count1 = 0;
                while ( it1.hasNext() )
                {
                    String deCodeString = it1.next();

                    String deType = deCodeType.get( count1 );
                    String sType = serviceType.get( count1 );

                    String tempStr = "";

                    Calendar tempStartDate = Calendar.getInstance();
                    Calendar tempEndDate = Calendar.getInstance();
                    List<Calendar> calendarList = new ArrayList<Calendar>( getStartingEndingPeriods( deType ) );
                    if ( calendarList == null || calendarList.isEmpty() )
                    {
                        tempStartDate.setTime( sDate );
                        tempEndDate.setTime( eDate );
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
                    else if ( deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) )
                    {
                        tempStr = parentUnit;
                    }
                    else if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                    {
                        tempStr = currentOrgUnit.getParent().getName();

                    }

                    else if ( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                    {
                        OrganisationUnit orgUnitP = new OrganisationUnit();

                        orgUnitP = currentOrgUnit.getParent();

                        tempStr = orgUnitP.getParent().getName();

                    }

                    else if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                    {
                        OrganisationUnit orgUnitP = new OrganisationUnit();

                        OrganisationUnit orgUnitPP = new OrganisationUnit();

                        orgUnitP = currentOrgUnit.getParent();

                        orgUnitPP = orgUnitP.getParent();

                        tempStr = orgUnitPP.getParent().getName();

                    }

                    else if ( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                    {
                        OrganisationUnit orgUnitP = new OrganisationUnit();

                        OrganisationUnit orgUnitPP = new OrganisationUnit();

                        OrganisationUnit orgUnitPPP = new OrganisationUnit();

                        orgUnitP = currentOrgUnit.getParent();

                        orgUnitPP = orgUnitP.getParent();

                        orgUnitPPP = orgUnitPP.getParent();

                        tempStr = orgUnitPPP.getParent().getName();

                    }

                    else if ( deCodeString.equalsIgnoreCase( "PERIOD" )
                        || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) )
                    {
                        tempStr = simpleDateFormat.format( sDate );

                    }
                    else if ( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) )
                    {
                        tempStr = monthFormat.format( sDate );

                    }
                    else if ( deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) )
                    {
                        tempStr = simpleMonthFormat.format( sDate );

                    }
                    else if ( deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) )
                    {
                        tempStr = simpleMonthFormat.format( eDate );

                    }

                    else if ( deCodeString.equalsIgnoreCase( "MONTH-START" ) )
                    {
                        tempStr = monthFormat.format( sDate );

                    }
                    else if ( deCodeString.equalsIgnoreCase( "MONTH-END" ) )
                    {
                        tempStr = monthFormat.format( eDate );

                    }

                    else if ( deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) )
                    {
                        tempStr = String.valueOf( tempStartDate.get( Calendar.WEEK_OF_MONTH ) );

                    }
                    else if ( deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) )
                    {
                        String startMonth = "";

                        startMonth = monthFormat.format( sDate );

                        if ( startMonth.equalsIgnoreCase( "April" ) )
                        {
                            tempStr = "Quarter I";
                        }

                        else if ( startMonth.equalsIgnoreCase( "July" ) )
                        {
                            tempStr = "Quarter II";
                        }
                        else if ( startMonth.equalsIgnoreCase( "October" ) )
                        {
                            tempStr = "Quarter III";
                        }

                        else
                        {
                            tempStr = "Quarter IV";

                            quarterPeriod = 1;

                        }
                    }

                    else if ( deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" ) )
                    {
                        String startMonth = "";

                        startMonth = monthFormat.format( sDate );

                        if ( startMonth.equalsIgnoreCase( "April" ) )
                        {
                            tempStr = "Q1";
                        }

                        else if ( startMonth.equalsIgnoreCase( "July" ) )
                        {
                            tempStr = "Q2";
                        }
                        else if ( startMonth.equalsIgnoreCase( "October" ) )
                        {
                            tempStr = "Q3";
                        }

                        else
                        {
                            tempStr = "Q4";

                            quarterPeriod = 1;

                        }
                    }

                    else if ( deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" ) )
                    {
                        String startMonth = "";

                        startMonth = monthFormat.format( sDate );

                        if ( startMonth.equalsIgnoreCase( "April" ) )
                        {
                            tempStr = "Apr - Jun";
                        }

                        else if ( startMonth.equalsIgnoreCase( "July" ) )
                        {
                            tempStr = "Jul - Sep";
                        }
                        else if ( startMonth.equalsIgnoreCase( "October" ) )
                        {
                            tempStr = "Oct - Dec";
                        }

                        else
                        {
                            tempStr = "Jan - Mar";

                            quarterPeriod = 1;

                        }
                    }

                    else if ( deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" ) )
                    {
                        String startMonth = "";

                        startMonth = monthFormat.format( sDate );

                        if ( startMonth.equalsIgnoreCase( "April" ) )
                        {
                            tempStr = "April - June";
                        }

                        else if ( startMonth.equalsIgnoreCase( "July" ) )
                        {
                            tempStr = "July - September";
                        }
                        else if ( startMonth.equalsIgnoreCase( "October" ) )
                        {
                            tempStr = "October - December";
                        }

                        else
                        {
                            tempStr = "January - March";

                            quarterPeriod = 1;

                        }
                    }

                    else if ( deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" ) )
                    {
                        String startMonth = "";

                        startMonth = monthFormat.format( sDate );

                        if ( startMonth.equalsIgnoreCase( "April" ) )
                        {
                            tempStr = "Apr";
                        }

                        else if ( startMonth.equalsIgnoreCase( "July" ) )
                        {
                            tempStr = "Jul";
                        }
                        else if ( startMonth.equalsIgnoreCase( "October" ) )
                        {
                            tempStr = "Oct";
                        }

                        else
                        {
                            tempStr = "Jan";

                            quarterPeriod = 1;

                        }
                    }

                    else if ( deCodeString.equalsIgnoreCase( "QUARTER-START" ) )
                    {
                        String startMonth = "";

                        startMonth = monthFormat.format( sDate );

                        if ( startMonth.equalsIgnoreCase( "April" ) )
                        {
                            tempStr = "April";
                        }

                        else if ( startMonth.equalsIgnoreCase( "July" ) )
                        {
                            tempStr = "July";
                        }
                        else if ( startMonth.equalsIgnoreCase( "October" ) )
                        {
                            tempStr = "October";
                        }

                        else
                        {
                            tempStr = "January";

                            quarterPeriod = 1;

                        }
                    }

                    else if ( deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" ) )
                    {
                        String endMonth = "";

                        endMonth = monthFormat.format( eDate );

                        if ( endMonth.equalsIgnoreCase( "June" ) )
                        {
                            tempStr = "Jun";
                        }

                        else if ( endMonth.equalsIgnoreCase( "September" ) )
                        {
                            tempStr = "Sep";
                        }
                        else if ( endMonth.equalsIgnoreCase( "December" ) )
                        {
                            tempStr = "Dec";
                        }

                        else
                        {
                            tempStr = "Mar";

                            quarterPeriod = 1;

                        }
                    }

                    else if ( deCodeString.equalsIgnoreCase( "QUARTER-END" ) )
                    {
                        String endMonth = "";

                        endMonth = monthFormat.format( eDate );

                        if ( endMonth.equalsIgnoreCase( "June" ) )
                        {
                            tempStr = "June";
                        }

                        else if ( endMonth.equalsIgnoreCase( "September" ) )
                        {
                            tempStr = "September";
                        }
                        else if ( endMonth.equalsIgnoreCase( "December" ) )
                        {
                            tempStr = "December";
                        }

                        else
                        {
                            tempStr = "March";

                            quarterPeriod = 1;

                        }
                    }

                    else if ( deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) )
                    {
                        sDateTemp = sDate;

                        Calendar tempQuarterYear = Calendar.getInstance();

                        tempQuarterYear.setTime( sDateTemp );

                        String startMonth = "";

                        startMonth = monthFormat.format( sDateTemp );

                        // periodType =
                        // periodService.getPeriodType(periodTypeIdTB);

                        if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                        {
                            sDateTemp = sDate;
                        }

                        else
                        {
                            if ( (startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                                .equalsIgnoreCase( "March" )) && periodType.getName().equalsIgnoreCase( "Quarterly" ) )
                            {
                                tempQuarterYear.roll( Calendar.YEAR, -1 );

                                sDateTemp = tempQuarterYear.getTime();

                            }
                        }

                        tempStr = yearFormat.format( sDateTemp );
                    }

                    else if ( deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" ) )
                    {
                        sDateTemp = sDate;

                        Calendar tempQuarterYear = Calendar.getInstance();

                        tempQuarterYear.setTime( sDateTemp );

                        String startMonth = "";

                        startMonth = monthFormat.format( sDateTemp );

                        // periodType =
                        // periodService.getPeriodType(periodTypeIdTB);

                        if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                        {
                            sDateTemp = sDate;
                        }

                        else
                        {
                            if ( (startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                                .equalsIgnoreCase( "March" )) && periodType.getName().equalsIgnoreCase( "Quarterly" ) )
                            {
                                tempQuarterYear.roll( Calendar.YEAR, -1 );

                                sDateTemp = tempQuarterYear.getTime();

                            }
                        }

                        tempStr = simpleYearFormat.format( sDateTemp );
                    }

                    else if ( deCodeString.equalsIgnoreCase( "YEAR-END" ) )
                    {

                        sDateTemp = sDate;

                        Calendar tempQuarterYear = Calendar.getInstance();

                        tempQuarterYear.setTime( sDate );

                        sDate = tempQuarterYear.getTime();

                        String startMonth = "";

                        startMonth = monthFormat.format( sDateTemp );

                        // periodType =
                        // periodService.getPeriodType(periodTypeIdTB);

                        if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                        {
                            tempQuarterYear.roll( Calendar.YEAR, 1 );

                            sDateTemp = tempQuarterYear.getTime();

                        }

                        if ( !(startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                            .equalsIgnoreCase( "March" )) )
                        {
                            tempQuarterYear.roll( Calendar.YEAR, 1 );

                            sDateTemp = tempQuarterYear.getTime();

                        }

                        tempStr = yearFormat.format( sDateTemp );
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

                        // periodType =
                        // periodService.getPeriodType(periodTypeIdTB);

                        tempQuarterYear.setTime( sDateTemp );

                        if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
                        {
                            sDateTemp = sDate;
                        }

                        else
                        {
                            if ( (startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                                .equalsIgnoreCase( "March" )) )
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

                        if ( !(startMonth.equalsIgnoreCase( "January" ) || startMonth.equalsIgnoreCase( "February" ) || startMonth
                            .equalsIgnoreCase( "March" )) )
                        {
                            tempQuarterYear.roll( Calendar.YEAR, 1 );

                            eDateTemp = tempQuarterYear.getTime();

                        }
                        endYear = yearFormat.format( eDateTemp );

                        tempStr = startYear + " - " + endYear;

                    }

                    else if ( deCodeString.equalsIgnoreCase( "SLNO" ) )
                    {
                        tempStr = "" + (orgUnitCount + 1);
                    }
                    else if ( deCodeString.equalsIgnoreCase( "NA" ) )
                    {
                        tempStr = " ";
                    }
                    else
                    {
                        rowCounter += 1;

                        if ( sType.equalsIgnoreCase( "dataelement" ) )
                        {
                            if ( aggCB == null )
                            {
                                tempStr = getIndividualResultDataValue( deCodeString, tempStartDate.getTime(),
                                    tempEndDate.getTime(), currentOrgUnit );
                            }
                            else
                            {
                                tempStr = getResultDataValue( deCodeString, tempStartDate.getTime(),
                                    tempEndDate.getTime(), currentOrgUnit );
                            }
                        }
                        else if ( sType.equalsIgnoreCase( "indicator-parent" ) )
                        {
                            if ( aggCB == null )
                            {
                                tempStr = getIndividualResultIndicatorValue( deCodeString, tempStartDate.getTime(),
                                    tempEndDate.getTime(), currentOrgUnit.getParent() );
                            }
                            else
                            {
                                tempStr = getResultIndicatorValue( deCodeString, tempStartDate.getTime(),
                                    tempEndDate.getTime(), currentOrgUnit.getParent() );
                            }
                        }
                        else if ( sType.equalsIgnoreCase( "dataelement-boolean" ) )
                        {
                            if ( aggCB == null )
                            {
                                tempStr = getBooleanDataValue( deCodeString, tempStartDate.getTime(),
                                    tempEndDate.getTime(), currentOrgUnit );
                            }
                            else
                            {
                                tempStr = getBooleanDataValue( deCodeString, tempStartDate.getTime(),
                                    tempEndDate.getTime(), currentOrgUnit );
                            }
                        }
                        else
                        {
                            if ( aggCB == null )
                            {
                                tempStr = getIndividualResultIndicatorValue( deCodeString, tempStartDate.getTime(),
                                    tempEndDate.getTime(), currentOrgUnit );
                            }
                            else
                            {
                                tempStr = getResultIndicatorValue( deCodeString, tempStartDate.getTime(),
                                    tempEndDate.getTime(), currentOrgUnit );
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
                    }
                    else
                    {

                        if ( reportModelTB.equalsIgnoreCase( "STATIC" ) )
                        {
                            if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "PERIOD" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" )
                                || deCodeString.equalsIgnoreCase( "MONTH-START" )
                                || deCodeString.equalsIgnoreCase( "MONTH-END" )
                                || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" )
                                || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" )
                                || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-START" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-END" )
                                || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" )
                                || deCodeString.equalsIgnoreCase( "YEAR-END" )
                                || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                            {

                            }
                            else
                            {
                                // tempColNo +=
                                // orgUnitCount;
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
                            }
                            else
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        }

                        if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-ORGUNIT" ) )
                        {
                            if ( deCodeString.equalsIgnoreCase( "FACILITYP" )
                                || deCodeString.equalsIgnoreCase( "FACILITYPP" )
                                || deCodeString.equalsIgnoreCase( "FACILITYPPP" )
                                || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "PERIOD" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" )
                                || deCodeString.equalsIgnoreCase( "MONTH-START" )
                                || deCodeString.equalsIgnoreCase( "MONTH-END" )
                                || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" )
                                || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" )
                                || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-START" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-END" )
                                || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" )
                                || deCodeString.equalsIgnoreCase( "YEAR-END" )
                                || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                            {

                            }

                            else
                            {
                                tempColNo += orgUnitCount;
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
                            }
                            else
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        }

                        if ( reportModelTB.equalsIgnoreCase( "STATIC-DATAELEMENTS" )
                            || reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" ) )
                        {
                            if ( deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                                || deCodeString.equalsIgnoreCase( "FACILITY" ) )
                            {

                            }
                            if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                            {

                            }
                            else if ( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "PERIOD" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" )
                                || deCodeString.equalsIgnoreCase( "MONTH-START" )
                                || deCodeString.equalsIgnoreCase( "MONTH-END" )
                                || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" )
                                || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" )
                                || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-START" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-END" )
                                || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" )
                                || deCodeString.equalsIgnoreCase( "YEAR-END" )
                                || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                            {

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
                            }
                            else
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        }

                        if ( reportModelTB.equalsIgnoreCase( "INDICATOR-AGAINST-PARENT" ) )
                        {
                            if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "PERIOD" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" )
                                || deCodeString.equalsIgnoreCase( "MONTH-START" )
                                || deCodeString.equalsIgnoreCase( "MONTH-END" )
                                || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" )
                                || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" )
                                || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-START" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-END" )
                                || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" )
                                || deCodeString.equalsIgnoreCase( "YEAR-END" )
                                || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                            {

                            }
                            else
                            {
                                // tempColNo +=
                                // (orgUnitCount * 2);
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
                            }
                            else
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        }

                        if ( reportModelTB.equalsIgnoreCase( "INDICATOR-AGAINST-SIBLINGS" )
                            || reportModelTB.equalsIgnoreCase( "INDICATOR-FOR-FEEDBACK" ) )
                        {

                            if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "PERIOD" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" )
                                || deCodeString.equalsIgnoreCase( "MONTH-START" )
                                || deCodeString.equalsIgnoreCase( "MONTH-END" )
                                || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" )
                                || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" )
                                || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-START" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-END" )
                                || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" )
                                || deCodeString.equalsIgnoreCase( "YEAR-END" )
                                || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                            {

                            }
                            else
                            {
                                tempColNo += orgUnitCount;
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
                            }
                            else
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        }
                        if ( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) )
                        {
                            if ( deCodeString.equalsIgnoreCase( "FACILITYP" )
                                || deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                            {

                            }

                            else if ( deCodeString.equalsIgnoreCase( "PERIOD" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" )
                                || deCodeString.equalsIgnoreCase( "MONTH-START" )
                                || deCodeString.equalsIgnoreCase( "MONTH-END" )
                                || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" )
                                || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" )
                                || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-START" )
                                || deCodeString.equalsIgnoreCase( "QUARTER-END" )
                                || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" )
                                || deCodeString.equalsIgnoreCase( "YEAR-END" )
                                || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                            {

                            }
                            else
                            {
                                tempRowNo += orgUnitCount;
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
                            }
                            else
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        }
                    }
                    count1++;
                }// inner while loop end

                outputReportWorkbook.write();
                outputReportWorkbook.close();

            }// intermediate while loop end
            orgUnitCount++;

            System.out.println( currentOrgUnit.getName() + " is Completed " );

            statusMessage = "Report Generation is complited. Please collect your reports from OutPut folder.";

        }// outer while loop end
        System.out.println( "Report Generation End Time is : \t" + new Date() );
        statementManager.destroy();
        return SUCCESS;
    }

    public List<Calendar> getStartingEndingPeriods( String deType )
    {

        List<Calendar> calendarList = new ArrayList<Calendar>();

        Calendar tempStartDate = Calendar.getInstance();
        Calendar tempEndDate = Calendar.getInstance();

        Period previousPeriod = new Period();
        previousPeriod = getPreviousPeriod();

        if ( deType.equalsIgnoreCase( "ccmcy" ) )
        {
            tempStartDate.setTime( sDate );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            tempEndDate.setTime( eDate );
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
            tempStartDate.setTime( sDate );
            tempEndDate.setTime( eDate );

            tempStartDate.roll( Calendar.YEAR, -1 );
            tempEndDate.roll( Calendar.YEAR, -1 );
        }
        else if ( deType.equalsIgnoreCase( "ccmpy" ) )
        {
            tempStartDate.setTime( sDate );
            tempEndDate.setTime( eDate );

            tempStartDate.roll( Calendar.YEAR, -1 );
            tempEndDate.roll( Calendar.YEAR, -1 );

            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );

        }
        else if ( deType.equalsIgnoreCase( "pmcy" ) )
        {
            tempStartDate.setTime( previousPeriod.getStartDate() );
            tempEndDate.setTime( previousPeriod.getEndDate() );

        }

        else
        {

            tempStartDate.setTime( sDate );
            tempEndDate.setTime( eDate );
        }

        // System.out.print(deType+" -- ");
        calendarList.add( tempStartDate );
        calendarList.add( tempEndDate );

        return calendarList;
    }

    public Period getPreviousPeriod()
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
        PeriodType periodType = PeriodType.getByNameIgnoreCase( "monthly" );
        period = getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ), periodType );

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

        Period newPeriod = new Period();

        newPeriod.setStartDate( firstDay );
        newPeriod.setEndDate( lastDay );
        newPeriod.setPeriodType( periodType );

        return newPeriod;
    }

    public List<String> getDECodes( String fileName )
    {
        List<String> deCodes = new ArrayList<String>();
        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName
            + File.separator + fileName;
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
                deCodes.add( ((Node) textDECodeList.item( 0 )).getNodeValue().trim() );
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
            ((x == null) ? e : x).printStackTrace();
        }
        catch ( Throwable t )
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
            DataSet ds = it.next();
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

            int deFlag1 = 0;
            int deFlag2 = 0;
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1,
                    replaceString.length() );

                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );

                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryService
                    .getDataElementCategoryOptionCombo( optionComboId );

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

                        deFlag2 = 1;
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
                        tempPeriod = periodList.get( 0 );
                    }

                    DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, tempPeriod,
                        optionCombo );

                    if ( dataValue != null )
                    {
                        // Works for both text and boolean data types

                        replaceString = dataValue.getValue();
                    }

                    else
                        replaceString = "";

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
                    if ( !(reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" )) )
                        resultValue = "" + (int) d;

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

    private String getIndividualResultDataValue( String formula, Date startDate, Date endDate,
        OrganisationUnit organisationUnit )
    {
        try
        {
            int deFlag1 = 0;
            int deFlag2 = 0;
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";
            boolean valueDoesNotExist = true;

            while ( matcher.find() )
            {

                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1,
                    replaceString.length() );

                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );

                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryService
                    .getDataElementCategoryOptionCombo( optionComboId );

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

                        deFlag2 = 1;
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
                        tempPeriod = periodList.get( 0 );
                    }

                    DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, tempPeriod,
                        optionCombo );

                    if ( dataValue != null )
                    {
                        // Works for both text and boolean data types

                        replaceString = dataValue.getValue();
                        valueDoesNotExist = false;
                    }

                    else
                        replaceString = "";

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
                    if ( !(reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" )) )
                        resultValue = "" + (int) d;

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

            if ( valueDoesNotExist )
                resultValue = " ";

            if ( resultValue.equalsIgnoreCase( "" ) )
                resultValue = " ";

            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    private String getBooleanDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit )
    {
        try
        {
            int deFlag1 = 0;
            int deFlag2 = 0;
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1,
                    replaceString.length() );

                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );

                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryService
                    .getDataElementCategoryOptionCombo( optionComboId );

                if ( dataElement == null || optionCombo == null )
                {
                    replaceString = "";
                    matcher.appendReplacement( buffer, replaceString );
                    continue;
                }

                if ( dataElement.getType().equalsIgnoreCase( "bool" ) )
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
                        tempPeriod = periodList.get( 0 );
                    }

                    DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, tempPeriod,
                        optionCombo );

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
                resultValue = buffer.toString();
            }
            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    private String getResultIndicatorValue( String formula, Date startDate, Date endDate,
        OrganisationUnit organisationUnit )
    {
        try
        {

            int deFlag1 = 0;
            int deFlag2 = 0;
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
                }
                else
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
                }
                catch ( Exception e )
                {
                    d = 0.0;
                }
                if ( d == -1 )
                    d = 0.0;
                else
                {
                    d = Math.round( d * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                    resultValue = "" + d;
                }

                if ( deFlag2 == 0 )
                {
                    resultValue = " ";
                }
            }
            else
            {
                resultValue = buffer.toString();
            }
            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    private String getIndividualResultIndicatorValue( String formula, Date startDate, Date endDate,
        OrganisationUnit organisationUnit )
    {
        try
        {

            int deFlag1 = 0;
            int deFlag2 = 0;
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
                }
                catch ( Exception e )
                {
                    System.out.println( "Exception while getting Numerator : " + numeratorExp + " for Indicaotr "
                        + indicator.getName() );
                    numeratorValue = 0.0;
                }

                double denominatorValue;
                try
                {
                    denominatorValue = Double.parseDouble( denominatorVal );
                }
                catch ( Exception e )
                {
                    System.out.println( "Exception while getting Deniminator : " + denominatorExp + " for Indicaotr "
                        + indicator.getName() );
                    denominatorValue = 1.0;
                }

                double aggregatedValue;
                try
                {
                    aggregatedValue = (numeratorValue / denominatorValue) * indicatorFactor;
                }
                catch ( Exception e )
                {
                    System.out.println( "Exception while calculating Indicator value for Indicaotr "
                        + indicator.getName() );
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
                }
                catch ( Exception e )
                {
                    d = 0.0;
                }
                if ( d == -1 )
                    d = 0.0;
                else
                {
                    d = Math.round( d * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                    resultValue = "" + d;
                }

                if ( deFlag2 == 0 )
                {
                    resultValue = " ";
                }
            }
            else
            {
                resultValue = buffer.toString();
            }
            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

}
