package org.hisp.dhis.dashboard.provider;

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

import static org.hisp.dhis.report.Report.TYPE_BIRT;
import static org.hisp.dhis.report.Report.TYPE_DEFAULT;
import static org.hisp.dhis.report.Report.TYPE_JASPER;
import static org.hisp.dhis.util.ContextUtils.getBaseUrl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dashboard.DashboardContent;
import org.hisp.dhis.dashboard.DashboardService;
import org.hisp.dhis.external.configuration.NoConfigurationFoundException;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.report.ReportManager;
import org.hisp.dhis.report.comparator.ReportComparator;
import org.hisp.dhis.report.manager.ReportConfiguration;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ReportContentProvider
    implements ContentProvider
{
    private static final Log log = LogFactory.getLog( ReportContentProvider.class );
    
    private static final String SEPARATOR = "/";
    private static final String BASE_QUERY = "frameset?__report=";
    private static final String JASPER_BASE_URL = "../dhis-web-reporting/getReportParams.action?id=";
    private static final String JASPER_RENDER_URL = "&mode=report&url=renderReport.action?id=";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private DashboardService dashboardService;

    public void setDashboardService( DashboardService dashboardService )
    {
        this.dashboardService = dashboardService;
    }
    
    private ReportManager reportManager;

    public void setReportManager( ReportManager reportManager )
    {
        this.reportManager = reportManager;
    }
    
    private String key;
    
    public void setKey( String key )
    {
        this.key = key;
    }

    // -------------------------------------------------------------------------
    // ContentProvider implementation
    // -------------------------------------------------------------------------

    public Map<String, Object> provide()
    {
        Map<String, Object> content = new HashMap<String, Object>();
        
        User user = currentUserService.getCurrentUser();
        
        if ( user != null )
        {
            DashboardContent dashboardContent = dashboardService.getDashboardContent( user );
            
            String framework = (String) systemSettingManager.getSystemSetting( SystemSettingManager.KEY_REPORT_FRAMEWORK, TYPE_DEFAULT );
            
            List<Report> reports = dashboardContent.getReports();
            
            if ( framework.equals( TYPE_JASPER ) )
            {
                for ( Report report : reports )
                {
                    report.setUrl( JASPER_BASE_URL + report.getId() + JASPER_RENDER_URL + report.getId() );
                }
            }
            else if ( framework.equals( TYPE_BIRT ) )
            {            
                try
                {
                    ReportConfiguration config = reportManager.getConfiguration();
                    
                    HttpServletRequest request = ServletActionContext.getRequest();
                    
                    String birtURL = getBaseUrl( request ) + config.getDirectory() + SEPARATOR + BASE_QUERY;
                    
                    for ( Report report : reports )
                    {
                        report.setUrl( birtURL + report.getDesign() );
                    }
                    
                }
                catch ( NoConfigurationFoundException ex )
                {
                    log.error( "Report configuration not set" );
                }
            }

            Collections.sort( reports, new ReportComparator() );
            
            content.put( key, reports );
        }
        
        return content;
    }
}
