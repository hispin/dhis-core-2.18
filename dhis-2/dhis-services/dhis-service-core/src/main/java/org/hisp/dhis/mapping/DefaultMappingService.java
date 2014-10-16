package org.hisp.dhis.mapping;

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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.RelativePeriods;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Jan Henrik Overland
 */
@Transactional
public class DefaultMappingService
    implements MappingService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MapStore mapStore;

    public void setMapStore( MapStore mapStore )
    {
        this.mapStore = mapStore;
    }

    private MapViewStore mapViewStore;

    public void setMapViewStore( MapViewStore mapViewStore )
    {
        this.mapViewStore = mapViewStore;
    }

    private MapLayerStore mapLayerStore;

    public void setMapLayerStore( MapLayerStore mapLayerStore )
    {
        this.mapLayerStore = mapLayerStore;
    }

    private GenericIdentifiableObjectStore<MapLegend> mapLegendStore;

    public void setMapLegendStore( GenericIdentifiableObjectStore<MapLegend> mapLegendStore )
    {
        this.mapLegendStore = mapLegendStore;
    }

    private GenericIdentifiableObjectStore<MapLegendSet> mapLegendSetStore;

    public void setMapLegendSetStore( GenericIdentifiableObjectStore<MapLegendSet> mapLegendSetStore )
    {
        this.mapLegendSetStore = mapLegendSetStore;
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

    // -------------------------------------------------------------------------
    // MappingService implementation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // MapLegend
    // -------------------------------------------------------------------------

    @Override
    public void addOrUpdateMapLegend( String name, Double startValue, Double endValue, String color, String image )
    {
        MapLegend mapLegend = getMapLegendByName( name );

        if ( mapLegend != null )
        {
            mapLegend.setName( name );
            mapLegend.setStartValue( startValue );
            mapLegend.setEndValue( endValue );
            mapLegend.setColor( color );
            mapLegend.setImage( image );

            mapLegendStore.update( mapLegend );
        }
        else
        {
            mapLegend = new MapLegend( name, startValue, endValue, color, image );

            mapLegendStore.save( mapLegend );
        }
    }

    @Override
    public int addMapLegend( MapLegend mapLegend )
    {
        return mapLegendStore.save( mapLegend );
    }

    @Override
    public void deleteMapLegend( MapLegend mapLegend )
    {
        mapLegendStore.delete( mapLegend );
    }

    @Override
    public MapLegend getMapLegend( int id )
    {
        return mapLegendStore.get( id );
    }

    @Override
    public MapLegend getMapLegend( String uid )
    {
        return mapLegendStore.getByUid( uid );
    }

    @Override
    public MapLegend getMapLegendByName( String name )
    {
        return mapLegendStore.getByName( name );
    }

    @Override
    public Collection<MapLegend> getAllMapLegends()
    {
        return mapLegendStore.getAll();
    }

    // -------------------------------------------------------------------------
    // MapLegendSet
    // -------------------------------------------------------------------------

    @Override
    public int addMapLegendSet( MapLegendSet mapLegendSet )
    {
        return mapLegendSetStore.save( mapLegendSet );
    }

    @Override
    public void updateMapLegendSet( MapLegendSet mapLegendSet )
    {
        mapLegendSetStore.update( mapLegendSet );
    }

    @Override
    public void addOrUpdateMapLegendSet( String name, String type, String symbolizer, Set<MapLegend> mapLegends )
    {
        MapLegendSet mapLegendSet = getMapLegendSetByName( name );

        Set<Indicator> indicators = new HashSet<>();

        Set<DataElement> dataElements = new HashSet<>();

        if ( mapLegendSet != null )
        {
            mapLegendSet.setSymbolizer( symbolizer );
            mapLegendSet.setMapLegends( mapLegends );

            mapLegendSetStore.update( mapLegendSet );
        }
        else
        {
            mapLegendSet = new MapLegendSet( name, type, symbolizer, mapLegends, indicators, dataElements );

            mapLegendSetStore.save( mapLegendSet );
        }
    }

    @Override
    public void deleteMapLegendSet( MapLegendSet mapLegendSet )
    {
        mapLegendSetStore.delete( mapLegendSet );
    }

    @Override
    public MapLegendSet getMapLegendSet( int id )
    {
        return mapLegendSetStore.get( id );
    }

    @Override
    public MapLegendSet getMapLegendSet( String uid )
    {
        return mapLegendSetStore.getByUid( uid );
    }

    @Override
    public MapLegendSet getMapLegendSetByName( String name )
    {
        return mapLegendSetStore.getByName( name );
    }

    @Override
    public Collection<MapLegendSet> getAllMapLegendSets()
    {
        return mapLegendSetStore.getAll();
    }

    // -------------------------------------------------------------------------
    // Map
    // -------------------------------------------------------------------------

    @Override
    public int addMap( Map map )
    {
        return mapStore.save( map );
    }

    @Override
    public void updateMap( Map map )
    {
        mapStore.update( map );
    }

    @Override
    public Map getMap( int id )
    {
        return mapStore.get( id );
    }

    @Override
    public Map getMap( String uid )
    {
        return mapStore.getByUid( uid );
    }

    @Override
    public Map getMapNoAcl( String uid )
    {
        return mapStore.getByUidNoAcl( uid );
    }

    @Override
    public void deleteMap( Map map )
    {
        mapStore.delete( map );
    }

    @Override
    public List<Map> getAllMaps()
    {
        return mapStore.getAll();
    }

    @Override
    public List<Map> getMapsBetweenLikeName( String name, int first, int max )
    {
        return mapStore.getAllLikeName( name, first, max );
    }

    // -------------------------------------------------------------------------
    // MapView
    // -------------------------------------------------------------------------

    @Override
    public int addMapView( MapView mapView )
    {
        return mapViewStore.save( mapView );
    }

    @Override
    public void updateMapView( MapView mapView )
    {
        mapViewStore.update( mapView );
    }

    @Override
    public void deleteMapView( MapView mapView )
    {
        mapViewStore.delete( mapView );
    }

    @Override
    public MapView getMapView( int id )
    {
        MapView mapView = mapViewStore.get( id );

        setMapViewLevel( mapView );

        return mapView;
    }

    @Override
    public MapView getMapView( String uid )
    {
        MapView mapView = mapViewStore.getByUid( uid );

        setMapViewLevel( mapView );

        return mapView;
    }

    private void setMapViewLevel( MapView mapView )
    {
        if ( mapView != null )
        {
            for ( OrganisationUnit unit : mapView.getOrganisationUnits() )
            {
                unit.setLevel( organisationUnitService.getLevelOfOrganisationUnit( unit.getId() ) );
            }
        }
    }

    @Override
    public MapView getMapViewByName( String name )
    {
        return mapViewStore.getByName( name );
    }

    @Override
    public MapView getIndicatorLastYearMapView( String indicatorUid, String organisationUnitUid, int level )
    {
        MapView mapView = new MapView();

        Period period = periodService.reloadPeriod( new RelativePeriods().setThisYear( true ).getRelativePeriods()
            .iterator().next() );

        Indicator indicator = indicatorService.getIndicator( indicatorUid );
        OrganisationUnit unit = organisationUnitService.getOrganisationUnit( organisationUnitUid );

        mapView.getIndicators().add( indicator );
        mapView.getPeriods().add( period );
        mapView.getOrganisationUnits().add( unit );
        mapView.getOrganisationUnitLevels().add( level );
        mapView.setName( indicator.getName() );

        return mapView;
    }

    @Override
    public Collection<MapView> getAllMapViews()
    {
        Collection<MapView> mapViews = mapViewStore.getAll();

        if ( mapViews.size() > 0 )
        {
            for ( MapView mapView : mapViews )
            {
                //TODO poor performance, fix

                for ( OrganisationUnit unit : mapView.getOrganisationUnits() )
                {
                    unit.setLevel( organisationUnitService.getLevelOfOrganisationUnit( unit.getId() ) );
                }
            }
        }

        return mapViews;
    }

    @Override
    public Collection<MapView> getMapViewsBetweenByName( String name, int first, int max )
    {
        return mapViewStore.getAllLikeName( name, first, max );
    }

    // -------------------------------------------------------------------------
    // MapLayer
    // -------------------------------------------------------------------------

    @Override
    public int addMapLayer( MapLayer mapLayer )
    {
        return mapLayerStore.save( mapLayer );
    }

    @Override
    public void updateMapLayer( MapLayer mapLayer )
    {
        mapLayerStore.update( mapLayer );
    }

    @Override
    public void addOrUpdateMapLayer( String name, String type, String url, String layers, String time,
        String fillColor, double fillOpacity, String strokeColor, int strokeWidth )
    {
        MapLayer mapLayer = mapLayerStore.getByName( name );

        if ( mapLayer != null )
        {
            mapLayer.setName( name );
            mapLayer.setType( type );
            mapLayer.setUrl( url );
            mapLayer.setLayers( layers );
            mapLayer.setTime( time );
            mapLayer.setFillColor( fillColor );
            mapLayer.setFillOpacity( fillOpacity );
            mapLayer.setStrokeColor( strokeColor );
            mapLayer.setStrokeWidth( strokeWidth );

            updateMapLayer( mapLayer );
        }
        else
        {
            addMapLayer( new MapLayer( name, type, url, layers, time, fillColor, fillOpacity, strokeColor, strokeWidth ) );
        }
    }

    @Override
    public void deleteMapLayer( MapLayer mapLayer )
    {
        mapLayerStore.delete( mapLayer );
    }

    @Override
    public MapLayer getMapLayer( int id )
    {
        return mapLayerStore.get( id );
    }

    @Override
    public MapLayer getMapLayer( String uid )
    {
        return mapLayerStore.getByUid( uid );
    }

    @Override
    public MapLayer getMapLayerByName( String name )
    {
        return mapLayerStore.getByName( name );
    }

    @Override
    public Collection<MapLayer> getMapLayersByType( String type )
    {
        return mapLayerStore.getMapLayersByType( type );
    }

    @Override
    public MapLayer getMapLayerByMapSource( String mapSource )
    {
        return mapLayerStore.getMapLayerByMapSource( mapSource );
    }

    @Override
    public Collection<MapLayer> getAllMapLayers()
    {
        return mapLayerStore.getAll();
    }

    @Override
    public int countMapViewMaps( MapView mapView )
    {
        return mapStore.countMapViewMaps( mapView );
    }

    @Override
    public int countDataSetMapViews( DataSet dataSet )
    {
        return mapViewStore.countDataSetAnalyticalObject( dataSet );
    }

    @Override
    public int countIndicatorMapViews( Indicator indicator )
    {
        return mapViewStore.countIndicatorAnalyticalObject( indicator );
    }

    @Override
    public int countDataElementMapViews( DataElement dataElement )
    {
        return mapViewStore.countDataElementAnalyticalObject( dataElement );
    }
    
    @Override
    public int countOrganisationUnitMapViews( OrganisationUnit organisationUnit )
    {
        return mapViewStore.countOrganisationUnitAnalyticalObject( organisationUnit );
    }

    @Override
    public int countMapLegendSetMapViews( MapLegendSet mapLegendSet )
    {
        return mapViewStore.countMapLegendSetMapViews( mapLegendSet );
    }
}
