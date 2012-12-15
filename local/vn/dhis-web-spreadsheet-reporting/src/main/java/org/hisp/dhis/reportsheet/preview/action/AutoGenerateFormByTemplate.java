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

import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.io.FilenameUtils.getName;
import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_SUM;
import static org.hisp.dhis.dataelement.DataElement.DOMAIN_TYPE_AGGREGATE;
import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_INT;
import static org.hisp.dhis.reportsheet.ExportItem.PERIODTYPE.SELECTED_MONTH;
import static org.hisp.dhis.reportsheet.ExportItem.TYPE.DATAELEMENT;
import static org.hisp.dhis.reportsheet.preview.action.HtmlHelper.FOREGROUND_COLOR;
import static org.hisp.dhis.reportsheet.preview.action.HtmlHelper.TEXT_COLOR;
import static org.hisp.dhis.reportsheet.utils.ExcelUtils.convertAlignmentString;
import static org.hisp.dhis.reportsheet.utils.ExcelUtils.readValueByPOI;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reportsheet.ExportItem;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportNormal;
import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.importitem.ImportItem;
import org.hisp.dhis.reportsheet.state.SelectionManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * Simple demo class which uses the api to present the contents of an excel 97
 * spreadsheet as an XML document, using a workbook and output stream of your
 * choice
 * 
 * @author Dang Duy Hieu
 * @author Chau Thu Tran
 * @version $Id$
 */

