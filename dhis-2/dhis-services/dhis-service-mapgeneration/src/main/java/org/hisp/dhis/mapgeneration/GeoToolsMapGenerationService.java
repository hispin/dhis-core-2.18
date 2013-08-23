package org.hisp.dhis.mapgeneration;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import static org.hisp.dhis.common.NameableObjectUtils.getList;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.mapgeneration.IntervalSet.DistributionStrategy;
import org.hisp.dhis.mapping.Map;
import org.hisp.dhis.mapping.MapView;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.springframework.util.Assert;

/**
 * An implementation of MapGenerationService that uses GeoTools to generate
 * maps.
 * 
 * @author Kenneth Solbø Andersen <kennetsa@ifi.uio.no>
 * @author Kristin Simonsen <krissimo@ifi.uio.no>
 * @author Kjetil Andresen <kjetand@ifi.uio.no>
 * @author Olai Solheim <olais@ifi.uio.no>
 */
public class GeoToolsMapGenerationService
    implements MapGenerationService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private AnalyticsService analyticsService;

    public void setAnalyticsService( AnalyticsService analyticsService )
    {
        this.analyticsService = analyticsService;
    }

    // -------------------------------------------------------------------------
    // MapGenerationService implementation
    // -------------------------------------------------------------------------

    public BufferedImage generateMapImage( MapView mapView )
    {
        Map map = new Map();
        
        map.getMapViews().add( mapView );
        
        return generateMapImage( map );
    }

    public BufferedImage generateMapImage( Map map )
    {
        return generateMapImage( map, 512, null );
    }
    
    public BufferedImage generateMapImage( Map map, Integer width, Integer height )
    {
        Assert.isTrue( map != null );
        
        if ( width == null && height == null )
        {
            width = MapUtils.DEFAULT_MAP_WIDTH;
        }

        InternalMap internalMap = new InternalMap();
        
        for ( MapView mapView : map.getMapViews() )
        {        
            InternalMapLayer mapLayer = getSingleInternalMapLayer( mapView );
            
            if ( mapLayer != null )
            {
                internalMap.getLayers().add( mapLayer );
            }
        }
        
        if ( internalMap.getLayers().isEmpty() )
        {
            return null;
        }
        
        // Build representation of a map using GeoTools, then render as image
        BufferedImage mapImage = MapUtils.render( internalMap, width, height );

        // Build the legend set, then render it to an image
        LegendSet legendSet = new LegendSet( internalMap.getLayers().get( 0 ) ); //TODO
        BufferedImage legendImage = legendSet.render();

        // Combine the legend image and the map image into one image
        BufferedImage finalImage = combineLegendAndMapImages( legendImage, mapImage );

        return finalImage;
    }

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------

    private static final String DEFAULT_COLOR_HIGH = "#ff0000";

    private static final String DEFAULT_COLOR_LOW = "#ffff00";

    private static final float DEFAULT_OPACITY = 0.75f;

    private static final String DEFAULT_STROKE_COLOR = "#ffffff";

    private static final int DEFAULT_STROKE_WIDTH = 1;

    private static final int DEFAULT_RADIUS_HIGH = 35;

    private static final int DEFAULT_RADIUS_LOW = 15;

    private InternalMapLayer getSingleInternalMapLayer( MapView mapView )
    {
        if ( mapView == null || mapView.getPeriod() == null || mapView.getParentOrganisationUnit() == null )
        {
            return null;
        }

        boolean isIndicator = MapView.VALUE_TYPE_INDICATOR.equals( mapView.getValueType() );

        List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.
            getOrganisationUnitsAtLevel( mapView.getOrganisationUnitLevel().getLevel(), mapView.getParentOrganisationUnit() ) );

        java.util.Map<String, OrganisationUnit> uidOuMap = new HashMap<String, OrganisationUnit>();
        
        for ( OrganisationUnit ou : organisationUnits )
        {
            uidOuMap.put( ou.getUid(), ou );
        }
        
        mapView.setOrganisationUnitsAtLevel( organisationUnits );
        
        Grid grid = getDataGrid( mapView );
        
        Collection<MapValue> mapValues = getMapValues( grid );
        
        if ( mapValues.isEmpty() )
        {
            return null;
        }
        
        // Get the name from the external layer
        String name = mapView.getName();

        // Get the period
        Period period = mapView.getPeriod();

        // Get the low and high radii
        int radiusLow = !isIndicator ? mapView.getRadiusLow() : DEFAULT_RADIUS_LOW;
        int radiusHigh = !isIndicator ? mapView.getRadiusHigh() : DEFAULT_RADIUS_HIGH;

        // Get the low and high colors, typically in hexadecimal form, e.g. #ff3200
        Color colorLow = MapUtils.createColorFromString( StringUtils.trimToNull( mapView.getColorLow() ) != null ? mapView.getColorLow()
            : DEFAULT_COLOR_LOW );
        Color colorHigh = MapUtils.createColorFromString( StringUtils.trimToNull( mapView.getColorHigh() ) != null ? mapView.getColorHigh()
            : DEFAULT_COLOR_HIGH );

        // TODO MapView should be extended to feature opacity
        float opacity = DEFAULT_OPACITY;

        // TODO MapView should be extended to feature stroke color
        Color strokeColor = MapUtils.createColorFromString( DEFAULT_STROKE_COLOR );

        // TODO MapView might be extended to feature stroke width
        int strokeWidth = DEFAULT_STROKE_WIDTH;

        // Create and setup an internal layer
        InternalMapLayer mapLayer = new InternalMapLayer();
        mapLayer.setName( name );
        mapLayer.setPeriod( period );
        mapLayer.setRadiusLow( radiusLow );
        mapLayer.setRadiusHigh( radiusHigh );
        mapLayer.setColorLow( colorLow );
        mapLayer.setColorHigh( colorHigh );
        mapLayer.setOpacity( opacity );
        mapLayer.setStrokeColor( strokeColor );
        mapLayer.setStrokeWidth( strokeWidth );

        // Build and set the internal GeoTools map objects for the layer
        
        for ( MapValue mapValue : mapValues )
        {
            // Get the org unit for this map value
            OrganisationUnit orgUnit = uidOuMap.get( mapValue.getOu() );
            
            if ( orgUnit != null && orgUnit.hasCoordinates() && orgUnit.hasFeatureType() )
            {
                buildSingleGeoToolsMapObjectForMapLayer( mapLayer, mapValue.getValue(), orgUnit );
            }
        }

        // Create an interval set for this map layer that distributes its map
        // objects into their respective intervals
        // TODO Make interval length a parameter
        IntervalSet.applyIntervalSetToMapLayer( DistributionStrategy.STRATEGY_EQUAL_RANGE, mapLayer, 5 );

        // Update the radius of each map object in this map layer according to
        // its map object's highest and lowest values
        if ( !isIndicator )
        {
            mapLayer.applyInterpolatedRadii();
        }

        return mapLayer;
    }

    /**
     * Creates a Grid with aggregated data.
     */
    private Grid getDataGrid( MapView mapView )
    {
        DataQueryParams params = new DataQueryParams();
        
        if ( mapView.getIndicator() != null )
        {
            params.setIndicators( getList( mapView.getIndicator() ) );
        }
        else if ( mapView.getDataElement() != null )
        {
            params.setDataElements( getList( mapView.getDataElement() ) );
        }
        
        //TODO operands

        params.setOrganisationUnits( mapView.getOrganisationUnitsAtLevel() );
        params.setFilterPeriods( getList( mapView.getPeriod() ) );
        
        return analyticsService.getAggregatedDataValues( params );
    }
    
    /**
     * Creates a list of aggregated map values.
     */
    private List<MapValue> getMapValues( Grid grid )
    {
        List<MapValue> mapValues = new ArrayList<MapValue>();

        for ( List<Object> row : grid.getRows() )
        {
            if ( row != null && row.size() >= 3 )
            {
                String ou = (String) row.get( 1 );
                Double value = (Double) row.get( 2 );
                
                mapValues.add( new MapValue( ou, value ) );
            }
        }

        return mapValues;
    }
    
    private InternalMapObject buildSingleGeoToolsMapObjectForMapLayer( InternalMapLayer mapLayer,
        double mapValue, OrganisationUnit orgUnit )
    {
        // Create and setup an internal map object
        InternalMapObject mapObject = new InternalMapObject();
        mapObject.setName( orgUnit.getName() );
        mapObject.setValue( mapValue );
        mapObject.setFillOpacity( mapLayer.getOpacity() );
        mapObject.setStrokeColor( mapLayer.getStrokeColor() );
        mapObject.setStrokeWidth( mapLayer.getStrokeWidth() );

        // Build and set the GeoTools-specific geometric primitive that outlines
        // the org unit on the map
        mapObject.setGeometry( InternalMapObject.buildAndApplyGeometryForOrganisationUnit( orgUnit ) );

        // Add the map object to the map layer
        mapLayer.addMapObject( mapObject );

        // Set the map layer for the map object
        mapObject.setMapLayer( mapLayer );

        return mapObject;
    }

    private BufferedImage combineLegendAndMapImages( BufferedImage legendImage, BufferedImage mapImage )
    {
        Assert.isTrue( legendImage != null );
        Assert.isTrue( mapImage != null );
        Assert.isTrue( legendImage.getType() == mapImage.getType() );

        // Create a new image with dimension (legend.width + map.width,
        // max(legend.height, map.height))
        BufferedImage finalImage = new BufferedImage( legendImage.getWidth() + mapImage.getWidth(), Math.max(
            mapImage.getHeight(), mapImage.getHeight() ), mapImage.getType() );

        // Draw the two images onto the final image with the legend to the left
        // and the map to the right
        Graphics g = finalImage.getGraphics();
        g.drawImage( legendImage, 0, 0, null );
        g.drawImage( mapImage, legendImage.getWidth(), 0, null );

        return finalImage;
    }
}
