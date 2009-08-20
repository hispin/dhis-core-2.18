package org.hisp.dhis.user.action;

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

import org.hisp.dhis.security.PasswordManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserStore;

import com.opensymphony.xwork2.Action;

public class ChangePasswordAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserStore userStore;

    private PasswordManager passwordManager;

    private CurrentUserService currentUserService;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String username;

    private String rawPassword;

    private String retypePassword;

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    public void setPasswordManager( PasswordManager passwordManager )
    {
        this.passwordManager = passwordManager;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    public void setRawPassword( String rawPassword )
    {
        this.rawPassword = rawPassword;
    }

    public void setRetypePassword( String retypePassword )
    {
        this.retypePassword = retypePassword;
    }

    // -------------------------------------------------------------------------
    // Implement Method
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        User user = userStore.getUser( currentUserService.getCurrentUser().getId() );

        UserCredentials userCredentials = userStore.getUserCredentials( user );

        username = userCredentials.getUsername();

        if ( rawPassword == null || retypePassword == null )
        {

            return INPUT;
        }

        rawPassword = rawPassword.trim();
        retypePassword = retypePassword.trim();

        if ( rawPassword.length() == 0 || retypePassword.length() == 0 )
        {

            return INPUT;
        }
        if ( !rawPassword.equals( retypePassword ) )
        {

            return INPUT;
        }

        userCredentials.setPassword( passwordManager.encodePassword( userCredentials.getUsername(), rawPassword ) );

        userStore.updateUserCredentials( userCredentials );

        userStore.updateUser( user );

        return SUCCESS;
    }
}