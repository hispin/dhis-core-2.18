package org.hisp.dhis.reportsheet.preview.action;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import static org.apache.commons.io.FilenameUtils.getExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reportsheet.ExportItem;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportNormal;
import org.hisp.dhis.reportsheet.ExportReportService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * Simple demo class which uses the api to present the contents of an excel 97
 * spreadsheet as an XML document, using a workbook and output stream of your
 * choice
 * 
 * @author Dang Duy Hieu
 * @version $Id$
 */

public class AutoGenerateFormByTemplate implements Action
{
    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private DataElementCategoryService categoryService;

    @Autowired
     private DataEntryFormService dataEntryFormService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private ExportReportService exportReportService;

    /**
     * The workbook we are reading from a given file
     */
    private Workbook WORKBOOK;

    public String execute()
    {
        Set<Integer> collectSheets = new HashSet<Integer>();
        collectSheets.add( 1 );

        try
        {
            autoGenerateFormByTemplate( "d:\\template_file.xls", collectSheets );
        }
        catch ( Exception e )
        {
            System.out.println( e.getMessage() );
        }

        return SUCCESS;
    }
    
    // -------------------------------------------------------------------------
    // Get & Set methods
    // -------------------------------------------------------------------------
    
    private void cleanUpForResponse()
    {
        System.gc();
    }

    /**
     * Constructor
     * 
     * @param w The workbook to interrogate
     * @param enc The encoding used by the output stream. Null or unrecognized
     *        values cause the encoding to default to UTF8
     * @param f Indicates whether the generated XML document should contain the
     *        cell format information
     * @exception java.io.IOException
     */

    private String excelFileName = "";

    private void autoGenerateFormByTemplate( String pathFileName, Set<Integer> collectSheets )
        throws Exception
    {
        this.cleanUpForResponse();

        InputStream inputSteam = new FileInputStream( pathFileName );
        excelFileName = new File( pathFileName ).getName();

        if ( getExtension( pathFileName ).equals( "xls" ) )
        {
            this.WORKBOOK = new HSSFWorkbook( inputSteam );
        }
        else
        {
            this.WORKBOOK = new XSSFWorkbook( inputSteam );
        }

        writeFormattedXML( collectSheets );
    }

    /**
     * Writes out the WORKBOOK data as XML, with formatting information
     * 
     * @param bDetailed
     * 
     * @throws Exception
     */

    private void writeFormattedXML( Collection<Integer> collectSheets )
        throws Exception
    {
        for ( Integer sheet : collectSheets )
        {
            createFormByComment( sheet );
        }
    }

    // -------------------------------------------------------------------------
    // Sub-methods
    // -------------------------------------------------------------------------

    private void createFormByComment( int sheetNo )
    {
        DataElementCategoryCombo catagoryCombo = categoryService.getDefaultDataElementCategoryOptionCombo()
            .getCategoryCombo();
        int catagoryOptionComboId = categoryService.getDefaultDataElementCategoryOptionCombo().getId();
        PeriodType periodType = PeriodType.getPeriodTypeByName( "Monthly" );
        StringBuffer htmlCode = new StringBuffer();
        Sheet s = WORKBOOK.getSheetAt( sheetNo - 1 );

        DataSet dataSet = new DataSet( excelFileName, excelFileName, periodType );

        // Generate report
        ExportReport exportReport = new ExportReportNormal();
        exportReport.setName( WORKBOOK.getSheetName( sheetNo - 1 ) );
        exportReport.setExcelTemplateFile( excelFileName );
        exportReport.setGroup( excelFileName );
        exportReport.setCreatedBy( "DHIS-System" );
        int reportId = exportReportService.addExportReport( exportReport );

        try
        {
            for ( Row row : s )
            {
                for ( Cell cell : row )
                {
                    Comment cmt = cell.getCellComment();
                    if ( cell.getCellComment() != null )
                    {
                        String deName = cell.getStringCellValue();
                        String[] indexes = cmt.getString().toString().split( "," );
                        int rowIndex = cell.getRowIndex();

                        for ( String index : indexes )
                        {
                            String name = deName + "(" + index + ")";
                            int idx = Integer.parseInt( index ) - 1;
                            
                            // Generate dataElement
                            DataElement dataElement = new DataElement( name );
                            dataElement.setShortName( name );
                            dataElement.setActive( true );
                            dataElement.setDomainType( "aggregate" );
                            dataElement.setType( DataElement.VALUE_TYPE_INT );
                            dataElement.setNumberType( DataElement.VALUE_TYPE_INT );
                            dataElement.setCategoryCombo( catagoryCombo );
                            dataElement.setAggregationOperator( "sum" );
                            dataElement.setZeroIsSignificant( false );
                            int deId = dataElementService.addDataElement( dataElement );

                            // Add the dataelement into the dataset
                            dataSet.addDataElement( dataElement );

                            // Put text field into the cell(rowIndex,idx)
                            // htmlCode.append( str );

                            // Generate report item
                            ExportItem exportItem = new ExportItem();
                            exportItem.setName( name );
                            exportItem.setItemType( "dataelement" );
                            exportItem.setRow( rowIndex );
                            exportItem.setColumn( idx );
                            exportItem.setExpression( "[" + deId + "." + catagoryOptionComboId + "]" );
                            exportItem.setPeriodType( "selected_month" );
                            exportItem.setSheetNo( (sheetNo) );
                            exportItem.setExportReport( exportReportService.getExportReport( reportId ) );
                            exportReportService.addExportItem( exportItem );

                        }
                    }
                }
            }

            DataEntryForm dataEntryForm = new DataEntryForm( "DataEntry form", htmlCode.toString() );
            dataEntryFormService.addDataEntryForm( dataEntryForm );

            dataSet.setDataEntryForm( dataEntryForm );
            dataSetService.addDataSet( dataSet );

            Set<DataSet> dataSets = new HashSet<DataSet>();
            dataSets.add( dataSet );
            
            exportReport.setDataSets( dataSets );
            exportReportService.updateExportReport( exportReport );

        }
        catch ( Exception e )
        {
            // Catch exception if any
            System.err.println( "Error: " + e.getMessage() );
        }
    }
}