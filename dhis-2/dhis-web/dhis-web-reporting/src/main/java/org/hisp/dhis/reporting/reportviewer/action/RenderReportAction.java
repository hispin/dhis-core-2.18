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
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.system.util.CodecUtils;
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
    private static final String EXT_PDF = ".pdf";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ReportService reportService;
        
    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private ReportTableService reportTableService;

    public void setReportTableService( ReportTableService reportTableService )
    {
        this.reportTableService = reportTableService;
    }
    
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }

    private Integer reportingPeriod;

    public void setReportingPeriod( Integer reportingPeriod )
    {
        this.reportingPeriod = reportingPeriod;
    }

    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
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
        
        JasperPrint print = null;

        if ( report.hasReportTable() ) // Use JR data source
        {
            ReportTable reportTable = report.getReportTable();
            
            Grid grid = reportTableService.getReportTableGrid( reportTable.getId(), format, reportingPeriod, organisationUnitId );
            
            print = JasperFillManager.fillReport( jasperReport, null, grid );
        }
        else // Assume SQL report and provide JDBC connection
        {
            Connection connection = statementManager.getHolder().getConnection();
            
            try
            {
                print = JasperFillManager.fillReport( jasperReport, null, connection );
            }
            finally
            {        
                connection.close();
            }
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
        Report report = reportService.getReport( id );
        
        return CodecUtils.filenameEncode( report.getName() ) + EXT_PDF;
    }
    
    @Override
    protected boolean disallowCache()
    {
        return true;
    }
    
    @Override
    protected boolean attachment()
    {
        return false;
    }
}
