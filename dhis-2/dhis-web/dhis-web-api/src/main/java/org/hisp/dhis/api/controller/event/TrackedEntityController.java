package org.hisp.dhis.api.controller.event;

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

import org.hisp.dhis.api.controller.AbstractCrudController;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.trackedentity.TrackedEntity;
import org.hisp.dhis.trackedentity.TrackedEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = TrackedEntityController.RESOURCE_PATH )
public class TrackedEntityController extends AbstractCrudController<TrackedEntity>
{
    public static final String RESOURCE_PATH = "/trackedEntities";

    @Autowired
    private TrackedEntityService trackedEntityService;

    //--------------------------------------------------------------------------
    // POST
    //--------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, consumes = { "application/xml", "text/xml" } )
    @ResponseStatus( HttpStatus.CREATED )
    public void postXmlObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        TrackedEntity trackedEntity = JacksonUtils.fromXml( input, TrackedEntity.class );
        trackedEntityService.addTrackedEntity( trackedEntity );

        response.setHeader( "Location", ContextUtils.getRootPath( request ) + RESOURCE_PATH + "/" + trackedEntity.getUid() );
    }

    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    @ResponseStatus( HttpStatus.CREATED )
    public void postJsonObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        TrackedEntity trackedEntity = JacksonUtils.fromJson( input, TrackedEntity.class );
        trackedEntityService.addTrackedEntity( trackedEntity );

        response.setHeader( "Location", ContextUtils.getRootPath( request ) + RESOURCE_PATH + "/" + trackedEntity.getUid() );
    }

    //--------------------------------------------------------------------------
    // PUT
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = { "application/xml", "text/xml" } )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putXmlObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        TrackedEntity trackedEntity = trackedEntityService.getTrackedEntity( uid );

        if ( trackedEntity == null )
        {
            ContextUtils.conflictResponse( response, "TrackedEntity does not exist: " + uid );
            return;
        }

        TrackedEntity newTrackedEntity = JacksonUtils.fromXml( input, TrackedEntity.class );
        newTrackedEntity.setUid( trackedEntity.getUid() );
        trackedEntity.mergeWith( newTrackedEntity );

        trackedEntityService.updateTrackedEntity( trackedEntity );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = "application/json" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putJsonObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        TrackedEntity trackedEntity = trackedEntityService.getTrackedEntity( uid );

        if ( trackedEntity == null )
        {
            ContextUtils.conflictResponse( response, "TrackedEntity does not exist: " + uid );
            return;
        }

        TrackedEntity newTrackedEntity = JacksonUtils.fromJson( input, TrackedEntity.class );
        newTrackedEntity.setUid( trackedEntity.getUid() );
        trackedEntity.mergeWith( newTrackedEntity );

        trackedEntityService.updateTrackedEntity( trackedEntity );
    }

    //--------------------------------------------------------------------------
    // DELETE
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void deleteObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid ) throws Exception
    {
        TrackedEntity trackedEntity = trackedEntityService.getTrackedEntity( uid );

        if ( trackedEntity == null )
        {
            ContextUtils.conflictResponse( response, "TrackedEntity does not exist: " + uid );
            return;
        }

        trackedEntityService.deleteTrackedEntity( trackedEntity );
    }
}
