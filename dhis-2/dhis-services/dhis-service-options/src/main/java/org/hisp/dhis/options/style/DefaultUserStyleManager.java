
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

package org.hisp.dhis.options.style;

import java.io.File;

import org.hisp.dhis.user.UserSettingService;

/**
 * @author Chau Thu Tran
 * @version DefaultUserStyleManager.java 2010-10-26 20:08:27Z $
 */

public class DefaultUserStyleManager
    implements UserStyleManager
{
    private static final String SETTING_NAME_STYLE = "currentStyle";

    private static final String SEPARATOR = "/";

    private static final String SYSTEM_SEPARATOR = File.separator;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }

    // -------------------------------------------------------------------------
    // UserStyleManager implementation
    // -------------------------------------------------------------------------

    public void setCurrentStyle( String file )
    {
        userSettingService.saveUserSetting( SETTING_NAME_STYLE, file );
    }

    public String getCurrentStyle()
    {
        return (String) userSettingService.getUserSetting( SETTING_NAME_STYLE );
    }

    public String getCurrentStyleDirectory()
    {
        String currentStyle = getCurrentStyle();

        if ( currentStyle.lastIndexOf( SEPARATOR ) != -1 )
        {
            return currentStyle.substring( 0, currentStyle.lastIndexOf( SEPARATOR ) );
        }

        if ( currentStyle.lastIndexOf( SYSTEM_SEPARATOR ) != -1 )
        {
            return currentStyle.substring( 0, currentStyle.lastIndexOf( SYSTEM_SEPARATOR ) );
        }

        return currentStyle;
    }
}
