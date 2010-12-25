package org.hisp.dhis.workbook.impl;

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

import static org.hisp.dhis.system.util.MathUtils.isNumeric;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.hisp.dhis.completeness.DataSetCompletenessResult;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reporttable.ReportTableData;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.validation.ValidationResult;
import org.hisp.dhis.workbook.WorkbookService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@Transactional
public class JExcelWorkbookService
    implements WorkbookService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ReportTableService reportTableService;

    public void setReportTableService( ReportTableService reportTableService )
    {
        this.reportTableService = reportTableService;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private static final WritableCellFormat FORMAT_TTTLE = new WritableCellFormat( new WritableFont(
        WritableFont.TAHOMA, 13, WritableFont.NO_BOLD, false ) );

    private static final WritableCellFormat FORMAT_LABEL = new WritableCellFormat( new WritableFont(
        WritableFont.ARIAL, 11, WritableFont.NO_BOLD, true ) );

    private static final WritableCellFormat FORMAT_TEXT = new WritableCellFormat( new WritableFont( WritableFont.ARIAL,
        11, WritableFont.NO_BOLD, false ) );

    // -------------------------------------------------------------------------
    // WorkbookService implementation
    // -------------------------------------------------------------------------

    public String writeReportTableData( OutputStream outputStream, int id, I18nFormat format )
    {
        ReportTableData data = reportTableService.getReportTableData( id, format );

        try
        {
            WritableWorkbook workbook = Workbook.createWorkbook( outputStream );

            WritableSheet sheet = workbook.createSheet( "Report table data", 0 );

            int rowNumber = 1;

            int columnIndex = 0;

            sheet.addCell( new Label( 0, rowNumber++, data.getName(), FORMAT_TTTLE ) );

            rowNumber++;

            for ( String column : data.getPrettyPrintColumns() )
            {
                sheet.addCell( new Label( columnIndex++, rowNumber, column, FORMAT_LABEL ) );
            }

            rowNumber++;

            for ( SortedMap<Integer, String> row : data.getRows() )
            {
                columnIndex = 0;

                for ( String value : row.values() )
                {
                    if ( isNumeric( value ) )
                    {
                        sheet.addCell( new Number( columnIndex++, rowNumber, Double.valueOf( value ), FORMAT_TEXT ) );
                    }
                    else
                    {
                        sheet.addCell( new Label( columnIndex++, rowNumber, value, FORMAT_TEXT ) );
                    }
                }

                rowNumber++;
            }

            workbook.write();

            workbook.close();
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to generate workbook for data elements", ex );
        }

        return data.getName();
    }

    public void writeDataSetCompletenessResult( Collection<DataSetCompletenessResult> results, OutputStream out,
        I18n i18n, OrganisationUnit unit, DataSet dataSet )
    {
        final int MARGIN_LEFT = 1;

        WritableCellFormat documentTitle = new WritableCellFormat( new WritableFont( WritableFont.TAHOMA, 15,
            WritableFont.NO_BOLD, false ) );
        WritableCellFormat subTitle = new WritableCellFormat( new WritableFont( WritableFont.TAHOMA, 13,
            WritableFont.NO_BOLD, false ) );
        WritableCellFormat columnHeader = new WritableCellFormat( new WritableFont( WritableFont.TAHOMA, 11,
            WritableFont.NO_BOLD, true ) );
        WritableCellFormat text = new WritableCellFormat( new WritableFont( WritableFont.ARIAL, 11,
            WritableFont.NO_BOLD, false ) );

        try
        {
            WritableWorkbook workbook = Workbook.createWorkbook( out );

            WritableSheet sheet = workbook.createSheet( "Data completeness", 0 );

            String dataSetName = dataSet != null ? " - " + dataSet.getName() : "";

            sheet.addCell( new Label( MARGIN_LEFT, 1, i18n.getString( "data_completeness_report" ) + " - "
                + unit.getName() + dataSetName, documentTitle ) );

            sheet.addCell( new Label( MARGIN_LEFT, 3, i18n.getString( "district_health_information_software" ) + " - "
                + DateUtils.getMediumDateString(), subTitle ) );

            int row = 5;

            sheet.addCell( new Label( MARGIN_LEFT, row, i18n.getString( "name" ), columnHeader ) );
            sheet.addCell( new Label( MARGIN_LEFT + 1, row, i18n.getString( "actual" ), columnHeader ) );
            sheet.addCell( new Label( MARGIN_LEFT + 2, row, i18n.getString( "target" ), columnHeader ) );
            sheet.addCell( new Label( MARGIN_LEFT + 3, row, i18n.getString( "percent" ), columnHeader ) );
            sheet.addCell( new Label( MARGIN_LEFT + 4, row, i18n.getString( "on_time" ), columnHeader ) );
            sheet.addCell( new Label( MARGIN_LEFT + 5, row, i18n.getString( "percent" ), columnHeader ) );

            row = 7;

            if ( results != null )
            {
                for ( DataSetCompletenessResult result : results )
                {
                    sheet.addCell( new Label( MARGIN_LEFT, row, result.getName(), text ) );
                    sheet.addCell( new Number( MARGIN_LEFT + 1, row, result.getRegistrations(), text ) );
                    sheet.addCell( new Number( MARGIN_LEFT + 2, row, result.getSources(), text ) );
                    sheet.addCell( new Number( MARGIN_LEFT + 3, row, result.getPercentage(), text ) );
                    sheet.addCell( new Number( MARGIN_LEFT + 4, row, result.getRegistrationsOnTime(), text ) );
                    sheet.addCell( new Number( MARGIN_LEFT + 5, row, result.getPercentageOnTime(), text ) );

                    row++;
                }
            }

            workbook.write();

            workbook.close();
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( "Failed to create workbook", ex );
        }
        catch ( RowsExceededException ex )
        {
            throw new RuntimeException( "Rows exceeded", ex );
        }
        catch ( WriteException ex )
        {
            throw new RuntimeException( "Write failed", ex );
        }
    }

    public void writeValidationResult( List<ValidationResult> results, OutputStream out, I18n i18n, I18nFormat format )
    {
        final int MARGIN_LEFT = 1;

        WritableCellFormat documentTitle = new WritableCellFormat( new WritableFont( WritableFont.TAHOMA, 15,
            WritableFont.NO_BOLD, false ) );
        WritableCellFormat subTitle = new WritableCellFormat( new WritableFont( WritableFont.TAHOMA, 13,
            WritableFont.NO_BOLD, false ) );
        WritableCellFormat columnHeader = new WritableCellFormat( new WritableFont( WritableFont.TAHOMA, 11,
            WritableFont.NO_BOLD, true ) );
        WritableCellFormat text = new WritableCellFormat( new WritableFont( WritableFont.ARIAL, 11,
            WritableFont.NO_BOLD, false ) );

        try
        {
            WritableWorkbook workbook = Workbook.createWorkbook( out );

            WritableSheet sheet = workbook.createSheet( "Validation results", 0 );

            sheet.addCell( new Label( MARGIN_LEFT, 1, i18n.getString( "data_quality_report" ), documentTitle ) );

            sheet.addCell( new Label( MARGIN_LEFT, 3, i18n.getString( "district_health_information_software" ) + " - "
                + DateUtils.getMediumDateString(), subTitle ) );

            int row = 5;

            sheet.addCell( new Label( MARGIN_LEFT + 0, row, i18n.getString( "source" ), columnHeader ) );
            sheet.addCell( new Label( MARGIN_LEFT + 1, row, i18n.getString( "period" ), columnHeader ) );
            sheet.addCell( new Label( MARGIN_LEFT + 2, row, i18n.getString( "left_side_description" ), columnHeader ) );
            sheet.addCell( new Label( MARGIN_LEFT + 3, row, i18n.getString( "value" ), columnHeader ) );
            sheet.addCell( new Label( MARGIN_LEFT + 4, row, i18n.getString( "operator" ), columnHeader ) );
            sheet.addCell( new Label( MARGIN_LEFT + 5, row, i18n.getString( "value" ), columnHeader ) );
            sheet.addCell( new Label( MARGIN_LEFT + 6, row, i18n.getString( "right_side_description" ), columnHeader ) );

            row = 7;

            if ( results != null )
            {
                for ( ValidationResult result : results )
                {
                    OrganisationUnit unit = (OrganisationUnit) result.getSource();

                    Period period = result.getPeriod();

                    sheet.addCell( new Label( MARGIN_LEFT + 0, row, unit.getName(), text ) );
                    sheet.addCell( new Label( MARGIN_LEFT + 1, row, format.formatPeriod( period ), text ) );
                    sheet.addCell( new Label( MARGIN_LEFT + 2, row, result.getValidationRule().getLeftSide()
                        .getDescription(), text ) );
                    sheet.addCell( new Number( MARGIN_LEFT + 3, row, result.getLeftsideValue(), text ) );
                    sheet.addCell( new Label( MARGIN_LEFT + 4, row, i18n.getString( result.getValidationRule()
                        .getOperator() ), text ) );
                    sheet.addCell( new Number( MARGIN_LEFT + 5, row, result.getRightsideValue(), text ) );
                    sheet.addCell( new Label( MARGIN_LEFT + 6, row, result.getValidationRule().getRightSide()
                        .getDescription(), text ) );

                    row++;
                }

                workbook.write();

                workbook.close();

            }
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( "Failed to create workbook", ex );
        }
        catch ( RowsExceededException ex )
        {
            throw new RuntimeException( "Rows exceeded", ex );
        }
        catch ( WriteException ex )
        {
            throw new RuntimeException( "Write failed", ex );
        }
    }
}
