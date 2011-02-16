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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.databrowser.DataBrowserPdfService;
import org.hisp.dhis.databrowser.DataBrowserTable;
import org.hisp.dhis.databrowser.MetaValue;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.util.SessionUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author briane, eivinhb
 * @version $Id ExportPDFAction.java Oct 21, 2010 ddhieu$
 */
public class ExportPDFAction
    implements Action
{
    private static final String KEY_DATABROWSERTITLENAME = "dataBrowserTitleName";

    private static final String KEY_DATABROWSERFROMDATE = "dataBrowserFromDate";

    private static final String KEY_DATABROWSERTODATE = "dataBrowserToDate";

    private static final String KEY_DATABROWSERPERIODTYPE = "dataBrowserPeriodType";

    private static final String KEY_DATABROWSERTABLE = "dataBrowserTableResults";

    private static final String REGEX_EXTENSION_PDF = "\\.pdf";

    private static final String EXTENSION_PDF = ".pdf";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataBrowserPdfService dataBrowserPdfService;

    public void setDataBrowserPdfService( DataBrowserPdfService dataBrowserPdfService )
    {
        this.dataBrowserPdfService = dataBrowserPdfService;
    }

    // -------------------------------------------------------------------------
    // I18n
    // -------------------------------------------------------------------------

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Input / output
    // -------------------------------------------------------------------------

    private DataBrowserTable dataBrowserTable;

    public List<MetaValue> getAllColumns()
    {
        return dataBrowserTable.getColumns();
    }

    public DataBrowserTable getDataBrowserTable()
    {
        return dataBrowserTable;
    }

    public List<List<String>> getAllCounts()
    {
        return dataBrowserTable.getCounts();
    }

    public Iterator<MetaValue> getRowNamesIterator()
    {
        return dataBrowserTable.getRows().iterator();
    }

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public void setFileName( String fileName )
    {
        this.fileName = fileName.replaceAll( REGEX_EXTENSION_PDF, "" ).concat( EXTENSION_PDF );
    }

    public String getFileName()
    {
        return this.fileName;
    }

    private int fontSize;

    public void setFontSize( int fontSize )
    {
        this.fontSize = fontSize;
    }

    private String pageLayout;

    public void setPageLayout( String pageLayout )
    {
        this.pageLayout = pageLayout;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // Get session variables set by SearchAction
        String dataBrowserTitleName = (String) SessionUtils.getSessionVar( KEY_DATABROWSERTITLENAME );
        String dataBrowserFromDate = (String) SessionUtils.getSessionVar( KEY_DATABROWSERFROMDATE );
        String dataBrowserToDate = (String) SessionUtils.getSessionVar( KEY_DATABROWSERTODATE );
        String dataBrowserPeriodType = (String) SessionUtils.getSessionVar( KEY_DATABROWSERPERIODTYPE );
        DataBrowserTable dataBrowserTable = (DataBrowserTable) SessionUtils.getSessionVar( KEY_DATABROWSERTABLE );

        // Export to PDF

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        dataBrowserPdfService.writeDataBrowserResult( dataBrowserTitleName, dataBrowserFromDate, dataBrowserToDate,
            dataBrowserPeriodType, pageLayout, fileName, fontSize, dataBrowserTable, baos, i18n );

        // Set final inputStream for Velocity
        inputStream = new ByteArrayInputStream( baos.toByteArray() );

        return SUCCESS;
    }

}
