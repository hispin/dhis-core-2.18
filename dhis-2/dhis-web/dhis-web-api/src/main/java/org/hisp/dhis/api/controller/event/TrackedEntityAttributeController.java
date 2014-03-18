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
import org.hisp.dhis.api.controller.WebMetaData;
import org.hisp.dhis.api.controller.WebOptions;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = TrackedEntityAttributeController.RESOURCE_PATH )
public class TrackedEntityAttributeController
    extends AbstractCrudController<TrackedEntityAttribute>
{
    public static final String RESOURCE_PATH = "/trackedEntityAttributes";

    @Autowired
    private TrackedEntityAttributeService trackedEntityAttributeService;

    @Autowired
    private ProgramService programService;

    @Override
    protected List<TrackedEntityAttribute> getEntityList( WebMetaData metaData, WebOptions options )
    {
        List<TrackedEntityAttribute> entityList = new ArrayList<TrackedEntityAttribute>();

        boolean withoutPrograms = options.getOptions().containsKey( "withoutPrograms" )
            && Boolean.parseBoolean( options.getOptions().get( "withoutPrograms" ) );

        if ( withoutPrograms )
        {
            entityList = new ArrayList<TrackedEntityAttribute>(
                trackedEntityAttributeService.getTrackedEntityAttributesWithoutProgram() );
        }

        else if ( options.getOptions().containsKey( "program" ) )
        {
            String programId = options.getOptions().get( "program" );
            Program program = programService.getProgram( programId );

            if ( program != null )
            {
                entityList = new ArrayList<TrackedEntityAttribute>( program.getEntityAttributes() );
            }
        }

        else if ( options.hasPaging() )
        {
            int count = manager.getCount( getEntityClass() );

            Pager pager = new Pager( options.getPage(), count, options.getPageSize() );
            metaData.setPager( pager );

            entityList = new ArrayList<TrackedEntityAttribute>( manager.getBetween( getEntityClass(),
                pager.getOffset(), pager.getPageSize() ) );
        }
        else
        {
            entityList = new ArrayList<TrackedEntityAttribute>( trackedEntityAttributeService.getAllTrackedEntityAttributes() );
        }

        return entityList;
    }

    //--------------------------------------------------------------------------
    // POST
    //--------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, consumes = { "application/xml", "text/xml" } )
    @ResponseStatus( HttpStatus.CREATED )
    public void postXmlObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        TrackedEntityAttribute trackedEntityAttribute = JacksonUtils.fromXml( input, TrackedEntityAttribute.class );
        trackedEntityAttributeService.addTrackedEntityAttribute( trackedEntityAttribute );

        response.setHeader( "Location", ContextUtils.getRootPath( request ) + RESOURCE_PATH + "/" + trackedEntityAttribute.getUid() );
    }

    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    @ResponseStatus( HttpStatus.CREATED )
    public void postJsonObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        TrackedEntityAttribute trackedEntityAttribute = JacksonUtils.fromJson( input, TrackedEntityAttribute.class );
        trackedEntityAttributeService.addTrackedEntityAttribute( trackedEntityAttribute );

        response.setHeader( "Location", ContextUtils.getRootPath( request ) + RESOURCE_PATH + "/" + trackedEntityAttribute.getUid() );
    }

    //--------------------------------------------------------------------------
    // PUT
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = { "application/xml", "text/xml" } )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putXmlObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        TrackedEntityAttribute trackedEntityAttribute = trackedEntityAttributeService.getTrackedEntityAttribute( uid );

        if ( trackedEntityAttribute == null )
        {
            ContextUtils.conflictResponse( response, "TrackedEntityAttribute does not exist: " + uid );
            return;
        }

        TrackedEntityAttribute newTrackedEntityAttribute = JacksonUtils.fromXml( input, TrackedEntityAttribute.class );
        newTrackedEntityAttribute.setUid( trackedEntityAttribute.getUid() );
        trackedEntityAttribute.mergeWith( newTrackedEntityAttribute );

        trackedEntityAttributeService.updateTrackedEntityAttribute( trackedEntityAttribute );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = "application/json" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putJsonObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        TrackedEntityAttribute trackedEntityAttribute = trackedEntityAttributeService.getTrackedEntityAttribute( uid );

        if ( trackedEntityAttribute == null )
        {
            ContextUtils.conflictResponse( response, "TrackedEntityAttribute does not exist: " + uid );
            return;
        }

        TrackedEntityAttribute newTrackedEntityAttribute = JacksonUtils.fromJson( input, TrackedEntityAttribute.class );
        newTrackedEntityAttribute.setUid( trackedEntityAttribute.getUid() );
        trackedEntityAttribute.mergeWith( newTrackedEntityAttribute );

        trackedEntityAttributeService.updateTrackedEntityAttribute( trackedEntityAttribute );
    }

    //--------------------------------------------------------------------------
    // DELETE
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void deleteObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid ) throws Exception
    {
        TrackedEntityAttribute trackedEntityAttribute = trackedEntityAttributeService.getTrackedEntityAttribute( uid );

        if ( trackedEntityAttribute == null )
        {
            ContextUtils.conflictResponse( response, "TrackedEntityAttribute does not exist: " + uid );
            return;
        }

        trackedEntityAttributeService.deleteTrackedEntityAttribute( trackedEntityAttribute );
    }
}
