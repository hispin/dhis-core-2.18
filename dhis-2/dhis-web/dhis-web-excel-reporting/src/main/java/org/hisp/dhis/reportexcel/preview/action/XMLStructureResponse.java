package org.hisp.dhis.reportexcel.preview.action;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import jxl.Cell;
import jxl.CellType;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.hisp.dhis.reportexcel.utils.StringUtils;

/**
 * Simple demo class which uses the api to present the contents of an excel 97
 * spreadsheet as an XML document, using a workbook and output stream of your
 * choice
 * 
 * @author Dang Duy Hieu
 * @version $Id$
 */

public class XMLStructureResponse
{
    /**
     * The encoding to write
     */
    private StringBuffer STRUCTURE_DATA_RESPONSE;

    /**
     * The encoding to write
     */
    private String ENCODING;

    /**
     * The workbook we are reading from
     */
    private Workbook WORKBOOK;

    private String PATH_FILE_NAME;

    private boolean bWRITE_VERSION;

    private boolean bWRITE_DTD;

    private static final String PREFIX_VERSION_XML = "<?xml version=\"1.0\"  encoding=\"UTF-8\"?>";

    private static final String DOCTYPE_NORMAL = "<!DOCTYPE WORKBOOK SYSTEM \"WORKBOOK.dtd\">";

    private static final String DOCTYPE_FORMAT = "<!DOCTYPE WORKBOOK SYSTEM \"formatWORKBOOK.dtd\">";

    private static final String WORKBOOK_OPENTAG = "<workbook>";

    private static final String WORKBOOK_CLOSETAG = "</workbook>";

    private static final String MERGEDCELL_OPENTAG = "<MergedCells>";

    private static final String MERGEDCELL_CLOSETAG = "</MergedCells>";

    private static final String PRINT_END_LINE = "\n";

