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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.ant.compress.taskdefs.Unzip;
import org.hisp.dhis.appmanager.AppManagerService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.security.authority.SystemAuthoritiesProvider;
import org.hisp.dhis.system.util.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Saptarshi Purkayastha
 */
public class AddAppAction
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

    private File file;

    public void setUpload( File file )
    {
        this.file = file;
    }

    private String fileName;

    public void setUploadFileName( String fileName )
    {
        this.fileName = fileName;
    }

    private String contentType;

    public void setUploadContentType( String contentType )
    {
        this.contentType = contentType;
    }

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
        if ( null != file )
        {
            if ( StreamUtils.isZip( new BufferedInputStream( new FileInputStream( file ) ) ) )
            {
                boolean manifestFound = false;
                ZipInputStream zis = new ZipInputStream( new FileInputStream( file ) );
                ZipEntry ze;
                
                while ( (ze = zis.getNextEntry()) != null )
                {
                    if ( ze.getName().equals( "manifest.webapp" ) )
                    {
                        manifestFound = true;
                        //TODO: Check duplicate app installation
                        String dest = appManagerService.getAppFolderPath() + File.separator
                            + fileName.substring( 0, fileName.lastIndexOf( '.' ) );
                        Unzip unzip = new Unzip();
                        unzip.setSrc( file );
                        unzip.setDest( new File( dest ) );
                        unzip.execute();
                        message = i18n.getString( "appmanager_install_success" );
                    }
                }
                
                zis.close();
                
                if ( !manifestFound )
                {
                    message = i18n.getString( "appmanager_invalid_package" );
                }
            }
            else
            {
                message = i18n.getString( "appmanager_not_zip" );
            }
        }
        
        return SUCCESS;
    }
}
