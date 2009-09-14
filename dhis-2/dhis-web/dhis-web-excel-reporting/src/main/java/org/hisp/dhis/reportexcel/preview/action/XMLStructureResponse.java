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
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.Font;
import jxl.format.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

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

    private static final String WORKBOOK_OPENTAG = "<workbook>";

    private static final String WORKBOOK_CLOSETAG = "</workbook>";

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

    public XMLStructureResponse( String pathFileName, String enc, boolean bFormat, boolean bWriteVersion,
        boolean bWriteDTD )
        throws Exception
    {
        this.PATH_FILE_NAME = pathFileName;
        this.WORKBOOK = Workbook.getWorkbook( new File( pathFileName ) );
        this.ENCODING = enc;
        this.bWRITE_DTD = bWriteDTD;
        this.bWRITE_VERSION = bWriteVersion;
        this.STRUCTURE_DATA_RESPONSE = new StringBuffer();

        if ( this.ENCODING == null || !this.ENCODING.equals( "UnicodeBig" ) )
        {
            this.ENCODING = "UTF8";
        }

        if ( bFormat )
        {
            writeFormattedXML();
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
            STRUCTURE_DATA_RESPONSE.append( "\n" );
        }

        if ( this.bWRITE_DTD )
        {
            STRUCTURE_DATA_RESPONSE.append( "<!DOCTYPE WORKBOOK SYSTEM \"WORKBOOK.dtd\">" );
            STRUCTURE_DATA_RESPONSE.append( "\n" );
        }

        STRUCTURE_DATA_RESPONSE.append( "\n" );
        STRUCTURE_DATA_RESPONSE.append( WORKBOOK_OPENTAG );
        STRUCTURE_DATA_RESPONSE.append( "\n" );

        for ( int sheet = 0; sheet < WORKBOOK.getNumberOfSheets(); sheet++ )
        {
            Sheet s = WORKBOOK.getSheet( sheet );

            STRUCTURE_DATA_RESPONSE.append( "  <sheet>" );
            STRUCTURE_DATA_RESPONSE.append( "\n" );
            STRUCTURE_DATA_RESPONSE.append( "    <name><![CDATA[" + s.getName() + "]]></name>" );
            STRUCTURE_DATA_RESPONSE.append( "\n" );

            Cell[] row = null;

            for ( int i = 0; i < s.getRows(); i++ )
            {
                STRUCTURE_DATA_RESPONSE.append( "    <row number=\"" + i + "\">" );
                STRUCTURE_DATA_RESPONSE.append( "\n" );
                row = s.getRow( i );

                for ( int j = 0; j < row.length; j++ )
                {
                    if ( row[j].getType() != CellType.EMPTY )
                    {
                        STRUCTURE_DATA_RESPONSE.append( "      <col number=\"" + j + "\">" );
                        STRUCTURE_DATA_RESPONSE.append( "<![CDATA[" + row[j].getContents() + "]]>" );
                        STRUCTURE_DATA_RESPONSE.append( "</col>" );
                        STRUCTURE_DATA_RESPONSE.append( "\n" );
                    }
                }
                STRUCTURE_DATA_RESPONSE.append( "    </row>" );
                STRUCTURE_DATA_RESPONSE.append( "\n" );
            }
            STRUCTURE_DATA_RESPONSE.append( "  </sheet>" );
            STRUCTURE_DATA_RESPONSE.append( "\n" );
        }
        STRUCTURE_DATA_RESPONSE.append( WORKBOOK_CLOSETAG );
        STRUCTURE_DATA_RESPONSE.append( "\n" );
    }

    /**
     * Writes out the WORKBOOK data as XML, with formatting information
     * @throws Exception 
     */

    private void writeFormattedXML()
        throws Exception
    {
        if ( this.bWRITE_VERSION )
        {
            STRUCTURE_DATA_RESPONSE.append( PREFIX_VERSION_XML );
            STRUCTURE_DATA_RESPONSE.append( "\n" );
        }

        if ( this.bWRITE_DTD )
        {
            STRUCTURE_DATA_RESPONSE.append( "<!DOCTYPE WORKBOOK SYSTEM \"formatWORKBOOK.dtd\">" );
            STRUCTURE_DATA_RESPONSE.append( "\n" );
        }

        STRUCTURE_DATA_RESPONSE.append( "\n" );
        STRUCTURE_DATA_RESPONSE.append( WORKBOOK_OPENTAG );
        STRUCTURE_DATA_RESPONSE.append( "\n" );

        FileInputStream fis = new FileInputStream( this.PATH_FILE_NAME );
        org.apache.poi.hssf.usermodel.HSSFWorkbook wb = (HSSFWorkbook) WorkbookFactory.create( fis );
        org.apache.poi.ss.usermodel.FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        for ( int sheet = 0; sheet < WORKBOOK.getNumberOfSheets(); sheet++ )
        {
            Sheet s = WORKBOOK.getSheet( sheet );
            org.apache.poi.ss.usermodel.Sheet sheetPOI = wb.getSheetAt( sheet );

            STRUCTURE_DATA_RESPONSE.append( "  <sheet>" );
            STRUCTURE_DATA_RESPONSE.append( "\n" );
            STRUCTURE_DATA_RESPONSE.append( "    <name><![CDATA[" + s.getName() + "]]></name>" );
            STRUCTURE_DATA_RESPONSE.append( "\n" );

            Cell[] cell = null;
            CellFormat format = null;
            Font font = null;

            boolean bFormula = false;
            double recalculatedValue = 0;

            for ( int i = 0; i < s.getRows(); i++ )
            {
                STRUCTURE_DATA_RESPONSE.append( "    <row number=\"" + i + "\">" );
                STRUCTURE_DATA_RESPONSE.append( "\n" );

                cell = s.getRow( i );

                for ( int j = 0; j < cell.length; j++ )
                {

                    // Remember that empty cells can contain format
                    // information
                    if ( (cell[j].getType() != CellType.EMPTY) || (cell[j].getCellFormat() != null) )
                    {

                        bFormula = false;

                        // check the cell formula
                        if ( cell[j].getType() == CellType.NUMBER_FORMULA
                            || cell[j].getType() == CellType.STRING_FORMULA
                            || cell[j].getType() == CellType.BOOLEAN_FORMULA
                            || cell[j].getType() == CellType.DATE_FORMULA
                            || cell[j].getType() == CellType.FORMULA_ERROR )
                        {
                            bFormula = true;
                            recalculatedValue = 0;

                            // suppose your formula is in Cell
                            org.apache.poi.ss.util.CellReference cellReference = new org.apache.poi.ss.util.CellReference(
                                cell[j].getRow(), cell[j].getColumn() );
                            org.apache.poi.ss.usermodel.Row rowPOI = sheetPOI.getRow( cellReference.getRow() );
                            org.apache.poi.ss.usermodel.Cell cellPOI = rowPOI.getCell( cellReference.getCol() );

                            if ( cellPOI != null )
                            {
                                switch ( evaluator.evaluateFormulaCell( cellPOI ) )
                                {
                                case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
                                    recalculatedValue = cellPOI.getNumericCellValue();
                                    break;

                                // CELL_TYPE_FORMULA will never occur
                                case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA:
                                    break;
                                }
                            }
                        }

                        // end of checking the cell formula
                        STRUCTURE_DATA_RESPONSE.append( "      <col number=\"" + j + "\">" );
                        STRUCTURE_DATA_RESPONSE.append( "\n" );
                        STRUCTURE_DATA_RESPONSE.append( "        <data>" );

                        // print data in cell
                        if ( bFormula )
                        {
                            STRUCTURE_DATA_RESPONSE.append( "<![CDATA[" + recalculatedValue + "]]>" );
                        }
                        else
                        {
                            STRUCTURE_DATA_RESPONSE.append( "<![CDATA[" + cell[j].getContents() + "]]>" );
                        }

                        STRUCTURE_DATA_RESPONSE.append( "</data>" );
                        STRUCTURE_DATA_RESPONSE.append( "\n" );

                        // The format information
                        format = cell[j].getCellFormat();

                        if ( format != null )
                        {
                            STRUCTURE_DATA_RESPONSE.append( "        <format align=\""
                                + format.getAlignment().getDescription() + "\"" );
                            STRUCTURE_DATA_RESPONSE.append( "\n" );

                            STRUCTURE_DATA_RESPONSE.append( "                valign=\""
                                + format.getVerticalAlignment().getDescription() + "\"" );
                            STRUCTURE_DATA_RESPONSE.append( ">" );
                            STRUCTURE_DATA_RESPONSE.append( "\n" );

                            // The font information
                            font = format.getFont();

                            STRUCTURE_DATA_RESPONSE.append( "          <font point_size=\"" + font.getPointSize()
                                + "\"" );
                            STRUCTURE_DATA_RESPONSE.append( "\n" );
                            STRUCTURE_DATA_RESPONSE.append( "                bold_weight=\"" + font.getBoldWeight()
                                + "\"" );
                            STRUCTURE_DATA_RESPONSE.append( "\n" );
                            STRUCTURE_DATA_RESPONSE.append( "                italic=\"" + font.isItalic() + "\"" );
                            STRUCTURE_DATA_RESPONSE.append( "\n" );
                            STRUCTURE_DATA_RESPONSE.append( "                underline=\""
                                + font.getUnderlineStyle().getDescription() + "\"" );
                            STRUCTURE_DATA_RESPONSE.append( "\n" );
                            STRUCTURE_DATA_RESPONSE.append( "                colour=\""
                                + font.getColour().getDescription() + "\"" );
                            STRUCTURE_DATA_RESPONSE.append( " />" );
                            STRUCTURE_DATA_RESPONSE.append( "\n" );

                            // The cell background information
                            if ( format.getBackgroundColour() != Colour.DEFAULT_BACKGROUND
                                || format.getPattern() != Pattern.NONE )
                            {
                                STRUCTURE_DATA_RESPONSE.append( "          <background colour=\""
                                    + format.getBackgroundColour().getDescription() + "\"" );
                                STRUCTURE_DATA_RESPONSE.append( " />" );
                                STRUCTURE_DATA_RESPONSE.append( "\n" );
                            }

                            // The cell number/date format
                            if ( !format.getFormat().getFormatString().equals( "" ) )
                            {
                                STRUCTURE_DATA_RESPONSE.append( "          <format_string string=\"" );
                                STRUCTURE_DATA_RESPONSE.append( format.getFormat().getFormatString() );
                                STRUCTURE_DATA_RESPONSE.append( "\" />" );
                                STRUCTURE_DATA_RESPONSE.append( "\n" );
                            }
                            STRUCTURE_DATA_RESPONSE.append( "        </format>" );
                            STRUCTURE_DATA_RESPONSE.append( "\n" );
                        }
                        STRUCTURE_DATA_RESPONSE.append( "      </col>" );
                        STRUCTURE_DATA_RESPONSE.append( "\n" );
                    }
                }
                STRUCTURE_DATA_RESPONSE.append( "    </row>" );
                STRUCTURE_DATA_RESPONSE.append( "\n" );
            }
            STRUCTURE_DATA_RESPONSE.append( "  </sheet>" );
            STRUCTURE_DATA_RESPONSE.append( "\n" );
        }
        STRUCTURE_DATA_RESPONSE.append( WORKBOOK_CLOSETAG );
        STRUCTURE_DATA_RESPONSE.append( "\n" );
    }
}
