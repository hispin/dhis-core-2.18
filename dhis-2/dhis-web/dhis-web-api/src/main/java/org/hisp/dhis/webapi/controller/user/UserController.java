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

import com.google.common.collect.Lists;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.controller.WebMetaData;
import org.hisp.dhis.webapi.controller.WebOptions;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.dxf2.metadata.ImportTypeSummary;
import org.hisp.dhis.hibernate.exception.CreateAccessDeniedException;
import org.hisp.dhis.hibernate.exception.UpdateAccessDeniedException;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.security.PasswordManager;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = UserController.RESOURCE_PATH )
public class UserController
    extends AbstractCrudController<User>
{
    public static final String RESOURCE_PATH = "/users";

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordManager passwordManager;

    @Override
    @PreAuthorize( "hasRole('ALL') or hasRole('F_USER_VIEW')" )
    public String getObjectList( @RequestParam Map<String, String> parameters, Model model, HttpServletResponse response, HttpServletRequest request )
    {
        return super.getObjectList( parameters, model, response, request );
    }

    @Override
    @PreAuthorize( "hasRole('ALL') or hasRole('F_USER_VIEW')" )
    public String getObject( @PathVariable( "uid" ) String uid, @RequestParam Map<String, String> parameters, Model model,
        HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        return super.getObject( uid, parameters, model, request, response );
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

            entityList = new ArrayList<User>( userService.getAllUsersBetween( pager.getOffset(), pager.getPageSize() ) );
        }
        else
        {
            entityList = new ArrayList<User>( userService.getAllUsers() );
        }

        return entityList;
    }

    @Override
    protected User getEntity( String uid )
    {
        return userService.getUser( uid );
    }

    //--------------------------------------------------------------------------
    // POST
    //--------------------------------------------------------------------------

    @Override
    @RequestMapping( method = RequestMethod.POST, consumes = { "application/xml", "text/xml" } )
    public void postXmlObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        if ( !aclService.canCreate( currentUserService.getCurrentUser(), getEntityClass() ) )
        {
            throw new CreateAccessDeniedException( "You don't have the proper permissions to create this object." );
        }

        User user = renderService.fromXml( request.getInputStream(), getEntityClass() );

        String encodePassword = passwordManager.encodePassword( user.getUsername(),
            user.getUserCredentials().getPassword() );
        user.getUserCredentials().setPassword( encodePassword );

        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), user, ImportStrategy.CREATE );
        renderService.toJson( response.getOutputStream(), summary );
    }

    @Override
    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    public void postJsonObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        if ( !aclService.canCreate( currentUserService.getCurrentUser(), getEntityClass() ) )
        {
            throw new CreateAccessDeniedException( "You don't have the proper permissions to create this object." );
        }

        User user = renderService.fromJson( request.getInputStream(), getEntityClass() );

        String encodePassword = passwordManager.encodePassword( user.getUsername(),
            user.getUserCredentials().getPassword() );
        user.getUserCredentials().setPassword( encodePassword );

        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), user, ImportStrategy.CREATE );
        renderService.toJson( response.getOutputStream(), summary );
    }

    //--------------------------------------------------------------------------
    // PUT
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = { "application/xml", "text/xml" } )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putXmlObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid, InputStream
        input ) throws Exception
    {
        User object = getEntity( uid );

        if ( object == null )
        {
            ContextUtils.conflictResponse( response, getEntityName() + " does not exist: " + uid );
            return;
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), object ) )
        {
            throw new UpdateAccessDeniedException( "You don't have the proper permissions to update this object." );
        }

        User parsed = renderService.fromXml( request.getInputStream(), getEntityClass() );
        parsed.setUid( uid );
        if (parsed.getUserCredentials().getPassword() != null)
        {
            String encodePassword = passwordManager.encodePassword( parsed.getUsername(),
                parsed.getUserCredentials().getPassword() );
            parsed.getUserCredentials().setPassword( encodePassword );
        }

        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), parsed, ImportStrategy.UPDATE );
        renderService.toJson( response.getOutputStream(), summary );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = "application/json" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putJsonObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid, InputStream
        input ) throws Exception
    {
        User object = getEntity( uid );

        if ( object == null )
        {
            ContextUtils.conflictResponse( response, getEntityName() + " does not exist: " + uid );
            return;
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), object ) )
        {
            throw new UpdateAccessDeniedException( "You don't have the proper permissions to update this object." );
        }

        User parsed = renderService.fromJson( request.getInputStream(), getEntityClass() );
        parsed.setUid( uid );

        if (parsed.getUserCredentials().getPassword() != null)
        {
            String encodePassword = passwordManager.encodePassword( parsed.getUsername(),
                parsed.getUserCredentials().getPassword() );
            parsed.getUserCredentials().setPassword( encodePassword );
        }

        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), parsed, ImportStrategy.UPDATE );
        renderService.toJson( response.getOutputStream(), summary );
    }
}
