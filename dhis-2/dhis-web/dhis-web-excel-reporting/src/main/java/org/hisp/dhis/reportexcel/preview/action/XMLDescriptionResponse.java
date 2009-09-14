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

import java.io.IOException;

import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;

/**
 * Simple demo class which uses the api to present the contents of an excel 97
 * spreadsheet as an XML document, using a workbook and output stream of your
 * choice
 * 
 * @author Dang Duy Hieu
 * @version $Id$
 */

public class XMLDescriptionResponse
{
    /**
     * The encoding to write
     */
    private String DESCRIPTION_DATA_RESPONSE;

    /**
     * The encoding to write
     */
    private String ENCODING;

    /**
     * The workbook we are reading from
     */
    private Workbook WORKBOOK;

    private boolean bWRITE_VERSION;

    private static final String PREFIX_VERSION_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    private static final String MERGEDCELL_OPENTAG = "<MergedCells>";

    private static final String MERGEDCELL_CLOSETAG = "</MergedCells>";

    public String getDESCRIPTION_DATA_RESPONSE()
    {
        return DESCRIPTION_DATA_RESPONSE;
    }

    public void setDESCRIPTION_DATA_RESPONSE( String description_data_response )
    {
        DESCRIPTION_DATA_RESPONSE = description_data_response;
    }

    /**
     * Constructor
     * 
     * @param w The workbook to interrogate
     * @param out The output stream to which the XML values are written
     * @param enc The encoding used by the output stream. Null or unrecognized
     *        values cause the encoding to default to UTF8
     * @param f Indicates whether the generated XML document should contain the
     *        cell format information
     * @exception java.io.IOException
     */

    public XMLDescriptionResponse( Workbook w, String encoding, boolean bWriteVersion )
        throws IOException
    {
        this.ENCODING = encoding;
        this.WORKBOOK = w;
        this.bWRITE_VERSION = bWriteVersion;
        this.DESCRIPTION_DATA_RESPONSE = "\n\n";

        if ( this.ENCODING == null || !this.ENCODING.equals( "UnicodeBig" ) )
        {

            this.ENCODING = "UTF8";
        }

        writeXMLDescription();
    }

    /**
     * Writes out the WORKBOOK data as XML, without formatting information
     */

    private void writeXMLDescription()
        throws IOException
    {
        Sheet sheet = this.WORKBOOK.getSheet( 0 );

        // Get the Range of the Merged Cells //
        Range[] aMergedCell = sheet.getMergedCells();

        int length_of_range = aMergedCell.length;
        int iColTopLeft = 0;
        int iRowTopLeft = 0;
        int iColBottomRight = 0;

        /*
         * Write XML Description Contents
         */
        if ( this.bWRITE_VERSION )
        {

            DESCRIPTION_DATA_RESPONSE += PREFIX_VERSION_XML;
            DESCRIPTION_DATA_RESPONSE += "\n\n";
        }

        // Open the main Tag //
        DESCRIPTION_DATA_RESPONSE += MERGEDCELL_OPENTAG;
        DESCRIPTION_DATA_RESPONSE += "\n\n";

        for ( int i = 0; i < length_of_range; i++ )
        {
            iColTopLeft = aMergedCell[i].getTopLeft().getColumn();
            iRowTopLeft = aMergedCell[i].getTopLeft().getRow();
            iColBottomRight = aMergedCell[i].getBottomRight().getColumn();

            if ( iColTopLeft != iColBottomRight )
            {
                DESCRIPTION_DATA_RESPONSE += "	" + "<cell" + " iRow=\"" + (iRowTopLeft + 1) + "\"" + " iCol=\""
                    + (iColTopLeft + 1) + "\" >" + (iColBottomRight - iColTopLeft + 1) + "</cell>";
                DESCRIPTION_DATA_RESPONSE += "\n";
            }
        }

        // Close the main Tag //
        DESCRIPTION_DATA_RESPONSE += "\n";
        DESCRIPTION_DATA_RESPONSE += MERGEDCELL_CLOSETAG;
        DESCRIPTION_DATA_RESPONSE += "\n";
    }
}
