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

import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;

/**
 * @author Jan Henrik Overland
 * @version $Id$
 */
public interface MappingStore
{
    String ID = MappingStore.class.getName();
    
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
     * Returns a Collection of all Maps.
     * 
     * @return a Collection of all Maps.
     */
    Collection<Map> getAllMaps();

    /**
     * Returns a Collection of all Maps at the given level.
     * 
     * @param organisationUnitLevel, the organisation unit level to return maps
     *        at.
     * @return a Collection with all Maps at the given level.
     */
    Collection<Map> getMapsAtLevel( OrganisationUnitLevel organisationUnitLevel );
    
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
     * Returns a Collection<MapOrganisationUnitRelation>.
     * 
     * @param map, the foreign Map in the MapOrganisationUnitRelation.
     * @param map, the foreign OrganisationUnit in the
     *        MapOrganisationUnitRelation.
     * @return a Collection<MapOrganisationUnitRelation> which contains the given Map and
     *         OrganisationUnit.
     */
    MapOrganisationUnitRelation getMapOrganisationUnitRelation( Map map, OrganisationUnit organisationUnit );
    
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
    Collection<MapOrganisationUnitRelation> getMapOrganisationUnitRelationByMap( Map map );
    
    /**
     * Deletes all MapOrganisationUnitRelations associated with the given
     * OrganisationUnit.
     * 
     * @param organisationUnit the OrganisationUnit.
     * @return the number of deleted objects.
     */
    int deleteMapOrganisationUnitRelations( OrganisationUnit organisationUnit );

    /**
     * Deletes all MapOrganisationUnitRelations associated with the given
     * Map.
     * 
     * @param map the Map.
     * @return the number of deleted objects.
     */
    int deleteMapOrganisationUnitRelations( Map map );
    
    // -------------------------------------------------------------------------
    // MapLegendSet
    // -------------------------------------------------------------------------    
    
    int addMapLegendSet( MapLegendSet legendSet );
    
    void updateMapLegendSet( MapLegendSet legendSet );
    
    void deleteMapLegendSet( MapLegendSet legendSet );
    
    MapLegendSet getMapLegendSet( int id );
    
    MapLegendSet getMapLegendSetByName( String name );
    
    Collection<MapLegendSet> getAllMapLegendSets();
    
    // -------------------------------------------------------------------------
    // MapView
    // -------------------------------------------------------------------------    
    
    int addMapView( MapView mapView );
    
    void updateMapView( MapView mapView );
    
    void deleteMapView( MapView view );
    
    MapView getMapView( int id );
    
    MapView getMapViewByName( String name );
    
    Collection<MapView> getAllMapViews();
    
    // -------------------------------------------------------------------------
    // MapLayer
    // -------------------------------------------------------------------------

    int addMapLayer( MapLayer mapLayer );
    
    void updateMapLayer( MapLayer mapLayer );
    
    void deleteMapLayer( MapLayer mapLayer );
    
    MapLayer getMapLayer( int id );
    
    MapLayer getMapLayerByName( String name );
    
    MapLayer getMapLayerByMapSource( String mapSource );
    
    Collection<MapLayer> getAllMapLayers();
}