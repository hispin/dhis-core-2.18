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

import org.hisp.dhis.user.User;

import java.util.Set;

/**
 * @author Halvdan Hoem Grelland <halvdanhg@gmail.com>
 */
public interface EmailService
{
    /**
     * Checks whether email is configured for the system or not.
     * @return true if all necessary email configurations are set.
     */
    boolean emailEnabled();

    /**
     * Sends an email to the recipient user from the sender.
     *
     * @param subject the subject text of the email.
     * @param text the text (body) of the email.
     * @param sender the sender of the email.
     * @param recipient the recipient of the email.
     * @param forceSend if true the email is sent regardless of the recipients' email notification settings.
     */
    void sendEmail( String subject, String text, User sender, User recipient, boolean forceSend );

    /**
     * Sends an email to multiple recipients from the sender.
     *
     * @param subject the subject text of the email.
     * @param text the text (body) of the email.
     * @param sender the sender of the email.
     * @param recipients the recipients of the email.
     * @param forceSend if true the email is sent regardless of the email notification settings of the recipients.
     */
    void sendEmail( String subject, String text, User sender, Set<User> recipients, boolean forceSend);

    /**
     * Sends an automatically generated email message to the current user.
     * Useful for testing the SMTP configuration of the system.
     */
    void sendTestEmail( );
}
