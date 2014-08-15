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

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.dxf2.metadata.ImportTypeSummary;
import org.hisp.dhis.hibernate.exception.CreateAccessDeniedException;
import org.hisp.dhis.hibernate.exception.UpdateAccessDeniedException;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.schema.descriptors.UserSchemaDescriptor;
import org.hisp.dhis.security.PasswordManager;
import org.hisp.dhis.security.RestoreOptions;
import org.hisp.dhis.security.SecurityService;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.webdomain.WebMetaData;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.setting.SystemSettingManager.KEY_ONLY_MANAGE_WITHIN_USER_GROUPS;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = UserSchemaDescriptor.API_ENDPOINT )
public class UserController
    extends AbstractCrudController<User>
{
    public static final String INVITE_PATH = "/invite";

    @Autowired
    private UserService userService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private PasswordManager passwordManager;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SystemSettingManager systemSettingManager;

    @Override
    @PreAuthorize( "hasRole('ALL') or hasRole('F_USER_VIEW')" )
    public RootNode getObjectList( @RequestParam Map<String, String> parameters, HttpServletResponse response, HttpServletRequest request )
    {
        return super.getObjectList( parameters, response, request );
    }

    @Override
    @PreAuthorize( "hasRole('ALL') or hasRole('F_USER_VIEW')" )
    public RootNode getObject( @PathVariable( "uid" ) String uid, @RequestParam Map<String, String> parameters,
        HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        return super.getObject( uid, parameters, request, response );
    }

    @Override
    protected List<User> getEntityList( WebMetaData metaData, WebOptions options )
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
    protected List<User> getEntity( String uid )
    {
        List<User> users = Lists.newArrayList();
        Optional<User> user = Optional.fromNullable( userService.getUser( uid ) );

        if ( user.isPresent() )
        {
            users.add( user.get() );
        }

        return users;
    }

    //--------------------------------------------------------------------------
    // POST
    //--------------------------------------------------------------------------

    @Override
    @RequestMapping( method = RequestMethod.POST, consumes = { "application/xml", "text/xml" } )
    public void postXmlObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        User user = renderService.fromXml( request.getInputStream(), getEntityClass() );

        createUser( user, response );
    }

    @Override
    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    public void postJsonObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        User user = renderService.fromJson( request.getInputStream(), getEntityClass() );

        createUser( user, response );
    }

    @RequestMapping( value = INVITE_PATH, method = RequestMethod.POST, consumes = { "application/xml", "text/xml" } )
    public void postXmlInvite( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        User user = renderService.fromXml( request.getInputStream(), getEntityClass() );

        inviteUser( user, request, response );
    }

    @RequestMapping( value = INVITE_PATH, method = RequestMethod.POST, consumes = "application/json" )
    public void postJsonInvite( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        User user = renderService.fromJson( request.getInputStream(), getEntityClass() );

        inviteUser( user, request, response );
    }

    //--------------------------------------------------------------------------
    // PUT
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = { "application/xml", "text/xml" } )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putXmlObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid, InputStream
        input ) throws Exception
    {
        List<User> users = getEntity( uid );

        if ( users.isEmpty() )
        {
            ContextUtils.conflictResponse( response, getEntityName() + " does not exist: " + uid );
            return;
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), users.get( 0 ) ) )
        {
            throw new UpdateAccessDeniedException( "You don't have the proper permissions to update this object." );
        }

        User parsed = renderService.fromXml( request.getInputStream(), getEntityClass() );
        parsed.setUid( uid );

        if ( parsed.getUserCredentials().getPassword() != null )
        {
            String encodePassword = passwordManager.encodePassword( parsed.getUsername(),
                parsed.getUserCredentials().getPassword() );
            parsed.getUserCredentials().setPassword( encodePassword );
        }

        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), parsed, ImportStrategy.UPDATE );
        renderService.toXml( response.getOutputStream(), summary );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = "application/json" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putJsonObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid, InputStream
        input ) throws Exception
    {
        List<User> users = getEntity( uid );

        if ( users.isEmpty() )
        {
            ContextUtils.conflictResponse( response, getEntityName() + " does not exist: " + uid );
            return;
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), users.get( 0 ) ) )
        {
            throw new UpdateAccessDeniedException( "You don't have the proper permissions to update this object." );
        }

        User parsed = renderService.fromJson( request.getInputStream(), getEntityClass() );
        parsed.setUid( uid );

        if ( parsed.getUserCredentials().getPassword() != null )
        {
            String encodePassword = passwordManager.encodePassword( parsed.getUsername(),
                parsed.getUserCredentials().getPassword() );
            parsed.getUserCredentials().setPassword( encodePassword );
        }

        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), parsed, ImportStrategy.UPDATE );
        renderService.toJson( response.getOutputStream(), summary );
    }

    //--------------------------------------------------------------------------
    // Supportive methods
    //--------------------------------------------------------------------------

    /**
     * Creates a user invitation and invites the user
     *
     * @param user     user object parsed from the POST request
     * @param response response for created user invitation
     * @throws Exception
     */
    private void inviteUser( User user, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        RestoreOptions restoreOptions = user.getUsername() == null || user.getUsername().isEmpty() ?
            RestoreOptions.INVITE_WITH_USERNAME_CHOICE : RestoreOptions.INVITE_WITH_DEFINED_USERNAME;

        securityService.prepareUserForInvite( user );

        createUser( user, response );

        securityService.sendRestoreMessage( user.getUserCredentials(),
            ContextUtils.getContextPath( request ), restoreOptions );
    }

    /**
     * Creates a user
     *
     * @param user     user object parsed from the POST request
     * @param response response for created user
     * @throws Exception
     */
    private void createUser( User user, HttpServletResponse response ) throws Exception
    {
        if ( currentUserService.getCurrentUser() == null )
        {
            throw new CreateAccessDeniedException( "Internal error: currentUserService.getCurrentUser() returns null." );
        }

        if ( !aclService.canCreate( currentUserService.getCurrentUser(), getEntityClass() ) )
        {
            throw new CreateAccessDeniedException( "You don't have the proper permissions to create this object." );
        }

        checkUserGroups( user );

        user.getUserCredentials().getCogsDimensionConstraints().addAll(
            currentUserService.getCurrentUser().getUserCredentials().getCogsDimensionConstraints() );

        user.getUserCredentials().getCatDimensionConstraints().addAll(
            currentUserService.getCurrentUser().getUserCredentials().getCatDimensionConstraints() );

        String encodePassword = passwordManager.encodePassword( user.getUsername(),
            user.getUserCredentials().getPassword() );
        user.getUserCredentials().setPassword( encodePassword );

        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), user, ImportStrategy.CREATE );

        renderService.toJson( response.getOutputStream(), summary );

        addUserGroups( user );
    }

    /**
     * Before adding the user, checks to see that any specified user groups
     * exist. Also checks to see that user can be created by the current
     * user, if it is required that the current user have read/write access
     * to a user group that is assigned to the new user.
     *
     * @param user user object parsed from the POST request
     */
    private void checkUserGroups( User user )
    {
        boolean writeGroupRequired = (Boolean) systemSettingManager.getSystemSetting( KEY_ONLY_MANAGE_WITHIN_USER_GROUPS, false );

        boolean writeGroupFound = false;

        if ( currentUserService.getCurrentUser() != null && user.getGroups() != null )
        {
            for ( UserGroup ug : user.getGroups() )
            {
                UserGroup group = userGroupService.getUserGroup( ug.getUid() );

                if ( group == null )
                {
                    throw new CreateAccessDeniedException( "Can't add user: Can't find user group with UID = " + ug.getUid() );
                }

                if ( writeGroupRequired && securityService.canWrite( group ) )
                {
                    writeGroupFound = true;

                    break;
                }
            }
        }

        if ( writeGroupRequired && !writeGroupFound )
        {
            throw new CreateAccessDeniedException( "The new user must be assigned to a user group to which you have write access." );
        }
    }

    /**
     * Adds user groups (if any) to the newly-created user
     *
     * @param user user object (including user groups) parsed from the POST request
     */
    private void addUserGroups( User user )
    {
        if ( user.getGroups() != null )
        {
            boolean writeGroupRequired = (Boolean) systemSettingManager.getSystemSetting( KEY_ONLY_MANAGE_WITHIN_USER_GROUPS, false );

            for ( UserGroup ug : new ArrayList<>( user.getGroups() ) )
            {
                UserGroup group = userGroupService.getUserGroup( ug.getUid() );

                if ( group != null && (!writeGroupRequired || securityService.canWrite( group )) )
                {
                    group.addUser( user );

                    userGroupService.updateUserGroup( group );
                }
            }
        }
    }
}