    // ------------------------------------------------
    // Get & Set methods //
    // ------------------------------------------------
    public String getSTRUCTURE_DATA_RESPONSE()
    {
        return STRUCTURE_DATA_RESPONSE.toString();
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

    public XMLStructureResponse()
    {
    }

    public XMLStructureResponse( String pathFileName, String enc, int sheetId, boolean bFormat, boolean bDetailed,
        boolean bWriteDescription, boolean bWriteVersion, boolean bWriteDTD )

        throws Exception
    {
        this.ENCODING = enc;
        this.bWRITE_DTD = bWriteDTD;
        this.bWRITE_VERSION = bWriteVersion;
        this.PATH_FILE_NAME = pathFileName;
        this.WORKBOOK = Workbook.getWorkbook( new File( pathFileName ) );
        this.STRUCTURE_DATA_RESPONSE = new StringBuffer();

        if ( this.ENCODING == null || !this.ENCODING.equals( "UnicodeBig" ) )
        {
            this.ENCODING = "UTF8";
        }

        if ( bFormat )
        {
            writeFormattedXML( sheetId, bDetailed, bWriteDescription );
        }
        else
        {
            writeXML();
        }
    }

    /**
     * Writes out the WORKBOOK data as XML, without formatting information
     */
    private void writeXML()
        throws IOException
    {
        if ( this.bWRITE_VERSION )
        {
            STRUCTURE_DATA_RESPONSE.append( PREFIX_VERSION_XML );
            STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
        }

        if ( this.bWRITE_DTD )
        {
            STRUCTURE_DATA_RESPONSE.append( DOCTYPE_NORMAL );
            STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
        }

        STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
        STRUCTURE_DATA_RESPONSE.append( WORKBOOK_OPENTAG );
        STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );

        for ( int sheet = 0; sheet < WORKBOOK.getNumberOfSheets(); sheet++ )
        {
            Sheet s = WORKBOOK.getSheet( sheet );

            STRUCTURE_DATA_RESPONSE.append( "  <sheet>" );
            STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
            STRUCTURE_DATA_RESPONSE.append( "    <name><![CDATA[" + s.getName() + "]]></name>" );
            STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );

            Cell[] row = null;

            for ( int i = 0; i < s.getRows(); i++ )
            {
                STRUCTURE_DATA_RESPONSE.append( "    <row number=\"" + i + "\">" );
                STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
                row = s.getRow( i );

                for ( int j = 0; j < row.length; j++ )
                {
                    if ( row[j].getType() != CellType.EMPTY )
                    {
                        STRUCTURE_DATA_RESPONSE.append( "      <col number=\"" + j + "\">" );
                        STRUCTURE_DATA_RESPONSE.append( "<![CDATA[" + row[j].getContents() + "]]>" );
                        STRUCTURE_DATA_RESPONSE.append( "</col>" );
                        STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
                    }
                }
                STRUCTURE_DATA_RESPONSE.append( "    </row>" );
                STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
            }
            STRUCTURE_DATA_RESPONSE.append( "  </sheet>" );
            STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
        }
        STRUCTURE_DATA_RESPONSE.append( WORKBOOK_CLOSETAG );
        STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
    }

    /**
     * Writes out the WORKBOOK data as XML, with formatting information
     * 
     * @param bDetailed
     * 
     * @throws Exception
     */

    private void writeFormattedXML( int sheetId, boolean bDetailed, boolean bWriteDescription )
        throws Exception
    {
        FileInputStream fis = new FileInputStream( this.PATH_FILE_NAME );
        HSSFWorkbook hssfwb = new HSSFWorkbook( fis );

        if ( bWriteDescription )
        {
            this.writeXMLDescription( sheetId );
        }

        if ( this.bWRITE_VERSION )
        {
            STRUCTURE_DATA_RESPONSE.append( PREFIX_VERSION_XML );
            STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
        }

        if ( this.bWRITE_DTD )
        {
            STRUCTURE_DATA_RESPONSE.append( DOCTYPE_FORMAT );
            STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
        }

        STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
        STRUCTURE_DATA_RESPONSE.append( WORKBOOK_OPENTAG );
        STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );

        if ( sheetId > 0 )
        {
            writeBySheetNo( hssfwb, (sheetId - 1), bDetailed );
        }
        else
        {
            for ( int sheet = 0; sheet < WORKBOOK.getNumberOfSheets(); sheet++ )
            {
                writeBySheetNo( hssfwb, sheet, bDetailed );
            }
        }

        STRUCTURE_DATA_RESPONSE.append( WORKBOOK_CLOSETAG );
        STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
    }

    // -------------------------------------------------------------------------
    // Sub-methods
    // -------------------------------------------------------------------------

    private void writeBySheetNo( org.apache.poi.ss.usermodel.Workbook wb, int sheetNo, boolean bDetailed )
    {
        Sheet s = WORKBOOK.getSheet( sheetNo );

        STRUCTURE_DATA_RESPONSE.append( "  <sheet>" );
        STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
        STRUCTURE_DATA_RESPONSE.append( "    <name><![CDATA[" + s.getName() + "]]></name>" );
        STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );

        Cell[] cell = null;
        CellFormat format = null;
        boolean bFormula = false;
        String recalculatedValue = "";

        org.apache.poi.ss.usermodel.Sheet sheet = wb.getSheetAt( sheetNo );
        org.apache.poi.ss.usermodel.FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        for ( int i = 0; i < s.getRows(); i++ )
        {
            STRUCTURE_DATA_RESPONSE.append( "    <row index=\"" + i + "\">" );
            STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );

            cell = s.getRow( i );

            for ( int j = 0; j < cell.length; j++ )
            {
                // Remember that empty cells can contain format
                // information
                if ( (cell[j].getType() != CellType.EMPTY) || (cell[j].getCellFormat() != null) )
                {
                    bFormula = false;

                    CellReference cellReference = new CellReference( i, j );
                    org.apache.poi.ss.usermodel.Row rowRef = sheet.getRow( cellReference.getRow() );
                    org.apache.poi.ss.usermodel.Cell cellRef = rowRef.getCell( cellReference.getCol() );

                    if ( (cellRef != null)
                        && (cellRef.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA) )
                    {
                        bFormula = true;
                        recalculatedValue = "";

                        // CELL_TYPE_NUMERIC if this cell is an
                        // NumbericCell
                        switch ( evaluator.evaluateInCell( cellRef ).getCellType() )
                        {
                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:

                            recalculatedValue = String.valueOf( cellRef.getNumericCellValue() );

                            break;

                        // CELL_TYPE_FORMULA will never occur
                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA:
                            System.out.println( "Place of cell :: [" + cellRef.getRowIndex() + "]["
                                + cellRef.getColumnIndex() + "]" );
                            break;

                        // CELL_TYPE_ERROR if this cell is an ErrorCell
                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_ERROR:

                            recalculatedValue = cell[j].getContents();
                            break;
                        }
                    }

                    // end of checking the cell formula
                    STRUCTURE_DATA_RESPONSE.append( "      <col no=\"" + j + "\">" );
                    STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
                    STRUCTURE_DATA_RESPONSE.append( "        <data>" );

                    if ( bFormula )
                    {
                        STRUCTURE_DATA_RESPONSE.append( "<![CDATA["
                            + StringUtils.checkingNumberDecimal( recalculatedValue ) + "]]>" );
                    }
                    else
                    {
                        STRUCTURE_DATA_RESPONSE.append( "<![CDATA["
                            + StringUtils.checkingNumberDecimal( cell[j].getContents() ) + "]]>" );
                    }

                    STRUCTURE_DATA_RESPONSE.append( "</data>" );
                    STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );

                    this.readingDetailsFormattedCell( format, cell[j], bDetailed );

                    STRUCTURE_DATA_RESPONSE.append( "      </col>" );
                    STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
                }
            }
            STRUCTURE_DATA_RESPONSE.append( "    </row>" );
            STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
        }
        STRUCTURE_DATA_RESPONSE.append( "  </sheet>" );
        STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
    }

    private void readingDetailsFormattedCell( jxl.format.CellFormat format, jxl.Cell objCell, boolean bDetailed )
    {
        // The format information
        format = objCell.getCellFormat();
        jxl.format.Font font = null;

        if ( format != null )
        {
            STRUCTURE_DATA_RESPONSE.append( "        <format align=\""
                + StringUtils.convertAlignmentString( format.getAlignment().getDescription() ) + "\"" );

            if ( bDetailed )
            {
                STRUCTURE_DATA_RESPONSE.append( "  valign=\"" + format.getVerticalAlignment().getDescription() + "\"" );
                STRUCTURE_DATA_RESPONSE.append( ">" );
                STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );

                // The font information
                font = format.getFont();

                STRUCTURE_DATA_RESPONSE.append( "          <font point_size=\"" + font.getPointSize() + "\"" );
                STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
                STRUCTURE_DATA_RESPONSE.append( "                bold_weight=\"" + font.getBoldWeight() + "\"" );
                STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
                STRUCTURE_DATA_RESPONSE.append( "                italic=\"" + font.isItalic() + "\"" );
                STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
                STRUCTURE_DATA_RESPONSE.append( "                underline=\""
                    + font.getUnderlineStyle().getDescription() + "\"" );
                STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
                STRUCTURE_DATA_RESPONSE.append( "                colour=\"" + font.getColour().getDescription() + "\"" );
                STRUCTURE_DATA_RESPONSE.append( " />" );
                STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );

                // The cell background information
                if ( format.getBackgroundColour() != Colour.DEFAULT_BACKGROUND || format.getPattern() != Pattern.NONE )
                {
                    STRUCTURE_DATA_RESPONSE.append( "          <background colour=\""
                        + format.getBackgroundColour().getDescription() + "\"" );
                    STRUCTURE_DATA_RESPONSE.append( " />" );
                    STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
                }

                // The cell number/date format
                if ( !format.getFormat().getFormatString().equals( "" ) )
                {
                    STRUCTURE_DATA_RESPONSE.append( "          <format_string string=\"" );
                    STRUCTURE_DATA_RESPONSE.append( format.getFormat().getFormatString() );
                    STRUCTURE_DATA_RESPONSE.append( "\" />" );
                    STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
                }
                STRUCTURE_DATA_RESPONSE.append( "        </format>" );
            }
            else
            {
                STRUCTURE_DATA_RESPONSE.append( "/>" );
            }
            STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
        }
    }

    // -------------------------------------------------------------------------
    // Get the merged cell's information
    // -------------------------------------------------------------------------
    private void writeXMLDescription( int sheetId )
        throws IOException
    {
        // Get the Range of the Merged Cells //
        if ( this.bWRITE_VERSION )
        {
            STRUCTURE_DATA_RESPONSE.append( PREFIX_VERSION_XML );
            STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE + PRINT_END_LINE );
        }

        // Open the main Tag //
        STRUCTURE_DATA_RESPONSE.append( MERGEDCELL_OPENTAG );
        STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );

        if ( sheetId > 0 )
        {
            writeBySheetNo( sheetId - 1 );
        }
        else
        {
            for ( int i = 0; i < WORKBOOK.getNumberOfSheets(); i++ )
            {
                writeBySheetNo( i );
            }

        }

        // Close the main Tag //
        STRUCTURE_DATA_RESPONSE.append( MERGEDCELL_CLOSETAG );
        STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
    }

    private void writeBySheetNo( int sheetNo )
    {
        Sheet sheet = WORKBOOK.getSheet( sheetNo );
        Range[] aMergedCell = sheet.getMergedCells();

        int iColTopLeft = 0;
        int iRowTopLeft = 0;
        int iColBottomRight = 0;

        for ( int j = 0; j < aMergedCell.length; j++ )
        {
            iColTopLeft = aMergedCell[j].getTopLeft().getColumn();
            iRowTopLeft = aMergedCell[j].getTopLeft().getRow();
            iColBottomRight = aMergedCell[j].getBottomRight().getColumn();

            if ( iColTopLeft != iColBottomRight )
            {
                STRUCTURE_DATA_RESPONSE.append( "  <cell" + " iKey=\"" + sheetNo + "#" + iRowTopLeft + "#"
                    + iColTopLeft + "\">" + (iColBottomRight - iColTopLeft + 1) + "</cell>" );
                STRUCTURE_DATA_RESPONSE.append( PRINT_END_LINE );
            }
        }
    }

    // -------------------------------------------------------------------------
    // main method
    // -------------------------------------------------------------------------

    public static void main( String[] args )
        throws Exception
    {
        // String fileName = "GenerateBaoCaoCongTacNam.xls";
        String fileName = "admin17.11.2009.10.24.00.AMBaoCaoCongTacThang.xls";

        System.out.println( new XMLStructureResponse( "c:\\Program Files\\DHIS2OH-2.0\\config\\excelreporting\\temp\\"
            + fileName, "UTF8", 1, true, false, true, false, false ).getSTRUCTURE_DATA_RESPONSE() );

    }

}
