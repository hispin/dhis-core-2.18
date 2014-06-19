package org.hisp.dhis.webapi.controller.event;

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

import static org.hisp.dhis.common.DimensionalObjectUtils.getDimensions;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.common.DimensionService;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.eventchart.EventChart;
import org.hisp.dhis.eventchart.EventChartService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.schema.descriptors.EventChartSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Jan Henrik Overland
 */
@Controller
@RequestMapping( value = EventChartSchemaDescriptor.API_ENDPOINT )
public class EventChartController
    extends AbstractCrudController<EventChart>
{
    @Autowired
    private EventChartService eventChartService;

    @Autowired
    private DimensionService dimensionService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramStageService programStageService;

    @Autowired
    private I18nManager i18nManager;

    //--------------------------------------------------------------------------
    // CRUD
    //--------------------------------------------------------------------------

    @Override
    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    public void postJsonObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        EventChart eventChart = JacksonUtils.fromJson( input, EventChart.class );

        mergeEventChart( eventChart );

        eventChartService.saveEventChart( eventChart );

        ContextUtils.createdResponse( response, "Event chart created", EventChartSchemaDescriptor.API_ENDPOINT + "/" + eventChart.getUid() );
    }

    @Override
    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = "application/json" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putJsonObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        EventChart eventChart = eventChartService.getEventChart( uid );

        if ( eventChart == null )
        {
            ContextUtils.notFoundResponse( response, "Event chart does not exist: " + uid );
            return;
        }

        EventChart newEventChart = JacksonUtils.fromJson( input, EventChart.class );

        mergeEventChart( newEventChart );

        eventChart.mergeWith( newEventChart );

        eventChartService.updateEventChart( eventChart );
    }

    @Override
    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void deleteObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid ) throws Exception
    {
        EventChart eventChart = eventChartService.getEventChart( uid );

        if ( eventChart == null )
        {
            ContextUtils.notFoundResponse( response, "Event report does not exist: " + uid );
            return;
        }

        eventChartService.deleteEventChart( eventChart );
    }

    //--------------------------------------------------------------------------
    // Hooks
    //--------------------------------------------------------------------------

    @Override
    protected void postProcessEntity( EventChart eventChart )
        throws Exception
    {
        eventChart.populateAnalyticalProperties();

        for ( OrganisationUnit organisationUnit : eventChart.getOrganisationUnits() )
        {
            eventChart.getParentGraphMap().put( organisationUnit.getUid(), organisationUnit.getParentGraph() );
        }

        I18nFormat format = i18nManager.getI18nFormat();

        if ( eventChart.getPeriods() != null && !eventChart.getPeriods().isEmpty() )
        {
            for ( Period period : eventChart.getPeriods() )
            {
                period.setName( format.formatPeriod( period ) );
            }
        }
    }

    //--------------------------------------------------------------------------
    // Supportive methods
    //--------------------------------------------------------------------------

    private void mergeEventChart( EventChart eventChart )
    {
        dimensionService.mergeAnalyticalObject( eventChart );

        eventChart.getColumnDimensions().clear();
        eventChart.getRowDimensions().clear();
        eventChart.getFilterDimensions().clear();

        eventChart.getColumnDimensions().addAll( getDimensions( eventChart.getColumns() ) );
        eventChart.getRowDimensions().addAll( getDimensions( eventChart.getRows() ) );
        eventChart.getFilterDimensions().addAll( getDimensions( eventChart.getFilters() ) );

        if ( eventChart.getProgram() != null )
        {
            eventChart.setProgram( programService.getProgram( eventChart.getProgram().getUid() ) );
        }

        if ( eventChart.getProgramStage() != null )
        {
            eventChart.setProgramStage( programStageService.getProgramStage( eventChart.getProgramStage().getUid() ) );
        }
    }
}
