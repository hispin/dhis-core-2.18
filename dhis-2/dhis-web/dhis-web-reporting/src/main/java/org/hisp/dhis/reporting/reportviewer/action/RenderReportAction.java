package org.hisp.dhis.reporting.reportviewer.action;

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

import java.io.OutputStream;
import java.sql.Connection;

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.system.util.StreamUtils;
import org.hisp.dhis.util.ContextUtils;
import org.hisp.dhis.util.StreamActionSupport;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class RenderReportAction
    extends StreamActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ReportService reportService;
        
    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    protected String execute( HttpServletResponse response, OutputStream out )
        throws Exception
    {
        Report report = reportService.getReport( id );
        
        JasperReport jasperReport = JasperCompileManager.compileReport( StreamUtils.getInputStream( report.getDesignContent() ) );
        
        Connection connection = statementManager.getHolder().getConnection();
        
        JasperPrint print = null;
        
        try
        {
            print = JasperFillManager.fillReport( jasperReport, null, connection );
        }
        finally
        {        
            connection.close();
        }
        
        if ( print != null )
        {
            JasperExportManager.exportReportToPdfStream( print, out );
        }
                
        return SUCCESS;
    }

    @Override
    protected String getContentType()
    {
        return ContextUtils.CONTENT_TYPE_PDF;
    }

    @Override
    protected String getFilename()
    {
        return "report.pdf";
    }
    
    @Override
    protected boolean disallowCache()
    {
        return true;
    }
    
    @Override
    protected boolean attachment()
    {
        return true;
    }
}
