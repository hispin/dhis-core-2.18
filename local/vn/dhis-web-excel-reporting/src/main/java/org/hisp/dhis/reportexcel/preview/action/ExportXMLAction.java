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

import org.hisp.dhis.reportexcel.ReportExcelService;
import org.hisp.dhis.reportexcel.state.SelectionManager;

import com.opensymphony.xwork2.Action;

/**
 * Simple demo class which uses the api to present the contents of an excel 97
 * spreadsheet as an XML document, using a workbook and output stream of your
 * choice
 * 
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class ExportXMLAction
    implements Action
{

    private static final String ENCODING = "UTF8";

    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportExcelService reportService;

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }

    private SelectionManager selectionManager;

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // -------------------------------------------
    // Input && Output
    // -------------------------------------------

    private String outputXLS;

    private String xmlStructureResponse;

    private File FILE_XLS;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public String getOutputXLS()
    {
        return outputXLS;
    }

    public void setOutputXLS( String outputXLS )
    {
        this.outputXLS = outputXLS;
    }

    public String getXmlStructureResponse()
    {
        return xmlStructureResponse;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @SuppressWarnings( "static-access" )
    public String execute()
        throws IOException
    {
        try
        {
            this.init();

            xmlStructureResponse = new XMLStructureResponse( this.FILE_XLS.getPath(), this.ENCODING, reportService
                .getSheets( selectionManager.getSelectedReportId() ), true, false, true, false, false )
                .getSTRUCTURE_DATA_RESPONSE();

            // this.FILE_XLS.deleteOnExit();

            return SUCCESS;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return ERROR;
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void init()
        throws Exception
    {
        this.FILE_XLS = new File( selectionManager.getDownloadFilePath() );

        // inputStream.close();
    }

}
