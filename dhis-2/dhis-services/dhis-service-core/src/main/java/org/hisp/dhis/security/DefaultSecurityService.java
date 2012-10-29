package org.hisp.dhis.security;

/*
 * Copyright (c) 2004-2012, University of Oslo
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.message.MessageSender;
import org.hisp.dhis.period.Cal;
import org.hisp.dhis.system.velocity.VelocityManager;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;

/**
 * @author Lars Helge Overland
 */
public class DefaultSecurityService
    implements SecurityService
{
    private static final String RESTORE_PATH = "/dhis-web-commons/security/restore.action";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PasswordManager passwordManager;

    public void setPasswordManager( PasswordManager passwordManager )
    {
        this.passwordManager = passwordManager;
    }

    private MessageSender emailMessageSender;

    public void setEmailMessageSender( MessageSender emailMessageSender )
    {
        this.emailMessageSender = emailMessageSender;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    // -------------------------------------------------------------------------
    // SecurityService implementation
    // -------------------------------------------------------------------------

    public boolean sendRestoreMessage( String username, String rootPath )
    {
        if ( username == null || rootPath == null )
        {
            return false;
        }
        
        UserCredentials credentials = userService.getUserCredentialsByUsername( username );
        
        if ( credentials == null )
        {
            return false;
        }
        
        // TODO check if email is configured
        
        String[] result = initRestore( credentials );
        
        Set<User> users = new HashSet<User>();
        users.add( credentials.getUser() );
        
        Map<String, String> vars = new HashMap<String, String>();
        vars.put( "rootPath", rootPath );
        vars.put( "restorePath", rootPath + RESTORE_PATH );
        vars.put( "token", result[0] );
        vars.put( "code", result[1] );
        vars.put( "username", username );
        
        String text1 = new VelocityManager().render( vars, "restore_message1.vm" );
        String text2 = new VelocityManager().render( vars, "restore_message2.vm" );
        
        emailMessageSender.sendMessage( "User account restore confirmation (message 1 of 2)", text1, null, users );
        emailMessageSender.sendMessage( "User account restore confirmation (message 2 of 2)", text2, null, users );
        
        return true;
    }

    public String[] initRestore( UserCredentials credentials )
    {
        String token = CodeGenerator.generateCode( 40 );
        String code = CodeGenerator.generateCode( 15 );
        
        String hashedToken = passwordManager.encodePassword( credentials.getUsername(), token );
        String hashedCode = passwordManager.encodePassword( credentials.getUsername(), code );
        
        Date expiry = new Cal().now().add( Calendar.HOUR_OF_DAY, 1 ).time();
        
        credentials.setRestoreToken( hashedToken );
        credentials.setRestoreCode( hashedCode );
        credentials.setRestoreExpiry( expiry );

        userService.updateUserCredentials( credentials );
        
        String[] result = { token, code };
        return result;
    }
    
    public boolean restore( String username, String token, String code, String newPassword )
    {
        if ( username == null || token == null || code == null || newPassword == null )
        {
            return false;
        }

        UserCredentials credentials = userService.getUserCredentialsByUsername( username );
        
        if ( credentials == null )
        {
            return false;
        }
        
        token = passwordManager.encodePassword( username, token );
        code = passwordManager.encodePassword( username, code );
        
        Date date = new Cal().now().time();

        if ( !credentials.canRestore( token, code, date ) )
        {
            return false;
        }
        
        newPassword = passwordManager.encodePassword( username, newPassword );
        
        credentials.setPassword( newPassword );
        
        credentials.setRestoreCode( null );
        credentials.setRestoreToken( null );
        credentials.setRestoreExpiry( null );
        
        userService.updateUserCredentials( credentials );
        
        return true;
    }
    
    public boolean verifyToken( String username, String token )
    {
        if ( username == null || token == null )
        {
            return false;
        }

        UserCredentials credentials = userService.getUserCredentialsByUsername( username );
        
        if ( credentials == null || credentials.getRestoreToken() == null )
        {
            return false;
        }
        
        token = passwordManager.encodePassword( username, token );
        
        return credentials.getRestoreToken().equals( token );
    }
}
