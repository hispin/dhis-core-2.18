package org.hisp.dhis.appmanager.action;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import com.opensymphony.xwork2.Action;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.appmanager.App;
import org.hisp.dhis.appmanager.AppManagerService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.security.authority.SystemAuthoritiesProvider;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Saptarshi Purkayastha
 */
public class DeleteAppAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    @Autowired
    private AppManagerService appManagerService;

    private SystemAuthoritiesProvider authoritiesProvider;

    public void setAuthoritiesProvider( SystemAuthoritiesProvider authoritiesProvider )
    {
        this.authoritiesProvider = authoritiesProvider;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        String appName = ServletActionContext.getRequest().getParameter( "appName" );
        
        if ( null != appName )
        {
            // TODO: Move to AppManagerService
            for ( App app : appManagerService.getInstalledApps() )
            {
                if ( app.getName().equals( appName ) )
                {
                    String folderPath = appManagerService.getAppFolderPath() + File.separator
                        + appManagerService.getAppFolderName( app );
                    FileUtils.forceDelete( new File( folderPath ) );
                    message = i18n.getString( "appmanager_delete_success" );
                }
            }
        }
        
        return SUCCESS;
    }
}
