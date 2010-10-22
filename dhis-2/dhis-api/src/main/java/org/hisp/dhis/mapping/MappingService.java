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

import java.util.Collection;
import java.util.Set;

import org.hisp.dhis.aggregation.AggregatedMapValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;

/**
 * @author Jan Henrik Overland
 * @version $Id$
 */
public interface MappingService
{
    final String ID = MappingService.class.getName();

    final String GEOJSON_DIR = "geojson";

    final String KEY_MAP_SOURCE_TYPE = "mapSource";

    final String MAP_SOURCE_TYPE_DATABASE = "database";

    final String MAP_SOURCE_TYPE_GEOJSON = "geojson";

    final String MAP_SOURCE_TYPE_SHAPEFILE = "shapefile";

    final String MAP_TEMPL_DIR = "map_temp";

    // -------------------------------------------------------------------------
    // DataMapValue
    // -------------------------------------------------------------------------

    Collection<AggregatedMapValue> getAggregatedDataMapValues( int dataElementId, int periodId, String mapLayerPath );

    Collection<AggregatedMapValue> getAggregatedDataMapValues( int dataElementId, int periodId, int level );

    Collection<AggregatedMapValue> getDataElementMapValues( int dataElementId, int periodId, int parentOrganisationUnitId );
    
    // -------------------------------------------------------------------------
    // IndicatorMapValue
    // -------------------------------------------------------------------------

    Collection<AggregatedMapValue> getAggregatedIndicatorMapValues( int indicatorId, Collection<Integer> periodIds,
        String mapLayerPath, String featureId );

    Collection<AggregatedMapValue> getAggregatedIndicatorMapValues( int indicatorId, int periodId, String mapLayerPath );

    Collection<AggregatedMapValue> getAggregatedIndicatorMapValues( int indicatorId, int periodId, int level );

    Collection<AggregatedMapValue> getIndicatorMapValues( int indicatorId, int periodId, int parentOrganisationUnitId );
    
    // -------------------------------------------------------------------------
    // Map
    // -------------------------------------------------------------------------

    /**
     * Adds a Map.
     * 
     * @param map, the Map to add.
     * @return a generated unique id of the added Map.
     */
    int addMap( Map map );

    /**
     * Adds a Map.
     * 
     * @param name, Map description.
     * @param mapLayerPath, the link to Geoserver.
     * @param type, "polygon" or "point"
     * @param organisationUnitId, the id of the organisation unit.
     * @param organisationUnitLevelId, the level of the organisation units into
     *        which the map is devided.
     * @param uniqueColumn, the shapefile column which holds the unique
     *        organisation unit value.
     * @param nameColumn, the shapefile column which holds the name of the
     *        organisation unit.
     * @param longitude
     * @param latitude
     * @param zoom
     * @return a generated unique id of the added Map.
     */
    int addMap( String name, String mapLayerPath, String type, String sourceType, int organisationUnitId,
        int organisationUnitLevelId, String nameColumn, String longitude, String latitude, int zoom );

    /**
     * Adds a map. If a map with the same mapLayerPath already exists, the map
     * will be updated.
     * 
     * @param name, Map description.
     * @param mapLayerPath, the link to Geoserver.
     * @param type, "polygon" or "point"
     * @param organisationUnitId, the id of the organisation unit.
     * @param organisationUnitLevelId, the level of the organisation units into
     *        which the map is devided.
     * @param nameColumn, the shapefile column which holds the name of the
     *        organisation unit.
     * @param longitude
     * @param latitude
     * @param zoom
     */
    void addOrUpdateMap( String name, String mapLayerPath, String type, String sourceType, int organisationUnitId,
        int organisationUnitLevelId, String nameColumn, String longitude, String latitude, int zoom );

    /**
     * Updates a Map.
     * 
     * @param map, the Map to update.
     */
    void updateMap( Map map );

    /**
     * Deletes a Map.
     * 
     * @param map, the Map to delete.
     */
    void deleteMap( Map map );

    /**
     * Returns the Map with the given id.
     * 
     * @param id, the id of the map.
     * @return the Map with the given id.
     */
    Map getMap( int id );

    /**
     * Returns the Map with the given map layer path.
     * 
     * @param id, the id of the map.
     * @return a Map.
     */
    Map getMapByMapLayerPath( String mapLayerPath );

    /**
     * Returns a Collection<Map> of maps with the right type.
     * 
     * @param type, the map type.
     * @return a Collection<Map>.
     */
    Collection<Map> getMapsByType( String type );

