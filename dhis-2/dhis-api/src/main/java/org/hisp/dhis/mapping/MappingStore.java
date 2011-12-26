package org.hisp.dhis.mapping;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.user.User;

/**
 * @author Jan Henrik Overland
 * @version $Id$
 */
public interface MappingStore
    extends GenericIdentifiableObjectStore<MapView>
{
    String ID = MappingStore.class.getName();

    // -------------------------------------------------------------------------
    // MapLegend
    // -------------------------------------------------------------------------

    int addMapLegend( MapLegend legend );

    void updateMapLegend( MapLegend legend );

    void deleteMapLegend( MapLegend legend );

    MapLegend getMapLegend( int id );

    MapLegend getMapLegendByName( String name );

    Collection<MapLegend> getAllMapLegends();

    // -------------------------------------------------------------------------
    // MapLegendSet
    // -------------------------------------------------------------------------

    int addMapLegendSet( MapLegendSet legendSet );

    void updateMapLegendSet( MapLegendSet legendSet );

    void deleteMapLegendSet( MapLegendSet legendSet );

    MapLegendSet getMapLegendSet( int id );

    MapLegendSet getMapLegendSetByName( String name );

    Collection<MapLegendSet> getMapLegendSetsByType( String type );

    Collection<MapLegendSet> getAllMapLegendSets();

    // -------------------------------------------------------------------------
    // MapView
    // -------------------------------------------------------------------------

    int addMapView( MapView mapView );

    void updateMapView( MapView mapView );

    void deleteMapView( MapView view );

    MapView getMapView( int id );

    MapView getMapViewByName( String name );

    Collection<MapView> getAllMapViews( User user );

    Collection<MapView> getMapViewsByFeatureType( String featureType, User user );

    // -------------------------------------------------------------------------
    // MapLayer
    // -------------------------------------------------------------------------

    int addMapLayer( MapLayer mapLayer );

    void updateMapLayer( MapLayer mapLayer );

    void deleteMapLayer( MapLayer mapLayer );

    MapLayer getMapLayer( int id );

    MapLayer getMapLayerByName( String name );

    Collection<MapLayer> getMapLayersByType( String type );

    MapLayer getMapLayerByMapSource( String mapSource );

    Collection<MapLayer> getAllMapLayers();
}