package org.hisp.dhis.settings.action.system;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import static org.hisp.dhis.setting.SystemSettingManager.DEFAULT_APPLICATION_TITLE;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_APPLICATION_FOOTER;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_APPLICATION_INTRO;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_APPLICATION_NOTIFICATION;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_APPLICATION_TITLE;

import java.util.Hashtable;
import java.util.Map;

import org.hisp.dhis.setting.SystemSettingManager;

import com.opensymphony.xwork2.Action;

/**
 * @author James Chang
 */
public class GetAppearanceSettingsStringAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    
    private String localeCode;

    public void setLocaleCode( String localeCode )
    {
        this.localeCode = localeCode;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Map<String, Object> translations = new Hashtable<String, Object>();

    public Map<String, Object> getTranslations()
    {
        return translations;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        // Add the key application data (with localeCode name) into translations map object
        translations.put( KEY_APPLICATION_TITLE, systemSettingManager.getSystemSetting( KEY_APPLICATION_TITLE + localeCode, DEFAULT_APPLICATION_TITLE ) );
        translations.put( KEY_APPLICATION_INTRO, systemSettingManager.getSystemSetting( KEY_APPLICATION_INTRO + localeCode, "" ) );
        translations.put( KEY_APPLICATION_NOTIFICATION, systemSettingManager.getSystemSetting( KEY_APPLICATION_NOTIFICATION + localeCode, "" ) );
        translations.put( KEY_APPLICATION_FOOTER, systemSettingManager.getSystemSetting( KEY_APPLICATION_FOOTER + localeCode, "" ) );
        
        return SUCCESS;
    }
}