    /**
     * Returns a Collection<Map> of maps by sourceType.
     * 
     * @return a Collection<Map>.
     */
    Collection<Map> getMapsBySourceType();

    /**
     * Returns a Collection of all Maps at the given level.
     * 
     * @param organisationUnitLevel, the organisation unit level to return maps
     *        at.
     * @return a Collection with all Maps at the given level.
     */
    Collection<Map> getMapsAtLevel( OrganisationUnitLevel organisationUnitLevel );

    /**
     * Returns a Collection of all registered Maps.
     * 
     * @return a Collection of all registered Maps.
     */
    Collection<Map> getAllMaps();

    /**
     * Returns a Collection of all generated Maps.
     * 
     * @return a Collection of all generated Maps.
     */
    Collection<Map> getAllGeneratedMaps();

    /**
     * Returns a Collection of all Maps.
     * 
     * @return a Collection of all Maps.
     */
    Collection<Map> getAllUserMaps();

    // -------------------------------------------------------------------------
    // MapOrganisationUnitRelation
    // -------------------------------------------------------------------------

    /**
     * Adds a MapOrganisationUnitRelation.
     * 
     * @param mapOrganisationUnitRelation, the MapOrganisationUnitRelation to
     *        add.
     * @return a generated unique id of the added MapOrganisationUnitRelation.
     */
    int addMapOrganisationUnitRelation( MapOrganisationUnitRelation mapOrganisationUnitRelation );

    /**
     * Adds a MapOrganisationUnitRelation.
     * 
     * @param mapLayerPath, the map the MapOrganisationUnitRelation should be
     *        added to.
     * @param organisationUnitId, an organisation unit in the database.
     * @param featureId, the id of an organisation unit in the shapefile.
     * @return a generated unique id of the added MapOrganisationUnitRelation.
     */
    int addMapOrganisationUnitRelation( String mapLayerPath, int organisationUnitId, String featureId );

    void addOrUpdateMapOrganisationUnitRelations( String mapLayerPath, String relations );

    /**
     * Adds a MapOrganisationUnitRelation. If it already exists, it will be
     * updated.
     * 
     * @param mapLayerPath, the map the MapOrganisationUnitRelation should be
     *        added to.
     * @param organisationUnitId, an organisation unit in the database.
     * @param featureId, the id of an organisation unit in the shapefile.
     * @return a generated unique id of the added MapOrganisationUnitRelation.
     */
    void addOrUpdateMapOrganisationUnitRelation( String mapLayerPath, int organisationUnitId, String featureId );

    /**
     * Updates a MapOrganisationUnitRelation.
     * 
     * @param mapOrganisationUnitRelation, the MapOrganisationUnitRelation to
     *        update.
     */
    void updateMapOrganisationUnitRelation( MapOrganisationUnitRelation mapOrganisationUnitRelation );

    /**
     * Deletes a MapOrganisationUnitRelation.
     * 
     * @param mapOrganisationUnitRelation, the MapOrganisationUnitRelation to
     *        delete.
     */
    void deleteMapOrganisationUnitRelation( MapOrganisationUnitRelation mapOrganisationUnitRelation );

    /**
     * Returns a MapOrganisationUnitRelation.
     * 
     * @param id, the id of the returned MapOrganisationUnitRelation.
     * @return the MapOrganisationUnitRelation with the given id.
     */
    MapOrganisationUnitRelation getMapOrganisationUnitRelation( int id );

    /**
     * Returns a Collection <MapOrganisationUnitRelation>.
     * 
     * @param map, the foreign Map in the MapOrganisationUnitRelation.
     * @param map, the foreign OrganisationUnit in the
     *        MapOrganisationUnitRelation.
     * @return a Collection<MapOrganisationUnitRelation> which contains the
     *         given Map and OrganisationUnit.
     */
    MapOrganisationUnitRelation getMapOrganisationUnitRelation( Map map, OrganisationUnit organisationUnit );

    MapOrganisationUnitRelation getMapOrganisationUnitRelationByFeatureId( String featureId, String mapLayerPath );

    /**
     * Returns a Collection of MapOrganisationUnitRelations.
     * 
     * @return a Collection of all MapOrganisationUnitRelations.
     */
    Collection<MapOrganisationUnitRelation> getAllMapOrganisationUnitRelations();

    /**
     * Returns a Collection of all MapOrganisationUnitRelations connected to the
     * given Map.
     * 
     * @param map, the Map to which the MapOrganisationUnitRelations are
     *        connected.
     * @return a Collection of MapOrganisationUnitRelations connected to the
     *         given Map.
     */
    Collection<MapOrganisationUnitRelation> getMapOrganisationUnitRelationsByMap( Map map );

