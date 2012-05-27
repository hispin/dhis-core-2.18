package org.hisp.dhis.api.controller;

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

import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.IdentifiableObjectParams;
import org.hisp.dhis.api.utils.WebLinkPopulator;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.dxf2.message.Message;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.message.MessageConversations;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = MessageConversationController.RESOURCE_PATH )
public class MessageConversationController
{
    public static final String RESOURCE_PATH = "/messageConversations";

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    //-------------------------------------------------------------------------------------------------------
    // GET
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET )
    public String getMessageConversations( IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        MessageConversations messageConversations = new MessageConversations();

        if ( params.isPaging() )
        {
            int total = messageService.getMessageConversationCount();

            Pager pager = new Pager( params.getPage(), total );
            messageConversations.setPager( pager );

            List<MessageConversation> list = new ArrayList<MessageConversation>(
                messageService.getMessageConversations( pager.getOffset(), pager.getPageSize() ) );

            messageConversations.setMessageConversations( list );
        }
        else
        {
            messageConversations.setMessageConversations( new ArrayList<MessageConversation>( messageService.getMessageConversations( 0, 1000 ) ) );
        }

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( messageConversations );
        }

        model.addAttribute( "model", messageConversations );

        return "messages";
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public String getMessageConversation( @PathVariable( "uid" ) String uid, IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        MessageConversation messageConversation = messageService.getMessageConversation( uid );

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( messageConversation );
        }

        model.addAttribute( "model", messageConversation );
        model.addAttribute( "viewClass", "detailed" );

        return "message";
    }

    //-------------------------------------------------------------------------------------------------------
    // POST for new MessageConversation
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/xml, text/xml"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_SEND_MESSAGE')" )
    public void postMessageConversationXML( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws IOException
    {
        Message message = JacksonUtils.fromXml( input, Message.class );

        List<User> users = new ArrayList<User>( message.getUsers() );
        message.getUsers().clear();

        for ( User user : users )
        {
            user = userService.getUser( user.getUid() );
            message.getUsers().add( user );
        }

        String metaData = MessageService.META_USER_AGENT + request.getHeader( ContextUtils.HEADER_USER_AGENT );

        int id = messageService.sendMessage( message.getSubject(), message.getText(), metaData, message.getUsers() );
        MessageConversation m = messageService.getMessageConversation( id );

        response.setStatus( HttpServletResponse.SC_CREATED );
        response.setHeader( "Location", MessageConversationController.RESOURCE_PATH + "/" + m.getUid() );
    }

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/json"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_SEND_MESSAGE')" )
    public void postMessageConversationJSON( HttpServletResponse response, InputStream input ) throws Exception
    {
    }

    //-------------------------------------------------------------------------------------------------------
    // POST for reply on existing MessageConversation
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.POST )
    public void postMessageConversationReply( @PathVariable( "uid" ) String uid, @RequestBody String body,
                                              HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        String metaData = MessageService.META_USER_AGENT + request.getHeader( ContextUtils.HEADER_USER_AGENT );

        MessageConversation messageConversation = messageService.getMessageConversation( uid );

        messageService.sendReply( messageConversation, body, metaData );
    }

    //-------------------------------------------------------------------------------------------------------
    // POST for feedback
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = "/feedback", method = RequestMethod.POST )
    public void postMessageConversationFeedback( @RequestParam( "subject" ) String subject, @RequestBody String body,
                                                 HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        String metaData = MessageService.META_USER_AGENT + request.getHeader( ContextUtils.HEADER_USER_AGENT );

        messageService.sendFeedback( subject, body, metaData );
    }
}
