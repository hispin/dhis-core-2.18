package org.hisp.dhis.reporting.reportviewer.action;

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

import static org.apache.commons.lang.StringUtils.defaultIfEmpty;

import java.io.OutputStream;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.util.ContextUtils;
import org.hisp.dhis.util.StreamActionSupport;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class RenderReportAction
    extends StreamActionSupport
{
    private static final String DEFAULT_TYPE = ReportService.REPORTTYPE_PDF;
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ReportService reportService;
        
    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
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

    private String reportingPeriod;

    public void setReportingPeriod( String reportingPeriod )
    {
        this.reportingPeriod = reportingPeriod;
    }

    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
    
    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    protected String execute( HttpServletResponse response, OutputStream out )
        throws Exception
    {
        type = defaultIfEmpty( type, DEFAULT_TYPE );
        
        Report report = reportService.getReport( id );

        Date date = reportingPeriod != null ? DateUtils.getMediumDate( reportingPeriod ) : new Date();
        
        reportService.renderReport( out, report, date, organisationUnitId, type, format );
        
        return SUCCESS;
    }

    @Override
    protected String getContentType()
    {
        return ContextUtils.getContentType( type, ContextUtils.CONTENT_TYPE_PDF );
    }

    @Override
    protected String getFilename()
    {
        Report report = reportService.getReport( id );
        
        return CodecUtils.filenameEncode( report.getName() ) + "." + defaultIfEmpty( type, DEFAULT_TYPE );
    }
    
    @Override
    protected boolean disallowCache()
    {
        return true;
    }
    
    @Override
    protected boolean attachment()
    {
        return !defaultIfEmpty( type, DEFAULT_TYPE ).equals( DEFAULT_TYPE );
    }
}