    /**
     * Returns a Collection of all existing MapOrganisationUnitRelations and the
     * MapOrganisationUnitRelations that are not yet created (no featureId).
     * 
     * @param map, the Map to which the MapOrganisationUnitRelations are
     *        connected.
     * @return a Collection of MapOrganisationUnitRelations.
     */
    Collection<MapOrganisationUnitRelation> getAvailableMapOrganisationUnitRelations( Map map );

    /**
     * Returns a Collection of all existing MapOrganisationUnitRelations and the
     * MapOrganisationUnitRelations that are not yet created (no featureId).
     * 
     * @param mapLayerPath, the map to which the MapOrganisationUnitRelations
     *        are connected.
     * @return a Collection of MapOrganisationUnitRelations.
     */
    Collection<MapOrganisationUnitRelation> getAvailableMapOrganisationUnitRelations( String mapLayerPath );

    /**
     * Deletes all MapOrganisationUnitRelations associated with the given
     * OrganisationUnit.
     * 
     * @param organisationUnit the OrganisationUnit.
     * @return the number of deleted objects.
     */
    int deleteMapOrganisationUnitRelations( OrganisationUnit organisationUnit );

    /**
     * Deletes all MapOrganisationUnitRelations associated with the given Map.
     * 
     * @param map the Map.
     * @return the number of deleted objects.
     */
    int deleteMapOrganisationUnitRelations( Map map );

    // -------------------------------------------------------------------------
    // MapLegend
    // -------------------------------------------------------------------------

    void addOrUpdateMapLegend( String name, Double startValue, Double endValue, String color );

    void deleteMapLegend( MapLegend legend );

    MapLegend getMapLegend( int id );

    MapLegend getMapLegendByName( String name );

    Collection<MapLegend> getAllMapLegends();

    // -------------------------------------------------------------------------
    // MapLegendSet
    // -------------------------------------------------------------------------

    int addMapLegendSet( MapLegendSet legendSet );

    void updateMapLegendSet( MapLegendSet legendSet );

    void addOrUpdateMapLegendSet( String name, String type, int method, int classes, String colorLow, String colorHigh,
        Set<MapLegend> mapLegends );

    void deleteMapLegendSet( MapLegendSet legendSet );

    MapLegendSet getMapLegendSet( int id );

    MapLegendSet getMapLegendSetByName( String name );

    Collection<MapLegendSet> getMapLegendSetsByType( String type );

    MapLegendSet getMapLegendSetByIndicator( int indicatorId );

    Collection<MapLegendSet> getAllMapLegendSets();

    boolean indicatorHasMapLegendSet( int indicatorId );

    // -------------------------------------------------------------------------
    // MapView
    // -------------------------------------------------------------------------

    int addMapView( MapView mapView );

    int addMapView( String name, String mapValueType, int indicatorGroupId, int indicatorId, int dataElementGroupId,
        int dataElementId, String periodTypeName, int periodId, String mapSourceType, String mapSource,
        String mapLegendType, int method, int classes, String bounds, String colorLow, String colorHigh, int mapLegendSetId,
        String longitude, String latitude, int zoom );

    void updateMapView( MapView mapView );

    void addOrUpdateMapView( String name, String mapValueType, int indicatorGroupId, int indicatorId,
        int dataElementGroupId, int dataElementId, String periodTypeName, int periodId, String mapSource,
        String mapLegendType, int method, int classes, String bounds, String colorLow, String colorHigh, int mapLegendSetId,
        String longitude, String latitude, int zoom );

    void deleteMapView( MapView view );

    MapView getMapView( int id );

    MapView getMapViewByName( String name );

    Collection<MapView> getMapViewsByMapSourceType();

    Collection<MapView> getAllMapViews();

    // -------------------------------------------------------------------------
    // MapLayer
    // -------------------------------------------------------------------------

    int addMapLayer( MapLayer mapLayer );

    void updateMapLayer( MapLayer mapLayer );

    void addOrUpdateMapLayer( String name, String type, String mapSource, String layer, String fillColor,
        double fillOpacity, String strokeColor, int strokeWidth );

    void deleteMapLayer( MapLayer mapLayer );

    MapLayer getMapLayer( int id );

    MapLayer getMapLayerByName( String name );

    Collection<MapLayer> getMapLayersByType( String type );

    Collection<MapLayer> getMapLayersByMapSourceType();

    MapLayer getMapLayerByMapSource( String mapSource );

    Collection<MapLayer> getAllMapLayers();
}