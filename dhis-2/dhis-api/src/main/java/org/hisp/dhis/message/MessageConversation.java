package org.hisp.dhis.message;

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

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.adapter.UserXmlAdapter;
import org.hisp.dhis.user.User;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;

/**
 * @author Lars Helge Overland
 */
@XmlRootElement( name = "messageConversation", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class MessageConversation
    extends BaseIdentifiableObject
{
    private String subject;

    private Set<UserMessage> userMessages = new HashSet<UserMessage>();

    private List<Message> messages = new ArrayList<Message>();

    private User lastSender;

    private transient boolean read;

    private transient String lastSenderSurname;

    private transient String lastSenderFirstname;

    public MessageConversation()
    {
    }

    public MessageConversation( String subject, User lastSender )
    {
        this.subject = subject;
        this.lastSender = lastSender;
    }

    @Override
    public String getName()
    {
        return subject;
    }

    public void addUserMessage( UserMessage userMessage )
    {
        this.userMessages.add( userMessage );
    }

    public void addMessage( Message message )
    {
        if ( message != null )
        {
            message.setAutoFields();
        }
        
        this.messages.add( message );
    }

    public boolean markRead( User user )
    {
        for ( UserMessage userMessage : userMessages )
        {
            if ( userMessage.getUser() != null && userMessage.getUser().equals( user ) )
            {
                boolean read = userMessage.isRead();
                
                userMessage.setRead( true );

                return !read;
            }
        }
        
        return false;
    }

    public boolean markUnread( User user )
    {
        for ( UserMessage userMessage : userMessages )
        {
            if ( userMessage.getUser() != null && userMessage.getUser().equals( user ) )
            {
                boolean read = userMessage.isRead();
                
                userMessage.setRead( false );

                return read;
            }
        }
        
        return false;
    }

    public void markReplied( User sender, Message message )
    {
        for ( UserMessage userMessage : userMessages )
        {
            if ( userMessage.getUser() != null && !userMessage.getUser().equals( sender ) )
            {
                userMessage.setRead( false );
            }
        }

        addMessage( message );

        this.setLastUpdated( new Date() );
        this.lastSender = sender;
    }

    public void remove( User user )
    {
        Iterator<UserMessage> iterator = userMessages.iterator();

        while ( iterator.hasNext() )
        {
            UserMessage userMessage = iterator.next();

            if ( userMessage.getUser() != null && userMessage.getUser().equals( user ) )
            {
                iterator.remove();

                return;
            }
        }
    }

    public Set<User> getUsers()
    {
        Set<User> users = new HashSet<User>();

        for ( UserMessage userMessage : userMessages )
        {
            users.add( userMessage.getUser() );
        }

        return users;
    }

    @XmlElement
    @JsonProperty
    public String getSubject()
    {
        return subject;
    }

    public void setSubject( String subject )
    {
        this.subject = subject;
    }

    /* @XmlElementWrapper( name = "userMessages" )
    @XmlElement( name = "userMessage" )
    @JsonProperty 
    TODO waiting for idObject version of userMessage */
    public Set<UserMessage> getUserMessages()
    {
        return userMessages;
    }

    public void setUserMessages( Set<UserMessage> userMessages )
    {
        this.userMessages = userMessages;
    }

    @XmlElementWrapper( name = "messages" )
    @XmlElement( name = "message" )
    @JsonProperty
    public List<Message> getMessages()
    {
        return messages;
    }

    public void setMessages( List<Message> messages )
    {
        this.messages = messages;
    }

    @XmlElement
    @XmlJavaTypeAdapter( UserXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    public User getLastSender()
    {
        return lastSender;
    }

    public void setLastSender( User lastSender )
    {
        this.lastSender = lastSender;
    }

    @XmlElement
    @JsonProperty
    public boolean isRead()
    {
        return read;
    }

    public void setRead( boolean read )
    {
        this.read = read;
    }

    public String getLastSenderName()
    {
        return lastSenderFirstname + " " + lastSenderSurname;
    }

    public String getLastSenderSurname()
    {
        return lastSenderSurname;
    }

    public void setLastSenderSurname( String lastSenderSurname )
    {
        this.lastSenderSurname = lastSenderSurname;
    }

    public String getLastSenderFirstname()
    {
        return lastSenderFirstname;
    }

    public void setLastSenderFirstname( String lastSenderFirstname )
    {
        this.lastSenderFirstname = lastSenderFirstname;
    }

    @Override
    public int hashCode()
    {
        return uid.hashCode();
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }

        if ( object == null )
        {
            return false;
        }

        if ( getClass() != object.getClass() )
        {
            return false;
        }

        final MessageConversation other = (MessageConversation) object;

        return uid.equals( other.uid );
    }

    @Override
    public String toString()
    {
        return "[" + subject + "]";
    }
}
