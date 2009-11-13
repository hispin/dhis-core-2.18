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
package org.hisp.dhis.reportexcel.export.action;

import static org.hisp.dhis.expression.Expression.SEPARATOR;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datamart.DataMartStore;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.ReportExcelItem;
import org.hisp.dhis.reportexcel.ReportExcelService;
import org.hisp.dhis.reportexcel.ReportLocationManager;
import org.hisp.dhis.reportexcel.period.db.PeriodDatabaseService;
import org.hisp.dhis.reportexcel.state.SelectionManager;
import org.hisp.dhis.reportexcel.utils.DateUtils;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public abstract class GenerateReportExcelSupport
    implements Action
{

    private static final String NULL_REPLACEMENT = "0";

    protected static final String[] chappter = { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI",
        "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX", "XXI", "XXII", "XXIII", "XXIV", "XXV", "XXVI",
        "XXVII", "XXVIII", "XXIX", "XXX" };

    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    protected OrganisationUnitSelectionManager organisationUnitSelectionManager;

    protected CurrentUserService currentUserService;

    protected AggregationService aggregationService;

    protected IndicatorService indicatorService;

    protected DataElementCategoryService categoryService;

    protected StatementManager statementManager;

    protected DataElementService dataElementService;

    protected ReportLocationManager reportLocationManager;

    protected ReportExcelService reportService;

    protected PeriodService periodService;

    protected I18nFormat format;

    protected DataMartStore dataMartStore;

    protected SelectionManager selectionManager;

    protected PeriodDatabaseService periodDatabaseService;

    // -------------------------------------------
    // Output
    // -------------------------------------------

    protected String outputXLS;

    protected InputStream inputStream;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
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

    public void setPeriodDatabaseService( PeriodDatabaseService periodDatabaseService )
    {
        this.periodDatabaseService = periodDatabaseService;
    }

    // -----------------------------------------
    // Local variable
    // -----------------------------------------
    protected File outputReportFile;

    File inputExcelTemplate;

    protected WritableWorkbook outputReportWorkbook;

    Date startDate;

    protected Date endDate;

    Date firstDayOfYear;

    Date last3MonthStartDate;

    Date last3MonthEndDate;

    Date last6MonthStartDate;

    Date last6MonthEndDate;

    Date endDateOfYear;

    Date startQuaterly;

    Date endQuaterly;

    Date startSixMonthly;

    Date endSixMonthly;

    // ------------------------------------------
    // Excel format
    // ------------------------------------------

    SimpleDateFormat dateformatter = new SimpleDateFormat( "dd.MM.yyyy.h.mm.ss.a" );

    WritableCellFormat text = new WritableCellFormat();

    protected WritableCellFormat textLeft = new WritableCellFormat();

    WritableCellFormat textRight = new WritableCellFormat();

    WritableFont writableNumberFont = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );

    WritableFont writableICDFont = new WritableFont( WritableFont.ARIAL, 11, WritableFont.BOLD );

    WritableFont writableChapterFont = new WritableFont( WritableFont.ARIAL, 11, WritableFont.BOLD, false,
        UnderlineStyle.NO_UNDERLINE, Colour.BLACK );

    protected WritableCellFormat textChapterLeft = new WritableCellFormat( writableChapterFont );

    WritableCellFormat textNumberBoldRight = new WritableCellFormat( writableNumberFont );

    protected WritableCellFormat textICDBoldJustify = new WritableCellFormat( writableICDFont );

    protected WritableCellFormat number = new WritableCellFormat();

    protected void installExcelFormat()
        throws WriteException
    {

        textLeft.setAlignment( Alignment.LEFT );
        textLeft.setBorder( Border.ALL, BorderLineStyle.THIN );

        textRight.setAlignment( Alignment.RIGHT );
        textRight.setBorder( Border.ALL, BorderLineStyle.THIN );

        textNumberBoldRight.setAlignment( Alignment.RIGHT );
        textNumberBoldRight.setBorder( Border.ALL, BorderLineStyle.THIN );

        textICDBoldJustify.setAlignment( Alignment.JUSTIFY );
        textICDBoldJustify.setBorder( Border.ALL, BorderLineStyle.THIN );
        textChapterLeft.setAlignment( Alignment.LEFT );
        textChapterLeft.setBorder( Border.ALL, BorderLineStyle.THIN );

        number.setBorder( Border.ALL, BorderLineStyle.THIN );
        number.setAlignment( Alignment.CENTRE );

    }

    protected void installPeriod( Period period )
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

    protected void installReadTemplateFile( ReportExcel reportExcel, Period period, OrganisationUnit organisationUnit )
        throws BiffException, IOException, RowsExceededException, WriteException, IndexOutOfBoundsException
    {

        File reportTempDir = reportLocationManager.getReportExcelTempDirectory();

        this.inputExcelTemplate = new File( reportLocationManager.getReportExcelTemplateDirectory() + File.separator
            + reportExcel.getExcelTemplateFile() );

        Calendar calendar = Calendar.getInstance();

        this.outputReportFile = new File( reportTempDir, currentUserService.getCurrentUsername()
            + this.dateformatter.format( calendar.getTime() ) + inputExcelTemplate.getName() );

        Workbook templateWorkbook = Workbook.getWorkbook( inputExcelTemplate );

        outputReportWorkbook = Workbook.createWorkbook( outputReportFile, templateWorkbook );

        ExcelUtils.writeValue( reportExcel.getOrganisationRow(), reportExcel.getOrganisationColumn(), organisationUnit
            .getName(), ExcelUtils.TEXT, outputReportWorkbook.getSheet( 0 ), text );

        ExcelUtils.writeValue( reportExcel.getPeriodRow(), reportExcel.getPeriodColumn(),
            format.formatPeriod( period ), ExcelUtils.TEXT, outputReportWorkbook.getSheet( 0 ), text );

    }

    protected double getIndicatorValue( ReportExcelItem reportItem, OrganisationUnit organisationUnit )
    {
        double value = 0.0;

        if ( reportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.SELECTED_MONTH ) )
        {
            value = MathUtils.calculateExpression( generateIndicatorExpression( reportItem, startDate, endDate,
                organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.LAST_3_MONTH ) )
        {
            value = MathUtils.calculateExpression( generateIndicatorExpression( reportItem, last3MonthStartDate,
                last3MonthEndDate, organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.SO_FAR_THIS_YEAR ) )
        {
            value = MathUtils.calculateExpression( generateIndicatorExpression( reportItem, firstDayOfYear, endDate,
                organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.LAST_6_MONTH ) )
        {
            value = MathUtils.calculateExpression( generateIndicatorExpression( reportItem, last6MonthStartDate,
                last6MonthEndDate, organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.YEARLY ) )
        {
            value = MathUtils.calculateExpression( generateIndicatorExpression( reportItem, firstDayOfYear,
                endDateOfYear, organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.QUATERLY ) )
        {
            value = MathUtils.calculateExpression( generateIndicatorExpression( reportItem, startQuaterly, endQuaterly,
                organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.SIX_MONTH ) )
        {
            value = MathUtils.calculateExpression( generateIndicatorExpression( reportItem, startSixMonthly,
                endSixMonthly, organisationUnit ) );
        }
        return value;

    }

    public void complete()
        throws IOException, WriteException
    {
        outputReportWorkbook.write();

        outputReportWorkbook.close();

        selectionManager.setDownloadFilePath( outputReportFile.getPath() );
    }

    protected String generateIndicatorExpression( ReportExcelItem reportItem, Date startDate, Date endDate,
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

    protected double getDataValue( ReportExcelItem reportItem, OrganisationUnit organisationUnit )
    {
        double value = 0.0;

        if ( reportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.SELECTED_MONTH ) )
        {

            value = MathUtils
                .calculateExpression( generateExpression( reportItem, startDate, endDate, organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.LAST_3_MONTH ) )
        {
            value = MathUtils.calculateExpression( generateExpression( reportItem, last3MonthStartDate,
                last3MonthEndDate, organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.SO_FAR_THIS_YEAR ) )
        {
            value = MathUtils.calculateExpression( generateExpression( reportItem, firstDayOfYear, endDate,
                organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.LAST_6_MONTH ) )
        {
            value = MathUtils.calculateExpression( generateExpression( reportItem, last6MonthStartDate,
                last6MonthEndDate, organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.YEARLY ) )
        {
            value = MathUtils.calculateExpression( generateExpression( reportItem, firstDayOfYear, endDateOfYear,
                organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.QUATERLY ) )
        {
            value = MathUtils.calculateExpression( generateExpression( reportItem, startQuaterly, endQuaterly,
                organisationUnit ) );
        }
        else if ( reportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.SIX_MONTH ) )
        {
            value = MathUtils.calculateExpression( generateExpression( reportItem, startSixMonthly, endSixMonthly,
                organisationUnit ) );
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
    protected String generateExpression( ReportExcelItem reportItem, Date startDate, Date endDate,
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

                DataElementCategoryOptionCombo optionCombo = categoryService
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

    protected void installReadTemplateFile( ReportExcel reportExcel, Period period,
        OrganisationUnitGroup organisationUnitGroup )
        throws BiffException, IOException, RowsExceededException, WriteException, IndexOutOfBoundsException
    {

        File reportTempDir = reportLocationManager.getReportExcelTempDirectory();

        this.inputExcelTemplate = new File( reportLocationManager.getReportExcelTemplateDirectory() + File.separator
            + reportExcel.getExcelTemplateFile() );

        Calendar calendar = Calendar.getInstance();

        this.outputReportFile = new File( reportTempDir, currentUserService.getCurrentUsername()
            + this.dateformatter.format( calendar.getTime() ) + inputExcelTemplate.getName() );

        Workbook templateWorkbook = Workbook.getWorkbook( inputExcelTemplate );

        outputReportWorkbook = Workbook.createWorkbook( outputReportFile, templateWorkbook );

        ExcelUtils.writeValue( reportExcel.getOrganisationRow(), reportExcel.getOrganisationColumn(),
            organisationUnitGroup.getName(), ExcelUtils.TEXT, outputReportWorkbook.getSheet( 0 ), text );

        ExcelUtils.writeValue( reportExcel.getPeriodRow(), reportExcel.getPeriodColumn(),
            format.formatPeriod( period ), ExcelUtils.TEXT, outputReportWorkbook.getSheet( 0 ), text );

    }

}
