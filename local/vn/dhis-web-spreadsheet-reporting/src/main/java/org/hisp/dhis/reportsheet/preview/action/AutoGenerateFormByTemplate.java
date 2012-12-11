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

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Collection;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Simple demo class which uses the api to present the contents of an excel 97
 * spreadsheet as an XML document, using a workbook and output stream of your
 * choice
 * 
 * @author Dang Duy Hieu
 * @version $Id$
 */

public class AutoGenerateFormByTemplate
{
    /**
     * The encoding to write
     */
    private StringBuffer xml = new StringBuffer( 200000 );

    /**
     * The workbook we are reading from a given file
     */
    private Workbook WORKBOOK;

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

    public AutoGenerateFormByTemplate( String pathFileName, Set<Integer> collectSheets )
        throws Exception
    {
        this.cleanUpForResponse();

        System.out.println( "\npathFileName : " + pathFileName );

        if ( getExtension( pathFileName ).equals( "xls" ) )
        {
            this.WORKBOOK = new HSSFWorkbook( new FileInputStream( pathFileName ) );
        }
        else
        {
            this.WORKBOOK = new XSSFWorkbook( new FileInputStream( pathFileName ) );
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
            createFormByComment2( sheet );
        }
    }

    // -------------------------------------------------------------------------
    // Sub-methods
    // -------------------------------------------------------------------------

    private void createFormByComment( int sheetNo )
    {
        Sheet s = WORKBOOK.getSheetAt( sheetNo - 1 );
        Comment cmt = null;
        String content = null;

        for ( Row row : s )
        {
            for ( Cell cell : row )
            {
                {
                    cmt = cell.getCellComment();

                    if ( cmt != null )
                    {
                        content = cmt.getString().getString();
                    }
                }
            }
        }
    }

    private void createFormByComment2( int sheetNo )
    {
        Sheet s = WORKBOOK.getSheetAt( sheetNo - 1 );
        Comment cmt = null;

        System.out.println( "\nsheet: " + s.getSheetName() );

        // Create file
        FileWriter fstream = null;
        BufferedWriter out = null;
        try
        {
            fstream = new FileWriter( "d:\\template_file.xls\\out.txt" );
            out = new BufferedWriter( fstream );

            for ( Row row : s )
            {
                for ( Cell cell : row )
                {
                    cmt = cell.getCellComment();
                    if ( cell.getCellComment() != null )
                    {
                        out.write( "\n\n NOT_NULL comment: " + cell.getCellComment().getRow() + ","
                            + cell.getCellComment().getColumn() );
                        out.write( "\t value2: " + cell.getCellComment().getString() );
                    }
                }
            }

            // Close the output stream
            out.close();
        }
        catch ( Exception e )
        {
            // Catch exception if any
            System.err.println( "Error: " + e.getMessage() );
        }
    }
}