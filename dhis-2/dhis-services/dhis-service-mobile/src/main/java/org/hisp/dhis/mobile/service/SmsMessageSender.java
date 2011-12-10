package org.hisp.dhis.mobile.service;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.api.sms.OutboundSmsService;
import org.hisp.dhis.message.MessageSender;
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

    @Autowired
    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    @Autowired( required = false )
    public void setOutboundSmsService( OutboundSmsService outboundSmsService )
    {
        this.outboundSmsService = outboundSmsService;
        
        log.info( "Found OutboundMessageService " + outboundSmsService.getClass().getSimpleName() + ". Enabling sms message sending.");
    }

    
    // -------------------------------------------------------------------------
    // MessageSender implementation
    // -------------------------------------------------------------------------


    @Override
    public void sendMessage( String subject, String text, User sender, Set<User> users )
    {

        if ( outboundSmsService == null || !outboundSmsService.isSmsServiceAvailable() )
        {
            return;
        }

        String name = "unknown";
        if ( sender != null )
            name = sender.getUsername();

        text = "From " + name + ", " + subject + ": " + text;

        // Simplistic cutoff 160 characters..
        int length = text.length();
        if ( length > 160 )
            text = text.substring( 0, 157 ) + "...";

        Map<User, Serializable> settings = userService.getUserSettings( KEY_MESSAGE_SMS_NOTIFICATION, false );

        List<String> recipients = new ArrayList<String>();

        for ( User user : users )
        {
            boolean smsNotification = settings.get( user ) != null && (Boolean) settings.get( user );

            String phoneNumber = user.getPhoneNumber();
            if ( smsNotification && phoneNumber != null && !phoneNumber.trim().isEmpty() )
            {
                recipients.add( phoneNumber );
                
                if (log.isDebugEnabled())
                    log.debug( "Adding user as sms recipient: " + user + " with phone number: " + phoneNumber );
            }
        }

        if ( !recipients.isEmpty() )
        {
            outboundSmsService.sendMessage( text, recipients.toArray( new String[recipients.size()] ) );
            if (log.isDebugEnabled()) {
                log.debug( "Sent message to " + recipients + ": " + text );
            }
        } else if ( log.isDebugEnabled() ) {
            log.debug( "No user to send message to" );
        }

    }

}
