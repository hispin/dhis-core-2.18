package org.hisp.dhis.mapping;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import static org.hisp.dhis.system.util.MathUtils.isNumeric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.UserSettingService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Jan Henrik Overland
 * @version $Id$
 */
@Transactional
public class DefaultMappingService
    implements MappingService
{
    private static final Log log = LogFactory.getLog( DefaultMappingService.class );
    
    private static final String RELATION_SEPARATOR = ";;";
    private static final String PAIR_SEPARATOR = "::";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MappingStore mappingStore;

    public void setMappingStore( MappingStore mappingStore )
    {
        this.mappingStore = mappingStore;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }

    // -------------------------------------------------------------------------
    // MappingService implementation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Map
    // -------------------------------------------------------------------------

    public int addMap( Map map )
    {
        return mappingStore.addMap( map );
    }

    public int addMap( String name, String mapLayerPath, String type, int organisationUnitId,
        int organisationUnitLevelId, String nameColumn, String longitude, String latitude, int zoom )
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        OrganisationUnitLevel organisationUnitLevel = organisationUnitService
            .getOrganisationUnitLevel( organisationUnitLevelId );

        Set<String> staticMapLayerPaths = null;

        Map map = new Map( name, mapLayerPath, type, organisationUnit, organisationUnitLevel, nameColumn,
            longitude, latitude, zoom, staticMapLayerPaths );

        return addMap( map );
    }

    public void addOrUpdateMap( String name, String mapLayerPath, String type, int organisationUnitId,
        int organisationUnitLevelId, String nameColumn, String longitude, String latitude, int zoom )
    {
        Map map = getMapByMapLayerPath( mapLayerPath );

        if ( map != null )
        {
            map.setName( name );
            map.setNameColumn( nameColumn );
            map.setLongitude( longitude );
            map.setLatitude( latitude );
            map.setZoom( zoom );

            updateMap( map );
        }
        else
        {
            OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

            OrganisationUnitLevel organisationUnitLevel = organisationUnitService
                .getOrganisationUnitLevel( organisationUnitLevelId );

            map = new Map( name, mapLayerPath, type, organisationUnit, organisationUnitLevel, nameColumn,
                longitude, latitude, zoom, null );

            addMap( map );
        }
    }

    public void updateMap( Map map )
    {
        mappingStore.updateMap( map );
    }

    public void deleteMap( Map map )
    {
        mappingStore.deleteMap( map );
    }

    public void deleteMapByMapLayerPath( String mapLayerPath )
    {
        Map map = getMapByMapLayerPath( mapLayerPath );

        deleteMap( map );
    }

    public Map getMap( int id )
    {
        return mappingStore.getMap( id );
    }

    public Map getMapByMapLayerPath( String mapLayerPath )
    {
        return mappingStore.getMapByMapLayerPath( mapLayerPath );
    }

    public Collection<Map> getMapsByType( String type )
    {
        return mappingStore.getMapsByType( type );
    }

    public Collection<Map> getMapsAtLevel( OrganisationUnitLevel organisationUnitLevel )
    {
        return mappingStore.getMapsAtLevel( organisationUnitLevel );
    }

    public Collection<Map> getAllMaps()
    {
        return mappingStore.getAllMaps();
    }

    public Collection<Map> getAllGeneratedMaps()
    {
        List<OrganisationUnitLevel> organisationUnitLevels = organisationUnitService.getOrganisationUnitLevels();

        List<Map> maps = new ArrayList<Map>();

        for ( OrganisationUnitLevel organisationUnitLevel : organisationUnitLevels )
        {
            Map map = new Map();
            map.setName( organisationUnitLevel.getName() );
            map.setMapLayerPath( String.valueOf( organisationUnitLevel.getLevel() ) );
            map.setOrganisationUnitLevel( organisationUnitLevel );
            maps.add( map );
        }

        return maps;
    }

    public Collection<Map> getAllUserMaps()
    {
        String type = (String) userSettingService.getUserSetting( KEY_MAP_SOURCE_TYPE, MAP_SOURCE_TYPE_DATABASE );

        return type != null && type.equals( MAP_SOURCE_TYPE_DATABASE ) ? getAllGeneratedMaps() : getAllMaps();
    }

    // -------------------------------------------------------------------------
    // MapOrganisationUnitRelation
    // -------------------------------------------------------------------------

    public int addMapOrganisationUnitRelation( MapOrganisationUnitRelation mapOrganisationUnitRelation )
    {
        return mappingStore.addMapOrganisationUnitRelation( mapOrganisationUnitRelation );
    }

    public int addMapOrganisationUnitRelation( String mapLayerPath, int organisationUnitId, String featureId )
    {
        Map map = getMapByMapLayerPath( mapLayerPath );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        MapOrganisationUnitRelation mapOrganisationUnitRelation = new MapOrganisationUnitRelation( map,
            organisationUnit, featureId );

        return addMapOrganisationUnitRelation( mapOrganisationUnitRelation );
    }

    public void addOrUpdateMapOrganisationUnitRelations( String mapLayerPath, String relations )
    {
        String[] rels = relations.split( RELATION_SEPARATOR );
        
        relationsLoop : for ( int i = 0; i < rels.length; i++ )
        {
            String[] rel = rels[i].split( PAIR_SEPARATOR );

            if ( rel.length != 2 )
            {
                log.warn( "Pair '" + toString( rel ) + "' is invalid for input '" + rels[i] + "'" );
                
                continue relationsLoop;
            }
            
            if ( !isNumeric( rel[0]) )
            {
                log.warn( "Organisation unit id '" + rel[0] + "' belonging to feature id '" + rel[1] + "' is not numeric" );
                
                continue relationsLoop;
            }
            
            addOrUpdateMapOrganisationUnitRelation( mapLayerPath, Integer.parseInt( rel[0] ), rel[1] );
        }
    }

    /**
     * Provides a textual representation of the contents of a String array.
     */
    private String toString( String[] array )
    {
        StringBuffer buffer = new StringBuffer( "{" );
        
        for ( int i = 0; i < array.length; i++ )
        {
            buffer.append( "[" + array[i] + "]," );
        }
        
        return buffer.append( "}" ).toString();
    }
    
    public void addOrUpdateMapOrganisationUnitRelation( String mapLayerPath, int organisationUnitId, String featureId )
    {
        Map map = getMapByMapLayerPath( mapLayerPath );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        MapOrganisationUnitRelation mapOrganisationUnitRelation = getMapOrganisationUnitRelation( map, organisationUnit );
        
        if ( mapOrganisationUnitRelation != null )
        {            
            mapOrganisationUnitRelation.setFeatureId( featureId );

            updateMapOrganisationUnitRelation( mapOrganisationUnitRelation );
        }
        else
        {
            mapOrganisationUnitRelation = new MapOrganisationUnitRelation( map, organisationUnit, featureId );

            addMapOrganisationUnitRelation( mapOrganisationUnitRelation );
        }
    }

    public void updateMapOrganisationUnitRelation( MapOrganisationUnitRelation mapOrganisationUnitRelation )
    {
        mappingStore.updateMapOrganisationUnitRelation( mapOrganisationUnitRelation );
    }

    public void deleteMapOrganisationUnitRelation( MapOrganisationUnitRelation mapOrganisationUnitRelation )
    {
        mappingStore.deleteMapOrganisationUnitRelation( mapOrganisationUnitRelation );
    }

    public MapOrganisationUnitRelation getMapOrganisationUnitRelation( int id )
    {
        return mappingStore.getMapOrganisationUnitRelation( id );
    }

    public MapOrganisationUnitRelation getMapOrganisationUnitRelation( Map map, OrganisationUnit organisationUnit )
    {
        return mappingStore.getMapOrganisationUnitRelation( map, organisationUnit );
    }

    public Collection<MapOrganisationUnitRelation> getAllMapOrganisationUnitRelations()
    {
        return mappingStore.getAllMapOrganisationUnitRelations();
    }

    public Collection<MapOrganisationUnitRelation> getMapOrganisationUnitRelationByMap( Map map )
    {
        return mappingStore.getMapOrganisationUnitRelationByMap( map );
    }

    public Collection<MapOrganisationUnitRelation> getAvailableMapOrganisationUnitRelations( Map map )
    {        
        Collection<OrganisationUnit> organisationUnits = organisationUnitService.getOrganisationUnitsAtLevel( map
            .getOrganisationUnitLevel().getLevel() );

        java.util.Map<Integer, MapOrganisationUnitRelation> relationMap = getRelationshipMap( mappingStore.getMapOrganisationUnitRelationByMap( map ) );
        
        Collection<MapOrganisationUnitRelation> availableRelations = new ArrayList<MapOrganisationUnitRelation>();
        
        for ( OrganisationUnit unit : organisationUnits )
        {
            MapOrganisationUnitRelation relation = relationMap.get( unit.getId() );
            
            availableRelations.add( relation != null ? relation : new MapOrganisationUnitRelation( map, unit, null ) );
        }
        
        return availableRelations;
    }
    
    /**
     * Returns a Map<Integer, MapOrganisationUnitRelation> where the key is the 
     * OrganisationUnit identifier and the value the MapOrganisationUnitRelation
     * itself.
     */
    private java.util.Map<Integer, MapOrganisationUnitRelation> getRelationshipMap( Collection<MapOrganisationUnitRelation> relations )
    {
        java.util.Map<Integer, MapOrganisationUnitRelation> map = new HashMap<Integer, MapOrganisationUnitRelation>();
        
        for ( MapOrganisationUnitRelation relation : relations )
        {
            map.put( relation.getOrganisationUnit().getId(), relation );
        }
        
        return map;
    }

    public Collection<MapOrganisationUnitRelation> getAvailableMapOrganisationUnitRelations( String mapLayerPath )
    {
        Map map = getMapByMapLayerPath( mapLayerPath );

        return getAvailableMapOrganisationUnitRelations( map );
    }

    public int deleteMapOrganisationUnitRelations( OrganisationUnit organisationUnit )
    {
        return mappingStore.deleteMapOrganisationUnitRelations( organisationUnit );
    }

    public int deleteMapOrganisationUnitRelations( Map map )
    {
        return mappingStore.deleteMapOrganisationUnitRelations( map );
    }

    // -------------------------------------------------------------------------
    // MapLegendSet
    // -------------------------------------------------------------------------

    public int addMapLegendSet( MapLegendSet mapLegendSet )
    {
        return mappingStore.addMapLegendSet( mapLegendSet );
    }

    public void updateMapLegendSet( MapLegendSet mapLegendSet )
    {
        mappingStore.updateMapLegendSet( mapLegendSet );
    }

    public void addOrUpdateMapLegendSet( String name, int method, int classes, String colorLow, String colorHigh,
        Collection<String> indicators )
    {
        MapLegendSet mapLegendSet = getMapLegendSetByName( name );

        Set<Indicator> indicatorSet = new HashSet<Indicator>();

        for ( String indicator : indicators )
        {
            indicatorSet.add( indicatorService.getIndicator( Integer.parseInt( indicator ) ) );
        }

        if ( mapLegendSet != null )
        {
            mapLegendSet.setMethod( method );
            mapLegendSet.setClasses( classes );
            mapLegendSet.setColorLow( colorLow );
            mapLegendSet.setColorHigh( colorHigh );
            mapLegendSet.setIndicators( indicatorSet );

            mappingStore.updateMapLegendSet( mapLegendSet );
        }
        else
        {
            mapLegendSet = new MapLegendSet( name, method, classes, colorLow, colorHigh, indicatorSet );

            mappingStore.addMapLegendSet( mapLegendSet );
        }
    }

    public void deleteMapLegendSet( MapLegendSet mapLegendSet )
    {
        mappingStore.deleteMapLegendSet( mapLegendSet );
    }

    public MapLegendSet getMapLegendSet( int id )
    {
        return mappingStore.getMapLegendSet( id );
    }

    public MapLegendSet getMapLegendSetByName( String name )
    {
        return mappingStore.getMapLegendSetByName( name );
    }

    public MapLegendSet getMapLegendSetByIndicator( int indicatorId )
    {
        Indicator indicator = indicatorService.getIndicator( indicatorId );

        Collection<MapLegendSet> mapLegendSets = mappingStore.getAllMapLegendSets();

        for ( MapLegendSet mapLegendSet : mapLegendSets )
        {
            if ( mapLegendSet.getIndicators().contains( indicator ) )
            {
                return mapLegendSet;
            }
        }

        return null;
    }

    public Collection<MapLegendSet> getAllMapLegendSets()
    {
        return mappingStore.getAllMapLegendSets();
    }

    public boolean indicatorHasMapLegendSet( int indicatorId )
    {
        Indicator indicator = indicatorService.getIndicator( indicatorId );

        Collection<MapLegendSet> mapLegendSets = mappingStore.getAllMapLegendSets();

        for ( MapLegendSet mapLegendSet : mapLegendSets )
        {
            if ( mapLegendSet.getIndicators().contains( indicator ) )
            {
                return true;
            }
        }

        return false;
    }

    // -------------------------------------------------------------------------
    // MapView
    // -------------------------------------------------------------------------

    public int addMapView( MapView mapView )
    {
        return mappingStore.addMapView( mapView );
    }

    public int addMapView( String name, int indicatorGroupId, int indicatorId, String periodTypeName, int periodId,
        String mapSourceType, String mapSource, int method, int classes, String colorLow, String colorHigh )
    {
        MapView mapView = new MapView();

        IndicatorGroup indicatorGroup = indicatorService.getIndicatorGroup( indicatorGroupId );

        Indicator indicator = indicatorService.getIndicator( indicatorId );

        PeriodType periodType = periodService.getPeriodTypeByClass( PeriodType.getPeriodTypeByName( periodTypeName )
            .getClass() );

        Period period = periodService.getPeriod( periodId );

        mapView.setName( name );
        mapView.setIndicatorGroup( indicatorGroup );
        mapView.setIndicator( indicator );
        mapView.setPeriodType( periodType );
        mapView.setPeriod( period );
        mapView.setMapSourceType( mapSourceType );
        mapView.setMapSource( mapSource );
        mapView.setMethod( method );
        mapView.setClasses( classes );
        mapView.setColorLow( colorLow );
        mapView.setColorHigh( colorHigh );

        return mappingStore.addMapView( mapView );
    }

    public void updateMapView( MapView mapView )
    {
        mappingStore.updateMapView( mapView );
    }

    public void addOrUpdateMapView( String name, int indicatorGroupId, int indicatorId, String periodTypeName,
        int periodId, String mapSourceType, String mapSource, int method, int classes, String colorLow, String colorHigh )
    {
        IndicatorGroup indicatorGroup = indicatorService.getIndicatorGroup( indicatorGroupId );

        Indicator indicator = indicatorService.getIndicator( indicatorId );

        PeriodType periodType = periodService.getPeriodTypeByClass( PeriodType.getPeriodTypeByName( periodTypeName )
            .getClass() );

        Period period = periodService.getPeriod( periodId );

        MapView mapView = mappingStore.getMapViewByName( name );

        if ( mapView != null )
        {
            mapView.setName( name );
            mapView.setIndicatorGroup( indicatorGroup );
            mapView.setIndicator( indicator );
            mapView.setPeriodType( periodType );
            mapView.setPeriod( period );
            mapView.setMapSourceType( mapSourceType );
            mapView.setMapSource( mapSource );
            mapView.setMethod( method );
            mapView.setClasses( classes );
            mapView.setColorLow( colorLow );
            mapView.setColorHigh( colorHigh );

            updateMapView( mapView );
        }
        else
        {
            mapView = new MapView( name, indicatorGroup, indicator, periodType, period, mapSourceType, mapSource,
                method, classes, colorLow, colorHigh );

            addMapView( mapView );
        }
    }

    public void deleteMapView( MapView view )
    {
        mappingStore.deleteMapView( view );
    }

    public MapView getMapView( int id )
    {
        return mappingStore.getMapView( id );
    }

    public MapView getMapViewByName( String name )
    {
        return mappingStore.getMapViewByName( name );
    }

    public Collection<MapView> getAllMapViews()
    {
        Collection<MapView> selectedMapViews = new ArrayList<MapView>();

        Collection<MapView> mapViews = mappingStore.getAllMapViews();

        String mapSourceType = (String) userSettingService.getUserSetting( KEY_MAP_SOURCE_TYPE,
            MAP_SOURCE_TYPE_SHAPEFILE );

        if ( mapViews != null )
        {
            for ( MapView mapView : mapViews )
            {
                if ( mapView.getMapSourceType().equals( mapSourceType ) )
                {
                    selectedMapViews.add( mapView );
                }
            }
        }

        return selectedMapViews;
    }

    // -------------------------------------------------------------------------
    // MapLayer
    // -------------------------------------------------------------------------

    public int addMapLayer( MapLayer mapLayer )
    {
        return mappingStore.addMapLayer( mapLayer );
    }

    public void updateMapLayer( MapLayer mapLayer )
    {
        mappingStore.updateMapLayer( mapLayer );
    }

    public void addOrUpdateMapLayer( String name, String type, String mapSource, String fillColor, double fillOpacity,
        String strokeColor, int strokeWidth )
    {
        MapLayer mapLayer = mappingStore.getMapLayerByName( name );

        if ( mapLayer != null )
        {
            mapLayer.setName( name );
            mapLayer.setType( type );
            mapLayer.setMapSource( mapSource );
            mapLayer.setFillColor( fillColor );
            mapLayer.setFillOpacity( fillOpacity );
            mapLayer.setStrokeColor( strokeColor );
            mapLayer.setStrokeWidth( strokeWidth );

            updateMapLayer( mapLayer );
        }
        else
        {
            addMapLayer( new MapLayer( name, type, mapSource, fillColor, fillOpacity, strokeColor, strokeWidth ) );
        }
    }

    public void deleteMapLayer( MapLayer mapLayer )
    {
        mappingStore.deleteMapLayer( mapLayer );
    }

    public MapLayer getMapLayer( int id )
    {
        return mappingStore.getMapLayer( id );
    }

    public MapLayer getMapLayerByName( String name )
    {
        return mappingStore.getMapLayerByName( name );
    }

    public MapLayer getMapLayerByMapSource( String mapSource )
    {
        return mappingStore.getMapLayerByMapSource( mapSource );
    }

    public Collection<MapLayer> getAllMapLayers()
    {
        return mappingStore.getAllMapLayers();
    }
}
