package org.hisp.dhis.dataanalyser.ta.action;

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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.SixMonthlyPeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;

import com.opensymphony.xwork2.Action;

public class GenerateTabularAnalysisResultAction
    implements Action
{

    private final String ORGUNITSELECTED = "orgUnitSelectedRadio";

    private final String ORGUNITGRP = "orgUnitGroupRadio";

    private final String ORGUNITLEVEL = "orgUnitLevelRadio";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // @SuppressWarnings("unused")
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

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input & Output
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

    private List<OrganisationUnit> selOUList;

    private List<Date> selStartPeriodList;

    private List<Date> selEndPeriodList;

    private OrganisationUnit selOrgUnit;

    private List<String> selectedServices;

    public void setSelectedServices( List<String> selectedServices )
    {
        this.selectedServices = selectedServices;
    }

    private String deSelection;

    public void setDeSelection( String deSelection )
    {
        this.deSelection = deSelection;
    }

    private List<String> orgUnitListCB;

    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }

    private Integer orgUnitLevelCB;

    public void setOrgUnitLevelCB( Integer orgUnitLevelCB )
    {
        this.orgUnitLevelCB = orgUnitLevelCB;
    }

    private String periodTypeLB;

    public void setPeriodTypeLB( String periodTypeLB )
    {
        this.periodTypeLB = periodTypeLB;
    }

    private String aggPeriodCB;

    public void setAggPeriodCB( String aggPeriodCB )
    {
        this.aggPeriodCB = aggPeriodCB;
    }

    private List<String> yearLB;

    public void setYearLB( List<String> yearLB )
    {
        this.yearLB = yearLB;
    }

    private List<String> periodLB;

    public void setPeriodLB( List<String> periodLB )
    {
        this.periodLB = periodLB;
    }

    private String ouRadio;

    public void setOuRadio( String ouRadio )
    {
        this.ouRadio = ouRadio;
    }

    List<String> periodNames;

    private Map<OrganisationUnit, Integer> ouChildCountMap;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        /* Initialization */
        statementManager.initialise();

        selOUList = new ArrayList<OrganisationUnit>();
        selStartPeriodList = new ArrayList<Date>();
        selEndPeriodList = new ArrayList<Date>();

        ouChildCountMap = new HashMap<OrganisationUnit, Integer>();

        String monthOrder[] = {  "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
        int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        
        
        /* Period Info */

        String startD = "";
        String endD = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
       
        periodNames = new ArrayList<String>();
        // for weekly period
        
        if ( periodTypeLB.equalsIgnoreCase( WeeklyPeriodType.NAME ) )
        {
            System.out.println( " Inside  weekly" );
            for ( String periodStr : periodLB )
            {
                String  startWeekDate = periodStr.split( "To" )[0] ; //for start week
                String  endWeekDate = periodStr.split( "To" )[1] ; //for end week
                
                startD = startWeekDate.trim();
                endD = endWeekDate.trim();
                
                selStartPeriodList.add( format.parseDate( startD ) );
                selEndPeriodList.add( format.parseDate( endD ) );
                
                periodNames.add( periodStr );
                //System.out.println( startD + " : " + endD );
            }
        }
        
        else
        {
            System.out.println( " Inside other than weekly" );
            for ( String year : yearLB )
            {
                int selYear = Integer.parseInt( year );
        
                if ( periodTypeLB.equalsIgnoreCase( YearlyPeriodType.NAME ) )
                {
                    startD = "" + selYear + "-01-01";
                    endD = "" + selYear  + "-12-31";
                    
                    selStartPeriodList.add( format.parseDate( startD ) );
                    selEndPeriodList.add( format.parseDate( endD ) );
                    
                    periodNames.add( "" + selYear );
        
                    continue;
                }
        
                for ( String periodStr : periodLB )
                {
                    if ( periodTypeLB.equalsIgnoreCase( MonthlyPeriodType.NAME ) )
                    {
                        int period = Integer.parseInt( periodStr );
                        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
                        
                        startD = "" + selYear + "-" + monthOrder[period] + "-01";
                        endD = "" + selYear  + "-" + monthOrder[period] + "-" + monthDays[period];
                    
                        //check for leapYear
                        if ( ((( selYear ) % 400 == 0 ) || ((( selYear) % 100 != 0 && ( selYear ) % 4 == 0))) && period == 1 )
                        {
                            endD = "" + selYear  + "-" + monthOrder[period] + "-" + ( monthDays[period] + 1 );
                        } 
                        selStartPeriodList.add( format.parseDate( startD ) );
                        selEndPeriodList.add( format.parseDate( endD ) );
                        
                        periodNames.add( simpleDateFormat.format( format.parseDate( startD ) ) );
                    }
                    else if ( periodTypeLB.equalsIgnoreCase( QuarterlyPeriodType.NAME ) )
                    {
                        int period = Integer.parseInt( periodStr );
                        if ( period == 0 )
                        {
                            startD = "" + selYear + "-01-01";
                            endD = "" + selYear + "-03-31";
                            periodNames.add( selYear + "-Q1" );
                        }
                        else if ( period == 1 )
                        {
                            startD = "" + selYear + "-04-01";
                            endD = "" + selYear + "-06-30";
                            periodNames.add( selYear + "-Q2" );
                        }
                        else if ( period == 2 )
                        {
                            startD = "" + selYear + "-07-01";
                            endD = "" + selYear + "-09-30";
                            periodNames.add( selYear + "-Q3" );
                        }
                        else
                        {
                            startD = "" + selYear + "-10-01";
                            endD = "" + selYear + "-12-31";
                            periodNames.add( (selYear) + "-Q4" );
                        }
                        selStartPeriodList.add( format.parseDate( startD ) );
                        selEndPeriodList.add( format.parseDate( endD ) );
                    }
                    else if ( periodTypeLB.equalsIgnoreCase( SixMonthlyPeriodType.NAME ) )
                    {
                        int period = Integer.parseInt( periodStr );
                        if ( period == 0 )
                        {
                            startD = "" + selYear + "-01-01";
                            endD = "" + selYear + "-06-30";
                            periodNames.add( selYear + "-HY1" );
                        }
                        else
                        {
                            startD = "" + selYear + "-07-01";
                            endD = "" + selYear + "-12-31";
                            periodNames.add( selYear + "-HY2" );
                        }
                       
                        selStartPeriodList.add( format.parseDate( startD ) );
                        selEndPeriodList.add( format.parseDate( endD ) );
                    }
                    else if ( periodTypeLB.equalsIgnoreCase( DailyPeriodType.NAME ) )
                    {
                       String  month = periodStr.split( "-" )[0] ;
                       String  date = periodStr.split( "-" )[1] ;
                      
                       startD = selYear + "-" + periodStr;
                       endD = selYear + "-" + periodStr ;
                       
                       if( selYear  % 4 != 0 && month.trim().equalsIgnoreCase( "02" )  && date.trim().equalsIgnoreCase( "29" ) )
                       {
                           continue;
                       }

                       startD = selYear + "-" + month + "-" + date;
                       endD = selYear  + "-" + month + "-" + date;
                       
                       selStartPeriodList.add( format.parseDate( startD ) );
                       selEndPeriodList.add( format.parseDate( endD ) );
                       System.out.println( startD + " *** " + endD );
                       periodNames.add( startD );
                    }
                }
            }
      }
        
        
 // calling diffrent functions       
    
    
    if ( ouRadio.equalsIgnoreCase( ORGUNITSELECTED ) )
    {
        System.out.println( "Report Generation Start Time is : \t" + new Date() );
        generateOrgUnitSelected();

    }
    else if ( ouRadio.equalsIgnoreCase( ORGUNITGRP ) )
    {
        System.out.println( "Report Generation Start Time is : \t" + new Date() );
        generateOrgUnitGroup();

    }
    else if ( ouRadio.equalsIgnoreCase( ORGUNITLEVEL ) )
    {
        System.out.println( "Report Generation Start Time is : \t" + new Date() );
        generateOrgUnitLevel();

    }

    statementManager.destroy();
    System.out.println( "Report Generation End Time is : \t" + new Date() );

    return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Methods for getting OrgUnitSelected wise List in Excel Sheet
    // -------------------------------------------------------------------------

    public void generateOrgUnitSelected()
        throws Exception
    {

        int startRow = 0;
        // int startCol = 0;
        int headerRow = 0;
        int headerCol = 0;

        System.out.println( "inside the generateOrgUnitSelected" );

        String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER )
            .getValue();
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.mergeCells( headerCol, headerRow, headerCol, headerRow + 1 );
        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        for ( String ouStr : orgUnitListCB )
        {
            OrganisationUnit ou = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouStr ) );
            selOUList.add( ou );
        }

        int c1 = headerCol + 1;
        sheet0.mergeCells( c1, headerRow, c1, headerRow + 1 );
        sheet0.addCell( new Label( c1, headerRow, "Facility", getCellFormat1() ) );
        c1++;

        int rowCount = 1;
        int colCount = 0;

        for ( OrganisationUnit ou : selOUList )
        {
            sheet0.addCell( new Number( headerCol, headerRow + 1 + rowCount, rowCount, getCellFormat2() ) );

            // System.out.println(colCount + " : " + minOULevel + " : " +
            // organisationUnitService.getLevelOfOrganisationUnit( ou ));

            // sheet0.mergeCells( colCount, headerRow+1+rowCount, colCount,
            // headerRow+1+rowCount+ouChildCountMap.get( ou ));
            sheet0.addCell( new Label( 1, rowCount + 1, ou.getName(), getCellFormat2() ) );

            /* Service Info */
            Indicator selIndicator = new Indicator();
            DataElement selDataElement = new DataElement();
            DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
            int flag = 0;
            colCount = c1;
            for ( String service : selectedServices )
            {
                String partsOfService[] = service.split( ":" );
                if ( partsOfService[0].equalsIgnoreCase( "I" ) )
                {
                    flag = 1;
                    selIndicator = indicatorService.getIndicator( Integer.parseInt( partsOfService[1] ) );
                    if ( rowCount == 1 )
                    {
                        if ( aggPeriodCB == null )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                        }
                        else
                        {
                            sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                        }

                        sheet0.addCell( new Label( colCount, startRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementService.getDataElement( Integer.parseInt( partsOfService[1] ) );

                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer
                            .parseInt( partsOfService[2] ) );
                        if ( rowCount == 1 )
                        {
                            if ( aggPeriodCB == null )
                            {
                                sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1,
                                    startRow );
                            }
                            else
                            {
                                sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                            }

                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName() + "-"
                                + selDecoc.getName(), getCellFormat1() ) );
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            if ( aggPeriodCB == null )
                            {
                                sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1,
                                    startRow );
                            }
                            else
                            {
                                sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                            }

                            sheet0
                                .addCell( new Label( colCount, startRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                    }
                }

                int periodCount = 0;
                double numAggValue = 0.0;
                double denAggValue = 0.0;
                double dvAggValue = 0.0;
                String aggStrValue = "";

                for ( Date sDate : selStartPeriodList )
                {
                    Date eDate = selEndPeriodList.get( periodCount );
                    double pwnumAggValue = 0.0;
                    double pwdenAggValue = 0.0;
                    double pwdvAggValue = 0.0;
                    double pwdAggIndValue = 0.0;

                    String tempStr = "";

                    Double tempAggVal;
                    if ( flag == 1 )
                    {
                        tempAggVal = aggregationService.getAggregatedNumeratorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwnumAggValue = tempAggVal;
                        tempAggVal = aggregationService.getAggregatedDenominatorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwdenAggValue = tempAggVal;

                        tempAggVal = aggregationService.getAggregatedIndicatorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwdAggIndValue = tempAggVal;

                        pwdAggIndValue = Math.round( pwdAggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        tempStr = "" + pwdAggIndValue;

                    }
                    else if ( flag == 2 )
                    {
                        if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                tempAggVal = aggregationService.getAggregatedDataValue( selDataElement, selDecoc,
                                    sDate, eDate, ou );
                                if ( tempAggVal == null )
                                    tempAggVal = 0.0;
                                pwdvAggValue = tempAggVal;

                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                if ( tempPeriod != null )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( ou, selDataElement,
                                        tempPeriod, selDecoc );

                                    if ( dataValue != null )
                                    {
                                        tempStr = dataValue.getValue();
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        else
                        {
                            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                                selDataElement.getCategoryCombo().getOptionCombos() );

                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator
                                        .next();

                                    tempAggVal = aggregationService.getAggregatedDataValue( selDataElement, decoc1,
                                        sDate, eDate, ou );
                                    if ( tempAggVal == null )
                                        tempAggVal = 0.0;
                                    pwdvAggValue += tempAggVal;
                                }

                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator
                                        .next();

                                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                    Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                    if ( tempPeriod != null )
                                    {
                                        DataValue dataValue = dataValueService.getDataValue( ou, selDataElement,
                                            tempPeriod, decoc1 );

                                        if ( dataValue != null )
                                        {
                                            tempStr += dataValue.getValue() + " : ";
                                        }
                                        else
                                        {
                                            tempStr = "  ";
                                        }
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                            }
                        }
                    }

                    if ( aggPeriodCB == null )
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, startRow + 1, periodNames.get( periodCount ),
                                getCellFormat1() ) );
                        }

                        if ( flag == 1 )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, pwdAggIndValue,
                                getCellFormat2() ) );
                        }
                        else
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) pwdvAggValue,
                                    getCellFormat2() ) );
                            }
                            else
                            {
                                sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, tempStr,
                                    getCellFormat2() ) );
                            }
                        }

                        colCount++;
                    }
                    else
                    {
                        numAggValue += pwnumAggValue;
                        denAggValue += pwdenAggValue;
                        dvAggValue += pwdvAggValue;
                        aggStrValue += tempStr + " - ";
                    }

                    periodCount++;
                }// Period Loop

                if ( aggPeriodCB != null )
                {
                    if ( flag == 1 )
                    {
                        try
                        {
                            double tempDouble = Double.parseDouble( selIndicator.getDenominator().trim() );

                            // if(tempInt == 1) denAggValue = 1;
                            denAggValue = tempDouble;
                        }
                        catch ( Exception e )
                        {
                            System.out.println( "Denominator is not expression" );
                        }

                        double aggIndValue = (numAggValue / denAggValue) * selIndicator.getIndicatorType().getFactor();
                        aggIndValue = Math.round( aggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        sheet0
                            .addCell( new Number( colCount, headerRow + 1 + rowCount, aggIndValue, getCellFormat2() ) );
                    }
                    else if ( flag == 2 )
                    {
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) dvAggValue,
                                getCellFormat2() ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, aggStrValue,
                                getCellFormat2() ) );
                        }
                    }

                    colCount++;
                }
                // colCount++;
            }// Service loop end

            rowCount++;
        }// Orgunit loop end

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Methods for getting OrgUnitGroup wise List in Excel Sheet
    // -------------------------------------------------------------------------

    public void generateOrgUnitGroup()
        throws Exception
    {
        int startRow = 0;
        // int startCol = 0;
        int headerRow = 0;
        int headerCol = 0;

        System.out.println( "inside the generateOrgUnitGroup" );

        String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER )
            .getValue();
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.mergeCells( headerCol, headerRow, headerCol, headerRow + 1 );
        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
        List<OrganisationUnit> selOUList = new ArrayList<OrganisationUnit>( organisationUnitService
            .getOrganisationUnitWithChildren( selOrgUnit.getId() ) );
        OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitLevelCB );
        List<OrganisationUnit> orgUnitList1 = new ArrayList<OrganisationUnit>( selOrgUnitGroup.getMembers() );

        selOUList.retainAll( orgUnitList1 );

        //Collections.sort( selOUList, new OrganisationUnitNameComparator() );
        // displayPropertyHandler.handle( selOUList );

        int minOULevel = 1;
        int maxOULevel = organisationUnitService.getNumberOfOrganisationalLevels();
        
        
        /*
        if ( orgUnitLevelCB != null )
        {
            minOULevel = organisationUnitService.getLevelOfOrganisationUnit( selOrgUnit );
        }
        */
        
        int c1 = headerCol + 1;

        for( int i = minOULevel; i <= maxOULevel; i++ )
        {
            sheet0.mergeCells( c1, headerRow, c1, headerRow + 1 );
            sheet0.addCell( new Label( c1, headerRow, "Level- "+i, getCellFormat1() ) );
            c1++;
        }

        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for ( OrganisationUnit ou : selOUList )
        {
            sheet0.addCell( new Number( headerCol, headerRow + 1 + rowCount, rowCount, getCellFormat2() ) );
            colCount = 1 + organisationUnitService.getLevelOfOrganisationUnit( ou ) - minOULevel;
            // System.out.println(colCount + " : " + minOULevel + " : " +
            // organisationUnitService.getLevelOfOrganisationUnit( ou ));

            // sheet0.mergeCells( colCount, headerRow+1+rowCount, colCount,
            // headerRow+1+rowCount+ouChildCountMap.get( ou ));
            sheet0.addCell( new Label( colCount, rowCount + 1, ou.getName(), getCellFormat2() ) );
            
            OrganisationUnit orgUnit = new OrganisationUnit();
            orgUnit = ou;
            int count1=1;
            while( orgUnit.getParent() != null )
            {
                orgUnit = orgUnit.getParent();
                sheet0.addCell( new Label( colCount-count1, rowCount + 1, orgUnit.getName(), getCellFormat2() ) );
                count1++;
            }
            
            
            /*
             * 
             * OrganisationUnit parentOu = ou; for(int i =
             * maxOuLevel-minOULevel+1; i >= minOULevel-1; i-- ) {
             * 
             * { parentOu = parentOu.getParent(); sheet0.addCell( new Label( i,
             * headerRow+rowCount, parentOu.getName(), getCellFormat2() ) ); } }
             */
            /* Service Info */
            Indicator selIndicator = new Indicator();
            DataElement selDataElement = new DataElement();
            DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
            int flag = 0;
            colCount = c1;

            for ( String service : selectedServices )
            {
                String partsOfService[] = service.split( ":" );
                if ( partsOfService[0].equalsIgnoreCase( "I" ) )
                {
                    flag = 1;
                    selIndicator = indicatorService.getIndicator( Integer.parseInt( partsOfService[1] ) );
                    if ( rowCount == 1 )
                    {
                        if ( aggPeriodCB == null )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                        }
                        else
                        {
                            sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                        }

                        sheet0.addCell( new Label( colCount, startRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementService.getDataElement( Integer.parseInt( partsOfService[1] ) );

                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer
                            .parseInt( partsOfService[2] ) );
                        if ( rowCount == 1 )
                        {
                            if ( aggPeriodCB == null )
                            {
                                sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1,
                                    startRow );
                            }
                            else
                            {
                                sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                            }

                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName() + "-"
                                + selDecoc.getName(), getCellFormat1() ) );
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            if ( aggPeriodCB == null )
                            {
                                sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1,
                                    startRow );
                            }
                            else
                            {
                                sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                            }

                            sheet0
                                .addCell( new Label( colCount, startRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                    }
                }

                int periodCount = 0;
                double numAggValue = 0.0;
                double denAggValue = 0.0;
                double dvAggValue = 0.0;
                String aggStrValue = "";

                for ( Date sDate : selStartPeriodList )
                {
                    Date eDate = selEndPeriodList.get( periodCount );
                    double pwnumAggValue = 0.0;
                    double pwdenAggValue = 0.0;
                    double pwdvAggValue = 0.0;
                    double pwdAggIndValue = 0.0;

                    String tempStr = "";

                    Double tempAggVal;
                    if ( flag == 1 )
                    {
                        tempAggVal = aggregationService.getAggregatedNumeratorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwnumAggValue = tempAggVal;
                        tempAggVal = aggregationService.getAggregatedDenominatorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwdenAggValue = tempAggVal;

                        tempAggVal = aggregationService.getAggregatedIndicatorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwdAggIndValue = tempAggVal;

                        pwdAggIndValue = Math.round( pwdAggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        tempStr = "" + pwdAggIndValue;

                    }
                    else if ( flag == 2 )
                    {
                        if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                tempAggVal = aggregationService.getAggregatedDataValue( selDataElement, selDecoc,
                                    sDate, eDate, ou );
                                if ( tempAggVal == null )
                                    tempAggVal = 0.0;
                                pwdvAggValue = tempAggVal;

                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                if ( tempPeriod != null )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( ou, selDataElement,
                                        tempPeriod, selDecoc );

                                    if ( dataValue != null )
                                    {
                                        tempStr = dataValue.getValue();
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        else
                        {
                            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                                selDataElement.getCategoryCombo().getOptionCombos() );

                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator
                                        .next();

                                    tempAggVal = aggregationService.getAggregatedDataValue( selDataElement, decoc1,
                                        sDate, eDate, ou );
                                    if ( tempAggVal == null )
                                        tempAggVal = 0.0;
                                    pwdvAggValue += tempAggVal;
                                }

                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator
                                        .next();

                                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                    Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                    if ( tempPeriod != null )
                                    {
                                        DataValue dataValue = dataValueService.getDataValue( ou, selDataElement,
                                            tempPeriod, decoc1 );

                                        if ( dataValue != null )
                                        {
                                            tempStr += dataValue.getValue() + " : ";
                                        }
                                        else
                                        {
                                            tempStr = "  ";
                                        }
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                            }
                        }
                    }

                    if ( aggPeriodCB == null )
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, startRow + 1, periodNames.get( periodCount ),
                                getCellFormat1() ) );
                        }

                        if ( flag == 1 )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, pwdAggIndValue,
                                getCellFormat2() ) );
                        }
                        else
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) pwdvAggValue,
                                    getCellFormat2() ) );
                            }
                            else
                            {
                                sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, tempStr,
                                    getCellFormat2() ) );
                            }
                        }

                        colCount++;
                    }
                    else
                    {
                        numAggValue += pwnumAggValue;
                        denAggValue += pwdenAggValue;
                        dvAggValue += pwdvAggValue;
                        aggStrValue += tempStr + " - ";
                    }

                    periodCount++;
                }// Period Loop

                if ( aggPeriodCB != null )
                {
                    if ( flag == 1 )
                    {
                        try
                        {
                            double tempDouble = Double.parseDouble( selIndicator.getDenominator().trim() );

                            // if(tempInt == 1) denAggValue = 1;
                            denAggValue = tempDouble;
                        }
                        catch ( Exception e )
                        {
                            System.out.println( "Denominator is not expression" );
                        }

                        double aggIndValue = (numAggValue / denAggValue) * selIndicator.getIndicatorType().getFactor();
                        aggIndValue = Math.round( aggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        sheet0
                            .addCell( new Number( colCount, headerRow + 1 + rowCount, aggIndValue, getCellFormat2() ) );
                    }
                    else if ( flag == 2 )
                    {
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) dvAggValue,
                                getCellFormat2() ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, aggStrValue,
                                getCellFormat2() ) );
                        }
                    }

                    colCount++;
                }
                // colCount++;
            }// Service loop end

            rowCount++;
        }// Orgunit loop end

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();

    }

    // -------------------------------------------------------------------------
    // Method for getting OrgUnit Level wise List in Excel Sheet
    // -------------------------------------------------------------------------
    public void generateOrgUnitLevel()
        throws Exception
    {
        int startRow = 0;
        int headerRow = 0;
        int headerCol = 0;

        String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.mergeCells( headerCol, headerRow, headerCol, headerRow + 1 );
        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
        selOUList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );

        Iterator<OrganisationUnit> ouIterator = selOUList.iterator();
        while ( ouIterator.hasNext() )
        {
            OrganisationUnit orgU = ouIterator.next();
            if ( organisationUnitService.getLevelOfOrganisationUnit( orgU ) > orgUnitLevelCB )
            {
                ouIterator.remove();
            }
        }

        int minOULevel = 1;
        int maxOuLevel = 1;
        if ( selOUList != null && selOUList.size() > 0 )
        {
            minOULevel = organisationUnitService.getLevelOfOrganisationUnit( selOUList.get( 0 ) );
        }
        maxOuLevel = orgUnitLevelCB;

        int c1 = headerCol + 1;
        for ( int i = minOULevel; i <= maxOuLevel; i++ )
        {
            sheet0.mergeCells( c1, headerRow, c1, headerRow + 1 );
            sheet0.addCell( new Label( c1, headerRow, "Level " + i, getCellFormat1() ) );
            c1++;
        }

        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for ( OrganisationUnit ou : selOUList )
        {
            sheet0.addCell( new Number( headerCol, headerRow + 1 + rowCount, rowCount, getCellFormat2() ) );
            colCount = 1 + organisationUnitService.getLevelOfOrganisationUnit( ou ) - minOULevel;
            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, ou.getName(), getCellFormat2() ) );

            colCount = c1;
            for ( String service : selectedServices )
            {
                String partsOfService[] = service.split( ":" );
                if ( partsOfService[0].equalsIgnoreCase( "I" ) )
                {
                    flag = 1;
                    selIndicator = indicatorService.getIndicator( Integer.parseInt( partsOfService[1] ) );
                    if ( rowCount == 1 )
                    {
                        if ( aggPeriodCB == null )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                        }
                        else
                        {
                            sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                        }
                        sheet0.addCell( new Label( colCount, startRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementService.getDataElement( Integer.parseInt( partsOfService[1] ) );

                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( partsOfService[2] ) );
                        if ( rowCount == 1 )
                        {
                            if ( aggPeriodCB == null )
                            {
                                sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            }
                            else
                            {
                                sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                            }
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            if ( aggPeriodCB == null )
                            {
                                sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            }
                            else
                            {
                                sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                            }
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                    }
                }

                int periodCount = 0;
                double numAggValue = 0.0;
                double denAggValue = 0.0;
                double dvAggValue = 0.0;
                String aggStrValue = "";

                for ( Date sDate : selStartPeriodList )
                {
                    Date eDate = selEndPeriodList.get( periodCount );
                    double pwnumAggValue = 0.0;
                    double pwdenAggValue = 0.0;
                    double pwdvAggValue = 0.0;
                    double pwdAggIndValue = 0.0;

                    String tempStr = "";

                    Double tempAggVal;
                    if ( flag == 1 )
                    {
                        tempAggVal = aggregationService.getAggregatedNumeratorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwnumAggValue = tempAggVal;
                        
                        tempAggVal = aggregationService.getAggregatedDenominatorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwdenAggValue = tempAggVal;

                        tempAggVal = aggregationService.getAggregatedIndicatorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwdAggIndValue = tempAggVal;

                        pwdAggIndValue = Math.round( pwdAggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        tempStr = "" + pwdAggIndValue;
                    }
                    else if ( flag == 2 )
                    {
                        if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                tempAggVal = aggregationService.getAggregatedDataValue( selDataElement, selDecoc, sDate, eDate, ou );
                                if ( tempAggVal == null )
                                    tempAggVal = 0.0;
                                pwdvAggValue = tempAggVal;

                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                if ( tempPeriod != null )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, selDecoc );

                                    if ( dataValue != null && dataValue.getValue() != null )
                                    {
                                        tempStr = dataValue.getValue();
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        else
                        {
                            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                                selDataElement.getCategoryCombo().getOptionCombos() );

                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator
                                        .next();

                                    tempAggVal = aggregationService.getAggregatedDataValue( selDataElement, decoc1,
                                        sDate, eDate, ou );
                                    if ( tempAggVal == null )
                                        tempAggVal = 0.0;
                                    pwdvAggValue += tempAggVal;
                                }

                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator
                                        .next();

                                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                    Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                    if ( tempPeriod != null )
                                    {
                                        DataValue dataValue = dataValueService.getDataValue( ou, selDataElement,
                                            tempPeriod, decoc1 );

                                        if ( dataValue != null )
                                        {
                                            tempStr += dataValue.getValue() + " : ";
                                        }
                                        else
                                        {
                                            tempStr = "  ";
                                        }
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                            }
                        }
                    }

                    if ( aggPeriodCB == null )
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, startRow + 1, periodNames.get( periodCount ),
                                getCellFormat1() ) );
                        }

                        if ( flag == 1 )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, pwdAggIndValue,
                                getCellFormat2() ) );
                        }
                        else
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) pwdvAggValue,
                                    getCellFormat2() ) );
                            }
                            else
                            {
                                sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, tempStr,
                                    getCellFormat2() ) );
                            }
                        }

                        colCount++;
                    }
                    else
                    {
                        numAggValue += pwnumAggValue;
                        denAggValue += pwdenAggValue;
                        dvAggValue += pwdvAggValue;
                        aggStrValue += tempStr + " - ";
                    }

                    periodCount++;
                }// Period Loop

                if ( aggPeriodCB != null )
                {
                    if ( flag == 1 )
                    {
                        try
                        {
                            double tempDouble = Double.parseDouble( selIndicator.getDenominator().trim() );

                            // if(tempInt == 1) denAggValue = 1;
                            denAggValue = tempDouble;
                        }
                        catch ( Exception e )
                        {
                            System.out.println( "Denominator is not expression" );
                        }

                        double aggIndValue = (numAggValue / denAggValue) * selIndicator.getIndicatorType().getFactor();
                        aggIndValue = Math.round( aggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        sheet0
                            .addCell( new Number( colCount, headerRow + 1 + rowCount, aggIndValue, getCellFormat2() ) );
                    }
                    else if ( flag == 2 )
                    {
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) dvAggValue,
                                getCellFormat2() ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, aggStrValue,
                                getCellFormat2() ) );
                        }
                    }

                    colCount++;
                }
                // colCount++;
            }// Service loop end

            rowCount++;
        }// Orgunit loop end
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
    }

    // Returns the OrgUnitTree for which Root is the orgUnit
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );

        ouChildCountMap.put( orgUnit, children.size() );

        Collections.sort( children, new OrganisationUnitNameComparator() );

        Iterator<OrganisationUnit> childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = (OrganisationUnit) childIterator.next();
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }// getChildOrgUnitTree end

    public WritableCellFormat getCellFormat1()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getCellFormat2()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

}// class end
