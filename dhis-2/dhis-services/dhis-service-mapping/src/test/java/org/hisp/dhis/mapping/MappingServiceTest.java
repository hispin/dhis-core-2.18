package org.hisp.dhis.mapping;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class MappingServiceTest
    extends DhisSpringTest
{
    private MappingService mappingService;

    private OrganisationUnit organisationUnit;

    private OrganisationUnitLevel organisationUnitLevel;

    private IndicatorGroup indicatorGroup;

    private IndicatorType indicatorType;

    private Indicator indicator;

    private DataElement dataElement;
    
    private DataElementGroup dataElementGroup;

    private PeriodType periodType;

    private Period period;

    private MapLegendSet mapLegendSet;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        mappingService = (MappingService) getBean( MappingService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        indicatorService = (IndicatorService) getBean( IndicatorService.ID );

        dataElementService = (DataElementService) getBean( DataElementService.ID );

        periodService = (PeriodService) getBean( PeriodService.ID );

        organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitLevel = new OrganisationUnitLevel( 1, "Level" );

        organisationUnitService.addOrganisationUnit( organisationUnit );
        organisationUnitService.addOrganisationUnitLevel( organisationUnitLevel );

        indicatorGroup = createIndicatorGroup( 'A' );
        indicatorService.addIndicatorGroup( indicatorGroup );

        indicatorType = createIndicatorType( 'A' );
        indicatorService.addIndicatorType( indicatorType );

        indicator = createIndicator( 'A', indicatorType );
        indicatorService.addIndicator( indicator );

        dataElement = createDataElement( 'A' );
        dataElementService.addDataElement( dataElement );
        
        dataElementGroup = createDataElementGroup( 'A' );
        dataElementGroup.getMembers().add( dataElement );
        dataElementService.addDataElementGroup( dataElementGroup );

        periodType = periodService.getPeriodTypeByName( MonthlyPeriodType.NAME );
        period = createPeriod( periodType, getDate( 2000, 1, 1 ), getDate( 2000, 2, 1 ) );
        periodService.addPeriod( period );

        mapLegendSet = createMapLegendSet( 'A', indicator );
        mappingService.addMapLegendSet( mapLegendSet );
    }

    // -------------------------------------------------------------------------
    // MapView tests
    // -------------------------------------------------------------------------

    @Test
    public void testAddGetMapView()
    {
        MapView mapView = new MapView( "MapViewA", OrganisationUnit.FEATURETYPE_MULTIPOLYGON,
            MappingService.MAP_VALUE_TYPE_INDICATOR, indicatorGroup, indicator, dataElementGroup,
            dataElement, MappingService.MAP_DATE_TYPE_FIXED, periodType, period, "", "", organisationUnit,
            organisationUnitLevel, MappingService.MAPLEGENDSET_TYPE_AUTOMATIC, 1, 1, "", "A", "B", mapLegendSet, 5, 20,
            "1", "1", 1 );

        int idA = mappingService.addMapView( mapView );

        assertEquals( mapView, mappingService.getMapView( idA ) );
        assertEquals( indicatorGroup, mappingService.getMapView( idA ).getIndicatorGroup() );
        assertEquals( indicator, mappingService.getMapView( idA ).getIndicator() );
        assertEquals( periodType, mappingService.getMapView( idA ).getPeriodType() );
        assertEquals( period, mappingService.getMapView( idA ).getPeriod() );
    }

    @Test
    public void testGetDeleteMapViewByName()
    {
        MapView mapView = new MapView( "MapViewA", OrganisationUnit.FEATURETYPE_MULTIPOLYGON,
            MappingService.MAP_VALUE_TYPE_INDICATOR, indicatorGroup, indicator, dataElementGroup,
            dataElement, MappingService.MAP_DATE_TYPE_FIXED, periodType, period, "", "", organisationUnit,
            organisationUnitLevel, MappingService.MAPLEGENDSET_TYPE_AUTOMATIC, 1, 1, "", "A", "B", mapLegendSet, 5, 20,
            "1", "1", 1 );

        int id = mappingService.addMapView( mapView );
        
        mapView = mappingService.getMapViewByName( "MapViewA" );
        
        mappingService.deleteMapView( mapView );
        
        assertNull( mappingService.getMapView( id ) );
    }
    
    @Test
    public void testGetAllMapViews()
    {
        MapView mapView1 = new MapView( "MapViewA", OrganisationUnit.FEATURETYPE_MULTIPOLYGON,
            MappingService.MAP_VALUE_TYPE_INDICATOR, indicatorGroup, indicator, dataElementGroup,
            dataElement, MappingService.MAP_DATE_TYPE_FIXED, periodType, period, "", "", organisationUnit,
            organisationUnitLevel, MappingService.MAPLEGENDSET_TYPE_AUTOMATIC, 1, 1, "", "A", "B", mapLegendSet, 5, 20,
            "1", "1", 1 );
        
        MapView mapView2 = new MapView( "MapViewB", OrganisationUnit.FEATURETYPE_POLYGON,
            MappingService.MAP_VALUE_TYPE_DATAELEMENT, indicatorGroup, indicator, dataElementGroup,
            dataElement, MappingService.MAP_DATE_TYPE_START_END, periodType, period, "", "", organisationUnit,
            organisationUnitLevel, MappingService.MAPLEGENDSET_TYPE_AUTOMATIC, 1, 1, "", "A", "B", mapLegendSet, 5, 20,
            "2", "2", 1 );

        mappingService.addMapView( mapView1 );
        mappingService.addMapView( mapView2 );
        
        assertEquals( 2, mappingService.getAllMapViews().size() );
    }
    
    
    @Test
    public void testGetMapViewsByFeatureType()
    {
        MapView mapView1 = new MapView( "MapViewA", OrganisationUnit.FEATURETYPE_MULTIPOLYGON,
            MappingService.MAP_VALUE_TYPE_INDICATOR, indicatorGroup, indicator, dataElementGroup,
            dataElement, MappingService.MAP_DATE_TYPE_FIXED, periodType, period, "", "", organisationUnit,
            organisationUnitLevel, MappingService.MAPLEGENDSET_TYPE_AUTOMATIC, 1, 1, "", "A", "B", mapLegendSet, 5, 20,
            "1", "1", 1 );
        
        MapView mapView2 = new MapView( "MapViewB", OrganisationUnit.FEATURETYPE_POLYGON,
            MappingService.MAP_VALUE_TYPE_DATAELEMENT, indicatorGroup, indicator, dataElementGroup,
            dataElement, MappingService.MAP_DATE_TYPE_START_END, periodType, period, "", "", organisationUnit,
            organisationUnitLevel, MappingService.MAPLEGENDSET_TYPE_AUTOMATIC, 1, 1, "", "A", "B", mapLegendSet, 5, 20,
            "2", "2", 1 );
        
        MapView mapView3 = new MapView( "MapViewC", OrganisationUnit.FEATURETYPE_MULTIPOLYGON,
            MappingService.MAP_VALUE_TYPE_DATAELEMENT, indicatorGroup, indicator, dataElementGroup,
            dataElement, MappingService.MAP_DATE_TYPE_START_END, periodType, period, "", "", organisationUnit,
            organisationUnitLevel, MappingService.MAPLEGENDSET_TYPE_AUTOMATIC, 1, 1, "", "A", "B", mapLegendSet, 5, 20,
            "3", "3", 1 );

        mappingService.addMapView( mapView1 );
        mappingService.addMapView( mapView2 );
        mappingService.addMapView( mapView3 );
        
        assertEquals( 1, mappingService.getMapViewsByFeatureType( OrganisationUnit.FEATURETYPE_POLYGON ).size() );
        assertEquals( 2, mappingService.getMapViewsByFeatureType( OrganisationUnit.FEATURETYPE_MULTIPOLYGON ).size() );
    }
    
    // -------------------------------------------------------------------------
    // MapLayer
    // -------------------------------------------------------------------------
    
    @Test
    public void testAddGetMapLayer()
    {
        MapLayer mapLayer = new MapLayer( "MapLayerA", MappingService.MAP_LAYER_TYPE_BASELAYER, "", "", "A", 0.1, "B", 1);
        
        int id = mappingService.addMapLayer( mapLayer );
        
        assertEquals( "MapLayerA", mappingService.getMapLayer( id ).getName() );
        assertEquals( MappingService.MAP_LAYER_TYPE_BASELAYER, mappingService.getMapLayer( id ).getType() );
        assertEquals( "A", mappingService.getMapLayer( id ).getFillColor() );
        assertEquals( "B", mappingService.getMapLayer( id ).getStrokeColor() );
        assertEquals( 0.1, mappingService.getMapLayer( id ).getFillOpacity() );
        assertEquals( 1, mappingService.getMapLayer( id ).getStrokeWidth() );
    }
    
    @Test
    public void testGetUpdateDeleteMapLayerByName()
    {
        MapLayer mapLayer = new MapLayer( "MapLayerA", MappingService.MAP_LAYER_TYPE_BASELAYER, "", "", "A", 0.1, "B", 1);
        
        int id = mappingService.addMapLayer( mapLayer );
        
        mapLayer = mappingService.getMapLayer( id );
        
        mapLayer.setName( "MapLayerB" );
        mapLayer.setFillOpacity( 0.05 );
        mapLayer.setStrokeWidth( 0 );
        
        mappingService.updateMapLayer( mapLayer );
        
        assertEquals( "MapLayerB", mappingService.getMapLayerByName( "MapLayerB" ).getName() );        
        assertEquals( 0.05, mappingService.getMapLayerByName( "MapLayerB" ).getFillOpacity() );
        assertEquals( 0, mappingService.getMapLayerByName( "MapLayerB" ).getStrokeWidth() );
    }
    
    @Test
    public void testGetAllMapLayers()
    {
        MapLayer mapLayer1 = new MapLayer( "MapLayerA", MappingService.MAP_LAYER_TYPE_BASELAYER, "mapSourceA", "layerA", "A", 0.1, "B", 1);
        MapLayer mapLayer2 = new MapLayer( "MapLayerB", MappingService.MAP_LAYER_TYPE_OVERLAY, "", "", "A", 0.1, "B", 1);
        MapLayer mapLayer3 = new MapLayer( "MapLayerC", MappingService.MAP_LAYER_TYPE_OVERLAY, "mapSourceC", "layerC", "C", 0.1, "D", 2);
        MapLayer mapLayer4 = new MapLayer( "MapLayerD", MappingService.MAP_LAYER_TYPE_BASELAYER, "mapSourceD", "layerA", "C", 0.1, "D", 2);
        
        int idA = mappingService.addMapLayer( mapLayer1 );
        int idB = mappingService.addMapLayer( mapLayer2 );
        int idC = mappingService.addMapLayer( mapLayer3 );
        
        assertEquals( mapLayer1, mappingService.getMapLayer( idA ) );
        assertEquals( mapLayer2, mappingService.getMapLayer( idB ) );
        assertEquals( mapLayer3, mappingService.getMapLayer( idC ) );
        assertTrue( !mappingService.getAllMapLayers().contains( mapLayer4 ) );

    }
    
    @Test
    public void testGetMapLayersByTypeOrMapSource()
    {
        List<MapLayer> baseLayers = new ArrayList<MapLayer>();
        List<MapLayer> overlayLayers = new ArrayList<MapLayer>();
        
        MapLayer mapLayer1 = new MapLayer( "MapLayerA", MappingService.MAP_LAYER_TYPE_BASELAYER, "mapSourceA", "layerA", "A", 0.1, "B", 1);
        MapLayer mapLayer2 = new MapLayer( "MapLayerB", MappingService.MAP_LAYER_TYPE_OVERLAY, "mapSourceB", "", "A", 0.1, "B", 1);
        MapLayer mapLayer3 = new MapLayer( "MapLayerC", MappingService.MAP_LAYER_TYPE_OVERLAY, "mapSourceC", "layerC", "C", 0.1, "D", 2);
        MapLayer mapLayer4 = new MapLayer( "MapLayerD", MappingService.MAP_LAYER_TYPE_BASELAYER, "mapSourceD", "layerA", "C", 0.1, "D", 2);
        
        baseLayers.add( mapLayer1 );
        baseLayers.add( mapLayer4 );
        
        overlayLayers.add( mapLayer2 );
        overlayLayers.add( mapLayer3 );
        
        int idA = mappingService.addMapLayer( mapLayer1 );
        int idB = mappingService.addMapLayer( mapLayer2 );
        int idC = mappingService.addMapLayer( mapLayer3 );
        int idD = mappingService.addMapLayer( mapLayer4 );
        
        assertEquals( baseLayers, mappingService.getMapLayersByType( MappingService.MAP_LAYER_TYPE_BASELAYER ) );
        assertEquals( overlayLayers, mappingService.getMapLayersByType( MappingService.MAP_LAYER_TYPE_OVERLAY ) );
        
        assertEquals( mappingService.getMapLayer( idA ), mappingService.getMapLayerByMapSource( "mapSourceA" ) );
        assertEquals( mappingService.getMapLayer( idB ), mappingService.getMapLayerByMapSource( "mapSourceB" ) );
        assertEquals( mappingService.getMapLayer( idC ), mappingService.getMapLayerByMapSource( "mapSourceC" ) );
        assertEquals( mappingService.getMapLayer( idD ), mappingService.getMapLayerByMapSource( "mapSourceD" ) );

    }
    
    // -------------------------------------------------------------------------
    // Map value tests
    // -------------------------------------------------------------------------

    @Test
    public void testMapValues()
    {
        mappingService.getDataElementMapValues( dataElement.getId(), period.getId(), organisationUnit.getId() );
        mappingService.getIndicatorMapValues( indicator.getId(), period.getId(), organisationUnit.getId() );
    }

}
