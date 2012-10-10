package org.hisp.dhis.api.controller.user;

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

import org.apache.commons.collections.CollectionUtils;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.webdomain.user.Dashboard;
import org.hisp.dhis.api.webdomain.user.Inbox;
import org.hisp.dhis.api.webdomain.user.Recipients;
import org.hisp.dhis.api.webdomain.user.Settings;
import org.hisp.dhis.common.view.BasicView;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.interpretation.Interpretation;
import org.hisp.dhis.interpretation.InterpretationService;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = CurrentUserController.RESOURCE_PATH, method = RequestMethod.GET )
public class CurrentUserController
{
    public static final String RESOURCE_PATH = "/currentUser";

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private InterpretationService interpretationService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @RequestMapping( produces = {"application/json", "text/*"} )
    public void getCurrentUser( HttpServletResponse response ) throws Exception
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            ContextUtils.notFoundResponse( response, "User object is null, user is not authenticated." );
            return;
        }

        JacksonUtils.toJson( response.getOutputStream(), currentUser );
    }

    @RequestMapping( value = "/inbox", produces = {"application/json", "text/*"} )
    public void getInbox( HttpServletResponse response ) throws Exception
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            ContextUtils.notFoundResponse( response, "User object is null, user is not authenticated." );
            return;
        }

        Inbox inbox = new Inbox();
        inbox.setMessageConversations( new ArrayList<MessageConversation>( messageService.getMessageConversations( 0, Integer.MAX_VALUE ) ) );
        inbox.setInterpretations( new ArrayList<Interpretation>( interpretationService.getInterpretations( 0, Integer.MAX_VALUE ) ) );

        JacksonUtils.toJson( response.getOutputStream(), inbox );
    }

    @RequestMapping( value = "/dashboard", produces = {"application/json", "text/*"} )
    public void getDashboard( HttpServletResponse response ) throws Exception
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            ContextUtils.notFoundResponse( response, "User object is null, user is not authenticated." );
            return;
        }

        Dashboard dashboard = new Dashboard();
        dashboard.setUnreadMessageConversation( messageService.getUnreadMessageConversationCount() );
        dashboard.setUnreadInterpretations( interpretationService.getNewInterpretationCount() );

        JacksonUtils.toJson( response.getOutputStream(), dashboard );
    }

    @RequestMapping( value = "/settings", produces = {"application/json", "text/*"} )
    public void getSettings( HttpServletResponse response ) throws Exception
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            ContextUtils.notFoundResponse( response, "User object is null, user is not authenticated." );
            return;
        }

        Settings settings = new Settings();
        settings.setFirstName( currentUser.getFirstName() );
        settings.setSurname( currentUser.getSurname() );
        settings.setEmail( currentUser.getEmail() );
        settings.setPhoneNumber( currentUser.getPhoneNumber() );

        JacksonUtils.toJson( response.getOutputStream(), settings );
    }

    @RequestMapping( value = "/settings", method = RequestMethod.POST, consumes = "application/xml" )
    public void postSettingsXml( HttpServletResponse response, HttpServletRequest request ) throws Exception
    {
        Settings settings = JacksonUtils.fromXml( request.getInputStream(), Settings.class );
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            ContextUtils.notFoundResponse( response, "User object is null, user is not authenticated." );
            return;
        }

        currentUser.setFirstName( settings.getFirstName() );
        currentUser.setSurname( settings.getSurname() );
        currentUser.setEmail( settings.getEmail() );
        currentUser.setPhoneNumber( settings.getPhoneNumber() );

        userService.updateUser( currentUser );
    }

    @RequestMapping( value = "/settings", method = RequestMethod.POST, consumes = "application/json" )
    public void postSettingsJson( HttpServletResponse response, HttpServletRequest request ) throws Exception
    {
        Settings settings = JacksonUtils.fromJson( request.getInputStream(), Settings.class );
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            ContextUtils.notFoundResponse( response, "User object is null, user is not authenticated." );
            return;
        }

        currentUser.setFirstName( settings.getFirstName() );
        currentUser.setSurname( settings.getSurname() );
        currentUser.setEmail( settings.getEmail() );
        currentUser.setPhoneNumber( settings.getPhoneNumber() );

        userService.updateUser( currentUser );
    }

    @RequestMapping( value = "/recipients", produces = {"application/json", "text/*"} )
    public void recipientsJson( HttpServletResponse response,
        @RequestParam( value = "filter" ) String filter ) throws IOException
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            ContextUtils.notFoundResponse( response, "User object is null, user is not authenticated." );
            return;
        }

        Recipients recipients = new Recipients();
        recipients.setOrganisationUnits( getOrganisationUnitsForUser( currentUser, filter ) );

        recipients.setUsers( new HashSet<User>( userService.getAllUsersBetweenByName( filter, 0, Integer.MAX_VALUE ) ) );
        recipients.setUserGroups( new HashSet<UserGroup>( userGroupService.getUserGroupsBetweenByName( filter, 0, Integer.MAX_VALUE ) ) );

        JacksonUtils.toJson( response.getOutputStream(), recipients );
    }

    @RequestMapping( value = "/organisationUnits", produces = {"application/json", "text/*"} )
    public void getOrganisationUnitsJson( HttpServletResponse response,
        @RequestParam( value = "withChildren", required = false ) boolean withChildren ) throws IOException
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            ContextUtils.notFoundResponse( response, "User object is null, user is not authenticated." );
            return;
        }

        Collection<OrganisationUnit> organisationUnits = currentUser.getOrganisationUnits();

        if ( withChildren )
        {
            for ( OrganisationUnit ou : organisationUnits )
            {
                organisationUnits.addAll( ou.getChildren() );
            }
        }

        JacksonUtils.toJsonWithView( response.getOutputStream(), organisationUnits, BasicView.class );
    }

    @RequestMapping( value = "/organisationUnits/{uid}/dataSets", produces = {"application/json", "text/*"} )
    public void getDataSetsJson( HttpServletResponse response,
        @PathVariable( value = "uid" ) String uid ) throws IOException
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            ContextUtils.notFoundResponse( response, "User object is null, user is not authenticated." );
            return;
        }

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( uid );

        if ( organisationUnit == null )
        {
            ContextUtils.notFoundResponse( response, "Organisation Unit UID is invalid." );
            return;
        }

        Set<DataSet> dataSets = new HashSet<DataSet>( CollectionUtils.intersection( organisationUnit.getDataSets(),
            currentUser.getUserCredentials().getAllDataSets() ) );

        JacksonUtils.toJsonWithView( response.getOutputStream(), dataSets, BasicView.class );
    }

    private Set<OrganisationUnit> getOrganisationUnitsForUser( User user, String filter )
    {
        Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();

        for ( OrganisationUnit organisationUnit : user.getOrganisationUnits() )
        {
            organisationUnits.add( organisationUnit );
            organisationUnits.addAll( organisationUnitService.getLeafOrganisationUnits( organisationUnit.getId() ) );
        }

        if ( filter != null )
        {
            Iterator<OrganisationUnit> iterator = organisationUnits.iterator();
            filter = filter.toUpperCase();

            while ( iterator.hasNext() )
            {
                OrganisationUnit organisationUnit = iterator.next();

                if ( !organisationUnit.getName().toUpperCase().contains( filter ) )
                {
                    iterator.remove();
                }
            }
        }

        return organisationUnits;
    }
}
