package org.hisp.dhis.workbook.impl;

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

import static org.hisp.dhis.system.util.MathUtils.isNumeric;

import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.hisp.dhis.completeness.DataSetCompletenessResult;
import org.hisp.dhis.completeness.DataSetCompletenessService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.reporttable.ReportTableData;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.workbook.WorkbookService;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
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

    private DataSetCompletenessService completenessService;

    public void setCompletenessService( DataSetCompletenessService completenessService )
    {
        this.completenessService = completenessService;
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
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private static final WritableCellFormat FORMAT_TTTLE = new WritableCellFormat( new WritableFont( WritableFont.TAHOMA, 13, WritableFont.NO_BOLD, false ) );
    private static final WritableCellFormat FORMAT_LABEL = new WritableCellFormat( new WritableFont( WritableFont.ARIAL, 11, WritableFont.NO_BOLD, true ) );   
    private static final WritableCellFormat FORMAT_TEXT = new WritableCellFormat( new WritableFont( WritableFont.ARIAL, 11, WritableFont.NO_BOLD, false ) );
    
    // -------------------------------------------------------------------------
    // WorkbookService implementation
    // -------------------------------------------------------------------------

    public void writeReportTableData( OutputStream outputStream, int id )
    {
        ReportTableData data = reportTableService.getReportTableData( id );
        
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
    }
    
    public void writeDataSetCompleteness( OutputStream outputStream, int periodId, int organisationUnitId )
    {
        Collection<DataSetCompletenessResult> results = completenessService.getDataSetCompleteness( periodId, organisationUnitId );
        
        try
        {
            WritableWorkbook workbook = Workbook.createWorkbook( outputStream );
            
            WritableSheet sheet = workbook.createSheet( "Report table data", 0 );
            
            int rowNumber = 1;

            sheet.addCell( new Label( 0, rowNumber++, "Data completeness", FORMAT_TTTLE ) );
            
            rowNumber++;
            
            sheet.addCell( new Label( 0, rowNumber, "Name", FORMAT_LABEL ) );
            sheet.addCell( new Label( 1, rowNumber, "Target", FORMAT_LABEL ) );
            sheet.addCell( new Label( 2, rowNumber, "Actual", FORMAT_LABEL ) );
            sheet.addCell( new Label( 3, rowNumber, "Percentage", FORMAT_LABEL ) );
            sheet.addCell( new Label( 4, rowNumber, "On time", FORMAT_LABEL ) );
            sheet.addCell( new Label( 5, rowNumber, "Percentage", FORMAT_LABEL ) );

            rowNumber++;
            
            for ( DataSetCompletenessResult result : results )
            {
                sheet.addCell( new Label( 0, rowNumber, result.getName(), FORMAT_TEXT ) );
                sheet.addCell( new Number( 1, rowNumber, result.getSources(), FORMAT_TEXT ) );
                sheet.addCell( new Number( 2, rowNumber, result.getRegistrations(), FORMAT_TEXT ) );
                sheet.addCell( new Number( 3, rowNumber, result.getPercentage(), FORMAT_TEXT ) );
                sheet.addCell( new Number( 4, rowNumber, result.getRegistrationsOnTime(), FORMAT_TEXT ) );
                sheet.addCell( new Number( 5, rowNumber, result.getPercentageOnTime(), FORMAT_TEXT ) );
                
                rowNumber++;
            }
            
            workbook.write();
            
            workbook.close();
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to generate workbook for data elements", ex );
        }
    }

    public void writeAllDataElements( OutputStream outputStream )
    {
        try
        {
            WritableWorkbook workbook = Workbook.createWorkbook( outputStream );
            
            WritableSheet sheet = workbook.createSheet( "Data elements", 0 );
            
            int rowNumber = 1;
            
            for ( DataElement element : dataElementService.getAllDataElements() )
            {            
                sheet.addCell( new Label( 0, rowNumber++, element.getName(), FORMAT_TTTLE ) );
                
                rowNumber++;
                
                sheet.addCell( new Label( 0, rowNumber, "Alternative name", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, element.getAlternativeName(), FORMAT_TEXT ) );

                sheet.addCell( new Label( 0, rowNumber, "Short name", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, element.getShortName(), FORMAT_TEXT ) );

                sheet.addCell( new Label( 0, rowNumber, "Code", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, element.getCode(), FORMAT_TEXT ) );

                sheet.addCell( new Label( 0, rowNumber, "Description", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, element.getDescription(), FORMAT_TEXT ) );

                sheet.addCell( new Label( 0, rowNumber, "Active", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, getBoolean().get( element.isActive() ), FORMAT_TEXT ) );

                sheet.addCell( new Label( 0, rowNumber, "Type", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, getType().get( element.getType() ), FORMAT_TEXT ) );

                sheet.addCell( new Label( 0, rowNumber, "Aggregation operator", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, getAggregationOperator().get( element.getAggregationOperator() ), FORMAT_TEXT ) );
                
                rowNumber++;
            }

            workbook.write();
            
            workbook.close();
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to generate workbook for data elements", ex );
        }
    }

    public void writeAllIndicators( OutputStream outputStream )
    {
        try
        {
            WritableWorkbook workbook = Workbook.createWorkbook( outputStream );
            
            WritableSheet sheet = workbook.createSheet( "Indicators", 0 );
            
            int rowNumber = 1;
            
            for ( Indicator indicator : indicatorService.getAllIndicators() )
            {            
                sheet.addCell( new Label( 0, rowNumber++, indicator.getName(), FORMAT_TTTLE ) );
                
                rowNumber++;
                
                sheet.addCell( new Label( 0, rowNumber, "Alternative name", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, indicator.getAlternativeName(), FORMAT_TEXT ) );

                sheet.addCell( new Label( 0, rowNumber, "Short name", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, indicator.getShortName(), FORMAT_TEXT ) );

                sheet.addCell( new Label( 0, rowNumber, "Code", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, indicator.getCode(), FORMAT_TEXT ) );

                sheet.addCell( new Label( 0, rowNumber, "Description", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, indicator.getDescription(), FORMAT_TEXT ) );

                sheet.addCell( new Label( 0, rowNumber, "Annualized", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, getBoolean().get( indicator.getAnnualized() ), FORMAT_TEXT ) );

                sheet.addCell( new Label( 0, rowNumber, "Indicator type", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, indicator.getIndicatorType().getName(), FORMAT_TEXT ) );
                
                sheet.addCell( new Label( 0, rowNumber, "Numerator description", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, indicator.getNumeratorDescription(), FORMAT_TEXT ) );

                sheet.addCell( new Label( 0, rowNumber, "Denominator description", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, indicator.getDenominatorDescription(), FORMAT_TEXT ) );
                
                rowNumber++;
            }

            workbook.write();
            
            workbook.close();
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to generate workbook for indicators", ex );
        }
    }

    public void writeAllOrganisationUnits( OutputStream outputStream )
    {
        try
        {
            WritableWorkbook workbook = Workbook.createWorkbook( outputStream );
            
            WritableSheet sheet = workbook.createSheet( "Organisation units", 0 );
            
            int rowNumber = 1;
            
            for ( OrganisationUnit unit : organisationUnitService.getAllOrganisationUnits() )
            {
                sheet.addCell( new Label( 0, rowNumber++, unit.getName(), FORMAT_TTTLE ) );
                
                rowNumber++;
                
                sheet.addCell( new Label( 0, rowNumber, "Short name", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, unit.getShortName(), FORMAT_TEXT ) );

                sheet.addCell( new Label( 0, rowNumber, "Code", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, unit.getCode(), FORMAT_TEXT ) );

                sheet.addCell( new Label( 0, rowNumber, "Active", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, getBoolean().get( unit.isActive() ), FORMAT_TEXT ) );

                sheet.addCell( new Label( 0, rowNumber, "Comment", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, unit.getComment(), FORMAT_TEXT ) );

                sheet.addCell( new Label( 0, rowNumber, "Geo code", FORMAT_LABEL ) );
                sheet.addCell( new Label( 1, rowNumber++, unit.getGeoCode(), FORMAT_TEXT ) );
                
                rowNumber++;
            }

            workbook.write();
            
            workbook.close();
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to generate workbook for organisation units", ex );
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Map<Boolean, String> getBoolean()
    {
        Map<Boolean, String> map = new HashMap<Boolean, String>();
        map.put( true, "Yes" );
        map.put( false, "No" );
        return map;
    }
    
    private Map<String, String> getType()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put( DataElement.TYPE_STRING, "Text" );
        map.put( DataElement.TYPE_INT, "Number" );
        map.put( DataElement.TYPE_BOOL, "Yes/No" );
        return map;
    }
    
    private Map<String, String> getAggregationOperator()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put( DataElement.AGGREGATION_OPERATOR_SUM, "Sum" );
        map.put( DataElement.AGGREGATION_OPERATOR_AVERAGE, "Average" );
        map.put( DataElement.AGGREGATION_OPERATOR_COUNT, "Count" );
        return map;
    }
}
