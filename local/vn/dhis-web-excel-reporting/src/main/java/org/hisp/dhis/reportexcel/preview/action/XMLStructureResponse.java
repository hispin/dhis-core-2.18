package org.hisp.dhis.reportexcel.preview.action;

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

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import jxl.Cell;
import jxl.CellType;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.Font;
import jxl.format.Pattern;

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
    private StringBuffer xml = new StringBuffer( 200000 );

    /**
     * The workbook we are reading from a given file
     */
    private Workbook WORKBOOK;

    private boolean bWRITE_VERSION;

    private boolean bWRITE_DTD;

    private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    private static final String DOCTYPE_NORMAL = "<!DOCTYPE WORKBOOK SYSTEM \"WORKBOOK.dtd\">";

    private static final String DOCTYPE_FORMAT = "<!DOCTYPE WORKBOOK SYSTEM \"formatWORKBOOK.dtd\">";

    private static final String WORKBOOK_OPENTAG = "<workbook>";

    private static final String WORKBOOK_CLOSETAG = "</workbook>";

    private static final String MERGEDCELL_OPENTAG = "<MergedCells>";

    private static final String MERGEDCELL_CLOSETAG = "</MergedCells>";

    // -------------------------------------------------------------------------
    // Get & Set methods
    // -------------------------------------------------------------------------
    
    protected String getXml()
    {
        return xml.toString();
    }

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

    public XMLStructureResponse( String pathFileName, Collection<Integer> collectSheets, boolean bWriteDTD,
        boolean bWriteVersion, boolean bFormat, boolean bDetailed, boolean bWriteDescription )
        throws Exception
    {
        this.cleanUpForResponse();

        this.bWRITE_DTD = bWriteDTD;
        this.bWRITE_VERSION = bWriteVersion;
        this.WORKBOOK = Workbook.getWorkbook( new File( pathFileName ) );

        if ( bFormat )
        {
            writeFormattedXML( collectSheets, bDetailed, bWriteDescription );
        }
        else
        {
            writeXML( collectSheets );
        }
    }

    /**
     * Writes out the WORKBOOK data as XML, without formatting information
     */
    private void writeXML( Collection<Integer> collectSheets )
        throws IOException
    {
        if ( this.bWRITE_VERSION )
        {
            xml.append( XML_VERSION );
        }

        if ( this.bWRITE_DTD )
        {
            xml.append( DOCTYPE_NORMAL );
        }

        xml.append( WORKBOOK_OPENTAG );

        for ( Integer sheet : collectSheets )
        {
            Sheet s = WORKBOOK.getSheet( sheet - 1 );

            xml.append( "<sheet id='" + sheet + "'>" );
            xml.append( "<name><![CDATA[" + s.getName() + "]]></name>" );

            Cell[] row = null;

            for ( int i = 0; i < s.getRows(); i++ )
            {
                xml.append( "<row number='" + i + "'>" );

                row = s.getRow( i );

                for ( int j = 0; j < row.length; j++ )
                {
                    if ( row[j].getType() != CellType.EMPTY )
                    {
                        xml.append( "<col number='" + j + "'>" );
                        xml.append( "<![CDATA[" + row[j].getContents() + "]]>" );
                        xml.append( "</col>" );
                    }
                }
                xml.append( "</row>" );
            }
            xml.append( "</sheet>" );
        }
        xml.append( WORKBOOK_CLOSETAG );
    }

    /**
     * Writes out the WORKBOOK data as XML, with formatting information
     * 
     * @param bDetailed
     * 
     * @throws Exception
     */

    private void writeFormattedXML( Collection<Integer> collectSheets, boolean bDetailed, boolean bWriteDescription )
        throws Exception
    {
        if ( bWriteDescription )
        {
            this.writeXMLMergedDescription( collectSheets );
        }

        if ( this.bWRITE_VERSION )
        {
            xml.append( XML_VERSION );
        }

        if ( this.bWRITE_DTD )
        {
            xml.append( DOCTYPE_FORMAT );
        }

        xml.append( WORKBOOK_OPENTAG );

        for ( Integer sheet : collectSheets )
        {
            writeBySheetNo( sheet, bDetailed );
        }

        xml.append( WORKBOOK_CLOSETAG );
    }

    // -------------------------------------------------------------------------
    // Sub-methods
    // -------------------------------------------------------------------------

    private void writeBySheetNo( int sheetNo, boolean bDetailed )
    {
        Sheet s = WORKBOOK.getSheet( sheetNo - 1 );

        xml.append( "<sheet id='" + (sheetNo) + "'>" );
        xml.append( "<name><![CDATA[" + s.getName() + "]]></name>" );

        Cell[] cell = null;

        for ( int i = 0; i < s.getRows(); i++ )
        {
            xml.append( "<row index='" + i + "'>" );

            cell = s.getRow( i );

            for ( int j = 0; j < cell.length; j++ )
            {
                // Remember that empty cells can contain format
                // information
                if ( !cell[j].getType().equals( CellType.EMPTY ) || (cell[j].getCellFormat() != null) )
                {
                    xml.append( "<col no='" + j + "'><data>" );
                    xml.append( "<![CDATA[" + StringUtils.applyPatternDecimalFormat( cell[j].getContents() )
                        + "]]></data>" );

                    this.readingDetailsFormattedCell( cell[j], bDetailed );

                    xml.append( "</col>" );
                }
            }
            xml.append( "</row>" );
        }
        xml.append( "</sheet>" );
    }

    private void readingDetailsFormattedCell( Cell objCell, boolean bDetailed )
    {
        // The format information
        CellFormat format = objCell.getCellFormat();
        Font font = null;

        if ( format != null )
        {
            xml.append( "<format align='" + StringUtils.convertAlignmentString( format.getAlignment().getDescription() )
                + "'" );

            if ( bDetailed )
            {
                xml.append( " valign='" + format.getVerticalAlignment().getDescription() + "'>" );

                // The font information
                font = format.getFont();

                xml.append( "<font point_size='" + font.getPointSize() + "'" );
                xml.append( " bold_weight='" + font.getBoldWeight() + "'" );
                xml.append( " italic='" + font.isItalic() + "'" );
                xml.append( " underline='" + font.getUnderlineStyle().getDescription() + "'" );
                xml.append( " colour='" + font.getColour().getDescription() + "'" );
                xml.append( " />" );

                // The cell background information
                if ( format.getBackgroundColour() != Colour.DEFAULT_BACKGROUND || format.getPattern() != Pattern.NONE )
                {
                    xml.append( "<background colour='" + format.getBackgroundColour().getDescription() + "'" );
                    xml.append( " />" );
                }

                // The cell number/date format
                if ( !format.getFormat().getFormatString().equals( "" ) )
                {
                    xml.append( "<format_string string='" );
                    xml.append( format.getFormat().getFormatString() );
                    xml.append( "' />" );
                }
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
    private void writeXMLMergedDescription( Collection<Integer> collectSheets )
        throws IOException
    {
        // Get the Range of the Merged Cells //
        if ( this.bWRITE_VERSION )
        {
            xml.append( XML_VERSION );
        }

        // Open the main Tag //
        xml.append( MERGEDCELL_OPENTAG );

        for ( Integer sheet : collectSheets )
        {
            writeBySheetNo( sheet );
        }

        // Close the main Tag //
        xml.append( MERGEDCELL_CLOSETAG );
    }

    private void writeBySheetNo( int sheetNo )
    {
        Sheet sheet = WORKBOOK.getSheet( sheetNo - 1 );
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
                xml.append( "<cell " + "iKey='" + (sheetNo) + "#" + iRowTopLeft + "#" + iColTopLeft + "'>"
                    + (iColBottomRight - iColTopLeft + 1) + "</cell>" );
            }
        }
    }
}