package org.hisp.dhis.appmanager;

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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.hisp.dhis.appmanager.AppManagerService.KEY_APP_FOLDER_PATH;
import org.hisp.dhis.datavalue.DefaultDataValueService;
import org.hisp.dhis.setting.SystemSettingManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Saptarshi Purkayastha
 */
public class DefaultAppManagerService
    implements AppManagerService
{
    private static final Log log = LogFactory.getLog( DefaultDataValueService.class );

    private HashMap<App, String> appFolderNames;

    @Autowired
    private SystemSettingManager appSettingManager;

    @Override
    public String getAppFolderPath()
    {
        return StringUtils.trimToNull( (String) appSettingManager.getSystemSetting( KEY_APP_FOLDER_PATH ) );
    }

    @Override
    public String getAppStoreUrl()
    {
        return StringUtils.trimToNull( (String) appSettingManager.getSystemSetting( KEY_APP_STORE_URL ) );
    }

    @Override
    public List<App> getInstalledApps()
    {
        this.appFolderNames = new HashMap<App, String>();
        List<App> appList = new ArrayList<App>();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );

        if ( null != getAppFolderPath() )
        {
            File appFolderPath = new File( getAppFolderPath() );
            if ( appFolderPath.isDirectory() )
            {
                File[] listFiles = appFolderPath.listFiles();
                for ( File folder : listFiles )
                {
                    if ( folder.isDirectory() )
                    {
                        File appManifest = new File( folder, "manifest.webapp" );
                        if ( appManifest.exists() )
                        {
                            try
                            {
                                App app = mapper.readValue( appManifest, App.class );
                                appList.add( app );
                                appFolderNames.put( app, folder.getName() );
                            }
                            catch ( IOException ex )
                            {
                                log.error( ex.getLocalizedMessage(), ex );
                            }
                        }
                    }
                }
            }
        }
        
        return appList;
    }

    @Override
    public void setAppFolderPath( String appFolderPath )
    {
        if(!appFolderPath.isEmpty())
        {
            try
            {
                File folder = new File( appFolderPath );
                if ( !folder.exists() )
                {
                    FileUtils.forceMkdir( folder );
                }
            }
            catch ( IOException ex )
            {
                log.error( ex.getLocalizedMessage(), ex );
            }
        }
        appSettingManager.saveSystemSetting( KEY_APP_FOLDER_PATH, appFolderPath );
    }

    @Override
    public void setAppStoreUrl( String appStoreUrl )
    {
        appSettingManager.saveSystemSetting( KEY_APP_STORE_URL, appStoreUrl );
    }

    @Override
    public String getAppFolderName( App app )
    {
        if ( null == appFolderNames )
        {
            getInstalledApps();
        }
        
        return appFolderNames.get( app );
    }
}
