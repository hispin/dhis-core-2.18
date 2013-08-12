package org.hisp.dhis.sms;

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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.message.Message;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.message.MessageConversationStore;
import org.hisp.dhis.message.MessageSender;
import org.hisp.dhis.message.UserMessage;
import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.incoming.IncomingSmsListener;
import org.hisp.dhis.sms.parse.ParserType;
import org.hisp.dhis.sms.parse.SMSParserException;
import org.hisp.dhis.smscommand.SMSCommand;
import org.hisp.dhis.smscommand.SMSCommandService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserService;
import org.springframework.transaction.annotation.Transactional;

public class DHISMessageAlertListener
    implements IncomingSmsListener
{
    private SmsSender smsSender;

    private MessageSender emailMessageSender;

    private MessageConversationStore messageConversationStore;

    private SMSCommandService smsCommandService;

    private UserService userService;

    public SMSCommandService getSmsCommandService()
    {
        return smsCommandService;
    }

    public void setSmsCommandService( SMSCommandService smsCommandService )
    {
        this.smsCommandService = smsCommandService;
    }
    
    @Transactional
    @Override
    public boolean accept( IncomingSms sms )
    {
        String message = sms.getText();
        String commandString = null;
        if ( message.indexOf( " " ) > 0 )
        {
            commandString = message.substring( 0, message.indexOf( " " ) );
            message = message.substring( commandString.length() );
        }
        else
        {
            commandString = message;
        }

        return smsCommandService.getSMSCommand( commandString, ParserType.ALERT_PARSER ) != null;
    }
    
    @Transactional
    @Override
    public void receive( IncomingSms sms )
    {
        String message = sms.getText();
        String commandString = null;
        if ( message.indexOf( " " ) > 0 )
        {
            commandString = message.substring( 0, message.indexOf( " " ) );
            message = message.substring( commandString.length() );
        }
        else
        {
            commandString = message;
        }

        SMSCommand smsCommand = smsCommandService.getSMSCommand( commandString, ParserType.ALERT_PARSER );
        UserGroup userGroup = smsCommand.getUserGroup();
        String senderPhoneNumber = StringUtils.replace( sms.getOriginator(), "+", "" );

        if ( userGroup != null )
        {
            Collection<User> users = userService.getUsersByPhoneNumber( senderPhoneNumber );

            if ( users != null && users.size() > 1 )
            {
                String messageMoreThanOneUser = "System only accepts sender's number assigned for one user, but found more than one user for this number: ";
                for ( Iterator<User> i = users.iterator(); i.hasNext(); )
                {
                    User user = i.next();
                    messageMoreThanOneUser += " " + user.getName();
                    if ( i.hasNext() )
                    {
                        messageMoreThanOneUser += ",";
                    }
                }
                throw new SMSParserException( messageMoreThanOneUser );
            }
            else if ( users != null && users.size() == 1 )
            {
                User sender = users.iterator().next();

                Set<User> receivers = new HashSet<User>( userGroup.getMembers() );

                // forward to user group by SMS
                smsSender.sendMessage( smsCommand.getName(), message, sender, receivers, true );

                // forward to user group by E-mail
                emailMessageSender.sendMessage( smsCommand.getName(), message, sender, receivers, false );

                // forward to user group by dhis message
                if ( sender != null )
                {
                    receivers.add( sender );
                }

                MessageConversation conversation = new MessageConversation( smsCommand.getName(), sender );

                conversation.addMessage( new Message( message, null, sender ) );

                for ( User receiver : receivers )
                {
                    boolean read = receiver != null && receiver.equals( sender );

                    conversation.addUserMessage( new UserMessage( receiver, read ) );
                }
                messageConversationStore.save( conversation );
                // confirm SMS was received and forwarded completely
                Set<User> feedbackList = new HashSet<User>();
                feedbackList.add( sender );
                smsSender.sendMessage( smsCommand.getName(), smsCommand.getReceivedMessage(), null,
                    feedbackList, true );
            }
            else if ( users == null || users.size() == 0 )
            {
                throw new SMSParserException(
                    "No user associated with this phone number. Please contact your supervisor." );

            }
        }
    }

    public SmsSender getSmsSender()
    {
        return smsSender;
    }

    public void setSmsSender( SmsSender smsSender )
    {
        this.smsSender = smsSender;
    }

    public MessageSender getEmailMessageSender()
    {
        return emailMessageSender;
    }

    public void setEmailMessageSender( MessageSender emailMessageSender )
    {
        this.emailMessageSender = emailMessageSender;
    }

    public MessageConversationStore getMessageConversationStore()
    {
        return messageConversationStore;
    }

    public void setMessageConversationStore( MessageConversationStore messageConversationStore )
    {
        this.messageConversationStore = messageConversationStore;
    }

    public UserService getUserService()
    {
        return userService;
    }

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }
}
