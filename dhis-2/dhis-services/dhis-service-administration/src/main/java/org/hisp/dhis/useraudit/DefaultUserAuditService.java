package org.hisp.dhis.useraudit;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.options.SystemSettingManager;
import org.springframework.transaction.annotation.Transactional;

import static org.hisp.dhis.options.SystemSettingManager.KEY_MAX_NUMBER_OF_ATTEMPTS;
import static org.hisp.dhis.options.SystemSettingManager.KEY_TIMEFRAME_MINUTES;

/**
 * @author Lars Helge Overland
 *
 * TODO: Cleanup code by MAX_NUMBER_OF_ATTEMPTS and TIMEFRAME_MINUTES loading
 * in system setting with default values through startup routine
 */
public class DefaultUserAuditService
    implements UserAuditService
{

    private static final Log log = LogFactory.getLog( DefaultUserAuditService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private UserAuditStore userAuditStore;

    public void setUserAuditStore( UserAuditStore userAuditStore )
    {
        this.userAuditStore = userAuditStore;
    }

    @Override
    public void registerLoginSuccess( String username )
    {
        log.info( "User login success: '" + username + "'" );

        resetLockoutTimeframe( username );
    }

    @Override
    public void registerLogout( String username )
    {
        log.info( "User logout: '" + username + "'" );
    }

    @Transactional
    @Override
    public void registerLoginFailure( String username )
    {
        log.info( "User login failure: '" + username + "'" );

        userAuditStore.saveLoginFailure( new LoginFailure( username, new Date() ) );

        int no = userAuditStore.getLoginFailures( username, getDate() );

        int MAX_NUMBER_OF_ATTEMPTS = 5; //DEFAULT

        if ( systemSettingManager.getSystemSetting( KEY_MAX_NUMBER_OF_ATTEMPTS ) != null )
        {
            MAX_NUMBER_OF_ATTEMPTS = (Integer) systemSettingManager.getSystemSetting( KEY_MAX_NUMBER_OF_ATTEMPTS );
        } else
        {
            systemSettingManager.saveSystemSetting( KEY_MAX_NUMBER_OF_ATTEMPTS, 5 );
        }

        if ( no >= MAX_NUMBER_OF_ATTEMPTS )
        {
            log.info( "Max number of login attempts exceeded: '" + username + "'" );
        }
    }

    @Transactional
    @Override
    public int getLoginFailures( String username )
    {
        int no = userAuditStore.getLoginFailures( username, getDate() );
        return no;
    }

    @Override
    public int getMaxAttempts()
    {
        int MAX_NUMBER_OF_ATTEMPTS = 5;

        if ( systemSettingManager.getSystemSetting( KEY_MAX_NUMBER_OF_ATTEMPTS ) != null )
        {
            MAX_NUMBER_OF_ATTEMPTS = (Integer) systemSettingManager.getSystemSetting( KEY_MAX_NUMBER_OF_ATTEMPTS );
        } else
        {
            systemSettingManager.saveSystemSetting( KEY_MAX_NUMBER_OF_ATTEMPTS, 5 );
        }

        return MAX_NUMBER_OF_ATTEMPTS;
    }

    @Override
    public int getLockoutTimeframe()
    {
        int TIMEFRAME_MINUTES = 10; //DEFAULT

        if ( systemSettingManager.getSystemSetting( KEY_TIMEFRAME_MINUTES ) != null )
        {
            TIMEFRAME_MINUTES = (Integer) systemSettingManager.getSystemSetting( KEY_TIMEFRAME_MINUTES );
        } else
        {
            systemSettingManager.saveSystemSetting( KEY_TIMEFRAME_MINUTES, 10 );
        }

        return TIMEFRAME_MINUTES;
    }

    @Override
    public void resetLockoutTimeframe( String username )
    {
        userAuditStore.resetLoginFailures( username, getDate() );
    }

    private Date getDate()
    {
        int TIMEFRAME_MINUTES = 10;

        if ( systemSettingManager.getSystemSetting( KEY_TIMEFRAME_MINUTES ) != null )
        {
            TIMEFRAME_MINUTES = (Integer) systemSettingManager.getSystemSetting( KEY_TIMEFRAME_MINUTES );
        } else
        {
            systemSettingManager.saveSystemSetting( KEY_TIMEFRAME_MINUTES, 10 );
        }

        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.MINUTE, TIMEFRAME_MINUTES * -1 );
        return cal.getTime();
    }
}
