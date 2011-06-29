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

import static org.hisp.dhis.reportexcel.utils.StringUtils.convertAlignmentString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import jxl.Cell;
import jxl.CellType;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.CellFormat;

import org.hisp.dhis.reportexcel.importitem.ExcelItem;
import org.hisp.dhis.reportexcel.importitem.ExcelItemGroup;

/**
 * 
 * @author Dang Duy Hieu
 * @version $Id XMLStructureResponse.java 2011-06-28 16:08:00$
 */

public class XMLStructureResponseImport
{
    /**
     * The encoding to write
     */
    private StringBuffer xml = new StringBuffer( 200000 );

    /**
     * The workbook we are reading from a given file
     */
    private Workbook WORKBOOK;

    private static final String WORKBOOK_OPENTAG = "<workbook>";

    private static final String WORKBOOK_CLOSETAG = "</workbook>";

    private static final String MERGEDCELL_OPENTAG = "<MergedCells>";

    private static final String MERGEDCELL_CLOSETAG = "</MergedCells>";

    // -------------------------------------------------------------------------
    // Get & Set methods
    // -------------------------------------------------------------------------

    public String getXml()
    {
        return xml.toString();
    }

    /**
     * Constructor
     * 
     * @param importItems
     * @param type The TYPE for importing
     * 
     * @param w The workbook to interrogate
     * @param enc The encoding used by the output stream. Null or unrecognized
     *        values cause the encoding to default to UTF8
     * @param f Indicates whether the generated XML document should contain the
     *        cell format information
     * @exception java.io.IOException
     */

    public XMLStructureResponseImport( String pathFileName, Collection<Integer> collectSheets,
        List<ExcelItem> importItems, boolean bWriteDescription, String type )
        throws Exception
    {
        this.cleanUpForResponse();

        FileInputStream inputStream = new FileInputStream( new File( pathFileName ) );

        this.WORKBOOK = Workbook.getWorkbook( inputStream );

        this.writeFormattedXML( collectSheets, importItems, bWriteDescription, type );
    }

    // -------------------------------------------------------------------------
    // Public methods
    // -------------------------------------------------------------------------

    public void writeData( int sheetNo, List<ExcelItem> importItems, String TYPE )
    {
        Sheet s = WORKBOOK.getSheet( sheetNo - 1 );

        xml.append( "<sheet id='" + (sheetNo) + "'>" );
        xml.append( "<name><![CDATA[" + s.getName() + "]]></name>" );

        Cell[] cell = null;
        int run = 0;

        for ( int i = 0; i < s.getRows(); i++ )
        {
            xml.append( "<row index='" + i + "'>" );

            cell = s.getRow( i );

            for ( int j = 0; j < cell.length; j++ )
            {
                // Remember that empty cells can contain format information
                if ( !cell[j].getType().equals( CellType.EMPTY ) || (cell[j].getCellFormat() != null) )
                {
                    xml.append( "<col no='" + j + "'" );

                    for ( ExcelItem importItem : importItems )
                    {
                        if ( (importItem.getSheetNo() == sheetNo) && (importItem.getRow() == (i + 1))
                            && (importItem.getColumn() == (j + 1)) )
                        {
                            if ( TYPE.equals( ExcelItemGroup.TYPE.NORMAL ) )
                            {
                                xml.append( " id='" + importItem.getExpression() + "'>" );
                            }
                            else if ( TYPE.equals( ExcelItemGroup.TYPE.CATEGORY ) )
                            {
                                xml.append( " id='" + importItem.getExpression() + "'>" );
                            }

                            break;
                        }

                        run++;
                    }

                    if ( run == importItems.size() )
                    {
                        xml.append( ">" );
                    } // end checking

                    xml.append( "<data><![CDATA[" + cell[j].getContents() + "]]></data>" );

                    this.readingDetailsFormattedCell( cell[j] );

                    xml.append( "</col>" );
                }
            }
            xml.append( "</row>" );
        }
        xml.append( "</sheet>" );
    }

    // -------------------------------------------------------------------------
    // Private methods
    // -------------------------------------------------------------------------

    private void cleanUpForResponse()
    {
        System.gc();
    }

    private void writeFormattedXML( Collection<Integer> collectSheets, List<ExcelItem> importItems,
        boolean bWriteDescription, String type )
        throws Exception
    {
        if ( bWriteDescription )
        {
            this.writeXMLMergedDescription( collectSheets );
        }

        xml.append( WORKBOOK_OPENTAG );

        for ( Integer sheet : collectSheets )
        {
            writeData( sheet, importItems, type );
        }

        xml.append( WORKBOOK_CLOSETAG );
    }

    private void writeXMLMergedDescription( Collection<Integer> collectSheets )
        throws IOException
    {
        xml.append( MERGEDCELL_OPENTAG );

        for ( Integer sheet : collectSheets )
        {
            writeBySheetNo( sheet );
        }

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

    private void readingDetailsFormattedCell( Cell objCell )
    {
        // The format information
        CellFormat format = objCell.getCellFormat();

        if ( format != null )
        {
            xml.append( "<format align='" + convertAlignmentString( format.getAlignment().getDescription() ) + "'/>" );
        }
    }
}