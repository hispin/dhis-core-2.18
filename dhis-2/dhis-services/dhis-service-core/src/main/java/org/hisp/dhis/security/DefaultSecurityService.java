package org.hisp.dhis.security;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.acl.AclService;
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.i18n.locale.LocaleManager;
import org.hisp.dhis.message.MessageSender;
import org.hisp.dhis.period.Cal;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.util.ValidationUtils;
import org.hisp.dhis.system.velocity.VelocityManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.user.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Lars Helge Overland
 */
public class DefaultSecurityService
    implements SecurityService
{
    private static final Log log = LogFactory.getLog( DefaultSecurityService.class );

    private static final String RESTORE_PATH = "/dhis-web-commons/security/";

    private static final int INVITED_USERNAME_UNIQUE_LENGTH = 15;
    private static final int INVITED_USER_PASSWORD_LENGTH = 40;

    private static final int RESTORE_TOKEN_LENGTH = 50;
    private static final int RESTORE_CODE_LENGTH = 15;

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

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private I18nManager i18nManager;

    public void setI18nManager( I18nManager i18nManager )
    {
        this.i18nManager = i18nManager;
    }

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private AclService aclService;

    // -------------------------------------------------------------------------
    // SecurityService implementation
    // -------------------------------------------------------------------------

    public boolean prepareUserForInvite( User user )
    {
        if ( user == null || user.getUserCredentials() == null )
        {
            return false;
        }

        if ( user.getUsername() == null || user.getUsername().isEmpty() )
        {
            String username = "user_invitation_" + CodeGenerator.generateCode( INVITED_USERNAME_UNIQUE_LENGTH );

            user.getUserCredentials().setUsername( username );
        }

        String rawPassword = CodeGenerator.generateCode( INVITED_USER_PASSWORD_LENGTH );

        user.setSurname( "(TBD)" );
        user.setFirstName( "(TBD)" );
        user.getUserCredentials().setPassword( passwordManager.encodePassword( user.getUsername(), rawPassword ) );

        return true;
    }

    public boolean sendRestoreMessage( UserCredentials credentials, String rootPath, RestoreOptions restoreOptions )
    {
        if ( credentials == null || rootPath == null )
        {
            return false;
        }

        RestoreType restoreType = restoreOptions.getRestoreType();

        if ( credentials.getUser() == null || credentials.getUser().getEmail() == null )
        {
            log.info( "Could not send " + restoreType.name() + " message as user does not exist or has no email: " + credentials );
            return false;
        }

        if ( !ValidationUtils.emailIsValid( credentials.getUser().getEmail() ) )
        {
            log.info( "Could not send " + restoreType.name() + " message as email is invalid" );
            return false;
        }

        if ( !systemSettingManager.emailEnabled() )
        {
            log.info( "Could not send " + restoreType.name() + " message as email is not configured" );
            return false;
        }

        if ( credentials.hasAnyAuthority( Arrays.asList( UserAuthorityGroup.CRITICAL_AUTHS ) ) )
        {
            log.info( "Not allowed to  " + restoreType.name() + " users with critical authorities" );
            return false;
        }

        String[] result = initRestore( credentials, restoreOptions );

        Set<User> users = new HashSet<>();
        users.add( credentials.getUser() );

        Map<String, Object> vars = new HashMap<>();
        vars.put( "rootPath", rootPath );
        vars.put( "restorePath", rootPath + RESTORE_PATH + restoreType.getAction() );
        vars.put( "token", result[0] );
        vars.put( "code", result[1] );
        vars.put( "username", credentials.getUsername() );

        User user = credentials.getUser();
        Locale locale = (Locale) userService.getUserSettingValue( user, UserSettingService.KEY_UI_LOCALE, LocaleManager.DHIS_STANDARD_LOCALE );

        I18n i18n = i18nManager.getI18n( locale );
        vars.put( "i18n" , i18n );

        // -------------------------------------------------------------------------
        // Render emails
        // -------------------------------------------------------------------------

        VelocityManager vm = new VelocityManager();

        String text1 = vm.render( vars, restoreType.getEmailTemplate() + "1" ),
               text2 = vm.render( vars, restoreType.getEmailTemplate() + "2" );

        String subject1 = i18n.getString( restoreType.getEmailSubject() ) + " (" + i18n.getString( "message" ).toLowerCase() + " 1 / 2)",
               subject2 = i18n.getString( restoreType.getEmailSubject() ) + " (" + i18n.getString( "message" ).toLowerCase() + " 2 / 2)";

        // -------------------------------------------------------------------------
        // Send emails
        // -------------------------------------------------------------------------

        emailMessageSender.sendMessage( subject1, text1, null, users, true );
        emailMessageSender.sendMessage( subject2, text2, null, users, true );

        return true;
    }

    public String[] initRestore( UserCredentials credentials, RestoreOptions restoreOptions )
    {
        String token = restoreOptions.getTokenPrefix() + CodeGenerator.generateCode( RESTORE_TOKEN_LENGTH );
        String code = CodeGenerator.generateCode( RESTORE_CODE_LENGTH );

        String hashedToken = passwordManager.encodePassword( credentials.getUsername(), token );
        String hashedCode = passwordManager.encodePassword( credentials.getUsername(), code );

        RestoreType restoreType = restoreOptions.getRestoreType();

        Date expiry = new Cal().now().add( restoreType.getExpiryIntervalType(), restoreType.getExpiryIntervalCount() ).time();

        credentials.setRestoreToken( hashedToken );
        credentials.setRestoreCode( hashedCode );
        credentials.setRestoreExpiry( expiry );

        userService.updateUserCredentials( credentials );

        String[] result = { token, code };
        return result;
    }

    public RestoreOptions getRestoreOptions( String token )
    {
        return RestoreOptions.getRestoreOptions( token );
    }

    public boolean restore( UserCredentials credentials, String token, String code, String newPassword, RestoreType restoreType )
    {
        if ( credentials == null || token == null || code == null || newPassword == null
            || !canRestoreNow( credentials, token, code, restoreType ) )
        {
            return false;
        }

        String username = credentials.getUsername();

        newPassword = passwordManager.encodePassword( username, newPassword );

        credentials.setPassword( newPassword );

        credentials.setRestoreCode( null );
        credentials.setRestoreToken( null );
        credentials.setRestoreExpiry( null );

        userService.updateUserCredentials( credentials );

        return true;
    }

    public boolean canRestoreNow( UserCredentials credentials, String token, String code, RestoreType restoreType )
    {
        if ( !verifyToken( credentials, token, restoreType ) )
        {
            return false;
        }

        String username = credentials.getUsername();

        String encodedToken = passwordManager.encodePassword( username, token );
        String encodedCode = passwordManager.encodePassword( username, code );

        Date date = new Cal().now().time();

        return credentials.canRestore( encodedToken, encodedCode, date );
    }

    public boolean verifyToken( UserCredentials credentials, String token, RestoreType restoreType )
    {
        if ( credentials == null || token == null || restoreType == null )
        {
            return false;
        }

        RestoreOptions restoreOptions = RestoreOptions.getRestoreOptions( token );

        if ( restoreOptions == null )
        {
            log.info( "Can't parse restore options for " + restoreType.name() + " from token " + token + " for user " + credentials );
            return false;
        }

        if ( restoreType != restoreOptions.getRestoreType() )
        {
            log.info( "Wrong prefix for restore type " + restoreType.name() + " on token " + token + " for user " + credentials );
            return false;
        }

        if ( credentials.getRestoreToken() == null )
        {
            log.info( "Could not verify token for " + restoreType.name() + " as user has no token: " + credentials );
            return false;
        }

        token = passwordManager.encodePassword( credentials.getUsername(), token );

        return credentials.getRestoreToken().equals( token );
    }

    @Override
    public boolean canCreatePublic( IdentifiableObject identifiableObject )
    {
        return !aclService.isShareable( identifiableObject.getClass() )
            || aclService.canCreatePublic( currentUserService.getCurrentUser(), identifiableObject.getClass() );
    }

    @Override
    public boolean canCreatePublic( String type )
    {
        Class<? extends IdentifiableObject> klass = aclService.classForType( type );

        return !aclService.isShareable( klass )
            || aclService.canCreatePublic( currentUserService.getCurrentUser(), klass );
    }

    @Override
    public boolean canCreatePrivate( IdentifiableObject identifiableObject )
    {
        return !aclService.isShareable( identifiableObject.getClass() )
            || aclService.canCreatePrivate( currentUserService.getCurrentUser(), identifiableObject.getClass() );
    }

    @Override
    public boolean canCreatePrivate( String type )
    {
        Class<? extends IdentifiableObject> klass = aclService.classForType( type );

        return !aclService.isShareable( klass )
            || aclService.canCreatePrivate( currentUserService.getCurrentUser(), klass );
    }

    @Override
    public boolean canRead( IdentifiableObject identifiableObject )
    {
        return !aclService.isSupported( identifiableObject.getClass() )
            || aclService.canRead( currentUserService.getCurrentUser(), identifiableObject );
    }

    @Override
    public boolean canWrite( IdentifiableObject identifiableObject )
    {
        return !aclService.isSupported( identifiableObject.getClass() )
            || aclService.canWrite( currentUserService.getCurrentUser(), identifiableObject );
    }

    @Override
    public boolean canUpdate( IdentifiableObject identifiableObject )
    {
        return !aclService.isSupported( identifiableObject.getClass() )
            || aclService.canUpdate( currentUserService.getCurrentUser(), identifiableObject );
    }

    @Override
    public boolean canDelete( IdentifiableObject identifiableObject )
    {
        return !aclService.isSupported( identifiableObject.getClass() )
            || aclService.canDelete( currentUserService.getCurrentUser(), identifiableObject );
    }

    @Override
    public boolean canManage( IdentifiableObject identifiableObject )
    {
        return !aclService.isShareable( identifiableObject.getClass() )
            || aclService.canManage( currentUserService.getCurrentUser(), identifiableObject );
    }
}
