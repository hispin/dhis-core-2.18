package org.hisp.dhis.email;

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

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.message.MessageSender;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Halvdan Hoem Grelland <halvdanhg@gmail.com>
 */
@Transactional
public class DefaultEmailService
    implements EmailService
{
    private static final String TEST_EMAIL_SUBJECT = "Test email from DHIS 2";
    private static final String TEST_EMAIL_TEXT = "This is an automatically generated email from ";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MessageSender emailMessageSender;

    public void setEmailMessageSender(MessageSender emailMessageSender)
    {
        this.emailMessageSender = emailMessageSender;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // EmailService implementation
    // -------------------------------------------------------------------------

    @Override
    public boolean emailEnabled()
    {
        return systemSettingManager.emailEnabled();
    }

    @Override
    public void sendEmail( String subject, String text, User sender, User recipient, boolean forceSend )
    {
        Set<User> recipients = new HashSet<User>();
        recipients.add( recipient );

        emailMessageSender.sendMessage( subject, text, sender, new HashSet<User>( recipients ), forceSend );
    }

    @Override
    public void sendEmail( String subject, String text, User sender, Set<User> recipients, boolean forceSend )
    {
        emailMessageSender.sendMessage( subject, text, sender, new HashSet<User>( recipients ), forceSend );
    }

    @Override
    public void sendTestEmail( )
    {
        String instanceName = systemSettingManager.getSystemSetting( SystemSettingManager.KEY_APPLICATION_TITLE ).toString();
        sendEmail( TEST_EMAIL_SUBJECT, TEST_EMAIL_TEXT + instanceName, null, currentUserService.getCurrentUser(), true );
    }
}
