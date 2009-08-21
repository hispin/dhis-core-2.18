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
package org.hisp.dhis.vn.report.export.action;

import static org.hisp.dhis.expression.Expression.SEPARATOR;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.AscendingPeriodComparator;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.vn.report.ReportExcelCategory;
import org.hisp.dhis.vn.report.ReportExcelGroupListing;
import org.hisp.dhis.vn.report.ReportExcelInterface;
import org.hisp.dhis.vn.report.ReportExcelNormal;
import org.hisp.dhis.vn.report.ReportExcelPeriodListing;
import org.hisp.dhis.vn.report.ReportExcelService;
import org.hisp.dhis.vn.report.ReportItem;
import org.hisp.dhis.vn.report.state.ReportLocationManager;
import org.hisp.dhis.vn.report.utils.DateUtils;
import org.hisp.dhis.vn.report.utils.ExcelUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class GenerateReportAction
    implements Action
{
    private static final String NULL_REPLACEMENT = "0";

    private static final String NULL_INDEX = "";

    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    private CurrentUserService currentUserService;

    private AggregationService aggregationService;

    private IndicatorService indicatorService;

    private DataElementCategoryOptionComboService dataElementCategoryOptionComboService;

    private DataElementService dataElementService;

    private ReportLocationManager reportLocationManager;

    private ReportExcelService reportService;

    private PeriodService periodService;

    private I18nFormat format;

    // -------------------------------------------
    // Input
    // -------------------------------------------

    private Integer reportId;

    private Integer periodId;

    // -------------------------------------------
    // Output
    // -------------------------------------------

    private String outputXLS;

    private InputStream inputStream;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryOptionComboService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    public String getOutputXLS()
    {
        return outputXLS;
    }

    public InputStream getInputStream()
    {
        return inputStream;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public void setReportLocationManager( ReportLocationManager reportLocationManager )
    {
        this.reportLocationManager = reportLocationManager;
    }

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }

    public void setPeriodId( Integer periodId )
    {
        this.periodId = periodId;
    }

    // -----------------------------------------
    // Local variable
    // -----------------------------------------

    Date startDate, endDate, firstDayOfYear, last3MonthStartDate, last3MonthEndDate, last6MonthStartDate,
        last6MonthEndDate, endDateOfYear, startQuaterly, endQuaterly, startSixMonthly, endSixMonthly;

    // TODO apply StatementManager init

    public String execute()
        throws Exception
    {
        SimpleDateFormat dateformatter = new SimpleDateFormat( "dd.MM.yyyy.h.mm.ss.a" );

        OrganisationUnit organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();

        ReportExcelInterface reportExcel = reportService.getReport( reportId.intValue() );

        Period period = periodService.getPeriod( periodId.intValue() );

        this.createDate( period );

        // Create Format

        WritableCellFormat text = new WritableCellFormat();

        WritableCellFormat textLeft = new WritableCellFormat();
        textLeft.setAlignment( Alignment.LEFT );
        textLeft.setBorder( Border.ALL, BorderLineStyle.THIN );

        WritableCellFormat textRight = new WritableCellFormat();
        textRight.setAlignment( Alignment.RIGHT );
        textRight.setBorder( Border.ALL, BorderLineStyle.THIN );

        WritableFont writableNumberFont = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat textNumberBoldRight = new WritableCellFormat( writableNumberFont );
        textNumberBoldRight.setAlignment( Alignment.RIGHT );
        textNumberBoldRight.setBorder( Border.ALL, BorderLineStyle.THIN );

        WritableFont writableICDFont = new WritableFont( WritableFont.ARIAL, 11, WritableFont.BOLD );
        WritableCellFormat textICDBoldJustify = new WritableCellFormat( writableICDFont );
        textICDBoldJustify.setAlignment( Alignment.JUSTIFY );
        textICDBoldJustify.setBorder( Border.ALL, BorderLineStyle.THIN );

        WritableFont writableChapterFont = new WritableFont( WritableFont.ARIAL, 12, WritableFont.BOLD, false,
            UnderlineStyle.NO_UNDERLINE, Colour.BLUE );
        WritableCellFormat textChapterLeft = new WritableCellFormat( writableChapterFont );
        textChapterLeft.setAlignment( Alignment.LEFT );
        textChapterLeft.setBackground( Colour.VERY_LIGHT_YELLOW );
        textChapterLeft.setBorder( Border.ALL, BorderLineStyle.THIN );

        WritableCellFormat number = new WritableCellFormat();
        number.setBorder( Border.ALL, BorderLineStyle.THIN );
        number.setAlignment( Alignment.CENTRE );

        // Copy template file to temp dir

        File reportTempDir = reportLocationManager.getReportTempDirectory();

        File inputExcelTemplate = new File( reportLocationManager.getReportTemplateDirectory() + File.separator
            + reportExcel.getExcelTemplateFile() );

        Calendar calendar = Calendar.getInstance();

        File outputReportFile = new File( reportTempDir, currentUserService.getCurrentUsername()
            + dateformatter.format( calendar.getTime() ) + inputExcelTemplate.getName() );

        // Write value into excel file

        Workbook templateWorkbook = Workbook.getWorkbook( inputExcelTemplate );

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( outputReportFile, templateWorkbook );

        ExcelUtils.writeValue( reportExcel.getOrganisationRow(), reportExcel.getOrganisationColumn(), organisationUnit
            .getName(), ExcelUtils.TEXT, outputReportWorkbook.getSheet( 0 ), text );

        ExcelUtils.writeValue( reportExcel.getPeriodRow(), reportExcel.getPeriodColumn(),
            format.formatPeriod( period ), ExcelUtils.TEXT, outputReportWorkbook.getSheet( 0 ), text );

        for ( Integer sheetNo : reportService.getSheets( this.reportId ) )
        {
            WritableSheet sheet = outputReportWorkbook.getSheet( sheetNo - 1 );

            Collection<ReportItem> reportItems = reportService.getReportItem( sheetNo, reportId );

            // Generate report for Report Normal

            if ( reportExcel instanceof ReportExcelNormal )
            {

                for ( ReportItem reportItem : reportItems )
                {
                    if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.DATAELEMENT ) )
                    {
                        double value = getDataValue( reportItem, organisationUnit );

                        ExcelUtils.writeValue( reportItem.getRow(), reportItem.getColumn(), String.valueOf( value ),
                            ExcelUtils.NUMBER, sheet, number );
                    }

                    if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.INDICATOR ) )
                    {
                        double value = getIndicatorValue( reportItem, organisationUnit );

                        ExcelUtils.writeValue( reportItem.getRow(), reportItem.getColumn(), String.valueOf( value ),
                            ExcelUtils.NUMBER, sheet, number );
                    }
                }
            }

            // -------------------------------------------------------
            // Generate Report Period Listing
            // -------------------------------------------------------

            if ( reportExcel instanceof ReportExcelPeriodListing )
            {
                // Get list monthly period of selected year
                PeriodType periodType = periodService.getPeriodTypeByName( "Monthly" );
                Date firstDateOfThisYear = DateUtils.getFirstDayOfYear( calendar.get( Calendar.YEAR ) );
                List<Period> periods = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType(
                    periodType, firstDateOfThisYear, endDate ) );
                Collections.sort( periods, new AscendingPeriodComparator() );

                for ( ReportItem reportItem : reportItems )
                {
                    if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.DATAELEMENT ) )
                    {
                        int i = 0;
                        for ( Period p : periods )
                        {
                            double value = MathUtils.calculateExpression( generateExpression( reportItem, p
                                .getStartDate(), p.getEndDate(), organisationUnit ) );

                            ExcelUtils.writeValue( reportItem.getRow(), reportItem.getColumn() + i, String
                                .valueOf( value ), ExcelUtils.NUMBER, sheet, number );

                            i++;
                        }
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.INDICATOR ) )
                    {
                        int i = 0;
                        for ( Period p : periods )
                        {
                            double value = MathUtils.calculateExpression( generateIndicatorExpression( reportItem, p
                                .getStartDate(), p.getEndDate(), organisationUnit ) );

                            ExcelUtils.writeValue( reportItem.getRow(), reportItem.getColumn() + i, String
                                .valueOf( value ), ExcelUtils.NUMBER, sheet, number );

                            i++;
                        }
                    }
                }

            }

            // -------------------------------------------------------
            // General report for Report Group Listing
            // -------------------------------------------------------

            if ( reportExcel instanceof ReportExcelGroupListing )
            {

                Set<OrganisationUnit> childrenOrganisation = organisationUnit.getChildren();

                List<OrganisationUnitGroup> groups = ((ReportExcelGroupListing) reportExcel)
                    .getOrganisationUnitGroups();

                int i = -1;

                // get Group position
                int groupCol = 0;
                int groupRow = 0;
                int serialCol = 0;
                int serialRow = 0;

                Set<Integer> colums = new HashSet<Integer>();
                Set<Integer> rowOrgUnitGroups = new HashSet<Integer>();

                for ( ReportItem reportItem : reportItems )

                {
                    if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.ORGANISATION ) )
                    {
                        groupCol = reportItem.getColumn();
                        groupRow = reportItem.getRow();
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.SERIAL ) )
                    {
                        serialCol = reportItem.getColumn();
                        serialRow = reportItem.getRow();
                    }
                    else
                    {
                        colums.add( reportItem.getColumn() );
                    }
                }

                if ( groups.size() > 1 )
                {
                    groupRow += 1;
                    serialRow += 1;
                }

                for ( OrganisationUnitGroup group : groups )
                {
                    //Map<Integer, String> formulas = new HashMap<Integer, String>();

                    List<OrganisationUnit> memberOfOrganisationUnitGroups = new ArrayList<OrganisationUnit>( group
                        .getMembers() );

                    memberOfOrganisationUnitGroups.retainAll( childrenOrganisation );

                    Collections.sort( memberOfOrganisationUnitGroups, new OrganisationUnitNameComparator() );

                    if ( !memberOfOrganisationUnitGroups.isEmpty() )
                    {

                        i++;

                        int serial = 1;

                        int currentGroupRow = groupRow + i;

                        ExcelUtils.writeValue( currentGroupRow, groupCol, group.getName(), ExcelUtils.TEXT, sheet,
                            textLeft );

                        rowOrgUnitGroups.add( currentGroupRow );

                        for ( Integer column : colums )
                        {

                            String columnName = ExcelUtils.convertColNumberToColName( column );

                            String formula = "SUM(" + columnName + (currentGroupRow + 1) + ":" + columnName
                                + (currentGroupRow + memberOfOrganisationUnitGroups.size()) + ")";

                            ExcelUtils.writeFormula( currentGroupRow, column, formula, sheet, number );

                        }

                        for ( OrganisationUnit orgUnit : memberOfOrganisationUnitGroups )
                        {
                            i++;

                            ExcelUtils.writeValue( serialRow + i, serialCol, String.valueOf( serial ),
                                ExcelUtils.NUMBER, sheet, number );

                            ExcelUtils.writeValue( groupRow + i, groupCol, orgUnit.getName(), ExcelUtils.TEXT, sheet,
                                textLeft );

                            /*
                         * 
                         */

                            for ( ReportItem reportItem : reportItems )

                            {
                                if ( !reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.SERIAL )
                                    && !reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.ORGANISATION ) )
                                {

                                    if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.FORMULA_EXCEL ) )
                                    {

                                        String formula = reportItem.getExpression();

                                        formula = formula.replace( "*", (groupRow + i) + "" ).toUpperCase();

                                        ExcelUtils.writeFormula( groupRow + i, reportItem.getColumn(), String
                                            .valueOf( formula ), sheet, number );
                                    }
                                    else if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.DATAELEMENT ) )
                                    {
                                        ExcelUtils.writeValue( groupRow + i, reportItem.getColumn(), String
                                            .valueOf( getDataValue( reportItem, orgUnit ) ), ExcelUtils.NUMBER, sheet,
                                            number );
                                    }
                                    else if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.INDICATOR ) )
                                    {
                                        ExcelUtils.writeValue( groupRow + i, reportItem.getColumn(), String
                                            .valueOf( getIndicatorValue( reportItem, orgUnit ) ), ExcelUtils.NUMBER,
                                            sheet, number );
                                    }
                                }

                            }

                            serial++;
                        }

                    }

                }// end for

                if ( groups.size() == 1 )
                {
                    ExcelUtils.writeValue( groupRow, groupCol, "T\u1ed5ng s\u1ed1", ExcelUtils.TEXT, sheet, textLeft );
                }
                else
                {
                    ExcelUtils.writeValue( groupRow - 1, groupCol, "T\u1ed5ng s\u1ed1", ExcelUtils.TEXT, sheet,
                        textLeft );
                    for ( Integer column : colums )
                    {

                        String columnName = ExcelUtils.convertColNumberToColName( column );

                        String formula = " 0 ";

                        for ( Integer row : rowOrgUnitGroups )
                        {

                            formula += "+" + columnName + "" + row.intValue();

                        }

                        ExcelUtils.writeFormula( groupRow - 1, column, formula, sheet, number );

                    }
                }
            }

            // HIEU DONE //
            // -----------------------------------------------------------------
            // GENERATING THE REPORT FOR CATEGORY REPORT //
            // -----------------------------------------------------------------
            if ( reportExcel instanceof ReportExcelCategory )
            {

                // Get list of item report from ReportExcelCategory

                // Get list of available DataElement from ReportExcelCategory
                List<DataElement> dataElements = ((ReportExcelCategory) reportExcel).getDataElements();

                if ( !(dataElements.isEmpty() || reportItems.isEmpty()) )
                {

                    // ReportItem.TYPE.SERIAL
                    int serialRow = 0;
                    int serialCol = 0;
                    // ReportItem.TYPE.DATAELEMENT
                    int elementRow = 0;
                    int elementCol = 0;
                    int index = -1;
                    int serial = 0;
                    int orders = dataElements.size();
                    // ReportItem.TYPE.ICD_CODE
                    int icdCodeRow = 0;
                    int icdCodeCol = 0;

                    boolean flagIcdCode = false;

                    // ReportItem.TYPE.ELEMENT_OPTIONCOMBO
                    List<Integer> aItemRow = new ArrayList<Integer>();
                    List<Integer> aItemCol = new ArrayList<Integer>();
                    List<ReportItem> aItemExpression = new ArrayList<ReportItem>();
                    // ReportItem.TYPE.JUMPING_STEP
                    List<Integer> aItemJumpingIndex = new ArrayList<Integer>();
                    // ReportItem.TYPE.RENAME
                    List<Integer> aItemRenameRow = new ArrayList<Integer>();
                    Map<Integer, String> aItemReplacedName = new HashMap<Integer, String>();

                    String sExpression = "";
                    String sDeName = "";

                    for ( ReportItem reportItem : reportItems )
                    {

                        if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.SERIAL ) )
                        {

                            serialRow = reportItem.getRow();
                            serialCol = reportItem.getColumn();
                        }
                        if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.DATAELEMENT ) )
                        {

                            elementRow = reportItem.getRow();
                            elementCol = reportItem.getColumn();
                        }
                        if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.ELEMENT_OPTIONCOMBO ) )
                        {

                            aItemRow.add( reportItem.getRow() );
                            aItemCol.add( reportItem.getColumn() );
                            aItemExpression.add( reportItem );
                        }
                        if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.ICD_CODE ) )
                        {

                            flagIcdCode = true;
                            icdCodeRow = reportItem.getRow();
                            icdCodeCol = reportItem.getColumn();
                        }
                        if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.JUMPING_STEP ) )
                        {

                            aItemJumpingIndex.add( reportItem.getRow() );
                        }
                        if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.RENAME ) )
                        {

                            aItemRenameRow.add( Integer.valueOf( reportItem.getRow() ) );
                            aItemReplacedName.put( Integer.valueOf( reportItem.getRow() ), reportItem.getExpression() );

                        }
                    }

                    if ( !aItemJumpingIndex.isEmpty() && !aItemReplacedName.isEmpty() )
                    {

                        Collections.sort( aItemJumpingIndex );
                    }
                    /**
	             * */
                    if ( !aItemRenameRow.isEmpty() )
                    {

                        Collections.sort( aItemRenameRow );
                    }
                    /**
	             * */
                    if ( serialCol > elementCol )
                    {

                        serialCol = 0;
                        elementCol = serialCol + 1;
                    }
                    if ( (serialRow - elementRow) > 0 )
                    {

                        elementRow = serialRow;
                    }
                    else
                    {
                        serialRow = elementRow;
                    }

                    for ( DataElement de : dataElements )
                    {

                        ++index;

                        sDeName = de.getName();

                        if ( aItemReplacedName.get( Integer.valueOf( elementRow + serial ) ) != null )
                        {
                            sDeName = aItemReplacedName.get( Integer.valueOf( elementRow + serial ) );
                        }

                        if ( flagIcdCode )
                        {

                            ExcelUtils.writeValue( icdCodeRow + serial, icdCodeCol, de.getCode(), ExcelUtils.TEXT,
                                sheet, textICDBoldJustify );
                        }

                        if ( aItemJumpingIndex.contains( Integer.valueOf( index ) ) )
                        {

                            ExcelUtils.writeValue( serialRow + serial, serialCol, NULL_INDEX, ExcelUtils.TEXT, sheet,
                                text );
                            ExcelUtils.writeValue( elementRow + serial, elementCol, sDeName, ExcelUtils.TEXT, sheet,
                                textChapterLeft );
                            aItemJumpingIndex.remove( Integer.valueOf( index ) );

                            if ( index > 0 )
                            {
                                --index;
                            }
                        }
                        else
                        {
                            ExcelUtils.writeValue( serialRow + serial, serialCol,
                                generateStringNumber( index, orders ), ExcelUtils.TEXT, sheet, textNumberBoldRight );
                            ExcelUtils.writeValue( elementRow + serial, elementCol, sDeName, ExcelUtils.TEXT, sheet,
                                textLeft );

                        }

                        for ( int i = 0; i < aItemExpression.size(); i++ )
                        {
                            sExpression = this.replacedStarSimpleByElementIDFromFormula( aItemExpression.get( i )
                                .getExpression(), de );

                            ReportItem reportItem = new ReportItem();

                            reportItem.setColumn( aItemExpression.get( i ).getColumn() );
                            reportItem.setRow( aItemExpression.get( i ).getRow() );
                            reportItem.setItemType( aItemExpression.get( i ).getItemType() );
                            reportItem.setName( aItemExpression.get( i ).getName() );
                            reportItem.setPeriodType( aItemExpression.get( i ).getPeriodType() );
                            reportItem.setExpression( sExpression );
                            reportItem.setId( aItemExpression.get( i ).getId() );

                            double value = getDataValue( reportItem, organisationUnit );

                            ExcelUtils.writeValue( aItemRow.get( i ) + serial, aItemCol.get( i ), String
                                .valueOf( value ), ExcelUtils.NUMBER, sheet, number );
                        }

                        serial++;

                    } // end of for
                } // end of if
            }
        }// end of if
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        // //

        outputReportWorkbook.write();

        outputReportWorkbook.close();

        outputXLS = outputReportFile.getName();

        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.delete();

        return SUCCESS;
    }

    // ======================================================
    // Indicator Value
    // ======================================================

    private double getIndicatorValue( ReportItem reportItem, OrganisationUnit organisationUnit )
    {
        double value = 0.0;

        if ( reportItem.getPeriodType().equalsIgnoreCase( ReportItem.PERIODTYPE.SELECTED_MONTH ) )
        {
            value = MathUtils.calculateExpression( generateIndicatorExpression( reportItem, startDate, endDate,
                organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportItem.PERIODTYPE.LAST_3_MONTH ) )
        {
            value = MathUtils.calculateExpression( generateIndicatorExpression( reportItem, last3MonthStartDate,
                last3MonthEndDate, organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportItem.PERIODTYPE.SO_FAR_THIS_YEAR ) )
        {
            value = MathUtils.calculateExpression( generateIndicatorExpression( reportItem, firstDayOfYear, endDate,
                organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportItem.PERIODTYPE.LAST_6_MONTH ) )
        {
            value = MathUtils.calculateExpression( generateIndicatorExpression( reportItem, last6MonthStartDate,
                last6MonthEndDate, organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportItem.PERIODTYPE.YEARLY ) )
        {
            value = MathUtils.calculateExpression( generateIndicatorExpression( reportItem, firstDayOfYear,
                endDateOfYear, organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportItem.PERIODTYPE.QUATERLY ) )
        {
            value = MathUtils.calculateExpression( generateIndicatorExpression( reportItem, startQuaterly, endQuaterly,
                organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportItem.PERIODTYPE.SIX_MONTH ) )
        {
            value = MathUtils.calculateExpression( generateIndicatorExpression( reportItem, startSixMonthly,
                endSixMonthly, organisationUnit ) );
        }
        return value;

    }

    private String generateIndicatorExpression( ReportItem reportItem, Date startDate, Date endDate,
        OrganisationUnit organisationUnit )
    {
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\])" );

            Matcher matcher = pattern.matcher( reportItem.getExpression() );
            StringBuffer buffer = new StringBuffer();

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                System.out.println( replaceString );

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );

                String indicatorIdString = replaceString.trim();

                int indicatorId = Integer.parseInt( indicatorIdString );

                Indicator indicator = indicatorService.getIndicator( indicatorId );

                double aggregatedValue = aggregationService.getAggregatedIndicatorValue( indicator, startDate, endDate,
                    organisationUnit );

                if ( aggregatedValue == AggregationService.NO_VALUES_REGISTERED )
                {
                    replaceString = NULL_REPLACEMENT;
                }
                else
                {
                    replaceString = String.valueOf( aggregatedValue );
                }

                matcher.appendReplacement( buffer, replaceString );
            }

            matcher.appendTail( buffer );

            return buffer.toString();
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    // ======================================================
    // DataElement Value
    // ======================================================

    public double getDataValue( ReportItem reportItem, OrganisationUnit organisationUnit )
    {
        double value = 0.0;

        boolean flag = false;

        if ( reportItem.getExpression().contains( "~" ) )
        {
            reportItem.setExpression( reportItem.getExpression().replace( "~", "" ) );
            flag = true;
        }

        if ( reportItem.getPeriodType().equalsIgnoreCase( ReportItem.PERIODTYPE.SELECTED_MONTH ) )
        {
            value = MathUtils
                .calculateExpression( generateExpression( reportItem, startDate, endDate, organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportItem.PERIODTYPE.LAST_3_MONTH ) )
        {
            value = MathUtils.calculateExpression( generateExpression( reportItem, last3MonthStartDate,
                last3MonthEndDate, organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportItem.PERIODTYPE.SO_FAR_THIS_YEAR ) )
        {
            value = MathUtils.calculateExpression( generateExpression( reportItem, firstDayOfYear, endDate,
                organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportItem.PERIODTYPE.LAST_6_MONTH ) )
        {
            value = MathUtils.calculateExpression( generateExpression( reportItem, last6MonthStartDate,
                last6MonthEndDate, organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportItem.PERIODTYPE.YEARLY ) )
        {
            value = MathUtils.calculateExpression( generateExpression( reportItem, firstDayOfYear, endDateOfYear,
                organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportItem.PERIODTYPE.QUATERLY ) )
        {
            value = MathUtils.calculateExpression( generateExpression( reportItem, startQuaterly, endQuaterly,
                organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportItem.PERIODTYPE.SIX_MONTH ) )
        {
            value = MathUtils.calculateExpression( generateExpression( reportItem, startSixMonthly, endSixMonthly,
                organisationUnit ) );
        }

        // Chau Thu Tran Update for true/false status with some value
        // if (value > 0) is exist element
        // else not exist element

        if ( flag )
        {

            value = (value > 0) ? 0 : 1;

        }// Chau Thu Tran Update

        return value;
    }

    /**
     * Converts an expression on the form<br>
     * [34] + [23], where the numbers are IDs of DataElements, to the form<br>
     * 200 + 450, where the numbers are the values of the DataValues registered
     * for the Period and source.
     * 
     * @return The generated expression
     */
    private String generateExpression( ReportItem reportItem, Date startDate, Date endDate,
        OrganisationUnit organisationUnit )
    {
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( reportItem.getExpression() );
            StringBuffer buffer = new StringBuffer();

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );

                String dataElementIdString = replaceString.substring( 0, replaceString.indexOf( SEPARATOR ) );
                String optionComboIdString = replaceString.substring( replaceString.indexOf( SEPARATOR ) + 1,
                    replaceString.length() );

                int dataElementId = Integer.parseInt( dataElementIdString );
                int optionComboId = Integer.parseInt( optionComboIdString );

                DataElement dataElement = dataElementService.getDataElement( dataElementId );

                DataElementCategoryOptionCombo optionCombo = dataElementCategoryOptionComboService
                    .getDataElementCategoryOptionCombo( optionComboId );

                double aggregatedValue = aggregationService.getAggregatedDataValue( dataElement, optionCombo,
                    startDate, endDate, organisationUnit );

                if ( aggregatedValue == AggregationService.NO_VALUES_REGISTERED )
                {
                    replaceString = NULL_REPLACEMENT;
                }
                else
                {
                    replaceString = String.valueOf( aggregatedValue );
                }

                matcher.appendReplacement( buffer, replaceString );
            }

            matcher.appendTail( buffer );

            return buffer.toString();
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }

    }

    // ------------------------------------------------------------------------------------------
    // //
    // HIEU DONE //
    private String generateStringNumber( int index, int orders )
    {

        String sResult = new String( String.valueOf( index ) );

        if ( index < orders )
        {

            int i = String.valueOf( index ).length();
            int j = String.valueOf( index ).length();
            int len = i - j;

            if ( i < j )
            {
                for ( i = 0; i < len; i++ )
                {
                    sResult = "0" + sResult;
                }
            }
        }
        return sResult;
    }

    private String replacedStarSimpleByElementIDFromFormula( String sExpression, DataElement de )
    {

        sExpression = sExpression.replaceAll( "\\[+\\*", "[" + de.getId() );

        return sExpression;
    }

    public void createDate( Period period )
    {
        Calendar calendar = Calendar.getInstance();

        // Monthly period
        startDate = period.getStartDate();

        endDate = period.getEndDate();

        // Last 3 month period
        // Last 2 months + this month = last 3 month

        last3MonthStartDate = DateUtils.getTimeRoll( startDate, Calendar.MONTH, -2 );

        last3MonthStartDate = DateUtils.getTimeRoll( last3MonthStartDate, Calendar.DATE, -1 );

        last3MonthEndDate = period.getEndDate();

        // So far this year period

        calendar.setTime( endDate );

        firstDayOfYear = DateUtils.getFirstDayOfYear( calendar.get( Calendar.YEAR ) );
        firstDayOfYear = DateUtils.getTimeRoll( firstDayOfYear, Calendar.DATE, -1 );
        endDateOfYear = DateUtils.getLastDayOfYear( calendar.get( Calendar.YEAR ) );

        // Last 6 month period
        // Last 5 months + this month = last 6 month

        last6MonthStartDate = DateUtils.getTimeRoll( startDate, Calendar.MONTH, -5 );

        last6MonthStartDate = DateUtils.getTimeRoll( last6MonthStartDate, Calendar.DATE, -1 );

        last6MonthEndDate = period.getEndDate();

        // Quaterly

        startQuaterly = DateUtils.getStartQuaterly( startDate );

        startQuaterly = DateUtils.getTimeRoll( startQuaterly, Calendar.DATE, -1 );

        endQuaterly = DateUtils.getEndQuaterly( startDate );

        // Six monthly

        startSixMonthly = DateUtils.getStartSixMonthly( startDate );

        startSixMonthly = DateUtils.getTimeRoll( startSixMonthly, Calendar.DATE, -1 );

        endSixMonthly = DateUtils.getEndSixMonthly( startDate );
    }

}
