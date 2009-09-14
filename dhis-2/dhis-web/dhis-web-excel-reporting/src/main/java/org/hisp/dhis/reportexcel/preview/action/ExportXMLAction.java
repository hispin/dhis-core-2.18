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
import java.io.IOException;
import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;

import org.hisp.dhis.reportexcel.ReportLocationManager;

import com.opensymphony.xwork2.Action;

/**
 * Simple demo class which uses the api to present the contents
 * of an excel 97 spreadsheet as an XML document, using a workbook
 * and output stream of your choice
 * 
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class ExportXMLAction
    implements Action
{
    private static final String SEPARATE = "/";

    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportLocationManager reportLocationManager;

    // -------------------------------------------
    // Input && Output
    // -------------------------------------------

    private InputStream inputStream;

    private String outputXLS;

    private String main_path;

    private String xmlDescriptionResponse;

    private String xmlStructureResponse;

    private String ENCODING;

    private Workbook WORKBOOK;

    private File FILE_XLS;

    private Sheet sheet;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public void setReportLocationManager( ReportLocationManager reportLocationManager )
    {
        this.reportLocationManager = reportLocationManager;
    }

    public void setInputStream( InputStream inputStream )
    {
        this.inputStream = inputStream;
    }

    public String getOutputXLS()
    {
        return outputXLS;
    }

    public void setOutputXLS( String outputXLS )
    {
        this.outputXLS = outputXLS;
    }

    public String getXmlDescriptionResponse()
    {
        return xmlDescriptionResponse;
    }

    public void setXmlDescriptionResponse( String xmlDescriptionResponse )
    {
        this.xmlDescriptionResponse = xmlDescriptionResponse;
    }

    public String getXmlStructureResponse()
    {
        return xmlStructureResponse;
    }

    public void setXmlStructureResponse( String xmlStructureResponse )
    {
        this.xmlStructureResponse = xmlStructureResponse;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws IOException
    {
        try
        {
            init();

            xmlDescriptionResponse = new XMLDescriptionResponse( WORKBOOK, ENCODING, false )
                .getDESCRIPTION_DATA_RESPONSE();

            xmlStructureResponse = new XMLStructureResponse( this.FILE_XLS.getPath(), ENCODING, true, false, false )
                .getSTRUCTURE_DATA_RESPONSE();

            this.FILE_XLS.deleteOnExit();

            return SUCCESS;
        }
        catch ( Exception e )
        {
            return ERROR;
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void init()
        throws Exception
    {
        this.main_path = this.replacedSeparateSimple( reportLocationManager.getReportTempDirectory().getPath() );

        this.FILE_XLS = new File( main_path + SEPARATE + this.outputXLS );

        this.WORKBOOK = Workbook.getWorkbook( this.FILE_XLS );

        inputStream.close();
    }

    private String replacedSeparateSimple( String path )
    {
        path = path.replace( "\\", SEPARATE );

        return path;
    }

    public Sheet getSheet()
    {
        return sheet;
    }

    public void setSheet( Sheet sheet )
    {
        this.sheet = sheet;
    }
}
