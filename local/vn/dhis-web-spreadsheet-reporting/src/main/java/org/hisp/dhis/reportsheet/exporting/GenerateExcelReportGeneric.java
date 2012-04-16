/*
 * Copyright (c) 2004-2011, University of Oslo
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
package org.hisp.dhis.reportsheet.exporting;

import static org.hisp.dhis.reportsheet.utils.FileUtils.checkingExtensionExcelFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.amplecode.quick.StatementManager;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.ReportLocationManager;
import org.hisp.dhis.reportsheet.preview.manager.InitializePOIStylesManager;
import org.hisp.dhis.reportsheet.state.SelectionManager;
import org.hisp.dhis.user.CurrentUserService;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class GenerateExcelReportGeneric
{
    static final short CELLSTYLE_ALIGN_LEFT = CellStyle.ALIGN_LEFT;

    static final short CELLSTYLE_ALIGN_CENTER = CellStyle.ALIGN_CENTER;

    static final short CELLSTYLE_ALIGN_RIGHT = CellStyle.ALIGN_RIGHT;

    static final short CELLSTYLE_ALIGN_JUSTIFY = CellStyle.ALIGN_JUSTIFY;

    static final short CELLSTYLE_BORDER = CellStyle.BORDER_THIN;

    static final short CELLSTYLE_BORDER_COLOR = IndexedColors.DARK_BLUE.getIndex();

    protected static final String[] chappter = { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI",
        "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX", "XXI", "XXII", "XXIII", "XXIV", "XXV", "XXVI",
        "XXVII", "XXVIII", "XXIX", "XXX" };

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    CurrentUserService currentUserService;

    IndicatorService indicatorService;

    InitializePOIStylesManager initPOIStylesManager;

    protected AggregationService aggregationService;

    protected AggregatedDataValueService aggregatedDataValueService;

    protected DataElementCategoryService categoryService;

    protected DataElementService dataElementService;

    protected PeriodService periodService;

    protected ExportReportService exportReportService;

    protected ReportLocationManager reportLocationManager;

    protected StatementManager statementManager;

    protected SelectionManager selectionManager;

    protected OrganisationUnitSelectionManager organisationUnitSelectionManager;

    protected DataValueService dataValueService;

    protected I18n i18n;

    protected I18nFormat format;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    protected InputStream inputStream;

    // -------------------------------------------------------------------------
    // Local variables
    // -------------------------------------------------------------------------

    protected File outputReportFile;

    protected FileInputStream inputStreamExcelTemplate;

    protected FileOutputStream outputStreamExcelTemplate;

    protected Workbook templateWorkbook;

    protected Sheet sheetPOI;

    protected Date startDate;

    protected Date endDate;

    protected Date firstDayOfMonth;

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

    // -------------------------------------------------------------------------
    // Excel format
    // -------------------------------------------------------------------------

    protected DataFormat dFormat;

    protected Font csFont;

    protected Font csFont11Bold;

    protected Font csFont10Bold;

    protected Font csFont12BoldCenter;

    protected CellStyle csNumber;

    protected CellStyle csFormula;

    protected CellStyle csText;

    protected CellStyle csText10Bold;

    protected CellStyle csTextSerial;

    protected CellStyle csTextICDJustify;

    protected CellStyle csText12BoldCenter;

    protected FormulaEvaluator evaluatorFormula;

    SimpleDateFormat dateformatter = new SimpleDateFormat( "dd.MM.yyyy.h.mm.ss.a" );

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    /**
     * @param i18n the i18n to set
     */
    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
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

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    public void setAggregatedDataValueService( AggregatedDataValueService aggregatedDataValueService )
    {
        this.aggregatedDataValueService = aggregatedDataValueService;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public void setReportLocationManager( ReportLocationManager reportLocationManager )
    {
        this.reportLocationManager = reportLocationManager;
    }

    public void setExportReportService( ExportReportService exportReportService )
    {
        this.exportReportService = exportReportService;
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

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

    public void createWorkbookInstance( ExportReport exportReport )
        throws FileNotFoundException, IOException
    {
        this.inputStreamExcelTemplate = new FileInputStream( reportLocationManager.getExportReportTemplateDirectory()
            + File.separator + exportReport.getExcelTemplateFile() );

        if ( checkingExtensionExcelFile( exportReport.getExcelTemplateFile() ) )
        {
            this.templateWorkbook = new HSSFWorkbook( this.inputStreamExcelTemplate );
        }
        else
        {
            this.templateWorkbook = new XSSFWorkbook( this.inputStreamExcelTemplate );
        }
    }

    public void initExcelFormat()
        throws Exception
    {
        sheetPOI = templateWorkbook.getSheetAt( 0 );
        csFont = templateWorkbook.createFont();
        csFont10Bold = templateWorkbook.createFont();
        csFont11Bold = templateWorkbook.createFont();
        csFont12BoldCenter = templateWorkbook.createFont();
        dFormat = templateWorkbook.createDataFormat();
        csNumber = templateWorkbook.createCellStyle();
        csFormula = templateWorkbook.createCellStyle();
        csText = templateWorkbook.createCellStyle();
        csText10Bold = templateWorkbook.createCellStyle();
        csTextSerial = templateWorkbook.createCellStyle();
        csTextICDJustify = templateWorkbook.createCellStyle();
        csText12BoldCenter = templateWorkbook.createCellStyle();
    }

    @SuppressWarnings( "static-access" )
    public void installDefaultExcelFormat()
        throws Exception
    {
        initPOIStylesManager.initDefaultFont( csFont );
        initPOIStylesManager.initDefaultCellStyle( csText, csFont );

        initPOIStylesManager.initFont( csFont10Bold, "Tahoma", (short) 10, Font.BOLDWEIGHT_BOLD, IndexedColors.BLACK
            .getIndex() );
        initPOIStylesManager.initFont( csFont11Bold, "Tahoma", (short) 11, Font.BOLDWEIGHT_BOLD,
            IndexedColors.DARK_BLUE.getIndex() );
        initPOIStylesManager.initFont( csFont12BoldCenter, "Tahoma", (short) 12, Font.BOLDWEIGHT_BOLD,
            IndexedColors.BLUE.getIndex() );

        initPOIStylesManager.initCellStyle( csNumber, csFont, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_ALIGN_RIGHT, false );
        initPOIStylesManager.initCellStyle( csFormula, csFont11Bold, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_ALIGN_RIGHT, true );
        initPOIStylesManager.initCellStyle( csText10Bold, csFont10Bold, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_ALIGN_LEFT,
            true );
        initPOIStylesManager.initCellStyle( csTextSerial, csFont, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_ALIGN_CENTER, false );
        initPOIStylesManager.initCellStyle( csTextICDJustify, csFont, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_ALIGN_JUSTIFY, true );
        initPOIStylesManager.initCellStyle( csText12BoldCenter, csFont12BoldCenter, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER,
            this.CELLSTYLE_BORDER_COLOR, this.CELLSTYLE_BORDER, this.CELLSTYLE_BORDER_COLOR,
            this.CELLSTYLE_ALIGN_CENTER, true );

    }

    public void initFormulaEvaluating()
    {
        this.evaluatorFormula = this.templateWorkbook.getCreationHelper().createFormulaEvaluator();
    }
}
