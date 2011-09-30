package org.hisp.dhis.message;

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

import static org.hisp.dhis.options.SystemSettingManager.KEY_EMAIL_HOST_NAME;
import static org.hisp.dhis.options.SystemSettingManager.KEY_EMAIL_PASSWORD;
import static org.hisp.dhis.options.SystemSettingManager.KEY_EMAIL_USERNAME;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.user.User;

/**
 * @author Lars Helge Overland
 */
public class EmailMessageSender
    implements MessageSender
{
    private static final int SMTP_PORT = 587;
    private static final String FROM_ADDRESS = "noreply@dhis2.org";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;
    
    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }
    
    // -------------------------------------------------------------------------
    // MessageSender implementation
    // -------------------------------------------------------------------------

    @Override
    public void sendMessage( String subject, String text, Set<User> users )
    {
        String hostName = StringUtils.trimToNull( (String) systemSettingManager.getSystemSetting( KEY_EMAIL_HOST_NAME ) );
        String username = StringUtils.trimToNull( (String) systemSettingManager.getSystemSetting( KEY_EMAIL_USERNAME ) );
        String password = StringUtils.trimToNull( (String) systemSettingManager.getSystemSetting( KEY_EMAIL_PASSWORD ) );
        
        if ( hostName == null || username == null || password == null )
        {
            return;
        }
        
        for ( User user : users )
        {
            try
            {
                String toAddress = StringUtils.trimToNull( user.getEmail() );
                
                if ( user.getEmail() != null )
                {
                    Email email = getEmail( hostName, username, password );
                    email.setSubject( subject );
                    email.setMsg( text );
                    email.addTo( toAddress );
                    email.send();
                }
            }
            catch ( EmailException ex )
            {
                throw new RuntimeException( ex );
            }
        }
    }

    private Email getEmail( String hostName, String username, String password )
        throws EmailException
    {
        Email email = new SimpleEmail();
        email.setHostName( hostName );
        email.setSmtpPort( SMTP_PORT );
        email.setAuthenticator( new DefaultAuthenticator( username, password ) );
        email.setTLS( true );
        email.setFrom( FROM_ADDRESS );
        
        return email;
    }
}
