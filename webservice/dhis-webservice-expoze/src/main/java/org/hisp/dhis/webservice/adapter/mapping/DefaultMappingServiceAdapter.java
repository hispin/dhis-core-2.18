package org.hisp.dhis.webservice.adapter.mapping;

import java.util.Collection;
import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.mapping.Map;
import org.hisp.dhis.mapping.MapOrganisationUnitRelation;
import org.hisp.dhis.mapping.MappingService;

public class DefaultMappingServiceAdapter
    implements MappingServiceAdapter
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MappingService mappingService;

    public void setMappingService( MappingService mappingService )
    {
        this.mappingService = mappingService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Map
    // -------------------------------------------------------------------------

    public int addMap( String mapLayerPath, int organisationUnitId, int organisationUnitLevelId, String uniqueColumn,
        String nameColumn, String longitude, String latitude, int zoom )
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        OrganisationUnitLevel organisationUnitLevel = organisationUnitService
            .getOrganisationUnitLevel( organisationUnitLevelId );

        Set<String> staticMapLayerPaths = null;

        Map map = new Map( mapLayerPath, organisationUnit, organisationUnitLevel, uniqueColumn, nameColumn, longitude,
            latitude, zoom, staticMapLayerPaths );

        return mappingService.addMap( map );
    }

    public void addOrUpdateMap( String mapLayerPath, int organisationUnitId, int organisationUnitLevelId,
        String uniqueColumn, String nameColumn, String longitude, String latitude, int zoom )
    {
        Map map = mappingService.getMapByMapLayerPath( mapLayerPath );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        OrganisationUnitLevel organisationUnitLevel = organisationUnitService
            .getOrganisationUnitLevel( organisationUnitLevelId );

        Set<String> staticMapLayerPaths = null;

        if ( map != null )
        {
            map.setOrganisationUnit( organisationUnit );
            map.setOrganisationUnitLevel( organisationUnitLevel );
            map.setUniqueColumn( uniqueColumn );
            map.setNameColumn( nameColumn );
            map.setLongitude( longitude );
            map.setLatitude( latitude );
            map.setZoom( zoom );

            mappingService.updateMap( map );
        }
        else
        {
            map = new Map( mapLayerPath, organisationUnit, organisationUnitLevel, uniqueColumn, nameColumn, longitude,
                latitude, zoom, staticMapLayerPaths );

            mappingService.addMap( map );
        }
    }

    // -------------------------------------------------------------------------
    // MapOrganisationUnitRelation
    // -------------------------------------------------------------------------

    public int addMapOrganisationUnitRelation( String mapLayerPath, int organisationUnitId, String featureId )
    {
        Map map = mappingService.getMapByMapLayerPath( mapLayerPath );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        MapOrganisationUnitRelation mapOrganisationUnitRelation = new MapOrganisationUnitRelation( map,
            organisationUnit, featureId );

        return mappingService.addMapOrganisationUnitRelation( mapOrganisationUnitRelation );
    }

    public void addOrUpdateMapOrganisationUnitRelation( String mapLayerPath, int organisationUnitId, String featureId )
    {
        Map map = mappingService.getMapByMapLayerPath( mapLayerPath );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        MapOrganisationUnitRelation mapOrganisationUnitRelation = mappingService.getMapOrganisationUnitRelation( map,
            organisationUnit );

        if ( mapOrganisationUnitRelation != null )
        {
            mapOrganisationUnitRelation.setFeatureId( featureId );

            mappingService.updateMapOrganisationUnitRelation( mapOrganisationUnitRelation );
        }
        else
        {
            mapOrganisationUnitRelation = new MapOrganisationUnitRelation( map, organisationUnit, featureId );

            mappingService.addMapOrganisationUnitRelation( mapOrganisationUnitRelation );
        }
    }

    public Collection<MapOrganisationUnitRelation> getAvailableMapOrganisationUnitRelations( String mapLayerPath )
    {
        Map map = mappingService.getMapByMapLayerPath( mapLayerPath );

        return mappingService.getAvailableMapOrganisationUnitRelations( map );
    }
}