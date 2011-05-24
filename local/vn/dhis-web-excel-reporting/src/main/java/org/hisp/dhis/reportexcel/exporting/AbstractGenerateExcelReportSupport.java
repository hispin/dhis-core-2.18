package org.hisp.dhis.reportexcel.exporting;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import static org.hisp.dhis.reportexcel.utils.DateUtils.getEndQuaterly;
import static org.hisp.dhis.reportexcel.utils.DateUtils.getEndSixMonthly;
import static org.hisp.dhis.reportexcel.utils.DateUtils.getFirstDayOfYear;
import static org.hisp.dhis.reportexcel.utils.DateUtils.getLastDayOfYear;
import static org.hisp.dhis.reportexcel.utils.DateUtils.getStartQuaterly;
import static org.hisp.dhis.reportexcel.utils.DateUtils.getStartSixMonthly;
import static org.hisp.dhis.reportexcel.utils.DateUtils.getTimeRoll;
import static org.hisp.dhis.reportexcel.utils.ExpressionUtils.generateExpression;
import static org.hisp.dhis.reportexcel.utils.ExpressionUtils.generateIndicatorExpression;
import static org.hisp.dhis.system.util.MathUtils.calculateExpression;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.ReportExcelItem;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public abstract class AbstractGenerateExcelReportSupport
    extends GenerateExcelReportGeneric
    implements Action
{
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        statementManager.initialise();

        Period period = periodGenericManager.getSelectedPeriod();

        ReportExcel exportReport = exportReportService.getExportReport( selectionManager.getSelectedReportId() );

        this.installPeriod( period );

        executeGenerateOutputFile( exportReport, period );

        this.complete();

        statementManager.destroy();

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Overriding abstract method(s)
    // -------------------------------------------------------------------------

    /**
     * The process method which must be implemented by subclasses.
     * 
     * @param period
     * @param exportReport
     * @param organisationUnit
     */
    protected abstract void executeGenerateOutputFile( ReportExcel exportReport, Period period )
        throws Exception;

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    protected void installExcelFormat()
    {
        // override
    }

    protected void installPeriod( Period period )
    {
        Calendar calendar = Calendar.getInstance();

        // Monthly period
        startDate = period.getStartDate();
        endDate = period.getEndDate();

        // Last 3 month period
        // Last 2 months + this month = last 3 month
        last3MonthStartDate = getTimeRoll( startDate, Calendar.MONTH, -2 );
        last3MonthStartDate = getTimeRoll( last3MonthStartDate, Calendar.DATE, -1 );
        last3MonthEndDate = period.getEndDate();

        // So far this year period
        calendar.setTime( endDate );

        firstDayOfYear = getFirstDayOfYear( calendar.get( Calendar.YEAR ) );
        firstDayOfYear = getTimeRoll( firstDayOfYear, Calendar.DATE, -1 );
        endDateOfYear = getLastDayOfYear( calendar.get( Calendar.YEAR ) );

        // Last 6 month period
        // Last 5 months + this month = last 6 month
        last6MonthStartDate = getTimeRoll( startDate, Calendar.MONTH, -5 );
        last6MonthStartDate = getTimeRoll( last6MonthStartDate, Calendar.DATE, -1 );
        last6MonthEndDate = period.getEndDate();

        // Quaterly
        startQuaterly = getStartQuaterly( startDate );
        startQuaterly = getTimeRoll( startQuaterly, Calendar.DATE, -1 );
        endQuaterly = getEndQuaterly( startDate );

        // Six monthly
        startSixMonthly = getStartSixMonthly( startDate );
        startSixMonthly = getTimeRoll( startSixMonthly, Calendar.DATE, -1 );
        endSixMonthly = getEndSixMonthly( startDate );
    }

    protected void installReadTemplateFile( ReportExcel exportReport, Period period, Object object )
        throws Exception
    {
        Calendar calendar = Calendar.getInstance();

        File reportTempDir = reportLocationManager.getReportExcelTemporaryDirectory();

        this.outputReportFile = new File( reportTempDir, currentUserService.getCurrentUsername()
            + this.dateformatter.format( calendar.getTime() ) + exportReport.getExcelTemplateFile() );

        this.outputStreamExcelTemplate = new FileOutputStream( outputReportFile );

        this.createWorkbookInstance( exportReport );

        this.initExcelFormat();

        this.installDefaultExcelFormat();

        this.initFormulaEvaluating();

        if ( exportReport.getOrganisationRow() != null && exportReport.getOrganisationColumn() != null )
        {
            String value = "";

            if ( object instanceof OrganisationUnit )
            {
                OrganisationUnit orgunit = (OrganisationUnit) object;

                value = orgunit.getName();
            }
            else
            {
                OrganisationUnitGroup orgunitGroup = (OrganisationUnitGroup) object;

                value = orgunitGroup.getName();
            }

            ExcelUtils.writeValueByPOI( exportReport.getOrganisationRow(), exportReport.getOrganisationColumn(), value,
                ExcelUtils.TEXT, templateWorkbook.getSheetAt( 0 ), csText );
        }

        if ( exportReport.getPeriodRow() != null && exportReport.getPeriodColumn() != null )
        {
            ExcelUtils.writeValueByPOI( exportReport.getPeriodRow(), exportReport.getPeriodColumn(), format
                .formatPeriod( period ), ExcelUtils.TEXT, templateWorkbook.getSheetAt( 0 ), csText );
        }
    }

    // -------------------------------------------------------------------------
    // DataElement Value
    // -------------------------------------------------------------------------

    protected double getDataValue( ReportExcelItem exportItem, OrganisationUnit organisationUnit )
    {
        double value = 0.0;

        if ( exportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.SELECTED_MONTH ) )
        {
            value = calculateExpression( generateExpression( exportItem, startDate, endDate, organisationUnit,
                dataElementService, categoryService, aggregationService ) );
        }
        else if ( exportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.LAST_3_MONTH ) )
        {
            value = calculateExpression( generateExpression( exportItem, last3MonthStartDate, last3MonthEndDate,
                organisationUnit, dataElementService, categoryService, aggregationService ) );
        }
        else if ( exportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.SO_FAR_THIS_YEAR ) )
        {
            value = calculateExpression( generateExpression( exportItem, firstDayOfYear, endDate, organisationUnit,
                dataElementService, categoryService, aggregationService ) );
        }
        else if ( exportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.LAST_6_MONTH ) )
        {
            value = calculateExpression( generateExpression( exportItem, last6MonthStartDate, last6MonthEndDate,
                organisationUnit, dataElementService, categoryService, aggregationService ) );
        }
        else if ( exportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.YEARLY ) )
        {
            value = calculateExpression( generateExpression( exportItem, firstDayOfYear, endDateOfYear,
                organisationUnit, dataElementService, categoryService, aggregationService ) );
        }
        else if ( exportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.QUARTERLY ) )
        {
            value = calculateExpression( generateExpression( exportItem, startQuaterly, endQuaterly, organisationUnit,
                dataElementService, categoryService, aggregationService ) );
        }
        else if ( exportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.SIX_MONTH ) )
        {
            value = calculateExpression( generateExpression( exportItem, startSixMonthly, endSixMonthly,
                organisationUnit, dataElementService, categoryService, aggregationService ) );
        }

        return value;
    }

    // -------------------------------------------------------------------------
    // Indicator Value
    // -------------------------------------------------------------------------

    protected double getIndicatorValue( ReportExcelItem exportItem, OrganisationUnit organisationUnit )
    {
        double value = 0.0;

        if ( exportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.SELECTED_MONTH ) )
        {
            value = calculateExpression( generateIndicatorExpression( exportItem, startDate, endDate, organisationUnit,
                indicatorService, aggregationService ) );
        }
        else if ( exportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.LAST_3_MONTH ) )
        {
            value = calculateExpression( generateIndicatorExpression( exportItem, last3MonthStartDate,
                last3MonthEndDate, organisationUnit, indicatorService, aggregationService ) );
        }
        else if ( exportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.SO_FAR_THIS_YEAR ) )
        {
            value = calculateExpression( generateIndicatorExpression( exportItem, firstDayOfYear, endDate,
                organisationUnit, indicatorService, aggregationService ) );
        }
        else if ( exportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.LAST_6_MONTH ) )
        {
            value = calculateExpression( generateIndicatorExpression( exportItem, last6MonthStartDate,
                last6MonthEndDate, organisationUnit, indicatorService, aggregationService ) );
        }
        else if ( exportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.YEARLY ) )
        {
            value = calculateExpression( generateIndicatorExpression( exportItem, firstDayOfYear, endDateOfYear,
                organisationUnit, indicatorService, aggregationService ) );
        }
        else if ( exportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.QUARTERLY ) )
        {
            value = calculateExpression( generateIndicatorExpression( exportItem, startQuaterly, endQuaterly,
                organisationUnit, indicatorService, aggregationService ) );
        }
        else if ( exportItem.getPeriodType().equalsIgnoreCase( ReportExcelItem.PERIODTYPE.SIX_MONTH ) )
        {
            value = calculateExpression( generateIndicatorExpression( exportItem, startSixMonthly, endSixMonthly,
                organisationUnit, indicatorService, aggregationService ) );
        }

        return value;
    }

    // -------------------------------------------------------------------------
    // Formulae methods
    // -------------------------------------------------------------------------

    protected void recalculatingFormula( Sheet sheet )
    {
        for ( Row row : sheet )
        {
            for ( Cell cell : row )
            {
                if ( (cell != null) && (cell.getCellType() == Cell.CELL_TYPE_FORMULA) )
                {
                    this.evaluatorFormula.evaluateFormulaCell( cell );
                }
            }
        }
    }

    protected void complete()
        throws IOException
    {
        this.templateWorkbook.write( outputStreamExcelTemplate );

        this.outputStreamExcelTemplate.close();

        selectionManager.setDownloadFilePath( outputReportFile.getPath() );
    }

}