public class AutoGenerateFormByTemplate
    implements Action
{
    private static final String REPORT_EXCEL_GROUP = "BAO CAO THONG KE";

    private static DataElementCategoryOptionCombo optionCombo = null;

    private String excelFileName = "";

    private String commonName = "";

    private static final String WORKBOOK_OPENTAG = "<workbook>";

    private static final String WORKBOOK_CLOSETAG = "</workbook>";

    private static final String MERGEDCELL_OPENTAG = "<MergedCells>";

    private static final String MERGEDCELL_CLOSETAG = "</MergedCells>";

    /**
     * The workbook we are reading from a given file
     */
    protected Workbook WORKBOOK;

    protected FormulaEvaluator evaluatorFormula;

    protected HtmlHelper htmlHelper;

    /**
     * The encoding to write
     */
    protected StringBuffer xml = new StringBuffer();

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    @Autowired
    protected SelectionManager selectionManager;

    // -------------------------------------------------------------------------
    // Input && Output
    // -------------------------------------------------------------------------

    private String xmlStructureResponse;

    public String getXmlStructureResponse()
    {
        return xmlStructureResponse;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        cleanUp();

        Set<Integer> collectSheets = new HashSet<Integer>();
        collectSheets.add( 1 );

        try
        {
            String pathFileName = selectionManager.getUploadFilePath();

            excelFileName = getName( pathFileName );
            commonName = getBaseName( pathFileName );

            if ( getExtension( pathFileName ).equals( "xls" ) )
            {
                WORKBOOK = new HSSFWorkbook( new FileInputStream( pathFileName ) );
                htmlHelper = new HSSFHtmlHelper( (HSSFWorkbook) WORKBOOK );
            }
            else
            {
                WORKBOOK = new XSSFWorkbook( new FileInputStream( pathFileName ) );
                htmlHelper = new XSSFHtmlHelper();
            }

            optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
            evaluatorFormula = WORKBOOK.getCreationHelper().createFormulaEvaluator();

            printXML( collectSheets, null );

            xmlStructureResponse = xml.toString();

            xml = null;
        }
        catch ( Exception e )
        {
            e.printStackTrace();

            cleanUp();
        }

        return SUCCESS;
    }

    private void printXML( Collection<Integer> collectSheets, List<ImportItem> importItems )
        throws Exception
    {
        printMergedInfo( collectSheets );

        xml.append( WORKBOOK_OPENTAG );

        for ( Integer sheet : collectSheets )
        {
            printData( sheet, importItems );
        }

        xml.append( WORKBOOK_CLOSETAG );
    }

    private void printData( int sheetNo, List<ImportItem> importItems )
    {
        PeriodType periodType = PeriodType.getPeriodTypeByName( MonthlyPeriodType.NAME );

        // Create new DataSet
        DataSet dataSet = new DataSet( commonName, commonName, periodType );

        // Create new ExportReport
        ExportReport exportReport = new ExportReportNormal( commonName, REPORT_EXCEL_GROUP, excelFileName, null );
        exportReportService.addExportReport( exportReport );

        Sheet s = WORKBOOK.getSheetAt( sheetNo - 1 );

        xml.append( "<sheet id='" + (sheetNo) + "'>" );
        xml.append( "<name><![CDATA[" + s.getSheetName() + "]]></name>" );

        try
        {
            for ( Row row : s )
            {
                xml.append( "<row index='" + row.getRowNum() + "'>" );

                Map<Integer, Integer> idxMap = new HashMap<Integer, Integer>();

                for ( Cell cell : row )
                {
                    Comment cmt = cell.getCellComment();
                    int rowIndex = cell.getRowIndex();
                    int colIndex = cell.getColumnIndex();

                    if ( cmt != null )
                    {
                        idxMap.clear();
                        String deName = cell.getStringCellValue();

                        String[] indexes = cmt.getString().toString().split( "," );

                        for ( String index : indexes )
                        {
                            String name = deName + " (" + index + ")";
                            int idx = Integer.parseInt( index );

                            // Generate DataElement
                            DataElement dataElement = new DataElement( name );
                            /** TAKE CARE OF SHORT_NAME IS TOO LONG */
                            dataElement.setShortName( name );
                            dataElement.setActive( true );
                            dataElement.setZeroIsSignificant( false );
                            dataElement.setDomainType( DOMAIN_TYPE_AGGREGATE );
                            dataElement.setType( VALUE_TYPE_INT );
                            dataElement.setNumberType( VALUE_TYPE_INT );
                            dataElement.setAggregationOperator( AGGREGATION_OPERATOR_SUM );
                            dataElement.setCategoryCombo( optionCombo.getCategoryCombo() );

                            int deId = dataElementService.addDataElement( dataElement );

                            idxMap.put( idx - 1, deId );

                            // Add the dataElement into the dataSet
                            dataSet.addDataElement( dataElement );

                            // Generate Report Item
                            ExportItem exportItem = new ExportItem();
                            exportItem.setName( name );
                            exportItem.setItemType( DATAELEMENT );
                            exportItem.setRow( rowIndex + 1 );
                            exportItem.setColumn( idx );
                            exportItem.setExpression( "[" + deId + "." + optionCombo.getId() + "]" );
                            exportItem.setPeriodType( SELECTED_MONTH );
                            exportItem.setSheetNo( sheetNo );
                            exportItem.setExportReport( exportReport );

                            exportReportService.addExportItem( exportItem );
                        }
                    }

                    if ( idxMap.containsKey( colIndex ) )
                    {
                        xml.append( "<col no='" + colIndex + "'>" );

                        xml.append( "<data><![CDATA[" + "<input id=\"" + idxMap.get( colIndex ) + "-"
                            + optionCombo.getId() + "-val\" style=\"width:7em;text-align:center\" value=\"\" title=\"\" />]]></data>" );

                        printFormatInfo( s, cell );

                        xml.append( "</col>" );
                    }
                    else if ( (cell.getCellStyle() != null || cell.getCellType() != Cell.CELL_TYPE_BLANK)
                        && !s.isColumnHidden( colIndex ) )
                    {
                        xml.append( "<col no='" + colIndex + "'>" );

                        xml.append( "<data><![CDATA["
                            + readValueByPOI( row.getRowNum() + 1, colIndex + 1, s, evaluatorFormula ) + "]]></data>" );

                        printFormatInfo( s, cell );

                        xml.append( "</col>" );
                    }
                }
                xml.append( "</row>" );
            }
            xml.append( "</sheet>" );

            // Update DataSet
            DataEntryForm dataEntryForm = new DataEntryForm( commonName, "<p></p>" );
            dataEntryFormService.addDataEntryForm( dataEntryForm );

            dataSet.setDataEntryForm( dataEntryForm );
            int dataSetId = dataSetService.addDataSet( dataSet );

            // Update ExportReport
            Set<DataSet> dataSets = new HashSet<DataSet>();
            dataSets.add( dataSet );

            exportReport.setDataSets( dataSets );
            exportReportService.updateExportReport( exportReport );

            xml.append( "<ds id='" + dataSetId + "' n='" + commonName + "'/>" );

        }
        catch ( Exception e )
        {
            cleanUp();

            e.printStackTrace();
        }
    }

    /**
     * Writes out the WORKBOOK data as XML, with formatting information
     * 
     * @throws Exception
     */

    private void cleanUp()
    {
        System.gc();
    }

    private void printFormatInfo( Sheet sheet, Cell objCell )
    {
        // The format information
        CellStyle format = objCell.getCellStyle();

        if ( format != null )
        {
            xml.append( "<format a='" + convertAlignmentString( format.getAlignment() ) + "'" );
            xml.append( " w='" + sheet.getColumnWidth( objCell.getColumnIndex() ) + "'" );
            xml.append( " b='"
                + (format.getBorderBottom() + format.getBorderLeft() + format.getBorderRight() + format.getBorderTop())
                + "'" );

            Font font = WORKBOOK.getFontAt( format.getFontIndex() );

            if ( font != null )
            {
                xml.append( "><font s='" + font.getFontHeightInPoints() + "'" );
                xml.append( " b='" + (font.getBoldweight() == Font.BOLDWEIGHT_BOLD ? "1" : "0") + "'" );
                xml.append( " i='" + font.getItalic() + "'" );
                xml.append( " c='" + htmlHelper.colorStyle( TEXT_COLOR, format ) + "'/>" );

                // The cell background information
                xml.append( "<bg c='" + htmlHelper.colorStyle( FOREGROUND_COLOR, format ) + "'/>" );
                xml.append( "</format>" );
            }
            else
            {
                xml.append( "/>" );
            }
        }
    }

    // -------------------------------------------------------------------------
    // Get the merged cell's information
    // -------------------------------------------------------------------------

    private void printMergedInfo( Collection<Integer> collectSheets )
        throws IOException
    {
        // Open the main Tag //
        xml.append( MERGEDCELL_OPENTAG );

        for ( Integer sheet : collectSheets )
        {
            printMergedInfoBySheetNo( sheet );
        }

        // Close the main Tag //
        xml.append( MERGEDCELL_CLOSETAG );
    }

    private void printMergedInfoBySheetNo( int sheetNo )
    {
        Sheet sheet = WORKBOOK.getSheetAt( sheetNo - 1 );
        CellRangeAddress cellRangeAddress = null;

        for ( int i = 0; i < sheet.getNumMergedRegions(); i++ )
        {
            cellRangeAddress = sheet.getMergedRegion( i );

            if ( cellRangeAddress.getFirstColumn() != cellRangeAddress.getLastColumn() )
            {
                xml.append( "<cell " + "iKey='" + (sheetNo) + "#" + cellRangeAddress.getFirstRow() + "#"
                    + cellRangeAddress.getFirstColumn() + "'>"
                    + (cellRangeAddress.getLastColumn() - cellRangeAddress.getFirstColumn() + 1) + "</cell>" );
            }
        }
    }
}