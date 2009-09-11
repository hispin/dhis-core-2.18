package org.hisp.dhis.vn.report.preview.action;

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

import static org.hisp.dhis.expression.Expression.SEPARATOR;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amplecode.quick.StatementManager;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
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
 * @author Dang Duy Hieu
 * @author Chau Thu Tran
 * @version $Id: GenerateReportAction.java 2009-09-11 16:50:00Z hieuduy$
 */
public class GeneratePreviewReportAction
    implements Action
{
    private static final String NULL_REPLACEMENT = "0";

    private static final String NULL_INDEX = "";

    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
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

    private DataElementCategoryOptionComboService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryOptionComboService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private ReportLocationManager reportLocationManager;

    public void setReportLocationManager( ReportLocationManager reportLocationManager )
    {
        this.reportLocationManager = reportLocationManager;
    }

    private ReportExcelService reportService;

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

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

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        statementManager.initialise();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateformatter = new SimpleDateFormat( "dd.MM.yyyy.h.mm.ss.a" );

        OrganisationUnit organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();

        ReportExcelInterface reportExcel = reportService.getReport( reportId.intValue() );

        Period period = periodService.getPeriod( periodId.intValue() );

        // Copy template file to temp dir

        File reportTempDir = reportLocationManager.getReportTempDirectory();

        FileInputStream inputStreamExcelTemplate = new FileInputStream( reportLocationManager
            .getReportTemplateDirectory()
            + File.separator + reportExcel.getExcelTemplateFile() );

        File outputReportFile = new File( reportTempDir, currentUserService.getCurrentUsername()
            + dateformatter.format( calendar.getTime() ) + new File( reportExcel.getExcelTemplateFile() ).getName() );

        FileOutputStream out = new FileOutputStream( outputReportFile );

        HSSFWorkbook templateWorkbook = new HSSFWorkbook( inputStreamExcelTemplate );

        HSSFSheet sheet = templateWorkbook.getSheetAt( 0 );

        templateWorkbook.setSheetName( 0, reportExcel.getName().replaceAll( " ", "" ) );

        /* style */
        HSSFFont csf = templateWorkbook.createFont();
        HSSFFont csfBold = templateWorkbook.createFont();
        HSSFDataFormat df = templateWorkbook.createDataFormat();
        HSSFCellStyle cs = templateWorkbook.createCellStyle();
        HSSFCellStyle csHeader = templateWorkbook.createCellStyle();
        HSSFCellStyle csSize = templateWorkbook.createCellStyle();
        HSSFCellStyle csCurrency = templateWorkbook.createCellStyle();
        HSSFCellStyle csSizeBold = templateWorkbook.createCellStyle();
        HSSFCellStyle csCurrencyBold = templateWorkbook.createCellStyle();
        HSSFCellStyle csPercentage = templateWorkbook.createCellStyle();
        HSSFCellStyle csNormal = templateWorkbook.createCellStyle();
        HSSFCellStyle csNormalOdd = templateWorkbook.createCellStyle();
        HSSFCellStyle csCurrencyOdd = templateWorkbook.createCellStyle();
        HSSFCellStyle csPercentageOdd = templateWorkbook.createCellStyle();
        HSSFCellStyle csSizeOdd = templateWorkbook.createCellStyle();
        HSSFCellStyle csCountOdd = templateWorkbook.createCellStyle();
        HSSFCellStyle csCount = templateWorkbook.createCellStyle();

        HSSFHeader header = sheet.getHeader();
        header.setCenter( "Center Header" );
        header.setLeft( "Left Header" );
        header.setRight( HSSFHeader.font( "Stencil-Normal", "Italic" ) + HSSFHeader.fontSize( (short) 16 )
            + "Right w/ Stencil-Normal Italic font and size 12" );

        // bold font
        csfBold.setFontName( "Tahoma" );
        csfBold.setFontHeightInPoints( (short) 8 );
        csfBold.setBoldweight( HSSFFont.BOLDWEIGHT_BOLD );
        csfBold.setColor( new HSSFColor.WHITE().getIndex() );

        // header format
        csHeader.setFont( csfBold );
        csHeader.setFillBackgroundColor( HSSFColor.LIGHT_ORANGE.index );
        csHeader.setFillForegroundColor( HSSFColor.LIGHT_ORANGE.index );
        csHeader.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );
        csHeader.setBorderBottom( HSSFCellStyle.BORDER_THIN );
        csHeader.setBottomBorderColor( HSSFColor.LIGHT_BLUE.index );
        csHeader.setBorderTop( HSSFCellStyle.BORDER_THIN );
        csHeader.setTopBorderColor( HSSFColor.LIGHT_BLUE.index );
        csHeader.setBorderLeft( HSSFCellStyle.BORDER_THIN );
        csHeader.setLeftBorderColor( HSSFColor.LIGHT_BLUE.index );
        csHeader.setBorderRight( HSSFCellStyle.BORDER_THIN );
        csHeader.setRightBorderColor( HSSFColor.LIGHT_BLUE.index );

        // size format
        csSizeBold.setFont( csfBold );
        csSizeBold.setBorderBottom( HSSFCellStyle.BORDER_THIN );
        csSizeBold.setBottomBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csSizeBold.setBorderTop( HSSFCellStyle.BORDER_THIN );
        csSizeBold.setTopBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csSizeBold.setBorderLeft( HSSFCellStyle.BORDER_THIN );
        csSizeBold.setLeftBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csSizeBold.setBorderRight( HSSFCellStyle.BORDER_THIN );
        csSizeBold.setRightBorderColor( HSSFColor.LIGHT_ORANGE.index );

        cs.setDataFormat( df.getFormat( "@" ) );

        csCurrencyBold.setFont( csfBold );
        csCurrencyBold.setBorderBottom( HSSFCellStyle.BORDER_THIN );
        csCurrencyBold.setBottomBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csCurrencyBold.setBorderTop( HSSFCellStyle.BORDER_THIN );
        csCurrencyBold.setTopBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csCurrencyBold.setBorderLeft( HSSFCellStyle.BORDER_THIN );
        csCurrencyBold.setLeftBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csCurrencyBold.setBorderRight( HSSFCellStyle.BORDER_THIN );
        csCurrencyBold.setRightBorderColor( HSSFColor.LIGHT_ORANGE.index );

        csf.setFontName( "Tahoma" );
        csf.setFontHeightInPoints( (short) 8 );
        csf.setBoldweight( HSSFFont.BOLDWEIGHT_NORMAL );

        csNormal.setFont( csf );
        csNormal.setFillBackgroundColor( HSSFColor.WHITE.index );
        csNormal.setFillForegroundColor( HSSFColor.WHITE.index );
        csNormal.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );
        csNormal.setBorderBottom( HSSFCellStyle.BORDER_THIN );
        csNormal.setBottomBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csNormal.setBorderTop( HSSFCellStyle.BORDER_THIN );
        csNormal.setTopBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csNormal.setBorderLeft( HSSFCellStyle.BORDER_THIN );
        csNormal.setLeftBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csNormal.setBorderRight( HSSFCellStyle.BORDER_THIN );
        csNormal.setRightBorderColor( HSSFColor.LIGHT_ORANGE.index );

        csNormalOdd.setFont( csf );
        csNormalOdd.setFillBackgroundColor( HSSFColor.LIGHT_GREEN.index );
        csNormalOdd.setFillForegroundColor( HSSFColor.LIGHT_GREEN.index );
        csNormalOdd.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );
        csNormalOdd.setBorderBottom( HSSFCellStyle.BORDER_THIN );
        csNormalOdd.setBottomBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csNormalOdd.setBorderTop( HSSFCellStyle.BORDER_THIN );
        csNormalOdd.setTopBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csNormalOdd.setBorderLeft( HSSFCellStyle.BORDER_THIN );
        csNormalOdd.setLeftBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csNormalOdd.setBorderRight( HSSFCellStyle.BORDER_THIN );
        csNormalOdd.setRightBorderColor( HSSFColor.LIGHT_ORANGE.index );

        csPercentage.setFont( csf );
        csPercentage.setFillBackgroundColor( HSSFColor.WHITE.index );
        csPercentage.setFillForegroundColor( HSSFColor.WHITE.index );
        csPercentage.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );
        csPercentage.setBorderBottom( HSSFCellStyle.BORDER_THIN );
        csPercentage.setBottomBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csPercentage.setBorderTop( HSSFCellStyle.BORDER_THIN );
        csPercentage.setTopBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csPercentage.setBorderLeft( HSSFCellStyle.BORDER_THIN );
        csPercentage.setLeftBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csPercentage.setBorderRight( HSSFCellStyle.BORDER_THIN );
        csPercentage.setRightBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csPercentage.setDataFormat( df.getFormat( "_(##.00_ %" ) );

        csPercentageOdd.setFont( csf );
        csPercentageOdd.setFillBackgroundColor( HSSFColor.LIGHT_GREEN.index );
        csPercentageOdd.setFillForegroundColor( HSSFColor.LIGHT_GREEN.index );
        csPercentageOdd.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );
        csPercentageOdd.setBorderBottom( HSSFCellStyle.BORDER_THIN );
        csPercentageOdd.setBottomBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csPercentageOdd.setBorderTop( HSSFCellStyle.BORDER_THIN );
        csPercentageOdd.setTopBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csPercentageOdd.setBorderLeft( HSSFCellStyle.BORDER_THIN );
        csPercentageOdd.setLeftBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csPercentageOdd.setBorderRight( HSSFCellStyle.BORDER_THIN );
        csPercentageOdd.setRightBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csPercentageOdd.setDataFormat( df.getFormat( "_(##.00_ %" ) );

        csCurrency.setFont( csf );
        csCurrency.setFillBackgroundColor( HSSFColor.WHITE.index );
        csCurrency.setFillForegroundColor( HSSFColor.WHITE.index );
        csCurrency.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );
        csCurrency.setBorderBottom( HSSFCellStyle.BORDER_THIN );
        csCurrency.setBottomBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csCurrency.setBorderTop( HSSFCellStyle.BORDER_THIN );
        csCurrency.setTopBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csCurrency.setBorderLeft( HSSFCellStyle.BORDER_THIN );
        csCurrency.setLeftBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csCurrency.setBorderRight( HSSFCellStyle.BORDER_THIN );
        csCurrency.setRightBorderColor( HSSFColor.LIGHT_ORANGE.index );

        csCurrencyOdd.setFont( csf );
        csCurrencyOdd.setFillBackgroundColor( HSSFColor.LIGHT_GREEN.index );
        csCurrencyOdd.setFillForegroundColor( HSSFColor.LIGHT_GREEN.index );
        csCurrencyOdd.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );
        csCurrencyOdd.setBorderBottom( HSSFCellStyle.BORDER_THIN );
        csCurrencyOdd.setBottomBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csCurrencyOdd.setBorderTop( HSSFCellStyle.BORDER_THIN );
        csCurrencyOdd.setTopBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csCurrencyOdd.setBorderLeft( HSSFCellStyle.BORDER_THIN );
        csCurrencyOdd.setLeftBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csCurrencyOdd.setBorderRight( HSSFCellStyle.BORDER_THIN );
        csCurrencyOdd.setRightBorderColor( HSSFColor.LIGHT_ORANGE.index );

        csSize.setFont( csf );
        csSize.setFillBackgroundColor( HSSFColor.WHITE.index );
        csSize.setFillForegroundColor( HSSFColor.WHITE.index );
        csSize.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );
        csSize.setBorderBottom( HSSFCellStyle.BORDER_THIN );
        csSize.setBottomBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csSize.setBorderTop( HSSFCellStyle.BORDER_THIN );
        csSize.setTopBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csSize.setBorderLeft( HSSFCellStyle.BORDER_THIN );
        csSize.setLeftBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csSize.setBorderRight( HSSFCellStyle.BORDER_THIN );
        csSize.setRightBorderColor( HSSFColor.LIGHT_ORANGE.index );

        csSizeOdd.setFont( csf );
        csSizeOdd.setFillBackgroundColor( HSSFColor.LIGHT_GREEN.index );
        csSizeOdd.setFillForegroundColor( HSSFColor.LIGHT_GREEN.index );
        csSizeOdd.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );
        csSizeOdd.setBorderBottom( HSSFCellStyle.BORDER_THIN );
        csSizeOdd.setBottomBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csSizeOdd.setBorderTop( HSSFCellStyle.BORDER_THIN );
        csSizeOdd.setTopBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csSizeOdd.setBorderLeft( HSSFCellStyle.BORDER_THIN );
        csSizeOdd.setLeftBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csSizeOdd.setBorderRight( HSSFCellStyle.BORDER_THIN );
        csSizeOdd.setRightBorderColor( HSSFColor.LIGHT_ORANGE.index );

        csCount.setFont( csf );
        csCount.setFillBackgroundColor( HSSFColor.WHITE.index );
        csCount.setFillForegroundColor( HSSFColor.WHITE.index );
        csCount.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );
        csCount.setBorderBottom( HSSFCellStyle.BORDER_THIN );
        csCount.setBottomBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csCount.setBorderTop( HSSFCellStyle.BORDER_THIN );
        csCount.setTopBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csCount.setBorderLeft( HSSFCellStyle.BORDER_THIN );
        csCount.setLeftBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csCount.setBorderRight( HSSFCellStyle.BORDER_THIN );
        csCount.setRightBorderColor( HSSFColor.LIGHT_ORANGE.index );
        csCount.setDataFormat( df.getFormat( "#,###\" \"" ) );

        csCountOdd.setFont( csf );
        csCountOdd.setBorderBottom( HSSFCellStyle.BORDER_THIN );
        csCountOdd.setBorderTop( HSSFCellStyle.BORDER_THIN );
        csCountOdd.setBorderLeft( HSSFCellStyle.BORDER_THIN );
        csCountOdd.setBorderRight( HSSFCellStyle.BORDER_THIN );

        ExcelUtils.writeValueByPOI( reportExcel.getOrganisationRow(), reportExcel.getOrganisationColumn(),
            organisationUnit.getName(), ExcelUtils.TEXT, sheet, csNormal );

        ExcelUtils.writeValueByPOI( reportExcel.getPeriodRow(), reportExcel.getPeriodColumn(), format
            .formatPeriod( period ), ExcelUtils.TEXT, sheet, csNormal );

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

        endQuaterly = DateUtils.getEndQuaterly( startDate );

        // Six monthly

        startSixMonthly = DateUtils.getStartSixMonthly( startDate );

        endSixMonthly = DateUtils.getEndSixMonthly( startDate );

        // Generate report for Report Normal

        if ( reportExcel instanceof ReportExcelNormal )
        {
            for ( ReportItem reportItem : reportExcel.getReportItems() )
            {
                if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.DATAELEMENT ) )
                {
                    double value = getDataValue( reportItem, organisationUnit );

                    ExcelUtils.writeValueByPOI( reportItem.getRow(), reportItem.getColumn(), String.valueOf( value ),
                        ExcelUtils.NUMBER, sheet, csCountOdd );
                }

                if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.INDICATOR ) )
                {
                    double value = getIndicatorValue( reportItem, organisationUnit );

                    ExcelUtils.writeValueByPOI( reportItem.getRow(), reportItem.getColumn(), String.valueOf( value ),
                        ExcelUtils.NUMBER, sheet, csCountOdd );
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
            List<Period> periods = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType( periodType,
                firstDateOfThisYear, endDate ) );
            Collections.sort( periods, new AscendingPeriodComparator() );

            for ( ReportItem reportItem : reportExcel.getReportItems() )
            {
                if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.DATAELEMENT ) )
                {
                    int i = 0;
                    for ( Period p : periods )
                    {
                        double value = MathUtils.calculateExpression( generateExpression( reportItem, p.getStartDate(),
                            p.getEndDate(), organisationUnit ) );

                        ExcelUtils.writeValueByPOI( reportItem.getRow(), reportItem.getColumn() + i, String
                            .valueOf( value ), ExcelUtils.NUMBER, sheet, csCountOdd );

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

                        ExcelUtils.writeValueByPOI( reportItem.getRow(), reportItem.getColumn() + i, String
                            .valueOf( value ), ExcelUtils.NUMBER, sheet, csCountOdd );

                        i++;
                    }
                }
            }

        }

        if ( reportExcel instanceof ReportExcelGroupListing )
        {

            Set<OrganisationUnit> childrenOrganisation = organisationUnit.getChildren();

            List<OrganisationUnitGroup> groups = ((ReportExcelGroupListing) reportExcel).getOrganisationUnitGroups();

            int i = -1;

            // get Group position
            int groupCol = 0;
            int groupRow = 0;
            int serialCol = 0;
            int serialRow = 0;

            Set<Integer> colums = new HashSet<Integer>();
            Set<Integer> rowOrgUnitGroups = new HashSet<Integer>();

            for ( ReportItem reportItem : ((ReportExcelGroupListing) reportExcel).getReportItems() )

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
                List<OrganisationUnit> memberOfOrganisationUnitGroups = new ArrayList<OrganisationUnit>( group
                    .getMembers() );

                memberOfOrganisationUnitGroups.retainAll( childrenOrganisation );

                Collections.sort( memberOfOrganisationUnitGroups, new OrganisationUnitNameComparator() );

                if ( !memberOfOrganisationUnitGroups.isEmpty() )
                {
                    i++;

                    int serial = 1;
                    int currentGroupRow = groupRow + i;

                    ExcelUtils.writeValueByPOI( currentGroupRow, groupCol, group.getName(), ExcelUtils.TEXT, sheet,
                        csNormal );

                    rowOrgUnitGroups.add( currentGroupRow );

                    for ( OrganisationUnit orgUnit : memberOfOrganisationUnitGroups )
                    {
                        i++;

                        ExcelUtils.writeValueByPOI( serialRow + i, serialCol, String.valueOf( serial ),
                            ExcelUtils.NUMBER, sheet, csCountOdd );

                        ExcelUtils.writeValueByPOI( groupRow + i, groupCol, orgUnit.getName(), ExcelUtils.TEXT, sheet,
                            csNormal );

                        for ( ReportItem reportItem : ((ReportExcelGroupListing) reportExcel).getReportItems() )

                        {
                            if ( !reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.SERIAL )
                                && !reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.ORGANISATION ) )
                            {

                                if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.FORMULA_EXCEL ) )
                                {

                                    String formula = reportItem.getExpression();

                                    formula = formula.replace( "*", (groupRow + i) + "" ).toUpperCase();

                                    ExcelUtils.writeFormulaByPOI( groupRow + i, reportItem.getColumn(), String
                                        .valueOf( formula ), sheet, csSizeOdd );
                                }
                                else if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.DATAELEMENT ) )
                                {
                                    ExcelUtils.writeValueByPOI( groupRow + i, reportItem.getColumn(), String
                                        .valueOf( getDataValue( reportItem, orgUnit ) ), ExcelUtils.NUMBER, sheet,
                                        csCountOdd );
                                }
                                else if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.INDICATOR ) )
                                {
                                    ExcelUtils.writeValueByPOI( groupRow + i, reportItem.getColumn(), String
                                        .valueOf( getIndicatorValue( reportItem, orgUnit ) ), ExcelUtils.NUMBER, sheet,
                                        csCountOdd );
                                }
                            }

                        }

                        serial++;
                    }

                    for ( Integer column : colums )
                    {

                        String columnName = ExcelUtils.convertColNumberToColName( column );

                        String formula = "SUM(" + columnName + (currentGroupRow + 1) + ":" + columnName
                            + (currentGroupRow + memberOfOrganisationUnitGroups.size()) + ")";

                        ExcelUtils.writeFormulaByPOI( currentGroupRow, column, formula, sheet, csSizeOdd );
                    }
                }

            }// end for

            if ( groups.size() == 1 )
            {
                ExcelUtils.writeValueByPOI( groupRow, groupCol, "T\u1ed5ng s\u1ed1", ExcelUtils.TEXT, sheet, csNormal );
            }
            else
            {
                ExcelUtils.writeValueByPOI( groupRow - 1, groupCol, "T\u1ed5ng s\u1ed1", ExcelUtils.TEXT, sheet,
                    csNormal );

                for ( Integer column : colums )
                {
                    String columnName = ExcelUtils.convertColNumberToColName( column );

                    String formula = " 0 ";

                    for ( Integer row : rowOrgUnitGroups )
                    {
                        formula += "+" + columnName + "" + row.intValue();
                    }

                    ExcelUtils.writeFormulaByPOI( groupRow - 1, column, formula, sheet, csSizeOdd );
                }
            }
        }

        // -----------------------------------------------------------------
        // GENERATING THE REPORT FOR CATEGORY REPORT //
        // -----------------------------------------------------------------
        if ( reportExcel instanceof ReportExcelCategory )
        {

            // Get list of item report from ReportExcelCategory
            Set<ReportItem> reportItems = ((ReportExcelCategory) reportExcel).getReportItems();

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

                if ( !aItemRenameRow.isEmpty() )
                {
                    Collections.sort( aItemRenameRow );
                }

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
                        ExcelUtils.writeValueByPOI( icdCodeRow + serial, icdCodeCol, de.getCode(), ExcelUtils.TEXT,
                            sheet, csNormalOdd );
                    }

                    if ( aItemJumpingIndex.contains( Integer.valueOf( index ) ) )
                    {
                        ExcelUtils.writeValueByPOI( serialRow + serial, serialCol, NULL_INDEX, ExcelUtils.TEXT, sheet,
                            csNormal );
                        ExcelUtils.writeValueByPOI( elementRow + serial, elementCol, sDeName, ExcelUtils.TEXT, sheet,
                            csNormalOdd );

                        aItemJumpingIndex.remove( Integer.valueOf( index ) );

                        if ( index > 0 )
                        {
                            --index;
                        }
                    }
                    else
                    {
                        ExcelUtils.writeValueByPOI( serialRow + serial, serialCol,
                            generateStringNumber( index, orders ), ExcelUtils.TEXT, sheet, csNormalOdd );
                        ExcelUtils.writeValueByPOI( elementRow + serial, elementCol, sDeName, ExcelUtils.TEXT, sheet,
                            csNormalOdd );
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

                        ExcelUtils.writeValueByPOI( aItemRow.get( i ) + serial, aItemCol.get( i ), String
                            .valueOf( value ), ExcelUtils.NUMBER, sheet, csCountOdd );
                    }

                    serial++;

                } // end of for
            } // end of if
        } // end of if

        templateWorkbook.write( out );

        out.close();

        outputXLS = outputReportFile.getName();

        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.delete();

        statementManager.destroy();

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

        if ( flag )
        {
            value = (value > 0) ? 0 : 1;
        }

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
}
