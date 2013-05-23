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

import java.util.List;

/**
 * @author Saptarshi Purkayastha
 */
public interface AppManagerService
{

    String ID = AppManagerService.class.getName();

    final String KEY_APP_FOLDER_PATH = "appFolderPath";

    final String KEY_APP_STORE_URL = "appStoreUrl";

    /**
     * Returns the full path to the folder where apps are extracted
     * @return app folder path 
     */
    String getAppFolderPath();

    /**
     * Returns the url of the app repository
     * @return url of appstore 
     */
    String getAppStoreUrl();

    /**
     * Returns a list of all the installed apps at @see getAppFolderPath
     * @return list of installed apps
     */
    List<App> getInstalledApps();

    /**
     * Returns the name of the specfic app folder
     * @param app
     * @return folder name of where app is installed
     */
    String getAppFolderName( App app );

    /**
     * Saves the folder in which apps will be expanded 
     */
    void setAppFolderPath( String appFolderPath );

    /**
     * Saves the URL of the apps repository
     */
    void setAppStoreUrl( String appStoreUrl );

}
