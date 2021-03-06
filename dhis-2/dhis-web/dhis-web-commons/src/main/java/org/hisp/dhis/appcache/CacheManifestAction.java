package org.hisp.dhis.appcache;

/*
 * Copyright (c) 2004-2015, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.i18n.locale.LocaleManager;
import org.hisp.dhis.system.SystemInfo;
import org.hisp.dhis.system.SystemService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw <abyota@gmail.com>
 *
 */
public class CacheManifestAction
    implements Action
{
    @Autowired
    private SystemService systemService;
    
    @Autowired
    private LocaleManager localeManager;

    private String appPath;

    public void setAppPath( String appPath )
    {
        this.appPath = appPath;
    }

    private String i18nPath;

    public void setI18nPath( String i18nPath )
    {
        this.i18nPath = i18nPath;
    }

    private String appCache;

    public void setAppCache( String appCache )
    {
        this.appCache = appCache;
    }

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        File cacheManifest = null;
        File i18nFolder = null;
        StringBuffer stringBuffer = null;

        String locale = localeManager.getCurrentLocale().toString();
        SystemInfo info = systemService.getSystemInfo();
        String revisionTag = "#Revision:" + info.getRevision();

        String defaultTranslationFile = "i18n_app.properties";
        String translationFile = "";
        
        if ( locale.equalsIgnoreCase( "en" ) )
        {
            translationFile = defaultTranslationFile;
        }
        else
        {
            translationFile = "i18n_app_" + locale + ".properties";
        }

        if ( appPath != null && appCache != null )
        {

            ServletContext servletContext = ServletActionContext.getServletContext();
            String fullPath = servletContext.getRealPath( appPath );

            File folder = new File( fullPath );
            File[] files = folder.listFiles();

            for ( int i = 0; i < files.length; i++ )
            {
                if ( files[i].isFile() && files[i].getName().equalsIgnoreCase( appCache ) )
                {
                    cacheManifest = new File( files[i].getAbsolutePath() );
                }

                if ( i18nPath != null && files[i].isDirectory() && files[i].getName().equalsIgnoreCase( i18nPath ) )
                {
                    i18nFolder = new File( files[i].getAbsolutePath() );
                }
            }
        }

        if ( cacheManifest != null )
        {
            try
            {
                FileReader fileReader = new FileReader( cacheManifest );
                BufferedReader bufferedReader = new BufferedReader( fileReader );
                stringBuffer = new StringBuffer();
                String line;
                while ( (line = bufferedReader.readLine()) != null )
                {
                    stringBuffer.append( line );
                    stringBuffer.append( "\n" );
                }
                stringBuffer.append( revisionTag );
                stringBuffer.append( "\n" );

                fileReader.close();

                if ( i18nFolder != null )
                {
                    File[] i18nfiles = i18nFolder.listFiles();
                    Boolean fileExists = false;
                    for ( int i = 0; i < i18nfiles.length; i++ )
                    {
                        if ( i18nfiles[i].isFile() && i18nfiles[i].getName().equalsIgnoreCase( translationFile ) )
                        {
                            fileExists = true;
                            stringBuffer.append( i18nPath + "/" + translationFile );
                            stringBuffer.append( "\n" );
                            break;
                        }
                    }

                    if ( !fileExists )
                    {
                        stringBuffer.append( i18nPath + "/" + defaultTranslationFile );
                        stringBuffer.append( "\n" );

                    }
                }

                inputStream = new ByteArrayInputStream( stringBuffer.toString().getBytes() );

                return SUCCESS;

            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }

        }

        stringBuffer = new StringBuffer();
        stringBuffer.append( "CACHE MANIFEST" );
        stringBuffer.append( revisionTag );
        stringBuffer.append( "\n" );
        stringBuffer.append( "NETWORK:" );
        stringBuffer.append( "\n" );
        stringBuffer.append( "*" );

        inputStream = new ByteArrayInputStream( stringBuffer.toString().getBytes() );

        return SUCCESS;
    }
}