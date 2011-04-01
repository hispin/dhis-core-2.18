package org.hisp.dhis.message;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class MessageServiceTest
    extends DhisSpringTest
{
    private User sender;
    private User userA;
    private User userB;

    private Message messageA;
    private Message messageB;
    
    private UserMessage userMessageA;
    private UserMessage userMessageB;
    private UserMessage userMessageC;
    private UserMessage userMessageD;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        userService = (UserService) getBean( UserService.ID );
        messageService = (MessageService) getBean( MessageService.ID );
        
        sender = createUser( 'S');
        userA = createUser( 'A' );
        userB = createUser( 'B' );

        userService.addUser( sender );
        userService.addUser( userA );
        userService.addUser( userB );

        messageA = new Message( "SubjectA", "TextA", sender );
        messageB = new Message( "SubjectB", "TextB", sender );
        
        userMessageA = new UserMessage( userA, messageA );
        userMessageB = new UserMessage( userB, messageA );
        userMessageC = new UserMessage( userA, messageB );
        userMessageD = new UserMessage( userB, messageB );
        
        messageA.getUserMessages().add( userMessageA );
        messageA.getUserMessages().add( userMessageB );
        messageB.getUserMessages().add( userMessageC );
        messageB.getUserMessages().add( userMessageD );
    }
    
    @Test
    public void testSendMessage()
    {
        messageA = new Message( "SubjectA", "TextA", sender );
        
        Set<User> users = new HashSet<User>();
        users.add( userA );
        users.add( userB );
        
        int idA = messageService.sendMessage( messageA, users );

        messageA = messageService.getMessage( idA );
        
        assertNotNull( messageA );
        assertEquals( "SubjectA", messageA.getSubject() );
        assertEquals( "TextA", messageA.getText() );
        assertEquals( 2, messageA.getUserMessages().size() );        
    }
    
    @Test
    public void testSaveMessage()
    {
        int idA = messageService.saveMessage( messageA );
        
        messageA = messageService.getMessage( idA );
        
        assertNotNull( messageA );
        assertEquals( "SubjectA", messageA.getSubject() );
        assertEquals( "TextA", messageA.getText() );
        assertEquals( 2, messageA.getUserMessages().size() );
        assertTrue( messageA.getUserMessages().contains( userMessageA ) );
        assertTrue( messageA.getUserMessages().contains( userMessageB ) );
    }

    @Test
    public void testGetUserMessages()
    {
        messageService.saveMessage( messageA );
        messageService.saveMessage( messageB );
        
        List<UserMessage> userMessages = messageService.getUserMessages( userA, 0, 10 );
        
        assertNotNull( userMessages );
        assertEquals( 2, userMessages.size() );
        assertTrue( userMessages.contains( userMessageA ) );
        assertTrue( userMessages.contains( userMessageC ) );
    }

    @Test
    public void testUpdateUserMessage()
    {
        messageService.saveMessage( messageA );
        
        assertNotNull( userMessageA );
        assertFalse( userMessageA.isRead() );
        
        userMessageA.setRead( true );
        
        int idA = userMessageA.getId();
        
        messageService.updateUserMessage( userMessageA );
        
        userMessageA = messageService.getUserMessage( idA );
        
        assertNotNull( userMessageA );
        assertTrue( userMessageA.isRead() );
    }
    
    @Test
    public void testDeleteUserMessage()
    {
        messageService.saveMessage( messageA );

        assertEquals( 2, messageA.getUserMessages().size() );
        assertTrue( messageA.getUserMessages().contains( userMessageA ) );
        assertTrue( messageA.getUserMessages().contains( userMessageB ) );
        
        messageService.deleteUserMessage( userMessageB );

        assertEquals( 1, messageA.getUserMessages().size() );
        assertTrue( messageA.getUserMessages().contains( userMessageA ) );
        
        messageService.deleteUserMessage( userMessageA );

        assertEquals( 0, messageA.getUserMessages().size() );
    }
    
    @Test
    public void testGetUserMessagesCount()
    {
        messageService.saveMessage( messageA );
        messageService.saveMessage( messageB );
     
        long count = messageService.getUnreadMessageCount( userA );
        
        assertEquals( 2, count );
        
        userMessageA.setRead( true );
        
        messageService.updateUserMessage( userMessageA );
        
        count = messageService.getUnreadMessageCount( userA );
        
        assertEquals( 1, count );
    }
}
