package org.hisp.dhis.webapi.controller.user;

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

import static org.hisp.dhis.common.IdentifiableObjectUtils.getUids;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.dxf2.metadata.ImportTypeSummary;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.schema.descriptors.UserSchemaDescriptor;
import org.hisp.dhis.security.RestoreOptions;
import org.hisp.dhis.security.SecurityService;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserGroupService;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.user.Users;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.webdomain.WebMetaData;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = UserSchemaDescriptor.API_ENDPOINT )
public class UserController
    extends AbstractCrudController<User>
{
    public static final String INVITE_PATH = "/invite";

    public static final String BULK_INVITE_PATH = "/invites";

    @Autowired
    private UserService userService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SystemSettingManager systemSettingManager;
    
    // -------------------------------------------------------------------------
    // GET
    // -------------------------------------------------------------------------

    @Override
    @PreAuthorize( "hasRole('ALL') or hasRole('F_USER_VIEW')" )
    public RootNode getObjectList( @RequestParam Map<String, String> parameters, HttpServletResponse response, HttpServletRequest request )
    {
        //TODO: Allow user with F_USER_VIEW_WITHIN_MANAGED_GROUP and restrict viewing to within managed groups.

        return super.getObjectList( parameters, response, request );
    }

    @Override
    @PreAuthorize( "hasRole('ALL') or hasRole('F_USER_VIEW')" )
    public RootNode getObject( @PathVariable( "uid" ) String uid, @RequestParam Map<String, String> parameters,
        HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        //TODO: Allow user with F_USER_VIEW_WITHIN_MANAGED_GROUP and restrict viewing to within managed groups.

        return super.getObject( uid, parameters, request, response );
    }

    @Override
    protected List<User> getEntityList( WebMetaData metaData, WebOptions options, List<String> filters )
    {
        List<User> entityList;

        if ( options.getOptions().containsKey( "query" ) )
        {
            entityList = Lists.newArrayList( manager.filter( getEntityClass(), options.getOptions().get( "query" ) ) );
        }
        else if ( options.hasPaging() )
        {
            int count = userService.getUserCount();

            Pager pager = new Pager( options.getPage(), count );
            metaData.setPager( pager );

            entityList = new ArrayList<>( userService.getAllUsersBetween( pager.getOffset(), pager.getPageSize() ) );
        }
        else
        {
            entityList = new ArrayList<>( userService.getAllUsers() );
        }

        return entityList;
    }

    @Override
    protected List<User> getEntity( String uid, WebOptions options )
    {
        List<User> users = Lists.newArrayList();
        Optional<User> user = Optional.fromNullable( userService.getUser( uid ) );

        if ( user.isPresent() )
        {
            users.add( user.get() );
        }

        return users;
    }

    // -------------------------------------------------------------------------
    // POST
    // -------------------------------------------------------------------------

    @Override
    @RequestMapping( method = RequestMethod.POST, consumes = { "application/xml", "text/xml" } )
    public void postXmlObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        User user = renderService.fromXml( request.getInputStream(), getEntityClass() );

        if ( !validateCreateUser( user, response ) )
        {
            return;
        }
        
        renderService.toXml( response.getOutputStream(), createUser( user, response ) );
    }

    @Override
    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    public void postJsonObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        User user = renderService.fromJson( request.getInputStream(), getEntityClass() );

        if ( !validateCreateUser( user, response ) )
        {
            return;
        }
        
        renderService.toJson( response.getOutputStream(), createUser( user, response ) );
    }

    @RequestMapping( value = INVITE_PATH, method = RequestMethod.POST, consumes = { "application/xml", "text/xml" } )
    public void postXmlInvite( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        User user = renderService.fromXml( request.getInputStream(), getEntityClass() );

        if ( !validateInviteUser( user, response ) )
        {
            return;
        }
        
        renderService.toXml( response.getOutputStream(), inviteUser( user, request, response ) );
    }

    @RequestMapping( value = INVITE_PATH, method = RequestMethod.POST, consumes = "application/json" )
    public void postJsonInvite( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        User user = renderService.fromJson( request.getInputStream(), getEntityClass() );

        if ( !validateInviteUser( user, response ) )
        {
            return;
        }
        
        renderService.toJson( response.getOutputStream(), inviteUser( user, request, response ) );
    }

    @RequestMapping( value = BULK_INVITE_PATH, method = RequestMethod.POST, consumes = { "application/xml", "text/xml" } )
    public void postXmlInvites( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        Users users = renderService.fromXml( request.getInputStream(), Users.class );

        for ( User user : users.getUsers() )
        {
            if ( !validateInviteUser( user, response ) )
            {
                return;
            }
        }
        
        for ( User user : users.getUsers() )
        {
            inviteUser( user, request, response );
        }
    }

    @RequestMapping( value = BULK_INVITE_PATH, method = RequestMethod.POST, consumes = "application/json" )
    public void postJsonInvites( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        Users users = renderService.fromJson( request.getInputStream(), Users.class );

        for ( User user : users.getUsers() )
        {
            if ( !validateInviteUser( user, response ) )
            {
                return;
            }
        }
        
        for ( User user : users.getUsers() )
        {
            inviteUser( user, request, response );
        }
    }

    // -------------------------------------------------------------------------
    // PUT
    // -------------------------------------------------------------------------

    @Override
    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = { "application/xml", "text/xml" } )
    public void putXmlObject( HttpServletResponse response, HttpServletRequest request, 
        @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        List<User> users = getEntity( uid );

        if ( users.isEmpty() )
        {
            ContextUtils.conflictResponse( response, getEntityName() + " does not exist: " + uid );
            return;
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), users.get( 0 ) ) )
        {
            ContextUtils.conflictResponse( response, "You don't have the proper permissions to update this object." );
            return;
        }

        User parsed = renderService.fromXml( request.getInputStream(), getEntityClass() );
        parsed.setUid( uid );

        if ( !userService.canAddOrUpdateUser( IdentifiableObjectUtils.getUids( parsed.getGroups() ) ) )
        {
            ContextUtils.conflictResponse( response, "You must have permissions to create user, or ability to manage at least one user group for the user." );
            return;
        }

        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), parsed, ImportStrategy.UPDATE );
        
        renderService.toXml( response.getOutputStream(), summary );
    }

    @Override
    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = "application/json" )
    public void putJsonObject( HttpServletResponse response, HttpServletRequest request, 
        @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        List<User> users = getEntity( uid );

        if ( users.isEmpty() )
        {
            ContextUtils.conflictResponse( response, getEntityName() + " does not exist: " + uid );
            return;
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), users.get( 0 ) ) )
        {
            ContextUtils.conflictResponse( response, "You don't have the proper permissions to update this object." );
            return;
        }

        User parsed = renderService.fromJson( request.getInputStream(), getEntityClass() );
        parsed.setUid( uid );
        
        if ( !userService.canAddOrUpdateUser( IdentifiableObjectUtils.getUids( parsed.getGroups() ) ) )
        {
            ContextUtils.conflictResponse( response, "You must have permissions to create user, or ability to manage at least one user group for the user." );
            return;
        }

        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), parsed, ImportStrategy.UPDATE );
        
        renderService.toJson( response.getOutputStream(), summary );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Validates whether the given user can be created.
     * 
     * @param user the user.
     * @param response the response.
     */
    private boolean validateCreateUser( User user, HttpServletResponse response )
    {
        if ( !aclService.canCreate( currentUserService.getCurrentUser(), getEntityClass() ) )
        {
            ContextUtils.conflictResponse( response, "You don't have the proper permissions to create this object." );
            return false;
        }

        if ( !userService.canAddOrUpdateUser( IdentifiableObjectUtils.getUids( user.getGroups() ) ) )
        {
            ContextUtils.conflictResponse( response, "You must have permissions to create user, or ability to manage at least one user group for the user." );
            return false;
        }
        
        List<String> uids = IdentifiableObjectUtils.getUids( user.getGroups() );
        
        for ( String uid : uids )
        {
            if ( !userGroupService.canAddOrRemoveMember( uid ) )
            {
                ContextUtils.conflictResponse( response, "You don't have permissions to add user to user group: " + uid );
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Creates a user.
     *
     * @param user user object parsed from the POST request.
     * @param response the response.
     */
    private ImportSummary createUser( User user, HttpServletResponse response ) throws Exception
    {
        user.getUserCredentials().getCogsDimensionConstraints().addAll(
            currentUserService.getCurrentUser().getUserCredentials().getCogsDimensionConstraints() );

        user.getUserCredentials().getCatDimensionConstraints().addAll(
            currentUserService.getCurrentUser().getUserCredentials().getCatDimensionConstraints() );

        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), user, ImportStrategy.CREATE );

        if ( summary.isStatus( ImportStatus.SUCCESS ) && summary.getImportCount().getImported() == 1 )
        {        
            userGroupService.addUserToGroups( user, IdentifiableObjectUtils.getUids( user.getGroups() ) );
        }

        return summary;
    }
    
    /**
     * Validates whether a user can be invited / created.
     * 
     * @param user the user.
     * @param response the response.
     */
    private boolean validateInviteUser( User user, HttpServletResponse response )
    {
        if ( !validateCreateUser( user, response ) )
        {
            return false;
        }
        
        UserCredentials credentials = user.getUserCredentials();

        if ( credentials == null )
        {
            ContextUtils.conflictResponse( response, "User credentials is not present" );
            return false;
        }
        
        credentials.setUser( user );
        
        List<UserAuthorityGroup> userRoles = userService.getUserRolesByUid( getUids( credentials.getUserAuthorityGroups() ) );

        for ( UserAuthorityGroup role : userRoles )
        {
            if ( role != null && role.hasCriticalAuthorities() )
            {
                ContextUtils.conflictResponse( response, "User cannot be invited with user role which has critical authorities: " + role );
                return false;
            }
        }
        
        String valid = securityService.validateInvite( user.getUserCredentials() );
        
        if ( valid != null )
        {
            ContextUtils.conflictResponse( response, valid + ": " + user.getUserCredentials() );
            return false;
        }
        
        return true;
    }
    
    /**
     * Creates a user invitation and invites the user.
     *
     * @param user user object parsed from the POST request.
     * @param response the response.
     */
    private ImportSummary inviteUser( User user, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        RestoreOptions restoreOptions = user.getUsername() == null || user.getUsername().isEmpty() ?
            RestoreOptions.INVITE_WITH_USERNAME_CHOICE : RestoreOptions.INVITE_WITH_DEFINED_USERNAME;

        securityService.prepareUserForInvite( user );

        ImportSummary summary = createUser( user, response );

        if ( summary.isStatus( ImportStatus.SUCCESS ) && summary.getImportCount().getImported() == 1 )
        {
            securityService.sendRestoreMessage( user.getUserCredentials(),
                ContextUtils.getContextPath( request ), restoreOptions );
        }

        return summary;
    }
}
