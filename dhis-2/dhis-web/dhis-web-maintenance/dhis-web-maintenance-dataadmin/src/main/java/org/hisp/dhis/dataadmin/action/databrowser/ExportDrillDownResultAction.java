package org.hisp.dhis.dataadmin.action.databrowser;

/*
 * Copyright (c) 2004-${year}, University of Oslo
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

import java.io.ByteArrayOutputStream;

import org.hisp.dhis.dataadmin.action.AbstractExportDataBrowserResult;
import org.hisp.dhis.databrowser.DataBrowserPdfService;
import org.hisp.dhis.databrowser.DataBrowserTable;
import org.hisp.dhis.databrowser.DataBrowserXLSService;
import org.hisp.dhis.i18n.I18n;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class ExportDrillDownResultAction
    extends AbstractExportDataBrowserResult
{
    private static final String REGEX_EXTENSION_PDF = "\\.pdf";

    private static final String REGEX_EXTENSION_XLS = "\\.xls";
    
    private static final String EXTENSION_PDF = ".pdf";

    private static final String EXTENSION_XLS = ".xls";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataBrowserPdfService dataBrowserPdfService;

    public void setDataBrowserPdfService( DataBrowserPdfService dataBrowserPdfService )
    {
        this.dataBrowserPdfService = dataBrowserPdfService;
    }

    private DataBrowserXLSService dataBrowserXLSService;

    public void setDataBrowserXLSService( DataBrowserXLSService dataBrowserXLSService )
    {
        this.dataBrowserXLSService = dataBrowserXLSService;
    }

    // -------------------------------------------------------------------------
    // Override
    // -------------------------------------------------------------------------

    @Override
    protected void executeExportResult( String dataBrowserTitleName, String dataBrowserFromDate,
        String dataBrowserToDate, String dataBrowserPeriodType, String pageLayout, int fontSize,
        DataBrowserTable dataBrowserTable, ByteArrayOutputStream baos, I18n i18n )
    {
        if ( this.exportType.equals( "pdf" ) )
        {
            this.contentType = "application/pdf";
            
            this.fileName = fileName.replaceAll( REGEX_EXTENSION_PDF, "" ).concat( EXTENSION_PDF );

            dataBrowserPdfService.writeDataBrowserResult( dataBrowserTitleName, dataBrowserFromDate, dataBrowserToDate,
                dataBrowserPeriodType, pageLayout, fontSize, dataBrowserTable, baos, i18n );
        }
        else
        {
            this.contentType = "application/xls";
            
            this.fileName = fileName.replaceAll( REGEX_EXTENSION_XLS, "" ).concat( EXTENSION_XLS );

            dataBrowserXLSService.writeDataBrowserResult( dataBrowserTitleName, dataBrowserFromDate, dataBrowserToDate,
                dataBrowserPeriodType, fontSize, dataBrowserTable, baos, i18n );
        }
    }
}
