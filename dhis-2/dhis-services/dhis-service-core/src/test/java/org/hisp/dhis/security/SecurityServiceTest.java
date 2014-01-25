package org.hisp.dhis.security;

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

import static org.junit.Assert.*;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class SecurityServiceTest
    extends DhisSpringTest
{
    private UserCredentials credentials;

    private UserCredentials otherCredentials;

    @Autowired
    private UserService userService; 
    
    @Autowired
    private PasswordManager passwordManager;
    
    @Autowired
    private SecurityService securityService;
    
    @Override
    public void setUpTest()
    {        
        credentials = new UserCredentials();
        credentials.setUsername( "johndoe" );
        credentials.setPassword( "" );

        User userA = createUser( 'A' );
        userA.setEmail( "validA@email.com" );
        userA.setUserCredentials( credentials );
        credentials.setUser( userA );
        userService.addUserCredentials( credentials );

        otherCredentials = new UserCredentials();
        otherCredentials.setUsername( "janesmith" );
        otherCredentials.setPassword( "" );

        User userB = createUser( 'B' );
        userB.setEmail( "validB@email.com" );
        userB.setUserCredentials( otherCredentials );
        otherCredentials.setUser( userB );
        userService.addUserCredentials( otherCredentials );
    }

    @Test
    public void testRestoreRecoverPassword()
    {
        String[] result = securityService.initRestore( credentials, RestoreType.RECOVER_PASSWORD );
        
        String token = result[0];
        String code = result[1];

        assertNotNull( token );
        assertNotNull( code );
        assertNotNull( credentials.getRestoreToken() );
        assertNotNull( credentials.getRestoreCode() );
        assertNotNull( credentials.getRestoreExpiry() );

        //
        // verifyToken()
        //
        assertFalse( securityService.verifyToken( otherCredentials, token, RestoreType.RECOVER_PASSWORD ) );

        assertFalse( securityService.verifyToken( credentials, "wrongToken", RestoreType.RECOVER_PASSWORD ) );

        assertFalse( securityService.verifyToken( credentials, token, RestoreType.INVITE ) );

        assertTrue( securityService.verifyToken( credentials, token, RestoreType.RECOVER_PASSWORD ) );

        //
        // canRestoreNow()
        //
        assertFalse( securityService.canRestoreNow( otherCredentials, token, code, RestoreType.RECOVER_PASSWORD ) );

        assertFalse( securityService.canRestoreNow( credentials, "wrongToken", code, RestoreType.RECOVER_PASSWORD ) );

        assertFalse( securityService.canRestoreNow( credentials, token, "wrongCode", RestoreType.RECOVER_PASSWORD ) );

        assertFalse( securityService.canRestoreNow( credentials, token, code, RestoreType.INVITE ) );

        assertTrue( securityService.canRestoreNow( credentials, token, code, RestoreType.RECOVER_PASSWORD ) );

        //
        // restore()
        //
        String password = "NewPassword1";

        assertFalse( securityService.restore( otherCredentials, token, code, password, RestoreType.RECOVER_PASSWORD ) );

        assertFalse( securityService.restore( credentials, "wrongToken", code, password, RestoreType.RECOVER_PASSWORD ) );

        assertFalse( securityService.restore( credentials, token, "wrongCode", password, RestoreType.RECOVER_PASSWORD ) );

        assertFalse( securityService.restore( credentials, token, code, password, RestoreType.INVITE ) );

        assertTrue( securityService.restore( credentials, token, code, password, RestoreType.RECOVER_PASSWORD ) );

        //
        // check password
        //
        String hashedPassword = passwordManager.encodePassword( credentials.getUsername(), password );

        assertEquals( hashedPassword, credentials.getPassword() );
    }

    @Test
    public void testRestoreInvite()
    {
        String[] result = securityService.initRestore( credentials, RestoreType.INVITE );
        String token = result[0];
        String code = result[1];

        assertNotNull( token );
        assertNotNull( code );
        assertNotNull( credentials.getRestoreToken() );
        assertNotNull( credentials.getRestoreCode() );
        assertNotNull( credentials.getRestoreExpiry() );

        //
        // verifyToken()
        //
        assertFalse( securityService.verifyToken( otherCredentials, token, RestoreType.INVITE ) );

        assertFalse( securityService.verifyToken( credentials, "wrongToken", RestoreType.INVITE ) );

        assertFalse( securityService.verifyToken( credentials, token, RestoreType.RECOVER_PASSWORD ) );

        assertTrue( securityService.verifyToken( credentials, token, RestoreType.INVITE ) );

        //
        // canRestoreNow()
        //
        assertFalse( securityService.canRestoreNow( otherCredentials, token, code, RestoreType.INVITE ) );

        assertFalse( securityService.canRestoreNow( credentials, "wrongToken", code, RestoreType.INVITE ) );

        assertFalse( securityService.canRestoreNow( credentials, token, "wrongCode", RestoreType.INVITE ) );

        assertFalse( securityService.canRestoreNow( credentials, token, code, RestoreType.RECOVER_PASSWORD ) );

        assertTrue( securityService.canRestoreNow( credentials, token, code, RestoreType.INVITE ) );

        //
        // restore()
        //
        String password = "NewPassword1";

        assertFalse( securityService.restore( otherCredentials, token, code, password, RestoreType.INVITE ) );

        assertFalse( securityService.restore( credentials, "wrongToken", code, password, RestoreType.INVITE ) );

        assertFalse( securityService.restore( credentials, token, "wrongCode", password, RestoreType.INVITE ) );

        assertFalse( securityService.restore( credentials, token, code, password, RestoreType.RECOVER_PASSWORD ) );

        assertTrue( securityService.restore( credentials, token, code, password, RestoreType.INVITE ) );

        //
        // check password
        //
        String hashedPassword = passwordManager.encodePassword( credentials.getUsername(), password );

        assertEquals( hashedPassword, credentials.getPassword() );
    }
}
