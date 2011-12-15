package org.hisp.dhis.sms.outbound;

/*
 * Copyright (c) 2011, University of Oslo
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

import static org.hisp.dhis.user.UserSettingService.KEY_MESSAGE_SMS_NOTIFICATION;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.message.MessageSender;
import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class SmsMessageSender
    implements MessageSender
{
    private static final Log log = LogFactory.getLog( SmsMessageSender.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserService userService;

    private OutboundSmsService outboundSmsService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    public void setOutboundSmsService( OutboundSmsService outboundSmsService )
    {
        this.outboundSmsService = outboundSmsService;
    }

    // -------------------------------------------------------------------------
    // MessageSender implementation
    // -------------------------------------------------------------------------

    @Override
    public void sendMessage( String subject, String text, User sender, Set<User> users )
    {

        if ( outboundSmsService == null || !outboundSmsService.isEnabled() )
        {
            return;
        }

        text = createMessage( subject, text, sender );

        Set<String> recipients = getRecipients( users );

        if ( !recipients.isEmpty() )
        {
            sendMessage( text, recipients );

        }
        else if ( log.isDebugEnabled() )
        {
            log.debug( "Not sending message to any of the recipients" );
        }

    }

    private Set<String> getRecipients( Set<User> users )
    {
        Set<String> recipients = new HashSet<String>();

        Map<User, Serializable> settings = userService.getUserSettings( KEY_MESSAGE_SMS_NOTIFICATION, false );

        for ( User user : users )
        {
            boolean smsNotification = settings.get( user ) != null && (Boolean) settings.get( user );

            String phoneNumber = user.getPhoneNumber();
            if ( smsNotification && phoneNumber != null && !phoneNumber.trim().isEmpty() )
            {
                recipients.add( phoneNumber );

                if ( log.isDebugEnabled() )
                    log.debug( "Adding user as sms recipient: " + user + " with phone number: " + phoneNumber );
            }
        }
        return recipients;
    }

    private String createMessage( String subject, String text, User sender )
    {
        String name = "unknown";
        if ( sender != null )
            name = sender.getUsername();

        text = "From " + name + ", " + subject + ": " + text;

        // Simplistic cutoff 160 characters..
        int length = text.length();
        if ( length > 160 )
            text = text.substring( 0, 157 ) + "...";
        return text;
    }

    private void sendMessage( String text, Set<String> recipients )
    {
        OutboundSms sms = new OutboundSms();
        sms.setMessage( text );
        sms.setRecipients( recipients );

        try
        {
            outboundSmsService.sendMessage( sms );

            if ( log.isDebugEnabled() )
            {
                log.debug( "Sent message to " + recipients + ": " + text );
            }
        }
        catch ( SmsServiceException e )
        {
            log.warn( "Unable to send message through sms: " + sms, e );
        }
    }

}
