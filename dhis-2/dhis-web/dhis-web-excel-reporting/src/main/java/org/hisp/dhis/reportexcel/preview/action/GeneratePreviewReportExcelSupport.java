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
package org.hisp.dhis.reportexcel.preview.action;

import static org.hisp.dhis.expression.Expression.SEPARATOR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import org.hisp.dhis.reportexcel.export.action.SelectionManager;
import org.hisp.dhis.reportexcel.preview.manager.InitializePOIStylesManager;
import org.hisp.dhis.reportexcel.utils.DateUtils;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Dang Duy Hieu
 * @author Tran Thanh Tri
 * @version $Id$
 * @since 2009-09-18
 */
public abstract class GeneratePreviewReportExcelSupport
    implements Action
{

    protected static final short CELLSTYLE_ALIGN_LEFT = HSSFCellStyle.ALIGN_LEFT;

    protected static final short CELLSTYLE_ALIGN_CENTER = HSSFCellStyle.ALIGN_CENTER;

    protected static final short CELLSTYLE_ALIGN_RIGHT = HSSFCellStyle.ALIGN_RIGHT;

    protected static final short CELLSTYLE_ALIGN_JUSTIFY = HSSFCellStyle.ALIGN_JUSTIFY;

    protected static final short CELLSTYLE_BORDER = HSSFCellStyle.BORDER_THIN;

    protected static final short CELLSTYLE_BORDER_COLOR = HSSFColor.LIGHT_ORANGE.index;

    private static final String NULL_REPLACEMENT = "0";

    protected static final String[] chappter = { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI",
        "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX", "XXI", "XXII", "XXIII", "XXIV", "XXV", "XXVI",
        "XXVII", "XXVIII", "XXIX", "XXX" };

    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    OrganisationUnitSelectionManager organisationUnitSelectionManager;

    CurrentUserService currentUserService;

    AggregationService aggregationService;

    IndicatorService indicatorService;

    DataElementCategoryService categoryService;

    DataElementService dataElementService;

    ReportLocationManager reportLocationManager;

    I18nFormat format;

    DataMartStore dataMartStore;

    InitializePOIStylesManager initPOIStylesManager;
    
    protected StatementManager statementManager;
    
    protected SelectionManager selectionManager;
    
    protected ReportExcelService reportService;

    protected PeriodService periodService;

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    String outputXLS;

    InputStream inputStream;
    
    protected Integer sheetId;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public Integer getSheetId()
    {
        return sheetId;
    }

    public void setSheetId( Integer sheetId )
    {
        this.sheetId = sheetId;
    }

    public String getOutputXLS()
    {
        return outputXLS;
    }

    public InputStream getInputStream()
    {
        return inputStream;
    }

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

    public InitializePOIStylesManager getInitPOIStylesManager()
    {
        return initPOIStylesManager;
    }

    public void setInitPOIStylesManager( InitializePOIStylesManager initPOIStylesManager )
    {
        this.initPOIStylesManager = initPOIStylesManager;
    }

    // -----------------------------------------
    // Local variable
    // -----------------------------------------
    protected File outputReportFile;

    protected FileInputStream inputStreamExcelTemplate;

    protected FileOutputStream outputStreamExcelTemplate;

    protected HSSFWorkbook templateWorkbook;

    protected HSSFSheet hssfSheet;

    protected Date startDate;

    protected Date endDate;

    protected Date firstDayOfYear;

    protected Date last3MonthStartDate;

    protected Date last3MonthEndDate;

    protected Date last6MonthStartDate;

    protected Date last6MonthEndDate;

    protected Date endDateOfYear;

    protected Date startQuaterly;

    protected Date endQuaterly;

    protected Date startSixMonthly;

    protected Date endSixMonthly;

    // ------------------------------------------
    // Excel format
    // ------------------------------------------
    protected HSSFFont csFont;

    protected HSSFHeader header;

    protected HSSFDataFormat dFormat;

    protected HSSFCellStyle csHeader;

    protected HSSFCellStyle csText;

    protected HSSFCellStyle csTextLeft;

    protected HSSFCellStyle csTextRight;

    protected HSSFCellStyle csTextICDJustify;

    protected HSSFCellStyle csTextChapterLeft;

    protected HSSFCellStyle csNumber;

    SimpleDateFormat dateformatter = new SimpleDateFormat( "dd.MM.yyyy.h.mm.ss.a" );

    private void initExcelFormat()
        throws Exception
    {
        hssfSheet = templateWorkbook.getSheetAt( 0 );
        header = hssfSheet.getHeader();
        csFont = templateWorkbook.createFont();
        dFormat = templateWorkbook.createDataFormat();
        csHeader = templateWorkbook.createCellStyle();
        csText = templateWorkbook.createCellStyle();
        csTextLeft = templateWorkbook.createCellStyle();
        csTextRight = templateWorkbook.createCellStyle();
        csTextICDJustify = templateWorkbook.createCellStyle();
        csTextChapterLeft = templateWorkbook.createCellStyle();
        csNumber = templateWorkbook.createCellStyle();

    }

    protected void installExcelFormat()
    {
        // override
    }

    @SuppressWarnings( "static-access" )
    protected void installDefaultExcelFormat()
        throws Exception
    {        
        initPOIStylesManager.initDefaultHeader( header );
        initPOIStylesManager.initDefaultFont( csFont );
        initPOIStylesManager.initDefaultCellStyle( csText, csFont );
        initPOIStylesManager.initCellStyle( csTextLeft, csFont, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_ALIGN_LEFT );
        initPOIStylesManager.initCellStyle( csTextRight, csFont, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_ALIGN_RIGHT );
        initPOIStylesManager.initCellStyle( csTextICDJustify, csFont, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_ALIGN_JUSTIFY );
        initPOIStylesManager.initCellStyle( csNumber, csFont, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_ALIGN_CENTER );
        
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
        throws Exception
    {
        Calendar calendar = Calendar.getInstance();

        File reportTempDir = reportLocationManager.getReportExcelTempDirectory();

        this.inputStreamExcelTemplate = new FileInputStream( reportLocationManager.getReportExcelTemplateDirectory()
            + File.separator + reportExcel.getExcelTemplateFile() );

        this.outputReportFile = new File( reportTempDir, currentUserService.getCurrentUsername()
            + this.dateformatter.format( calendar.getTime() ) + reportExcel.getExcelTemplateFile() );

        this.outputStreamExcelTemplate = new FileOutputStream( outputReportFile );

        this.templateWorkbook = new HSSFWorkbook( inputStreamExcelTemplate );

        this.templateWorkbook.setSheetName( 0, reportExcel.getName().replaceAll( " ", "" ) );

        this.initExcelFormat();

        this.installDefaultExcelFormat();

        ExcelUtils.writeValueByPOI( reportExcel.getOrganisationRow(), reportExcel.getOrganisationColumn(),
            organisationUnit.getName(), ExcelUtils.TEXT, templateWorkbook.getSheetAt( 0 ), csText );

        ExcelUtils.writeValueByPOI( reportExcel.getPeriodRow(), reportExcel.getPeriodColumn(), format
            .formatPeriod( period ), ExcelUtils.TEXT, templateWorkbook.getSheetAt( 0 ), csText );

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
        throws Exception
    {
        Calendar calendar = Calendar.getInstance();

        File reportTempDir = reportLocationManager.getReportExcelTempDirectory();
       
        this.inputStreamExcelTemplate = new FileInputStream( reportLocationManager.getReportExcelTemplateDirectory()
            + File.separator + reportExcel.getExcelTemplateFile() );
       
        this.outputReportFile = new File( reportTempDir, currentUserService.getCurrentUsername()
            + this.dateformatter.format( calendar.getTime() ) + reportExcel.getExcelTemplateFile() );
       
        this.outputStreamExcelTemplate = new FileOutputStream( outputReportFile );
       
        this.templateWorkbook = new HSSFWorkbook( inputStreamExcelTemplate );
 
        this.templateWorkbook.setSheetName( 0, reportExcel.getName().replaceAll( " ", "" ) );
 
        this.initExcelFormat();
 
        this.installDefaultExcelFormat();
       
        ExcelUtils.writeValueByPOI( reportExcel.getOrganisationRow(), reportExcel.getOrganisationColumn(),
            organisationUnitGroup.getName(), ExcelUtils.TEXT, templateWorkbook.getSheetAt( 0 ), csText );

        ExcelUtils.writeValueByPOI( reportExcel.getPeriodRow(), reportExcel.getPeriodColumn(), format
            .formatPeriod( period ), ExcelUtils.TEXT, templateWorkbook.getSheetAt( 0 ), csText );

    }

    protected void complete()
        throws IOException
    {
        this.templateWorkbook.write( outputStreamExcelTemplate );

        this.outputStreamExcelTemplate.close();

        selectionManager.setReportExcelOutput( outputReportFile.getPath() );

        // inputStream = new BufferedInputStream( new FileInputStream(
        // this.outputReportFile ) );
        // outputReportFile.delete();
    }

}
