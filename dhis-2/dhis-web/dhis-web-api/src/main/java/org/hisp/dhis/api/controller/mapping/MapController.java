package org.hisp.dhis.api.controller.mapping;

/*
 * Copyright (c) 2011, University of Oslo
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

import static org.hisp.dhis.period.PeriodType.getPeriodFromIsoString;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.controller.AbstractCrudController;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.mapgeneration.MapGenerationService;
import org.hisp.dhis.mapping.Map;
import org.hisp.dhis.mapping.MapView;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = MapController.RESOURCE_PATH )
public class MapController
    extends AbstractCrudController<MapView>
{
    public static final String RESOURCE_PATH = "/maps";

    @Autowired
    private MappingService mappingService;

    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private IndicatorService indicatorService;
    
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private PeriodService periodService;
        
    @Autowired
    private MapGenerationService mapGenerationService;

    @Autowired
    private ContextUtils contextUtils;

    //--------------------------------------------------------------------------
    // CRUD
    //--------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    @PreAuthorize( "hasRole('F_GIS_ADMIN') or hasRole('ALL')" )
    public void postJsonObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        Map map = JacksonUtils.fromJson( input, Map.class );
        
        if ( map.getUser() != null )
        {
            map.setUser( userService.getUser( map.getUser().getUid() ) );
        }
        
        for ( MapView view : map.getViews() )
        {
            if ( view.getIndicatorGroup() != null )
            {
                view.setIndicatorGroup( indicatorService.getIndicatorGroup( view.getIndicatorGroup().getUid() ) );
            }
            
            if ( view.getIndicator() != null )
            {
                view.setIndicator( indicatorService.getIndicator( view.getIndicator().getUid() ) );
            }
            
            if ( view.getDataElementGroup() != null )
            {
                view.setDataElementGroup( dataElementService.getDataElementGroup( view.getDataElementGroup().getUid() ) );
            }
            
            if ( view.getDataElement() != null )
            {
                view.setDataElement( dataElementService.getDataElement( view.getDataElement().getUid() ) );
            }
            
            if ( view.getPeriod() != null )
            {
                view.setPeriod( periodService.reloadPeriod( getPeriodFromIsoString( view.getPeriod().getUid() ) ) );
            }
            
            if ( view.getParentOrganisationUnit() != null )
            {
                view.setParentOrganisationUnit( organisationUnitService.getOrganisationUnit( view.getParentOrganisationUnit().getUid() ) );
            }
            
            if ( view.getOrganisationUnitLevel() != null )
            {
                view.setOrganisationUnitLevel( organisationUnitService.getOrganisationUnitLevel( view.getOrganisationUnitLevel().getUid() ) );
            }
            
            if ( view.getLegendSet() != null )
            {
                view.setLegendSet( mappingService.getMapLegendSet( view.getLegendSet().getUid() ) );
            }
            
            if ( view.getOrganisationUnitGroupSet() != null )
            {
                view.setOrganisationUnitGroupSet( organisationUnitGroupService.getOrganisationUnitGroupSet( view.getOrganisationUnitGroupSet().getUid() ) );
            }
            
            mappingService.addMapView( view );
        }
        
        mappingService.addMap( map );
        
        ContextUtils.createdResponse( response, "Map created", RESOURCE_PATH + "/" + map.getUid() );
    }

    //--------------------------------------------------------------------------
    // Data
    //--------------------------------------------------------------------------

    @RequestMapping( value = { "/{uid}/data", "/{uid}/data.png" }, method = RequestMethod.GET )
    public void getMap( @PathVariable String uid, HttpServletResponse response ) throws Exception
    {
        MapView mapView = getEntity( uid );

        renderMapViewPng( mapView, response );
    }

    @RequestMapping( value = { "/data", "/data.png" }, method = RequestMethod.GET )
    public void getMap( Model model,
        @RequestParam( value = "in" ) String indicatorUid,
        @RequestParam( value = "ou" ) String organisationUnitUid,
        @RequestParam( value = "level", required = false ) Integer level,
        HttpServletResponse response ) throws Exception
    {
        if ( level == null )
        {
            OrganisationUnit unit = organisationUnitService.getOrganisationUnit( organisationUnitUid );

            level = organisationUnitService.getLevelOfOrganisationUnit( unit.getId() );
            level++;
        }

        MapView mapView = mappingService.getIndicatorLastYearMapView( indicatorUid, organisationUnitUid, level );

        renderMapViewPng( mapView, response );
    }

    //--------------------------------------------------------------------------
    // Supportive methods
    //--------------------------------------------------------------------------

    private void renderMapViewPng( MapView mapView, HttpServletResponse response )
        throws Exception
    {
        BufferedImage image = mapGenerationService.generateMapImage( mapView );

        if ( image != null )
        {
            contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_PNG, CacheStrategy.RESPECT_SYSTEM_SETTING, "mapview.png", false );

            ImageIO.write( image, "PNG", response.getOutputStream() );
        }
        else
        {
            response.setStatus( HttpServletResponse.SC_NO_CONTENT );
        }
    }
}
