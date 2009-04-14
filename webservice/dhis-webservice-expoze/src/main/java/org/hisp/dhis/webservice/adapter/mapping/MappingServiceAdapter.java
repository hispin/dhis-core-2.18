package org.hisp.dhis.webservice.adapter.mapping;

import java.util.Collection;

import org.hisp.dhis.mapping.MapOrganisationUnitRelation;

public interface MappingServiceAdapter
{
    // -------------------------------------------------------------------------
    // Map
    // -------------------------------------------------------------------------

    int addMap( String mapLayerPath, int organisationUnitId, int organisationUnitLevelId, String uniqueColumn,
        String nameColumn, String longitude, String latitude, int zoom );

    void addOrUpdateMap( String mapLayerPath, int organisationUnitId, int organisationUnitLevelId, String uniqueColumn,
        String nameColumn, String longitude, String latitude, int zoom );

    // -------------------------------------------------------------------------
    // MapOrganisationUnitRelation
    // -------------------------------------------------------------------------

    int addMapOrganisationUnitRelation( String mapLayerPath, int organisationUnitId, String featureId );

    void addOrUpdateMapOrganisationUnitRelation( String mapLayerPath, int organisationUnitId, String featureId );

    Collection<MapOrganisationUnitRelation> getAvailableMapOrganisationUnitRelations( String mapLayerPath );
}
