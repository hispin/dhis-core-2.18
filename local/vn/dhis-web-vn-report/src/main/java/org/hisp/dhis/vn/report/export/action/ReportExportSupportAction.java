package org.hisp.dhis.vn.report.export.action;

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

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.vn.report.ReportExcelService;
import org.hisp.dhis.vn.report.ReportItem;
import org.hisp.dhis.vn.report.state.ReportLocationManager;
import org.hisp.dhis.vn.report.utils.DateUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public abstract class ReportExportSupportAction
    implements Action
{
    private static final String NULL_REPLACEMENT = "0";
    
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    OrganisationUnitSelectionManager organisationUnitSelectionManager;

    CurrentUserService currentUserService;

    AggregationService aggregationService;

    IndicatorService indicatorService;

    DataElementCategoryOptionComboService dataElementCategoryOptionComboService;

    StatementManager statementManager;

    DataElementService dataElementService;

    ReportLocationManager reportLocationManager;

    ReportExcelService reportService;

    PeriodService periodService;

    I18nFormat format;

    // -------------------------------------------
    // Input
    // -------------------------------------------

    Integer reportId;

    Integer periodId;

    // -------------------------------------------
    // Output
    // -------------------------------------------

    String outputXLS;

    InputStream inputStream;

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

    // ------------------------------------------
    // Excel format
    // ------------------------------------------

    SimpleDateFormat dateformatter = new SimpleDateFormat( "dd.MM.yyyy.h.mm.ss.a" );

    WritableCellFormat text = new WritableCellFormat();

    WritableCellFormat textLeft = new WritableCellFormat();

    WritableCellFormat textRight = new WritableCellFormat();

    WritableFont writableNumberFont = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );

    WritableFont writableICDFont = new WritableFont( WritableFont.ARIAL, 11, WritableFont.BOLD );

    WritableFont writableChapterFont = new WritableFont( WritableFont.ARIAL, 11, WritableFont.BOLD, false,
        UnderlineStyle.NO_UNDERLINE, Colour.BLACK );

    WritableCellFormat textChapterLeft = new WritableCellFormat( writableChapterFont );

    WritableCellFormat textNumberBoldRight = new WritableCellFormat( writableNumberFont );

    WritableCellFormat textICDBoldJustify = new WritableCellFormat( writableICDFont );

    WritableCellFormat number = new WritableCellFormat();

    void installExcelFormat()
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

    void installPeriod( Period period )
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

}
